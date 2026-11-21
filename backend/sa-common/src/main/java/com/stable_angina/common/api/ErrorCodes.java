package com.stable_angina.common.api;

/**
 * 统一错误码（与 docs/01_后端架构设计.md §11 一致）
 */
public final class ErrorCodes {

    private ErrorCodes() {
    }

    /** 成功 */
    public static final int SUCCESS = 0;
    /** 通用失败 */
    public static final int FAIL = -1;
    /** 参数错误 */
    public static final int BAD_REQUEST = 400;
    /** 未登录 */
    public static final int UNAUTHORIZED = 401;
    /** 无权限 */
    public static final int FORBIDDEN = 403;
    /** 用户已停用 */
    public static final int USER_DISABLED = 1001;
    /** 用户不存在 */
    public static final int USER_NOT_FOUND = 1002;
    /** 账号或密码错误 */
    public static final int BAD_CREDENTIALS = 1003;
    /** 用户名已存在 */
    public static final int USERNAME_EXISTS = 1004;
    /** 手机号已存在 */
    public static final int PHONE_EXISTS = 1005;
    /** 系统异常 */
    public static final int SERVER_ERROR = 500;
    /** AI 调用失败 */
    public static final int AI_ERROR = 6001;
}
