const repo = require('../Repositories/transaction_repo');

exports.insertToCart = (req, res) => {
  const { user_id, product_id, quantity } = req.body;

  if (!user_id || !product_id || !quantity || quantity <= 0) {
    return res.status(400).json({
      message: 'user_id, product_id, dan quantity wajib diisi dengan benar.',
    });
  }

  repo.getProductStockById(product_id, (err, results) => {
    if (err) {
      console.error('Error saat cek produk:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Produk tidak ditemukan.' });
    }

    const availableStock = results[0].stock_quantity;

    if (availableStock < quantity) {
      return res.status(400).json({
        message: `Stok tidak mencukupi. Stok tersedia: ${availableStock}`,
      });
    }

    repo.checkCartItem(user_id, product_id, (err, cartResults) => {
      if (err) {
        console.error('Error saat cek cart:', err);
        return res.status(500).json({ message: 'Terjadi kesalahan saat mengecek cart.' });
      }

      if (cartResults.length > 0) {
        repo.updateCartQuantity(user_id, product_id, quantity, (err) => {
          if (err) {
            console.error('Error saat update cart:', err);
            return res.status(500).json({ message: 'Gagal mengupdate cart.' });
          }

          res.status(200).json({ message: 'Cart berhasil diperbarui.' });
        });
      } else {
        repo.insertCartItem(user_id, product_id, quantity, (err) => {
          if (err) {
            console.error('Error saat insert ke cart:', err);
            return res.status(500).json({ message: 'Gagal menambahkan ke cart.' });
          }

          res.status(201).json({ message: 'Produk berhasil ditambahkan ke cart.' });
        });
      }
    });
  });
};

exports.getTransactionById = (req, res) => {
  const transactionId = req.params.id;

  if (!transactionId) {
    return res.status(400).json({ message: 'transaction_id wajib diisi.' });
  }

  repo.getTransactionById(transactionId, (err, results) => {
    if (err) {
      console.error('Error query transaksi:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan saat mengambil data transaksi.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Transaksi tidak ditemukan.' });
    }

    const trx = results[0];

    res.status(200).json({
      message: 'Transaksi ditemukan.',
      data: {
        transaction_id: trx.transaction_id,
        order_id: trx.order_id,
        user_id: trx.user_id,
        product_id: trx.product_id,
        product_name: trx.product_name,
        product_price: trx.product_price,
        quantity: trx.quantity,
        subtotal: trx.subtotal,
        total_amount: trx.total_amount,
        status_order: trx.status_order,
        created_at: trx.created_at
      }
    });
  });
};

exports.getCartByUserId = (req, res) => {
  const userId = req.params.user_id;

  if (!userId) {
    return res.status(400).json({ message: 'user_id wajib diisi.' });
  }

  repo.getCartByUserId(userId, (err, results) => {
    if (err) {
      console.error('Error saat ambil cart:', err);
      return res.status(500).json({ message: 'Terjadi kesalahan server saat mengambil cart.' });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: 'Cart kosong atau user tidak ditemukan.' });
    }

    res.status(200).json({
      message: 'Cart ditemukan.',
      data: results.map(item => ({
        cart_id: item.cart_id,
        product_id: item.product_id,
        product_name: item.product_name,
        product_price: item.product_price,
        quantity: item.quantity,
        stock_quantity: item.stock_quantity
      }))
    });
  });
};

exports.checkoutCart = (req, res) => {
console.log("==> Checkout controller dipanggil");
console.log('Checkout request body:', req.body);
  const { user_id, delivery_method } = req.body;


  if (!user_id || !delivery_method) {
    return res.status(400).json({ message: 'user_id dan delivery_method wajib diisi.' });
  }

  // Ambil isi cart user
  repo.getCartByUserId(user_id, (err, cartItems) => {
    if (err) return res.status(500).json({ message: 'Gagal mengambil cart' });
    if (cartItems.length === 0) return res.status(400).json({ message: 'Cart kosong' });

    const subtotal = cartItems.reduce((sum, item) => sum + item.product_price * item.quantity, 0);
    const deliveryFee = delivery_method === 'Delivery' ? 25000 : 0;
    const total = subtotal + deliveryFee;

    // Ambil saldo user
    repo.getUserWallet(user_id, (err, result) => {
      if (err) return res.status(500).json({ message: 'Gagal ambil data wallet' });

      const currentWallet = result[0].wallet;

      if (currentWallet < total) {
        return res.status(400).json({ message: 'Saldo tidak cukup untuk checkout.' });
      }

      // Lakukan transaksi
      repo.createOrder(user_id, total, (err, orderResult) => {
        if (err) return res.status(500).json({ message: 'Gagal membuat order.' });

        const orderId = orderResult.insertId;

        // Simpan setiap item sebagai transaksi
        const tasks = cartItems.map(item => {
          return new Promise((resolve, reject) => {
            repo.insertTransaction(orderId, item.product_id, item.quantity, item.product_price * item.quantity, (err) => {
              if (err) reject(err);
              else resolve();
            });
          });
        });

        // Jalankan semua transaksi
        Promise.all(tasks).then(() => {
          // Kurangi saldo wallet
          repo.updateUserWallet(user_id, currentWallet - total, (err) => {
            if (err) return res.status(500).json({ message: 'Gagal update saldo' });

            // Hapus cart
            repo.clearCart(user_id, (err) => {
              if (err) return res.status(500).json({ message: 'Gagal hapus cart' });

              res.status(200).json({ message: 'Checkout berhasil', total_dibayar: total });
            });
          });
        }).catch(err => {
          console.error(err);
          res.status(500).json({ message: 'Gagal menyimpan transaksi' });
        });
      });
    });
  });
};