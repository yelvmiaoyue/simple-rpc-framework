package priv.patrick.server.enums;

import lombok.Getter;

/**
 * 统一响应结果枚举
 * @author Patrick_zhou
 */

@Getter
public enum ResultCodeEnum {
    //成功
    SUCCESS(true,"0", "操作成功"),
    //失败
    FAIL(false,"-1", "操作失败");

    private Boolean success;

    private String code;

    private String message;

    ResultCodeEnum(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
