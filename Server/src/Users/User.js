const { DateTime } = require('luxon');
const uniqid = require('uniqid');
const Events = require('./../Events/Events');
const Error = require('./../Errors/Error');
const { ChatBox, getConnectedChat } = require('./../Chat');
const { getConnectedSocket, getConnectedUser } = require('./index');

class User {
    constructor(userInfo) {
        this.id = userInfo.account_id;
        this.email = userInfo.email;
        this.nickName = userInfo.nick_name;
        this.listFriend = [];
        this.listBlockedUser = [];
    }

    sendMessage(data) {
        let { chatID } = data;
        if (chatID === null || chatID === undefined) {    // private chat may be don't have chatID
            const sql = `SELECT * FROM private_chat WHERE ( first_user_id = ${this.id} AND 
                second_user_id = ${data.toUserID} ) OR ( first_user_id = ${data.toUserID} AND 
                    second_user_id = ${this.id} )`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            getConnectedSocket(this.id).emit(Events.SEND_MESSAGE, Error.unknowError());
                        }
                        else {
                            chatID = result[0].chat_id;
                            this.sendMessageToChatID(chatID, data);
                        }
                    })
        }
        else {
            this.sendMessageToChatID(chatID, data);
        }
    }

    sendMessageToChatID(chatID, data) {
        const time = DateTime.local().setZone('utc+0').toSQL().slice(0, 19);
        const sql = `INSERT INTO message (chat_id, sender_id, time, message) 
        VALUES ('${chatID}', ${this.id}, '${time}', '${data.message}')`;
        DB.query(sql, (err, result) => {
            if (err) {
                connectedSockets[this.id].emit(Events.SEND_MESSAGE, Error.unknowError());
            }
            else {
                let chatBox = getConnectedChat(chatID);
                if (chatBox === undefined || chatBox === null) {
                    if (data.toUserID !== undefined && data.toUserID !== null) {    // Private chat
                        chatBox = new ChatBox(chatID, [this.id, data.toUserID]);
                        chatBox.emitMessage({ 
                                id: this.id, 
                                email: this.email, 
                                nickName: this.nickName 
                            },
                            time, 
                            data.message
                        );
                    } 
                    else {  // Group chat
                        const sql = `SELECT user_id FROM group_user WHERE chat_id = '${chatID}'`;
                        DB.query(sql, (err, result) => {
                            if (err) {
                                connectedSockets[this.id].emit(Events.SEND_MESSAGE, Error.messageNotSend());
                            }
                            else {
                                let listUserID = [];
                                result.forEach((row) => listUserID.push(row.user_id));
                                chatBox = new ChatBox(chatID, listUserID);
                                chatBox.emitMessage({ 
                                        id: this.id, 
                                        email: this.email, 
                                        nickName: this.nickName 
                                    },
                                    time, 
                                    data.message
                                );
                            }
                        });
                    }
                }
                else {
                    chatBox.emitMessage({ 
                            id: this.id, 
                            email: this.email, 
                            nickName: this.nickName 
                        },
                        time, 
                        data.message
                    );
                }
            }
        });
    }

    requestFriend(data) {
        // relationship
        // 0: friend
        // 1: pending_1_2
        // 2: pending_2_1
        // 3: blocking_1_2
        // 4: blocking_2_1
        const { userEmail } = data;
        try {
            const sql = `SELECT * FROM accounts WHERE email = ${DB.escape(userEmail)}`;
            DB.query(sql, (err, result) => {
                if (err) {
                    getConnectedSocket(this.id).emit(Events.REQUEST_ADD_FRIEND, Error.unknowError());
                }
                else {
                    if (result.length === 0) return;
                    const secondUserID = result[0].account_id;
                    const sql = `INSERT INTO relationship (first_user_id, second_user_id, type) VALUES (${this.id}, ${secondUserID}, 1)`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            getConnectedSocket(this.id).emit(Events.REQUEST_ADD_FRIEND, Error.unknowError());
                        }
                        else {
                            const socket = getConnectedSocket(secondUserID);
                            console.log(getConnectedUser(socket.id));
                            if (socket !== undefined && socket !== null) {
                                socket.emit(Events.NOTIFY_FRIEND_REQUEST, {
                                    error: false,
                                    message: 'PENDING_FRIEND_REQUEST',
                                    userInfo: this.exportInfo()
                                });
                            }
                        }
                    });
                }
            });
        } catch (error) {
            console.log(error);
        }
    }

    responseFriendRequest(data) {
        const { isAccept, userID } = data;
        if (isAccept === true) {
            const chatID = uniqid.process();
            console.log('chatID: ' + chatID);
            const sql = `INSERT INTO chat_box (chat_id) VALUES ('${chatID}')`;
            DB.query(sql, (err, result) => {
                if (err) {
                    console.log('err while insert chatID');
                    getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, Error.unknowError());
                }
                else {
                    console.log('chatID: ' + chatID);
                    const sql = `INSERT INTO private_chat (first_user_id, second_user_id, chat_id) 
                                VALUES (${this.id}, ${userID}, '${chatID}')`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            console.log('err while insert to private chat');
                            getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, Error.unknowError());
                        }
                        else {
                            const sql = `UPDATE relationship SET type = 0 WHERE (first_user_id = ${this.id} AND second_user_id = ${userID})
                                                                                OR (first_user_id = ${userID} AND second_user_id = ${this.id})`;
                            DB.query(sql, (err, result) => {
                                if (err) {
                                    console.log('err while update relationship');
                                    getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, Error.unknowError());
                                }
                                else {
                                    getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, {
                                        error: false,
                                        message: 'ACCEPT_SUCCESSFULLY'
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    
    requestListFriend(data) {
        const sql = `SELECT * FROM relationship WHERE (first_user_id = ${this.id} AND type = 0) OR
                                                        (second_user_id = ${this.id} AND type = 0)`;
        DB.query(sql, (err, result) => {
            if (err) {
                console.log('err while get list friend: ' + err);
            }
            else {
                const totalFriend = result.length;
                if (totalFriend === 0) {   
                    getConnectedSocket(this.id).emit(Events.RESPONSE_LIST_FRIEND, {
                        error: false,
                        listFriend: this.listFriend
                    });
                }
                result.map((user) => {
                    // Get friendID
                    let friendID = user.first_user_id;    
                    if (friendID === this.id) {
                        friendID = user.second_user_id;
                    }

                    const  sql = `SELECT * FROM accounts WHERE account_id = ${friendID}`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            console.log(err);
                        }
                        else {
                            this.listFriend.push({
                                id: result[0].account_id,
                                email: result[0].email
                            });
                            if (this.listFriend.length === totalFriend) {
                                getConnectedSocket(this.id).emit(Events.RESPONSE_LIST_FRIEND, {
                                    error: false,
                                    listFriend: this.listFriend
                                });
                            }
                        }
                    });
                });
            }
        });
    }
    
    requestMessage(data) {
        let { chatID } = data;
        if (chatID === null || chatID === undefined) {    // private chat may be don't have chatID
            const sql = `SELECT * FROM private_chat WHERE ( first_user_id = ${this.id} AND 
                    second_user_id = ${data.toUserID} ) OR ( first_user_id = ${data.toUserID} AND 
                    second_user_id = ${this.id} )`;
            DB.query(sql, (err, result) => {
                if (err) {
                    getConnectedSocket(this.id).emit(Events.REQUEST_MESSAGE, Error.unknowError());
                }
                else {
                    if (result.length === 0) {
                        console.log('chat_id not exists');
                        return;
                    }
                    chatID = result[0].chat_id;
                    this.getMessage(chatID);
                }
            });
        }
        else {
            this.getMessage(chatID);
        }
    }
    
    getMessage(chatID) {
        const sql = `SELECT * FROM message WHERE chat_id = '${chatID}'`;
        let messageData = [];
        let senderName = {};
        DB.query(sql, (err, result) => {
            if (err) {
                console.log('Err while get message from chatbox: ' + err);
                getConnectedSocket(this.id).emit(Events.REQUEST_MESSAGE, Error.unknowError());
            }
            let numMessage = result.length;
            result.map((row) => {
                let strTime = row.time;
                if (senderName[row.sender_id] === undefined || senderName[row.sender_id] === null) {
                    // Get sender's name
                    const sql = `SELECT * FROM accounts WHERE account_id = ${row.sender_id}`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            console.log(`Err while get sender's name: ${err}`);
                        }
                        else {
                            senderName[row.sender_id] = result[0].email;
                            messageData.push({
                                sender: result[0].email,
                                message: row.message,
                                time: strTime.slice(0, 19).replace("T", " "),
                                chatID: row.chat_id
                            });
                            if (messageData.length === numMessage) {
                                getConnectedSocket(this.id).emit(Events.REQUEST_MESSAGE, {
                                    error: false,
                                    messageData
                                });
                            }
                        }
                    });
                }
                else {
                    messageData.push({
                        sender: senderName[row.sender_id],
                        message: row.message,
                        time: strTime.slice(0, 19).replace("T", " "),
                        chatID: row.chat_id
                    });
                    if (messageData.length === numMessage) {
                        getConnectedSocket(this.id).emit(Events.REQUEST_MESSAGE, {
                            error: false,
                            messageData
                        });
                    }
                }
            });
        });
    }

    exportInfo() {
        return {
            id: this.id,
            email: this.email,
            nickName: this.nickName
            // ....
        }
    }
}

module.exports = User;
