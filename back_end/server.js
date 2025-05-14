const express = require('express');
const cors = require('cors');
const session = require('express-session');
const bodyParser = require('body-parser');
const userRoute = require('./route/userRoute');

const app = express();
const PORT = 3000;

app.use(cors({
origin: 'http://10.0.2.2:3000', // Android emulator IP ke localhost
credentials: true
}));

app.use(bodyParser.json());
app.use(session({
secret: 'aeki-secret',
resave: false,
saveUninitialized: true,
cookie: { secure: false } // true kalau pakai HTTPS
}));

app.use('/users', userRoute);

app.get('/', (req, res) => {
res.send('AEKI backend is running...');
});

app.listen(PORT, () => {
console.log("Server running on port ${PORT}");
});