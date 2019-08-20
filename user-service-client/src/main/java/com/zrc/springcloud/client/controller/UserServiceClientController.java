package com.zrc.springcloud.client.controller;

import com.zrc.springcloud.api.IUserService;
import com.zrc.springcloud.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 10:37
 * Description: No Description
 */
@RestController
public class UserServiceClientController implements IUserService {
    @Autowired
    private IUserService userService;

    // 通过方法继承，URL 映射 ："/user/save"
    @Override
    public boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 通过方法继承，URL 映射 ："/user/find/all"
    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
