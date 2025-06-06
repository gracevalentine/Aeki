const database = require('../../Configuration/database');

// Fungsi untuk mencari produk berdasarkan nama
const searchProduct = (productName, callback) => {
  const sql = 'SELECT * FROM products WHERE name LIKE ?';
  const searchQuery = `%${productName}%`; // Menggunakan LIKE untuk pencarian yang lebih fleksibel

  database.query(sql, [searchQuery], (err, results) => {
    if (err) {
      console.error('Error saat mencari produk:', err);
      return callback(err, null);
    }
    callback(null, results);
  });
};

// Fungsi untuk mendapatkan detail produk berdasarkan ID
const getProductDetail = (productId, callback) => {
  const sql = 'SELECT * FROM products WHERE product_id = ?';

  database.query(sql, [productId], (err, result) => {
    if (err) {
      console.error('Error saat ambil detail produk:', err);
      return callback(err, null);
    }
    callback(null, result);
  });
};

// Fungsi untuk melakukan pembelian produk
const buyProduct = (user_id, product_id, quantity, payment_method, callback) => {
  const queryProduct = 'SELECT price, stock_quantity FROM products WHERE product_id = ?';

  database.query(queryProduct, [product_id], (err, productResults) => {
    if (err) return callback(err, null);
    if (productResults.length === 0) return callback('Produk tidak ditemukan.', null);

    const product = productResults[0];

    if (product.stock_quantity < quantity) {
      return callback('Stok tidak cukup. Stok tersedia: ${product.stock_quantity}, null');
    }

    const totalAmount = product.price * quantity;

    // Cek saldo wallet jika metode pembayaran E_WALLET
    if (payment_method === 'E_WALLET') {
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
    } else {
      proceedTransaction();
    }

    function proceedTransaction() {
      database.beginTransaction(err => {
        if (err) return callback(err, null);

        const insertOrder = 'INSERT INTO orders (user_id, product_id, quantity, total_amount, status_order, created_at) VALUES (?, ?, ?, ?, ?, NOW())';
        database.query(insertOrder, [user_id, product_id, quantity, totalAmount, 'PROCESSING'], (err, orderResult) => {
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

            const insertPayment = 'INSERT INTO payment (order_id, payment_method, payment_status, payment_date) VALUES (?, ?, ?, NOW())';
            database.query(insertPayment, [orderId, payment_method, 'PAID'], (err) => {
              if (err) {
                return database.rollback(() => {
                  console.error('Error insert payment:', err);
                  return callback('Gagal menyimpan data pembayaran.', null);
                });
              }

              if (payment_method === 'E_WALLET') {
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
              } else {
                insertTransaction();
              }

              function insertTransaction() {
                const insertTransaction = `
                  INSERT INTO \transaction\ (order_id, product_id, quantity, subtotal)
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

                  callback(null, {
                    message: 'Pembelian berhasil.',
                    order_id: orderId,
                    user_id,
                    product_id,
                    quantity,
                    total_amount: totalAmount,
                    payment_method,
                    payment_status: 'COMPLETED'
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

module.exports = { searchProduct, getProductDetail, buyProduct };