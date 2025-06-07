const database = require('../../Configuration/database');

const getWalletByUserId = (userId, callback) => {
  const sql = 'SELECT wallet FROM customer WHERE customer_id = ?';
  database.query(sql, [userId], callback);
};

const updateWalletByUserId = (userId, newBalance, callback) => {
  const sql = 'UPDATE customer SET wallet = ? WHERE customer_id = ?';
  database.query(sql, [newBalance, userId], callback);
};

module.exports = {
  getWalletByUserId,
  updateWalletByUserId
};
