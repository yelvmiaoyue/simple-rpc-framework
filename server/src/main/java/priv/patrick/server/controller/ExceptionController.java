package priv.patrick.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import priv.patrick.server.model.Result;

@RestControllerAdvice//(assignableTypes = GreetingController.class)
@Slf4j
public class ExceptionController extends BaseController {
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        log.error(ex.toString());
        return returnFail(ex.toString());
    }
}
