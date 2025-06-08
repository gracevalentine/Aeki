const express = require('express');
const router = express.Router();
const productController = require('../Controller/productController');

router.get('/searchProduct', productController.searchProduct);
router.get('/product/:id', productController.getProductDetail);

module.exports = router;