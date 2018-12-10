const { getConnectedSocket } = require('./../Users');
const Events = require('./../Events/Events');

class ChatBox {
    constructor(chatID, listUserID) {
        this.chatID = chatID;
        this.listUserID = [...listUserID];
    }

    emitMessage(sender, time, message) {
        this.listUserID.forEach((userID) => {
            getConnectedSocket(userID).emit(Events.RECIEVE_MESSAGE, {
                senderID: sender.id,
                senderEmai: sender.email,
                senderNickName: sender.nickName,
                time,
                message,
                chatID: this.chatID
            });
        });
    }
}

module.exports = ChatBox;
