const express = require('express');
const cors = require('cors');
const session = require('express-session');
const userRoute = require('./route/userRoute');
const productRoute = require('./route/productRoute');

const app = express();
const PORT = 3000;

// Harus sebelum routes
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

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

app.get('/', (req, res) => {
res.send('AEKI backend is running...');
});

app.listen(PORT, () => {
console.log(`Server running on port ${PORT}`);
});