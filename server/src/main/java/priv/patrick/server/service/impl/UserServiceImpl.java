package priv.patrick.server.service.impl;

import priv.patrick.model.po.User;
import priv.patrick.service.UserService;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    @Override
    public List<User> listUsers(List<Integer> ids) {
        List<User> list = new ArrayList<>();
        for (Integer id : ids) {
            list.add(User.builder().id(id).username(String.valueOf(id)).password(String.valueOf(id)).build());
        }
        return list;
    }

    @Override
    public User getUser(int id) {
        return User.builder().id(id).username(String.valueOf(id)).password(String.valueOf(id)).build();
    }
}
