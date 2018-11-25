const sha256 = require('sha256');
const Events = require('./../Events/Events');
const Error = require('./../Errors/Error');
const { sendVerifyCode } = require('./../MailService');

const validateSignup = (socket, signupData) => {
    if (signupData === null || signupData === undefined) return;
    try {
        const sql = `SELECT * FROM accounts WHERE email = ${DB.escape(signupData.email)}`;
        DB.query(sql, (err, result) => {
            if (err) {
                socket.emit(Events.RESPONSE_SIGNUP, Error.unknowError());
            }
            else if (result.length > 0) {
                socket.emit(Events.RESPONSE_SIGNUP, Error.emailAlreadyExisted());
            }
            else {
                const sql = `INSERT INTO accounts (email, password, type, created_date, nick_name, verified) 
                            VALUES (${DB.escape(signupData.email)}, '${sha256(signupData.password)}', 1, NOW(), null, 0)`;
                DB.query(sql, (err, result) => {
                    if (err) {
                        socket.emit(Events.RESPONSE_SIGNUP, Error.unknowError());
                    }
                    else {
                        const code = Math.round(Math.random() * (9999 - 1000) + 1000);
                        const sql = `INSERT INTO verify_email (email, code) 
                                    VALUES (${DB.escape(signupData.email)}, ${code})`;
                        DB.query(sql, (err, result) => {
                            if (err) {
                                socket.emit(Events.RESPONSE_SIGNUP, Error.unknowError());
                            }
                            else {
                                sendVerifyCode(socket, code, signupData.email);
                            }
                        });
                    }
                });          
            }
        });
    } catch (error) {
        console.log(error);
    }
}

module.exports = {
    validateSignup
}
