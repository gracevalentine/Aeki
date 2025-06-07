const express = require('express');
const router = express.Router();
const userController = require('../Controller/userController');

router.post('/login', userController.loginUser);
router.post('/logout', userController.logoutUser);
router.post('/signup', userController.signupUser);
router.get('/profile/:id', userController.getUserProfile);
router.post('/topup', userController.topUpWallet);
router.get('/wallet/:id', userController.getWallet);


module.exports = router;