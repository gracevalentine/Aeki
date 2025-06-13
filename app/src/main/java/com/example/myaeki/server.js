const express = require('express');
const cors = require('cors');
const session = require('express-session');
const cookieParser = require('cookie-parser');
const userRoute = require('./Authentication/Route/userRoute');
const productRoute = require('./Product/Route/productRoute');
const wallet_route = require('./Wallet/Route/wallet_route');
const transaction_route = require('./Transaction/Route/transaction_route');

const app = express();
const PORT = 3000;

// Harus sebelum routes
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

app.use(cors({
  origin: 'http://10.0.2.2:3000', // emulator IP
  credentials: true
}));



app.use(session({
  secret: 'secret_key',
  resave: false,
  saveUninitialized: true,
  cookie: { secure: false }
}));

app.use('/users', userRoute);
app.use('/products', productRoute);
app.use('/wallet', wallet_route);
app.use('/transaction', transaction_route);

app.get('/', (req, res) => {
  res.send('AEKI backend is running...');
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
