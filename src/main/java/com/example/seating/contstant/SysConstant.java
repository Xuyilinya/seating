package com.example.seating.contstant;

public class SysConstant {

    /**
     * 用户状态 1：正常 0：黑名单
     */
    public static final int USER_STATUS_ENABLE = 1;
    public static final int USER_STATUS_BLACK_LIST = 0;

    /**
     * 黑名单状态：0：无效；1：生效
     */
    public static final int BLACK_LIST_STATUS_VALID = 1;
    public static final int BLACK_LIST_STATUS_INVALID = 0;

    /**
     * 黑名单结束状态：0：自动结束；1：系统管理员结束
     */
    public static final int BLACK_LIST_END_STATUS_AUTO = 0;
    public static final int BLACK_LIST_END_STATUS_MANUAL = 1;

    /**
     * 预约单状态：0：未签到；1：已签到；2：未履行；3：已退座
     */
    public static final int ORDER_STATUS_NOT_SIGN_IN = 0;
    public static final int ORDER_STATUS_SIGN_IN = 1;
    public static final int ORDER_STATUS_OVERDUE = 2;
    public static final int ORDER_STATUS_CANCEL = 3;

    /**
     * 座位状态 0:禁用；1：可用；2：已预约；3：暂离
     */
    public static final int SEAT_STATUS_UNUSABLE = 0;
    public static final int SEAT_STATUS_USABLE = 1;
    public static final int SEAT_STATUS_APPOINTMENT=2;
    public static final int SEAT_STATUS_LEAVE = 3;




}
