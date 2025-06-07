const express = require('express');
const router = express.Router();
const transactionController = require('../Controller/transaction_controller');

router.post('/cart', transactionController.insertToCart);
router.get('/:id', transactionController.getTransactionById); 

module.exports = router;
