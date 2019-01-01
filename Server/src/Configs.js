const mysql = require('mysql');
const  SECRET_KEY = 'ALIBABA_CAI_WAN_SI+DA-RACH-RA-LAM_BA';

const db_config = {
    host: 'den1.mysql5.gear.host',
    user: 'group4chatapp1',
    password: 'Jl269yt8!-6H',
    database: 'group4chatapp1',
    dateStrings: true
};

let DBConnection;

function handleDisconnect() {
    DBConnection = mysql.createConnection(db_config);   // Recreate the connection, since
                                                        // the old one cannot be reused.
  
    DBConnection.connect(function(err) {                // The server is either down
        if(err) {                                         // or restarting (takes a while sometimes).
            console.log('Error when connecting to db:', err);
            setTimeout(handleDisconnect, 2000); // We introduce a delay before attempting to reconnect,
        } 
        else {
            console.log('Connected to DB!');
            global.DB = DBConnection;
        }                                       // to avoid a hot loop, and to allow our node script to
    });                                         // process asynchronous requests in the meantime.
                                                // If you're also serving http, display a 503 error.
    DBConnection.on('error', function(err) {
        console.log('db error', err);
        if(err.code === 'PROTOCOL_CONNECTION_LOST') {   // Connection to the MySQL server is usually
            handleDisconnect();                         // lost due to either server restart, or a
        } else {                                        // connnection idle timeout (the wait_timeout
            throw err;                                  // server variable configures this)
        }
    });
}
  

module.exports = {
    handleDisconnect,
    SECRET_KEY
}
