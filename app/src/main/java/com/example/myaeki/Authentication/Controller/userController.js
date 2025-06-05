const userRepository = require('../Repositories/userRepository');

// LOGIN
exports.loginUser = (req, res) => {
  const { email, password } = req.body || {};

  if (!email || !password) {
    return res.status(400).json({ message: 'Email and password are required.' });
  }

  userRepository.getUserByEmailAndPassword(email, password, (err, results) => {
    if (err) {
      console.error('Login error:', err);
      return res.status(500).json({ message: 'Server error' });
    }

    if (results.length === 0) {
      return res.status(401).json({ message: 'Invalid credentials' });
    }

    req.session.user = {
      user_id: results[0].user_id,
      email: results[0].email
    };

    res.json({
      message: 'Login successful',
      user: {
        id: results[0].user_id,
        first_name: results[0].first_name,
        last_name: results[0].last_name,
        email: results[0].email
      }
    });
  });
};

// LOGOUT
exports.logoutUser = (req, res) => {
  req.session.destroy(err => {
    if (err) {
      return res.status(500).json({ message: 'Logout failed' });
    }
    res.clearCookie('connect.sid');
    res.json({ message: 'Logout successful' });
  });
};

// SIGNUP
exports.signupUser = async (req, res) => {
  const {
    first_name,
    last_name,
    email,
    password,
    email_checkbox,
    address,
    postal_code
  } = req.body || {};

  if (!first_name || !last_name || !email || !password || !address || !postal_code) {
    return res.status(400).json({ message: 'First name, last name, email, password, address, dan postal_code wajib diisi.' });
  }

  try {
    const [existingUsers] = await userRepository.checkEmailExists(email);

    if (existingUsers.length > 0) {
      return res.status(409).json({ message: 'Email sudah terdaftar.' });
    }

    const [resultUser] = await userRepository.insertUser(password, email, first_name, last_name);
    const newUserId = resultUser.insertId;

    await userRepository.insertCustomer(newUserId, address, postal_code);

    return res.status(201).json({
      message: 'Signup berhasil.',
      user: {
        id: newUserId,
        first_name,
        last_name,
        email,
        address,
        postal_code
      }
    });

  } catch (err) {
    console.error('Error saat signup:', err.message);
    return res.status(500).json({
      message: 'Terjadi kesalahan server.',
      error: err.message
    });
  }
};

// GET USER PROFILE
exports.getUserProfile = async (req, res) => {
  const userId = req.params.id;

  try {
    const [userResult] = await userRepository.getUserProfileById(userId);

    if (userResult.length === 0) {
      return res.status(404).json({ message: 'User tidak ditemukan.' });
    }

    return res.status(200).json({
      message: 'Profil user ditemukan.',
      user: userResult[0]
    });
  } catch (err) {
    console.error('Error saat ambil profile:', err);
    return res.status(500).json({ message: 'Gagal ambil data user.', error: err.message });
  }
};

// TOP UP WALLET
exports.topUpWallet = (req, res) => {
  const { user_id, amount } = req.body;

  if (!user_id || !amount || amount <= 0) {
    return res.status(400).json({ message: 'User ID dan jumlah top-up harus valid.' });
  }

  userRepository.getWalletByUserId(user_id, (err, results) => {
    if (err) {
      return res.status(500).json({ message: 'Gagal mengambil data customer.', error: err });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Customer tidak ditemukan.' });
    }

    const currentBalance = results[0].wallet || 0;
    const newBalance = parseFloat(currentBalance) + parseFloat(amount);

    userRepository.updateWalletByUserId(user_id, newBalance, (err2, result2) => {
      if (err2) {
        return res.status(500).json({ message: 'Gagal melakukan top-up.', error: err2 });
      }

      res.status(200).json({
        message: 'Top-up berhasil.',
        topup_sebanyak: amount,
        saldo_terbaru: newBalance
      });
    });
  });
};