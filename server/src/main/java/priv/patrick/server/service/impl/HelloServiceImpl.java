package priv.patrick.server.service.impl;

import priv.patrick.service.HelloService;

import java.util.HashMap;
import java.util.Map;

public class HelloServiceImpl implements HelloService {
    @Override
    public Integer add(int a, int b) {
        return a + b;
    }

    @Override
    public Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("hello", "world");
        return map;
    }
}
