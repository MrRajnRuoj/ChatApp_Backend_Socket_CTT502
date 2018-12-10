const sha256 = require('sha256');
const jwt = require('jsonwebtoken');
const { SECRET_KEY } = require('./../../Configs');
const Events = require('./../../Events/Events');
const Error = require('./../../Errors/Error');
const User = require('./../../Users/User');
const { addConnectedUser, addConnectedSocket } = require('./../../Users');

const validateLogin = (socket, loginData) => {
    if (loginData === null || loginData === undefined) return;
    try {
        if (loginData.token !== undefined && loginData.token !== null) {
            verifyToken(socket, loginData.token);
        }
        else {
            const sql = `SELECT * FROM accounts WHERE email = ${DB.escape(loginData.email)}`;
            DB.query(sql, (err, result) => {
                if (err) {
                    socket.emit(Events.RESPONSE_LOGIN, Error.unknowError());
                }
                else if (result.length == 0) {
                    socket.emit(Events.RESPONSE_LOGIN, Error.emailNotExist());
                } 
                else {
                    if (result[0].verified === 0) { // Email not verified yet
                        socket.emit(Events.RESPONSE_LOGIN, {
                            error: true,
                            message: 'VERIFY_ACCOUNT'
                        });
                    }
                    else if (result[0].password !== sha256(loginData.password)) {
                        socket.emit(Events.RESPONSE_LOGIN, Error.emailOrPasswordNotCorrect());
                    }
                    else {  // Login successfully
                        const { email, account_id } = result[0];
                        const token = jwt.sign({
                            account_id,
                            email
                        }, SECRET_KEY);
                        handleConnectedUser(socket, result[0], token);
                    }
                }
            });
        }
    } catch (error) {
        console.log(error);
    }
}

const verifyToken = (socket, token) => {
    let decode = jwt.verify(token, SECRET_KEY);
    const sql = `SELECT * FROM accounts WHERE email = ${DB.escape(decode.email)}`;
    DB.query(sql, (err, result) => {
        if (err) {
            socket.emit(Events.RESPONSE_LOGIN, Error.unknowError());
        }
        else if (result.length > 0) {   // Login successfully
            handleConnectedUser(socket, result[0], token);
        }
        else {
            socket.emit(Events.RESPONSE_LOGIN, Error.invalidToken());
        }
    });
}

const handleConnectedUser = (socket, userInfo, token) => {
    let user = new User(userInfo);
    socket.emit(Events.RESPONSE_LOGIN, {
        error: false,
        message: 'LOGIN_SUCCESS',
        userInfo: user.exportInfo(),
        token
    });
    addConnectedUser(socket.id, user);
    addConnectedSocket(userInfo.account_id, socket);
}

module.exports = {
    validateLogin
}
