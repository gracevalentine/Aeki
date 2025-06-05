const express = require('express');
const router = express.Router();
const topup_controller = require('../Controller/topup_wallet_controller');

router.post('/topup', topup_controller.topUpWallet);router.post('/topup', userController.topUpWallet);