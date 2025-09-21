package com.sunrizon.horizon.enums;

import lombok.Getter;

/**
 * 标准API响应码枚举
 * 包含成功、失败和常见HTTP状态码
 */
@Getter
public enum ResponseCode {

  // ==================== 成功状态 ====================
  /** 操作成功 */
  SUCCESS(200, "操作成功"),

  // ==================== 客户端错误 4xx ====================
  /** 请求参数错误 */
  BAD_REQUEST(400, "请求参数错误"),
  
  /** 未授权访问 */
  UNAUTHORIZED(401, "未授权访问"),
  
  /** 禁止访问 */
  FORBIDDEN(403, "禁止访问"),
  
  /** 资源不存在 */
  NOT_FOUND(404, "资源不存在"),
  
  /** 请求方法不允许 */
  METHOD_NOT_ALLOWED(405, "请求方法不允许"),
  
  /** 资源冲突，如重复数据 */
  CONFLICT(409, "资源冲突"),
  
  /** 参数验证失败 */
  VALIDATION_ERROR(422, "参数验证失败"),
  
  /** 请求过于频繁 */
  TOO_MANY_REQUESTS(429, "请求过于频繁"),

  // ==================== 服务器错误 5xx ====================
  /** 服务器内部错误 */
  INTERNAL_ERROR(500, "服务器内部错误"),
  
  /** 服务不可用 */
  SERVICE_UNAVAILABLE(503, "服务不可用"),
  
  /** 通用失败 */
  FAILURE(500, "操作失败"),

  // ==================== 业务错误 ====================
  /** 用户不存在 */
  USER_NOT_FOUND(1001, "用户不存在"),
  
  /** 用户已存在 */
  USER_ALREADY_EXISTS(1002, "用户已存在"),
  
  /** 用户状态无效 */
  USER_INVALID_STATUS(1003, "用户状态无效"),
  
  /** 密码错误 */
  USER_PASSWORD_INCORRECT(1004, "密码错误"),
  
  /** 账户已被禁用 */
  USER_ACCOUNT_DISABLED(1005, "账户已被禁用"),
  
  /** 账户已被锁定 */
  USER_ACCOUNT_LOCKED(1006, "账户已被锁定"),
  
  /** 邮箱已存在 */
  USER_EMAIL_EXISTS(1007, "邮箱已存在"),
  
  /** 用户名已存在 */
  USER_USERNAME_EXISTS(1008, "用户名已存在"),

  /** 文章不存在 */
  ARTICLE_NOT_FOUND(2001, "文章不存在"),
  
  /** 文章状态无效 */
  ARTICLE_INVALID_STATUS(2002, "文章状态无效"),
  
  /** 无权限操作该文章 */
  ARTICLE_PERMISSION_DENIED(2003, "无权限操作该文章"),
  
  /** 文章标题已存在 */
  ARTICLE_TITLE_EXISTS(2004, "文章标题已存在"),
  
  /** 文章别名已存在 */
  ARTICLE_SLUG_EXISTS(2005, "文章别名已存在"),

  /** 分类不存在 */
  CATEGORY_NOT_FOUND(3001, "分类不存在"),
  
  /** 分类名称已存在 */
  CATEGORY_NAME_EXISTS(3002, "分类名称已存在"),
  
  /** 分类下存在文章，无法删除 */
  CATEGORY_HAS_ARTICLES(3003, "分类下存在文章，无法删除"),

  /** 标签不存在 */
  TAG_NOT_FOUND(4001, "标签不存在"),
  
  /** 标签名称已存在 */
  TAG_NAME_EXISTS(4002, "标签名称已存在"),

  /** 角色不存在 */
  ROLE_NOT_FOUND(5001, "角色不存在"),
  
  /** 角色名称已存在 */
  ROLE_NAME_EXISTS(5002, "角色名称已存在"),
  
  /** 角色正在使用中，无法删除 */
  ROLE_IN_USE(5003, "角色正在使用中，无法删除"),

  /** 权限不足 */
  PERMISSION_DENIED(6001, "权限不足"),
  
  /** 权限不存在 */
  PERMISSION_NOT_FOUND(6002, "权限不存在"),

  /** 文件不存在 */
  FILE_NOT_FOUND(7001, "文件不存在"),
  
  /** 文件上传失败 */
  FILE_UPLOAD_FAILED(7002, "文件上传失败"),
  
  /** 文件类型不支持 */
  FILE_TYPE_NOT_SUPPORTED(7003, "文件类型不支持"),
  
  /** 文件大小超出限制 */
  FILE_SIZE_EXCEEDED(7004, "文件大小超出限制"),

  /** 邮件发送失败 */
  EMAIL_SEND_FAILED(8001, "邮件发送失败"),
  
  /** 短信发送失败 */
  SMS_SEND_FAILED(8002, "短信发送失败"),
  
  /** 第三方服务错误 */
  THIRD_PARTY_SERVICE_ERROR(8003, "第三方服务错误");

  private final int status;
  private final String message;

  ResponseCode(int status, String message) {
    this.status = status;
    this.message = message;
  }

  /**
   * 根据状态码获取响应码
   */
  public static ResponseCode getByStatus(int status) {
    for (ResponseCode code : values()) {
      if (code.getStatus() == status) {
        return code;
      }
    }
    return FAILURE;
  }
}
