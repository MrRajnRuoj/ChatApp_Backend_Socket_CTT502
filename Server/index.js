const express = require('express');
const app = express();
const server = require('http').createServer(app);
const io = require('socket.io').listen(server);
const { handleDisconnect } = require('./src/Configs');
const SocketManager = require('./src/SocketManager');

const PORT = process.env.PORT || 3231;

handleDisconnect();     // Create and handle DB connection

io.sockets.on('connection', SocketManager);

server.listen(PORT, () => {
    console.log('Server is running on port: ' + PORT);
});
 