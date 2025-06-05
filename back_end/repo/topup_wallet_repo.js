const database = require('../../Configuration/database');

// Cek saldo saat top-up
const getWalletByUserId = (userId, callback) => {
  const sql = 'SELECT wallet FROM customer WHERE customer_id = ?';
  database.query(sql, [userId], callback);
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