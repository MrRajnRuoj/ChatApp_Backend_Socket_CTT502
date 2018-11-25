const mysql = require('mysql');

const DBConnection = mysql.createConnection({
    host: 'den1.mysql5.gear.host',
    user: 'group4chatapp1',
    password: 'Jl269yt8!-6H',
    database: 'group4chatapp1'
});

const  SECRET_KEY = 'ALIBABA_CAI_WAN_SI+DA-RACH-RA-LAM_BA';

module.exports = {
    DBConnection,
    SECRET_KEY
}
