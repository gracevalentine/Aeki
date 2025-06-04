const express = require('express');
const router = express.Router();
const transactionController = require('../controller/transactionController');

router.post('/cart', transactionController.insertToCart);
router.get('/:id', transactionController.getTransactionById); // ← ini tambahan route baru

module.exports = router;
