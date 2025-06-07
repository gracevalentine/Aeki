const database = require('../../Configuration/database');

// Cek stok produk
const getProductStockById = (product_id, callback) => {
  const sql = 'SELECT stock_quantity FROM products WHERE product_id = ?';
  database.query(sql, [product_id], callback);
};

// Cek apakah item sudah ada di cart
const checkCartItem = (user_id, product_id, callback) => {
  const sql = 'SELECT * FROM cart WHERE user_id = ? AND product_id = ?';
  database.query(sql, [user_id, product_id], callback);
};

// Update quantity cart
const updateCartQuantity = (user_id, product_id, quantity, callback) => {
  const sql = 'UPDATE cart SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?';
  database.query(sql, [quantity, user_id, product_id], callback);
};

// Insert item baru ke cart
const insertCartItem = (user_id, product_id, quantity, callback) => {
  const sql = 'INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)';
  database.query(sql, [user_id, product_id, quantity], callback);
};

// Ambil data transaksi berdasarkan ID
const getTransactionById = (transactionId, callback) => {
  const sql = `
    SELECT t.transaction_id, t.order_id, t.product_id, t.quantity, t.subtotal,
           p.name AS product_name, p.price AS product_price,
           o.user_id, o.total_amount, o.status_order, o.created_at
    FROM \`transaction\` t
    JOIN products p ON t.product_id = p.product_id
    JOIN orders o ON t.order_id = o.order_id
    WHERE t.transaction_id = ?
  `;
  database.query(sql, [transactionId], callback);
};

// Ambil semua item cart by user_id
const getCartByUserId = (user_id, callback) => {
  const sql = `
    SELECT c.cart_id, c.product_id, c.quantity,
           p.name AS product_name, p.price AS product_price, p.stock_quantity
    FROM cart c
    JOIN products p ON c.product_id = p.product_id
    WHERE c.user_id = ?
  `;
  database.query(sql, [user_id], callback);
};

module.exports = {
  getProductStockById,
  checkCartItem,
  updateCartQuantity,
  insertCartItem,
  getTransactionById,
  getCartByUserId
};