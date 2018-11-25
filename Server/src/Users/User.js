class User {
    constructor(socket, userInfo) {
        this.id = userInfo.id;
        this.email = userInfo.email;
        // ...    
        this.connectedSockets[userInfo.id] = socket;
    }

    static connectedSockets = {};

    exportInfo() {
        return {
            id: this.id,
            email: this.email,
            // ....
        }
    }
}

module.exports = User;
