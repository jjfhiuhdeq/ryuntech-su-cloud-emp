package com.ryuntech.common.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author antu
 * @date 2019-05-22
 */
@Getter
@AllArgsConstructor
public enum CommonEnums {

    LOGIN_ERROR(100, "用户名或密码错误"),
    PARAM_ERROR(101, "参数错误"),
    PARAM_PARSE_ERROR(1010, "验证码错误"),
    PARAM_MOBILE_ERROR(1011, "手机号错误"),
    USER_ERROR(102, "获取用户信息失败"),
    LOGOUT_ERROR(103, "退出失败"),
    SYSTEM_ERROR(104, "系统内部错误"),
    OPERATE_ERROR(106, "操作失败"),
    SETTLEMENT_ERROR(105, "发起失败"),
    ORDER_ERROR(106, "该用户有订单待处理，请勿重复申请");



    private final int code;
    private final String msg;

}
