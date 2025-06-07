const express = require('express');
const router = express.Router();
const topupController = require('../Controller/topup_wallet_controller');

router.post('/topup', topupController.topUpWallet);
router.get('/users/wallet/:id', topupController.getWalletByUserId);


module.exports = router;