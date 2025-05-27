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