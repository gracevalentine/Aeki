const express = require('express');
const router = express.Router();
const productController = require('../Controller/productController');

router.get('/searchProduct', productController.searchProduct);
router.post('/buy', productController.buyProduct);
router.get('/product/:id', productController.getProductDetail);

module.exports = router;