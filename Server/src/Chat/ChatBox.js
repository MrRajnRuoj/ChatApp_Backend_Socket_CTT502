const { getConnectedSocket } = require('./../Users');
const Events = require('./../Events/Events');

class ChatBox {
    constructor(chatID, listUserID) {
        this.chatID = chatID;
        this.listUserID = [...listUserID];
    }

    emitMessage(sender, time, message) {
        try {
            this.listUserID.forEach((userID) => {
                let socket = getConnectedSocket(userID);
                console.log(socket + ' - ' + userID);
                if (socket !== undefined && socket !== null) {
                    socket.emit(Events.RECIEVE_MESSAGE, {
                        senderID: sender.id,
                        senderEmai: sender.email,
                        senderNickName: sender.nickName,
                        time,
                        message,
                        chatID: this.chatID
                    });
                }
            });
        } catch (error) {
            console.log(error);
        }
    }
}

module.exports = ChatBox;
