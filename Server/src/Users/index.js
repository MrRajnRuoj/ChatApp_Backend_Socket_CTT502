const connectedUsers = {};

const getConnectedUser = (socketID) => connectedUsers[socketID];

const addConnectedUser = (socketID, user) => {
    connectedUsers[socketID] = user;
}

const removeConnectedUser = (socketID) => connectedUsers[socketID] = undefined;

module.exports = {
    getConnectedUser,
    addConnectedUser,
    removeConnectedUser
}
