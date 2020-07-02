package priv.patrick.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Result<T> {
    private Boolean success;

    private String code;

    private String message;

    private T data;

    private Result(){}
}
