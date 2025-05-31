exports.insertToCart = (req, res) => {
  const { user_id, product_id, quantity } = req.body;

  // Validasi input
  if (!user_id || !product_id || !quantity || quantity <= 0) {
    return res.status(400).json({
      message: 'user_id, product_id, dan quantity wajib diisi dengan benar.',
    });
  }

  // Cek apakah produk ada
  const checkProductQuery = 'SELECT stock_quantity FROM products WHERE product_id = ?';
  database.query(checkProductQuery, [product_id], (err, results) => {
    if (err) {
      console.error('Error saat cek produk:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    const availableStock = results[0].stock_quantity;

    if (availableStock < quantity) {
      return res.status(400).json({
        message: `Stok tidak mencukupi. Stok tersedia: ${availableStock}`,
      });
    }

    // Cek apakah produk sudah ada di keranjang user
    const checkCartQuery = 'SELECT * FROM cart WHERE user_id = ? AND product_id = ?';
    database.query(checkCartQuery, [user_id, product_id], (err, cartResults) => {
      if (err) {
        console.error('Error saat cek cart:', err);
        return res.status(500).json({ message: 'Terjadi kesalahan saat mengecek cart.' });
      }

      if (cartResults.length > 0) {
        // Jika sudah ada, update quantity
        const updateCartQuery = 'UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?';
        database.query(updateCartQuery, [quantity, user_id, product_id], (err) => {
          if (err) {
            console.error('Error saat update cart:', err);
            return res.status(500).json({ message: 'Gagal mengupdate cart.' });
          }

          res.status(200).json({ message: 'Cart berhasil diperbarui.' });
        });
      } else {
        // Jika belum ada, insert baru
        const insertCartQuery = 'INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)';
        database.query(insertCartQuery, [user_id, product_id, quantity], (err) => {
          if (err) {
            console.error('Error saat insert ke cart:', err);
            return res.status(500).json({ message: 'Gagal menambahkan ke cart.' });
          }

          res.status(201).json({ message: 'Produk berhasil ditambahkan ke cart.' });
        });
      }
    });
  });
};
