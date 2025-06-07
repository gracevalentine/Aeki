const express = require('express');
const router = express.Router();
const transactionController = require('../Controller/transaction_controller');

router.post('/cart', transactionController.insertToCart);
router.get('/:id', transactionController.getTransactionById);
router.get('/cart/:user_id', transactionController.getCartByUserId);


module.exports = router;
