package priv.patrick.service;

import priv.patrick.model.po.User;

import java.util.List;

public interface UserService {
    /**
     * 查找id大于参数的用户
     *
     * @param id 下限id
     * @return
     */
    List<User> listIdGtParamUsers(int id);

    /**
     * @param id 用户id
     * @return
     */
    User getUser(int id);
}
