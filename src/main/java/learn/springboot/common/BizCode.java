package learn.springboot.common;

public enum BizCode {
    PARAM_INVALID(400, "参数非法"),
    USER_NOT_FOUND(1001, "用户不存在"),
    INSUFFICIENT_BALANCE(1002, "余额不足"),
    LOGIN_REQUIRED(1003, "请先登录");
    private final int code;
    private final String msg;
    BizCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
