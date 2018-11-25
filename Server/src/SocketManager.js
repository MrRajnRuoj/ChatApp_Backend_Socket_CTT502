const io = require('../index').io;
const Events = require('./Events');
const { validateLogin } = require('./Authentication/Login');

module.exports = function(socket) {
    socket.on(Events.REQUEST_LOGIN, (loginData) => {
        validateLogin(socket, loginData);
    });
}
