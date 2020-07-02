package priv.patrick.server.controller;

import priv.patrick.server.enums.ResultCodeEnum;
import priv.patrick.server.model.Result;

/**
 * @author Patrick_zhou
 */
public abstract class BaseController {
    protected <T> Result<T> returnSuccess() {
        return new Result<>(ResultCodeEnum.SUCCESS.getSuccess(), ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), null);
    }

    protected <T> Result<T> returnSuccess(T data) {
        return new Result<>(ResultCodeEnum.SUCCESS.getSuccess(), ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), data);
    }

    protected <T> Result<T> returnFail() {
        return new Result<>(ResultCodeEnum.FAIL.getSuccess(), ResultCodeEnum.FAIL.getCode(), ResultCodeEnum.FAIL.getMessage(), null);
    }

    protected <T> Result<T> returnFail(String message) {
        return new Result<>(ResultCodeEnum.FAIL.getSuccess(), ResultCodeEnum.FAIL.getCode(), message, null);
    }

}
