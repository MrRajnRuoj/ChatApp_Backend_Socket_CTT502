module.exports = {
    unknowError() {
        return {
            error: true,
            message: `UNKNOW_ERROR`
        }
    },

    emailNotExist() {
        return {
            error: true,
            message: `EMAIL_NOT_EXIST`
        }
    },

    emailOrPasswordNotCorrect() {
        return {
            error: true,
            message: `EMAIL_OR_PASSWORD_NOT_CORRECT`
        }
    }
}