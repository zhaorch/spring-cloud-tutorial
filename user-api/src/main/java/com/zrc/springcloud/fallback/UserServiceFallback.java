package com.zrc.springcloud.fallback;

import com.zrc.springcloud.api.IUserService;
import com.zrc.springcloud.domain.User;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 9:23
 * Description: No Description
 */

public class UserServiceFallback implements IUserService {
    @Override
    public boolean saveUser(User user) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
