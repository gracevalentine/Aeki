const express = require('express');
const router = express.Router();
const userController = require('../controller/userController');

router.post('/login', userController.loginUser);
router.post('/logout', userController.logoutUser);
router.post('/signup', userController.signupUser);
router.get('/profile/:id', userController.getUserProfile);

module.exports = router;    