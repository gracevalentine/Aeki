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
  const { user_id, product_id, quantity, payment_method} = req.body;

  if (!user_id || !product_id || !quantity || quantity <= 0 || !payment_method) {
    return res.status(400).json({ message: 'user_id, product_id, quantity, dan payment_method wajib diisi dengan benar.' });
  }

  // Cek produk dan stok
  const queryProduct = 'SELECT price, stock_quantity FROM products WHERE product_id = ?';
  database.query(queryProduct, [product_id], (err, results) => {
    if (err) {
      console.error('Error query produk:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    const product = results[0];

    if (product.stock_quantity < quantity) {
      return res.status(400).json({ message: `Stok tidak cukup. Stok tersedia: ${product.stock_quantity}` });
    }

    const totalAmount = product.price * quantity;

    database.beginTransaction(err => {
      if (err) {
        return res.status(500).json({ message: 'Gagal memulai transaksi.' });
      }

      // Insert ke orders
      const insertOrder = 'INSERT INTO orders (user_id, product_id, quantity, total_amount, status_order, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())';
      database.query(insertOrder, [user_id, product_id, quantity, totalAmount, 'PROCESSING'], (err, orderResult) => {
        if (err) {
          return database.rollback(() => {
            console.error('Error insert order:', err);
            res.status(500).json({ message: 'Gagal membuat order.' });
          });
        }

        const orderId = orderResult.insertId;

        // Update stok produk
        const updateStock = 'UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?';
        database.query(updateStock, [quantity, product_id], (err) => {
          if (err) {
            return database.rollback(() => {
              console.error('Error update stok:', err);
              res.status(500).json({ message: 'Gagal mengupdate stok produk.' });
            });
          }

          // Insert ke payment
          const insertPayment = 'INSERT INTO payment (order_id, payment_method, payment_status, payment_date) VALUES (?, ?, ?, NOW())';
          database.query(insertPayment, [orderId, payment_method, 'PAID'], (err) => {
            if (err) {
              return database.rollback(() => {
                console.error('Error insert payment:', err);
                res.status(500).json({ message: 'Gagal menyimpan data pembayaran.' });
              });
            }

            // Commit transaksi
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
          });
        });
      });
    });
  });
};