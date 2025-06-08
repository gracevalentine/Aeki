const database = require('../../Configuration/database');

// Fungsi untuk mencari produk berdasarkan nama
const searchProduct = (name, callback) => {
  const sql = 'SELECT * FROM products WHERE name LIKE ?';
  const searchQuery = `%${name}%`;

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


module.exports = { searchProduct, getProductDetail };