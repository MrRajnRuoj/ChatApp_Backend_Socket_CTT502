const Events = require('./../Events/Events');
const Error = require('./../Errors/Error');
const { DateTime } = require('luxon');
const { ChatBox, getConnectedChat } = require('./../Chat');

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
                            connectedSockets[this.id].emit(Events.SEND_MESSAGE, Error.unknowError());
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
                    if (data.toUserID !== undefined || data.toUserID !== null) {    // Private chat
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
                                chatBox = new ChatBox(chatID, result);
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
