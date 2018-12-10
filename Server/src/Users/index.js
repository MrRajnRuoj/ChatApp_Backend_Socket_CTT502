const connectedUsers = {};  // SocketID -> User
const connectedSockets = {};  // UserID -> Socket

const getConnectedUser = (socketID) => {
    return connectedUsers[socketID];
}

const getConnectedSocket = (userID) => {
    return connectedSockets[userID];
}

const addConnectedUser = (socketID, user) => {
    connectedUsers[socketID] = user;
}

const addConnectedSocket = (userID, socket) => {
    connectedSockets[userID] = socket;
}

const removeConnectedUser = (socketID) => {
    connectedUsers[socketID] = undefined;
}

const removeConnectedSocket = (userID) => {
    connectedSockets[userID] = undefined;
}

module.exports = {
    getConnectedUser,
    addConnectedUser,
    removeConnectedUser,
    getConnectedSocket,
    addConnectedSocket,
    removeConnectedSocket
}
