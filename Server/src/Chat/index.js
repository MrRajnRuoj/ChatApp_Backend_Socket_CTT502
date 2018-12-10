const ChatBox = require('./ChatBox');

const connectedChatBox = {};

const getConnectedChat = (chatID) => {
    return connectedChatBox[chatID];
}

const addConnectedChat = (chatID, ChatBox) => {
    connectedChatBox[chatID] = ChatBox;
}

const removeConnectedChat = (chatID) => {
    connectedChatBox[chatID] = undefined;
}

module.exports = {
    ChatBox,
    getConnectedChat,
    addConnectedChat,
    removeConnectedChat
}
