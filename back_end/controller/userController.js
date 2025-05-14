const database = require('../configuration/database');

exports.loginUser = (req, res) => {
const { username, password } = req.body;

if (!username || !password) {
return res.status(400).json({ message: 'Username or password missing' });
}

const sql = 'SELECT * FROM   WHERE username = ? AND password = ?';
database.query(sql, [username, password], (err, results) => {
if (err) {
console.error('Login error:', err);
return res.status(500).json({ message: 'Server error' });
}
if (results.length === 0) {
  return res.status(401).json({ message: 'Invalid credentials' });
}

// Save session
req.session.user = {
  user_id: results[0].user_id,
  username: results[0].username
};

res.json({
  message: 'Login successful',
  user: {
    id: results[0].user_id,
    username: results[0].username,
    email: results[0].email
  }
});
});
};

exports.logoutUser = (req, res) => {
req.session.destroy((err) => {
if (err) {
return res.status(500).json({ message: 'Logout failed' });
}
res.clearCookie('connect.sid');
res.json({ message: 'Logout successful' });
});
};