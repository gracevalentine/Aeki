const express = require('express');
const router = express.Router();
const transactionController = require('../controller/transactionController');

router.post('/cart', transactionController.insertToCart);

module.exports = router;