package priv.patrick.service;

import priv.patrick.model.po.User;

import java.util.List;

public interface UserService {
    /**
     * @param ids id集合
     * @return
     */
    List<User> listUsers(List<Integer> ids);

    /**
     * @param id 用户id
     * @return
     */
    User getUser(int id);
}
