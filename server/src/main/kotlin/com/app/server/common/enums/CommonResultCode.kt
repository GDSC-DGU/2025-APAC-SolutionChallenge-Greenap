package com.app.server.common.enums

enum class CommonResultCode(
    override val code: String,
    override val message: String
) : ResultCode {
    // success
    SUCCESS("COM000", "Success"),

    // failure
    // common
    BAD_REQUEST("COM001", "Bad Request"),
    NOT_FOUND("COM002", "Not Found"),
    UNAUTHORIZED("COM003", "Unauthorized"),
    FORBIDDEN("COM004", "Forbidden"),
    INTERNAL_SERVER_ERROR("COM005", "Internal Server Error"),
    ILLEGAL_ARGUMENT("COM006", "Illegal Argument"),
    TIMEOUT("COM007", "Timeout"),
    EXTERNAL_SERVER_ERROR("COM008", "External Server Error"),

    // domain
    AUTH_EXCEPTION("AUT000", "Auth Exception"),
    OAUTH2_EXCEPTION("OAU000", "OAuth2 Exception"),
    USER_EXCEPTION("USR000", "User Exception"),
    NOTIFICATION_EXCEPTION("NOT000", "Notification Exception"),
    ;

}

