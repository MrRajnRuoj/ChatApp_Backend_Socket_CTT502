const sha256 = require('sha256');
const jwt = require('jsonwebtoken');
const { SECRET_KEY } = require('./../Configs');
const Events = require('./../Events');
const Error = require('./../Error');

const validateLogin = (socket, loginData) => {
    try {
        const sql = `SELECT * FROM account WHERE email = ${DB.escape(loginData.email)}`;
        DB.query(sql, (err, result) => {
            if (err) {
                socket.emit(Events.RESPONSE_LOGIN, Error.unknowError());
            }
            else if (result.length == 0) {
                socket.emit(Events.RESPONSE_LOGIN, Error.emailNotExist());
            } 
            else {
                if (result[0].password !== sha256(loginData.password)) {
                    socket.emit(Events.RESPONSE_LOGIN, Error.emailOrPasswordNotCorrect());
                }
                else {
                    const token = jwt.sign({
                        id: result[0].id,
                        email: result[0].email
                    }, SECRET_KEY);
                    socket.emit(Events.RESPONSE_LOGIN, {
                        error: false,
                        message: '',
                        email: result[0].email,
                        token
                    });
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
