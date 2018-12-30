# Zoola - API Server

### 1. Request Signup 
**Method:** `emit`

**Event:** `REQUEST_SIGNUP`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| email    | String | Email của user    |
| password | String | Password của user |

### 2. Response Signup 
**Method:** `on`

**Event:** `RESPONSE_SIGNUP`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

|Error Status| Label                | Description                                |
|:----:|:----------------------:|:--------------------------------------------:|
|true| `UNKNOW_ERROR`         | Lỗi không xác định :))                     |
|true| `EMAIL_ALREADY_EXISTS` | Email đã tồn tại                           |
|false| `VERIFY_ACCOUNT`       | Đăng ký thành công, yêu cầu xác thực email |

### 3. Request Login 
**Method:** `emit`

**Event:** `REQUEST_LOGIN`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| token    | String | Token đăng nhập của user    |
| email    | String | Email của user    |
| password | String | Password của user |

### 4. Response Login 
**Method:** `on`

**Event:** `RESPONSE_LOGIN`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |
|token|String|Token đăng nhập|
|userInfo|Object|Thông tin user|


**[userInfo]**

|   Field  |  Type  |    Description    |
|:--------:|:------:|:-----------------:|
|    id    | Int    | ID của user       |
|   email  | String | Email của user    |
| nickName | String | Nickname của user |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                      |
|     true     | `EMAIL_ALREADY_EXISTS`          | Email đã tồn tại                            |
|     false    | `VERIFY_ACCOUNT`                | Email chưa xác thực, yêu cầu xác thực email |
|     true     | `EMAIL_OR_PASSWORD_NOT_CORRECT` | Sai tài khoản hoặc mật khẩu                 |
|     false    | `LOGIN_SUCCESS`                 | Đăng nhập thành công                        |


### 5. Request Verify Email 
**Method:** `emit`

**Event:** `REQUEST_VERIFY_EMAIL`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| email    | String | Email của user    |
| code | Int | Verify code |

### 6. Response Verify Email 
**Method:** `on`

**Event:** `RESPONSE_VERIFY_EMAIL`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                      |
|     false     | `VERIFY_SUCCESSFULLY`          | Verify thành công                          |
|     true    | `INCORRECT_VERIFY_CODE`                | Verify code không chính xác |


### 7. Request Resend Verify Code
**Method:** `emit`

**Event:** `REQUEST_RESEND_VERIFY_CODE`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| email    | String | Email của user    |

### 8. Response Resend Verify Code
**Method:** `on`

**Event:** `RESPONSE_RESEND_VERIFY_CODE`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                      |
|     false     | `VERIFY_ACCOUNT`          | Yêu cầu verify email                          |

### 9. Request Add Friend
**Method:** `emit`

**Event:** `REQUEST_ADD_FRIEND`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| userEmail    | String | Email của người muốn thêm bạn    |

### 10. Response To Request Add Friend
**Method:** `on`

**Event:** `REQUEST_ADD_FRIEND`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                      |

### 11. Nontify Friend Request
**Method:** `on`

**Event:** `NOTIFY_FRIEND_REQUEST`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |
| userInfo | Object | Thông tin người gửi yêu cầu |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                    |


**[userInfo]**

|   Field  |  Type  |    Description    |
|:--------:|:------:|:-----------------:|
|    id    | Int    | ID của user       |
|   email  | String | Email của user    |
| nickName | String | Nickname của user |

### 12. Response Friend Request
**Method:** `emit`

**Event:** `RESPONSE_FRIEND_REQUEST`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| isAccept    | Boolean | `yes`: Đồng ý, `no`: Không đồng ý    |
| userID    |  Int    |   ID của người gửi yêu cầu kết bạn |

### 13. Response Friend Request
**Method:** `on`

**Event:** `RESPONSE_FRIEND_REQUEST`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|     true     | `UNKNOW_ERROR`                  | Lỗi không xác định :))                    |
|     false    | `ACCEPT_SUCCESSFULLY`          | Kết bạn thành công                         |


### 14. Request Logout
**Method:** `emit`

**Event:** `REQUEST_LOGOUT`


### 15. Send Message
**Method:** `emit`

**Event:** `SEND_MESSAGE`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| chatID    | String | chatID của người/group muốn chat (có thể ko có)   |
| toUserID    |  Int    |   ID của người muốn chat |
| message   | String    | Nội dung tin nhắn     |


### 16. Response Send Message
**Method:** `on`

**Event:** `SEND_MESSAGE`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| message | String | Thông điệp trả về |


**[message]**

| Error Status |             Label             |                 Description                 |
|:------------:|:-----------------------------:|:-------------------------------------------:|
|    true      | `UNKNOW_ERROR`                 | Lỗi không xác định :))                     |
|    true      | `MESSAGE_NOT_SEND`             | Message gửi ko thành công                  |


### 17. Recieve Message
**Method:** `on`

**Event:** `RECIEVE_MESSAGE`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| senderID | Int    | ID người gửi      |
| senderEmail | String | Email người gửi |
| senderNickName | String | Nickname người gửi |
| time      | String    |   Thời gian gửi tính theo UTC +0, format: "yyyy-mm-dd hh:mm:ss" |
| message | String | Nội dung tin nhắn |
| chatID    | String    | chatID của người/group |


### 18. Request List Friend
**Method:** `emit`

**Event:** `REQUEST_LIST_FRIEND`


### 19. Response Request List Friend
**Method:** `on`

**Event:** `RESPONSE_LIST_FRIEND`

**Parameter:**

| Field    | Type   | Description       |
|:----------:|:--------:|:-------------------:|
| error    | Boolean | Có lỗi: `true`, không lỗi: `false`    |
| listFriend | Array Object | Danh sách bạn bè |


**[object]**

|   Field  |  Type  |    Description    |
|:--------:|:------:|:-----------------:|
|    id    | Int    | ID của user       |
|   email  | String | Email của user    |


## vẫn còn tiếp....