const sha256 = require('sha256');
const jwt = require('jsonwebtoken');
const { SECRET_KEY } = require('./../../Configs');
const Events = require('./../../Events/Events');
const Error = require('./../../Errors/Error');
const User = require('./../../Users/User');
const { addConnectedUser } = require('./../../Users');

const validateLogin = (socket, loginData) => {
    if (loginData === null || loginData === undefined) return;
    try {
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
                    const { email, id } = result[0];
                    const token = jwt.sign({
                        id,
                        email
                    }, SECRET_KEY);
                    let user = new User(socket, result[0]);
                    socket.emit(Events.RESPONSE_LOGIN, {
                        error: false,
                        message: '',
                        userInfo: user.exportInfo(),
                        token
                    });
                    addConnectedUser(socket.id, user);
                }
            }
        });
    } catch (error) {
        console.log(error);
    }
}

module.exports = {
    validateLogin
}
