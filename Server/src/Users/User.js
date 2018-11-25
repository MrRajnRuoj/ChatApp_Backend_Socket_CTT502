let connectedSockets = {};
class User {
    constructor(socket, userInfo) {
        this.id = userInfo.id;
        this.email = userInfo.email;
        // ...    
        connectedSockets[userInfo.id] = socket;
    }


    exportInfo() {
        return {
            id: this.id,
            email: this.email,
            // ....
        }
    }
}

module.exports = User;
