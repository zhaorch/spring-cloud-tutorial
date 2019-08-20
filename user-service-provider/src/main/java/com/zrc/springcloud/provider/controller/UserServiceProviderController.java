package com.zrc.springcloud.provider.controller;

import com.zrc.springcloud.api.IUserService;
import com.zrc.springcloud.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 9:36
 * Description: No Description
 */
@RestController
public class UserServiceProviderController implements IUserService{
    @Autowired
    @Qualifier("inMemoryUserService") // 实现 Bean ： InMemoryUserService
    private IUserService userService;


    @Override
    public boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
