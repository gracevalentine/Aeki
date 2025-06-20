const productRepository = require('../Repositories/productRepository');

// Fungsi untuk mencari produk
// Controller
exports.searchProduct = (req, res) => {
console.log("Query received:", req.query);
  const { name } = req.query;

  if (!name) {
    return res.status(400).json({ message: 'Nama produk wajib diisi.' });
  }

  productRepository.searchProduct(name, (err, results) => {
    if (err) return res.status(500).json({ message: err });
    if (results.length === 0) return res.status(404).json({ message: 'Produk tidak ditemukan.' });

    res.json({
      message: 'Produk ditemukan.',
      products: results
    });
  });
};

// Fungsi untuk mendapatkan detail produk
exports.getProductDetail = (req, res) => {
  const productId = req.params.id;

  productRepository.getProductDetail(productId, (err, product) => {
    if (err) {
      return res.status(500).json({ message: err });
    }

    if (!product) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    res.status(200).json({
      message: 'Detail produk ditemukan.',
      product
    });
  });
};

