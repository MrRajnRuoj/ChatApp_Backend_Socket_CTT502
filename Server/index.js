const express = require('express');
const app = express();
const server = require('http').createServer(app);
const io = require('socket.io').listen(server);
const { DBConnection } = require('./src/Configs');
const SocketManager = require('./src/SocketManager');

const PORT = process.env.PORT || 3231;

DBConnection.connect((error) => {
    if (error) throw error;
    console.log('Connected to DB!');
});
global.DB = DBConnection;

io.sockets.on('connection', SocketManager);

server.listen(PORT, () => {
    console.log('Server is running on port: ' + PORT);
});
 