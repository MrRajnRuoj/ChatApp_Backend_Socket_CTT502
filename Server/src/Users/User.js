const { DateTime } = require('luxon');
const uniqid = require('uniqid');
const Events = require('./../Events/Events');
const Error = require('./../Errors/Error');
const { ChatBox, getConnectedChat } = require('./../Chat');
const { getConnectedSocket } = require('./index');

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
                    const secondUserID = result[0].account_id;
                    const sql = `INSERT INTO relationship (first_user_id, second_user_id, type) VALUES (${this.id}, ${secondUserID}, 1)`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            getConnectedSocket(this.id).emit(Events.REQUEST_ADD_FRIEND, Error.unknowError());
                        }
                        else {
                            const socket = getConnectedSocket(secondUserID);
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
            const sql = `INSERT INTO chat_box (chat_id) VALUES (${chatID})`;
            DB.query(sql, (err, result) => {
                if (err) {
                    getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, Error.unknowError());
                }
                else {
                    const sql = `INSERT INTO private_chat (first_user_id, second_user_id, chat_id) 
                                VALUES (${this.id}, ${userID}, '${chatID}')`;
                    DB.query(sql, (err, result) => {
                        if (err) {
                            getConnectedSocket(this.id).emit(Events.RESPONSE_FRIEND_REQUEST, Error.unknowError());
                        }
                        else {
                            const sql = `UPDATE relationship SET type = 0 WHERE (first_user_id = ${this.id} AND second_user_id = ${userID})
                                                                                OR (first_user_id = ${userID} AND second_user_id = ${this.id})`;
                            DB.query(sql, (err, result) => {
                                if (err) {
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
                console.log(err);
            }
            else {
                const totalFriend = result.length;
                result.map((user) => {
                    // Get friendID
                    const friendID = user.first_user_id;    
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
