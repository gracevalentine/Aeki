const database = require('../configuration/database');

exports.searchProduct = async (req, res) => {
  const { productName } = req.query; // Mengambil nama produk dari query parameter

  // Validasi input
  if (!productName) {
    return res.status(400).json({ message: 'Nama produk wajib diisi.' });
  }

  // Query untuk mencari produk berdasarkan nama
  const sql = 'SELECT * FROM products WHERE name LIKE ?';
  const searchQuery = `%${productName}%`; // Menggunakan LIKE untuk pencarian yang lebih fleksibel

  database.query(sql, [searchQuery], (err, results) => {
    if (err) {
      console.error('Error saat mencari produk:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    // Jika produk ditemukan, kembalikan detail produk
    res.json({
      message: 'Produk ditemukan.',
      products: results // Mengembalikan semua produk yang cocok
    });
  });
};

exports.buyProduct = (req, res) => {
  const { user_id, product_id, quantity, payment_method } = req.body;

  if (!user_id || !product_id || !quantity || quantity <= 0 || !payment_method) {
    return res.status(400).json({
      message: 'user_id, product_id, quantity, dan payment_method wajib diisi dengan benar.'
    });
  }

  const queryProduct = 'SELECT price, stock_quantity FROM products WHERE product_id = ?';
  database.query(queryProduct, [product_id], (err, productResults) => {
    if (err) {
      console.error('Error query produk:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server.' });
    }

    if (productResults.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    const product = productResults[0];

    if (product.stock_quantity < quantity) {
      return res.status(400).json({
        message: `Stok tidak cukup. Stok tersedia: ${product.stock_quantity}`
      });
    }

    const totalAmount = product.price * quantity;

    // Cek saldo wallet jika metode WALLET
    if (payment_method === 'E_WALLET') {
      const queryWallet = 'SELECT c.wallet FROM users u JOIN customer c ON u.user_id = c.customer_id WHERE u.user_id = ?';
      database.query(queryWallet, [user_id], (err, userResults) => {
        if (err) {
          console.error('Error query user wallet:', err);
          return res.status(500).json({ message: 'Terjadi kesalahan saat cek saldo wallet.' });
        }

        if (userResults.length === 0) {
          return res.status(404).json({ message: 'User tidak ditemukan.' });
        }

        const userWallet = userResults[0].wallet;

        if (userWallet < totalAmount) {
          return res.status(400).json({ message: 'Saldo wallet tidak cukup.' });
        }

        // Lanjut ke transaksi
        lanjutkanTransaksi();
      });
    } else {
      // Metode selain wallet, langsung lanjut
      lanjutkanTransaksi();
    }

    function lanjutkanTransaksi() {
      database.beginTransaction(err => {
        if (err) {
          return res.status(500).json({ message: 'Gagal memulai transaksi.' });
        }

        const insertOrder = 'INSERT INTO orders (user_id, product_id, quantity, total_amount, status_order, created_at) VALUES (?, ?, ?, ?, ?, NOW())';
        database.query(insertOrder, [user_id, product_id, quantity, totalAmount, 'PROCESSING'], (err, orderResult) => {
          if (err) {
            return database.rollback(() => {
              console.error('Error insert order:', err);
              res.status(500).json({ message: 'Gagal membuat order.' });
            });
          }

          const orderId = orderResult.insertId;

          const updateStock = 'UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?';
          database.query(updateStock, [quantity, product_id], (err) => {
            if (err) {
              return database.rollback(() => {
                console.error('Error update stok:', err);
                res.status(500).json({ message: 'Gagal mengupdate stok produk.' });
              });
            }

            const insertPayment = 'INSERT INTO payment (order_id, payment_method, payment_status, payment_date) VALUES (?, ?, ?, NOW())';
            database.query(insertPayment, [orderId, payment_method, 'PAID'], (err) => {
              if (err) {
                return database.rollback(() => {
                  console.error('Error insert payment:', err);
                  res.status(500).json({ message: 'Gagal menyimpan data pembayaran.' });
                });
              }

              // Kalau pakai wallet, kurangi saldo user
              if (payment_method === 'E_WALLET') {
                const updateWallet = 'UPDATE customer SET wallet = wallet - ? WHERE customer_id = ?';
                database.query(updateWallet, [totalAmount, user_id], (err) => {
                  if (err) {
                    return database.rollback(() => {
                      console.error('Error update wallet:', err);
                      res.status(500).json({ message: 'Gagal mengurangi saldo wallet.' });
                    });
                  }

                  commitSelesai();
                });
              } else {
                commitSelesai();
              }

              function commitSelesai() {
                database.commit(err => {
                  if (err) {
                    return database.rollback(() => {
                      console.error('Error commit transaksi:', err);
                      res.status(500).json({ message: 'Gagal menyelesaikan transaksi.' });
                    });
                  }

                  res.status(200).json({
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


exports.getProductDetail = async (req, res) => {
  const productId = req.params.id;

  try {
    const [productResult] = await database.promise().query(
      `SELECT * FROM products WHERE product_id = ?`,
      [productId]
    );

    if (productResult.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    return res.status(200).json({
      message: 'Detail produk ditemukan.',
      product: productResult[0]
    });
  } catch (err) {
    console.error('Error saat ambil detail produk:', err);
    return res.status(500).json({ message: 'Gagal ambil data produk.', error: err.message });
  }
};