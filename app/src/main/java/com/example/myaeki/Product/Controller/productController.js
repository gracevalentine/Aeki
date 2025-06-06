const productRepository = require('../Repositories/productRepository');

// Fungsi untuk mencari produk
// Controller
exports.searchProduct = (req, res) => {
  const { productName } = req.query;

  if (!productName) {
    return res.status(400).json({ message: 'Nama produk wajib diisi.' });
  }

  productRepository.searchProduct(productName, (err, results) => {
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

// Fungsi untuk melakukan pembelian produk
exports.buyProduct = (req, res) => {
  const { user_id, product_id, quantity, payment_method } = req.body;

  if (!user_id || !product_id || !quantity || quantity <= 0 || !payment_method) {
    return res.status(400).json({
      message: 'user_id, product_id, quantity, dan payment_method wajib diisi dengan benar.'
    });
  }

  productRepository.buyProduct(user_id, product_id, quantity, payment_method, (err, result) => {
    if (err) {
      return res.status(500).json({ message: err });
    }
    res.status(200).json(result);
  });
};