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

## vẫn còn tiếp....