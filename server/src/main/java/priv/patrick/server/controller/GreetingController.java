package priv.patrick.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priv.patrick.server.model.Result;
import priv.patrick.server.model.vo.Greeting;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class GreetingController extends BaseController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Result<Greeting> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return returnSuccess(new Greeting(counter.incrementAndGet(), String.format(template, name)));
    }

}
