package com.example.seating.utils;

public enum ReturnEnum {
    /**
     * 接口返回状态码：
     * 1000：成功
     * 1001：失败
     * 1002：重要参数不可用
     * 1003: 参数为空或null
     * 1004:用户未登录
     */
    RETURN_STATUS_SUCCESS("1000","success!"),
    RETURN_STATUS_FAIL("1001","fail!"),
    RETURN_STATUS_PARAM_INVALID("1002","parameter invalid!"),
    RETURN_STATUS_PARAM_IS_BLANK("1003","parameter is null or empty"),
    RETURN_STATUS_USER_IS_NOT_LOGIN("1004","user is not login");

    private String code;
    private String msg;

     ReturnEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg(){
         return msg;
    }
}
