const io = require('../index').io;
const Events = require('./Events/Events');
const { validateLogin } = require('./Authentication/Login');
const { validateSignup } = require('./Authentication/Signup');

module.exports = function(socket) {
    socket.on(Events.REQUEST_LOGIN, (loginData) => {
        validateLogin(socket, loginData);
    });

    socket.on(Events.REQUEST_SIGNUP, (signupData) => {
        validateSignup(socket, signupData);
    })
}
