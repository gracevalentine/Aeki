const express = require('express');
const router = express.Router();
const transactionController = require('../Route/transaction_controller');

router.post('/cart', transactionController.insertToCart);
router.get('/:id', transactionController.getTransactionById); 

module.exports = router;
