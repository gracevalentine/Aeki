const database = require('../../Configuration/database');

// Login
const getUserByEmailAndPassword = (email, password, callback) => {
  const sql = 'SELECT * FROM users WHERE email = ? AND password = ?';
  database.query(sql, [email, password], callback);
};

// Cek email saat signup
const checkEmailExists = (email) => {
  return database.promise().query(
    'SELECT * FROM users WHERE email = ?',
    [email]
  );
};

// Insert ke tabel users saat signup
const insertUser = (password, email, first_name, last_name) => {
  return database.promise().query(
    'INSERT INTO users (password, email, first_name, last_name) VALUES (?, ?, ?, ?)',
    [password, email, first_name, last_name]
  );
};

// Insert ke tabel customer saat signup
const insertCustomer = (userId, address, postal_code) => {
  return database.promise().query(
    'INSERT INTO customer (customer_id, address, postal_code) VALUES (?, ?, ?)',
    [userId, address, postal_code]
  );
};

// Ambil data user untuk profil
const getUserProfileById = (userId) => {
  return database.promise().query(
    `SELECT
       u.user_id,
       u.first_name,
       u.last_name,
       u.email,
       u.created_at,
       c.address,
       c.postal_code,
       c.phone_number,
       c.wallet       
     FROM users u
     LEFT JOIN customer c ON u.user_id = c.customer_id
     WHERE u.user_id = ?`,
    [userId]
  );
};



// Cek saldo saat top-up
const getWalletByUserId = (userId) => {
  // Gunakan database.promise().query() yang return-nya [rows, fields]
  return database.promise().query(
    `SELECT wallet FROM customer WHERE customer_id = ?`,
    [userId]
  );
};

// Update saldo wallet saat top-up
const updateWalletByUserId = (userId, newBalance, callback) => {
  const sql = 'UPDATE customer SET wallet = ? WHERE customer_id = ?';
  database.query(sql, [newBalance, userId], callback);
};

module.exports = {
  getUserByEmailAndPassword,
  checkEmailExists,
  insertUser,
  insertCustomer,
  getUserProfileById,
  getWalletByUserId,
  updateWalletByUserId
};