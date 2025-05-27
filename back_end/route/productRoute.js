const express = require('express');
const router = express.Router();
const productController = require('../controller/productController');

router.get('/searchProduct', productController.searchProduct);

module.exports = router;