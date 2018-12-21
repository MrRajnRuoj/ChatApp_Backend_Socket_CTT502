const io = require('./../index').io;
const Events = require('./Events/Events');
const { validateLogin } = require('./Authentication/Login');
const { validateSignup, verifyEmail, resendVerifyCode } = require('./Authentication/Signup');
const { getConnectedUser } = require('./Users')

module.exports = function(socket) {
    console.log('client connected');
    socket.on(Events.REQUEST_LOGIN, (loginData) => {
        validateLogin(socket, loginData);
    });

    socket.on(Events.REQUEST_SIGNUP, (signupData) => {
        validateSignup(socket, signupData);
    });

    socket.on(Events.REQUEST_VERIFY_EMAIL, (verifyData) => {
        verifyEmail(socket, verifyData);
    });

    socket.on(Events.REQUEST_RESEND_VERIFY_CODE, (data) => {
        resendVerifyCode(socket, data);
    });

    socket.on(Events.SEND_MESSAGE, (data) => {
        getConnectedUser(socket.id).sendMessage(data);
    });

    socket.on(Events.REQUEST_ADD_FRIEND, (data) => {
        getConnectedUser(socket.id).requestFriend(data);
    });

    socket.on(Events.RESPONSE_ADD_FRIEND, (data) => {
        getConnectedUser(socket.id).responseFriendRequest(data);
    })
   
}
