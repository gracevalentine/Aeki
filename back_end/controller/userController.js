const database = require('../configuration/database');

exports.loginUser = (req, res) => {
// console.log("BODY:", req.body);
// console.log('REQ BODY DEBUG:', req.body);
// console.log('Headers:', req.headers['content-type']);

const { email, password } = req.body || {};
if (!email || !password) {
return res.status(400).json({ message: 'Email and password are required. ' });
}

const sql = 'SELECT * FROM users WHERE email = ? AND password = ?';
database.query(sql, [email, password], (err, results) => {
if (err) {
console.error('Login error:', err);
return res.status(500).json({ message: 'Server error' });
}
if (results.length === 0) {
  return res.status(401).json({ message: 'Invalid credentials' });
}

// Simpan session
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

exports.logoutUser = (req, res) => {
req.session.destroy(err => {
if (err) {
return res.status(500).json({ message: 'Logout failed' });
}
res.clearCookie('connect.sid');
res.json({ message: 'Logout successful' });
});
};


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

  // Validasi wajib
  if (!first_name || !last_name || !email || !password || !address || !postal_code) {
    return res.status(400).json({ message: 'First name, last name, email, password, address, dan postal_code wajib diisi.' });
  }

  try {
    // Cek apakah email sudah terdaftar
    const [existingUsers] = await database.promise().query(
      'SELECT * FROM users WHERE email = ?',
      [email]
    );

    if (existingUsers.length > 0) {
      return res.status(409).json({ message: 'Email sudah terdaftar.' });
    }

    // Langsung simpan password tanpa hashing (tidak aman untuk produksi!)
    const plainPassword = password;

    // Insert ke tabel users
    const [resultUser] = await database.promise().query(
      'INSERT INTO users (password, email, first_name, last_name) VALUES (?, ?, ?, ?)',
      [plainPassword, email, first_name, last_name]
    );

    const newUserId = resultUser.insertId;

    // Insert ke tabel customers
    await database.promise().query(
      'INSERT INTO customer (customer_id, address, postal_code) VALUES (?, ?, ?)',
      [newUserId, address, postal_code]
    );

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
    console.error('Error saat signup:', err.message); // Tambahkan log detail
    return res.status(500).json({
      message: 'Terjadi kesalahan server.',
      error: err.message // Tampilkan pesan error di response untuk debugging
    });
  }
};


exports.getUserProfile = async (req, res) => {
  const userId = req.params.id;

  try {
    const [userResult] = await database.promise().query(
      `SELECT 
         u.user_id,
         u.first_name,
         u.last_name,
         u.email,
         u.created_at,
         c.address,
         c.postal_code,
         c.phone_number
       FROM users u
       LEFT JOIN customer c ON u.user_id = c.customer_id
       WHERE u.user_id = ?`,
      [userId]
    );

    if (userResult.length === 0) {
      return res.status(404).json({ message: 'User tidak ditemukan.' });
    }

    return res.status(200).json({
      message: 'Profil user ditemukan.',
      user: userResult[0]
    });
  } catch (err) {
    console.error('Error saat ambil profile:', err);
    return res.status(500).json({ message: 'Gagal ambil data user.', error: err.message });
  }
};