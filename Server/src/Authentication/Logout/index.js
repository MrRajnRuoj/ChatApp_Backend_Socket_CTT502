const Events = require('./../../Events/Events');
const { getConnectedUser, removeConnectedSocket, removeConnectedUser } = require('./../../Users');

const handleLogoutUser = (socket) => {
    const userID = getConnectedUser(socket.id);
    removeConnectedUser(socket.id);
    if (userID !== null && userID !== undefined)
        removeConnectedSocket(userID);
}

module.exports = {
    handleLogoutUser
}
