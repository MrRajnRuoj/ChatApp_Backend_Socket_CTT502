let connectedSockets = {};
class User {
    constructor(socket, userInfo) {
        this.id = userInfo.id;
        this.email = userInfo.email;
        this.nickName = userInfo.nick_name;
        this.listFriend = [];
        this.listBlockedUser = [];
        connectedSockets[userInfo.id] = socket;
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
