const express = require('express');
const router = express.Router();
const topupController = require('../Controller/topup_wallet_controller');

router.post('/topup', topupController.topUpWallet);

module.exports = router;