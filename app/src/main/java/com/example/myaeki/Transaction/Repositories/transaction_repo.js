const database = require('../../Configuration/database');

// Cek stok produk
const getProductStockById = (product_id, callback) => {
  const sql = 'SELECT stock_quantity FROM products WHERE product_id = ?';
  database.query(sql, [product_id], callback);
};

// Cek apakah item sudah ada di cart
const checkCartItem = (user_id, product_id, callback) => {
  const sql = 'SELECT * FROM cart WHERE user_id = ? AND product_id = ?';
  database.query(sql, [user_id, product_id], callback);
};

// Update quantity cart
const updateCartQuantity = (user_id, product_id, quantity, callback) => {
  const sql = 'UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?';
  database.query(sql, [quantity, user_id, product_id], callback);
};

// Insert item baru ke cart
const insertCartItem = (user_id, product_id, quantity, callback) => {
  const sql = 'INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)';
  database.query(sql, [user_id, product_id, quantity], callback);
};

// Ambil data transaksi berdasarkan ID
const getTransactionById = (transactionId, callback) => {
  const sql = `
    SELECT t.transaction_id, t.order_id, t.product_id, t.quantity, t.subtotal,
           p.name AS product_name, p.price AS product_price,
           o.user_id, o.total_amount, o.created_at
    FROM \`transaction\` t
    JOIN products p ON t.product_id = p.product_id
    JOIN orders o ON t.order_id = o.order_id
    WHERE t.transaction_id = ?
  `;
  database.query(sql, [transactionId], callback);
};

// Ambil semua item cart by user_id
const getCartByUserId = (user_id, callback) => {
  const sql = `
    SELECT c.cart_id, c.product_id, c.quantity,
           p.name AS product_name, p.price AS product_price, p.stock_quantity
    FROM cart c
    JOIN products p ON c.product_id = p.product_id
    WHERE c.user_id = ?
  `;
  database.query(sql, [user_id], callback);
};

// Ambil wallet user
const getUserWallet = (user_id, callback) => {
  const sql = 'SELECT c.wallet FROM users u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_id = ?';
  database.query(sql, [user_id], callback);
};

// Update wallet user
const updateUserWallet = (user_id, newWallet, callback) => {
  const sql = 'UPDATE users SET wallet = ? WHERE user_id = ?';
  database.query(sql, [newWallet, user_id], callback);
};

// Simpan order
const createOrder = (user_id, total_amount, callback) => {
  const sql = 'INSERT INTO orders (user_id, total_amount, created_at) VALUES (?, ?, NOW())';
  database.query(sql, [user_id, total_amount], callback);
};

// Simpan transaksi
const insertTransaction = (order_id, product_id, quantity, subtotal, callback) => {
  const sql = 'INSERT INTO `transaction` (order_id, product_id, quantity, subtotal) VALUES (?, ?, ?, ?)';
  database.query(sql, [order_id, product_id, quantity, subtotal], callback);
};

// Hapus cart
const clearCart = (user_id, callback) => {
  const sql = 'DELETE FROM cart WHERE user_id = ?';
  database.query(sql, [user_id], callback);
};

const buyProduct = (user_id, product_id, quantity, callback) => {
  const queryProduct = 'SELECT price, stock_quantity FROM products WHERE product_id = ?';

  database.query(queryProduct, [product_id], (err, productResults) => {
    if (err) return callback(err, null);
    if (productResults.length === 0) return callback('Produk tidak ditemukan.', null);

    const product = productResults[0];

    if (product.stock_quantity < quantity) {
      return callback(`Stok tidak cukup. Stok tersedia: ${product.stock_quantity}`, null);
    }

    const totalAmount = product.price * quantity;

      const queryWallet = 'SELECT c.wallet FROM users u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_id = ?';
      database.query(queryWallet, [user_id], (err, userResults) => {
        if (err) return callback(err, null);
        if (userResults.length === 0) return callback('User tidak ditemukan.', null);

        const userWallet = userResults[0].wallet;

        if (userWallet < totalAmount) {
          return callback('Saldo wallet tidak cukup.', null);
        }

        proceedTransaction();
      });

    function proceedTransaction() {
      database.beginTransaction(err => {
        if (err) return callback(err, null);

        const insertOrder = 'INSERT INTO orders (user_id, product_id, quantity, total_amount, created_at) VALUES (?, ?, ?, ?, NOW())';
        database.query(insertOrder, [user_id, product_id, quantity, totalAmount], (err, orderResult) => {
          if (err) {
            return database.rollback(() => {
              console.error('Error insert order:', err);
              return callback('Gagal membuat order.', null);
            });
          }

          const orderId = orderResult.insertId;

          const updateStock = 'UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?';
          database.query(updateStock, [quantity, product_id], (err) => {
            if (err) {
              return database.rollback(() => {
                console.error('Error update stok:', err);
                return callback('Gagal mengupdate stok produk.', null);
              });
            }

            const insertPayment = 'INSERT INTO payment (order_id, payment_date) VALUES (?, NOW())';
            database.query(insertPayment, [orderId], (err) => {
              if (err) {
                return database.rollback(() => {
                  console.error('Error insert payment:', err);
                  return callback('Gagal menyimpan data pembayaran.', null);
                });
              }
                const updateWallet = 'UPDATE customer SET wallet = wallet - ? WHERE customer_id = ?';
                database.query(updateWallet, [totalAmount, user_id], (err) => {
                  if (err) {
                    return database.rollback(() => {
                      console.error('Error update wallet:', err);
                      return callback('Gagal mengurangi saldo wallet.', null);
                    });
                  }
                  insertTransaction();
                });
              

              function insertTransaction() {
                const insertTransaction = `
                  INSERT INTO transaction (order_id, product_id, quantity, subtotal)
                  VALUES (?, ?, ?, ?)
                `;
                database.query(insertTransaction, [orderId, product_id, quantity, totalAmount], (err) => {
                  if (err) {
                    return database.rollback(() => {
                      console.error('Error insert transaction:', err);
                      return callback('Gagal menyimpan transaksi.', null);
                    });
                  }
                  commitTransaction();
                });
              }

              function commitTransaction() {
                database.commit(err => {
                  if (err) {
                    return database.rollback(() => {
                      console.error('Error commit transaksi:', err);
                      return callback('Gagal menyelesaikan transaksi.', null);
                    });
                  }

                  // Setelah transaksi berhasil, hapus atau update quantity di cart
                  const updateCartQuantity = `
                    UPDATE cart 
                    SET quantity = quantity - ? 
                    WHERE user_id = ? AND product_id = ? AND quantity >= ?
                  `;

                  database.query(updateCartQuantity, [quantity, user_id, product_id, quantity], (err, result) => {
                    if (err) {
                      console.error('Gagal update quantity di cart:', err);
                    } else {
                      if (result.affectedRows === 0) {
                        // Jika quantity kurang dari yang dibeli, hapus item dari cart
                        const deleteFromCart = 'DELETE FROM cart WHERE user_id = ? AND product_id = ?';
                        database.query(deleteFromCart, [user_id, product_id], (err) => {
                          if (err) {
                            console.error('Gagal hapus item dari cart:', err);
                          }
                        });
                      }
                    }
                    // Kirim response sukses ke caller
                    callback(null, {
                      message: 'Pembelian berhasil.',
                      order_id: orderId,
                      user_id,
                      product_id,
                      quantity,
                      total_amount: totalAmount
                    });
                  });
                });
              }
            });
          });
        });
      });
    }
  });
};


module.exports = {
  getProductStockById,
  checkCartItem,
  updateCartQuantity,
  insertCartItem,
  getTransactionById,
  getCartByUserId,
  getUserWallet,
  updateUserWallet,
  createOrder,
  insertTransaction,
  clearCart,
  buyProduct
};

