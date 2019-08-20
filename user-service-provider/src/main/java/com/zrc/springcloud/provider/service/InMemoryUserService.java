package com.zrc.springcloud.provider.service;

import com.zrc.springcloud.api.IUserService;
import com.zrc.springcloud.domain.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ZRC
 * Date Time: 2019/8/20 9:34
 * Description: No Description
 */

@Service("inMemoryUserService")
public class InMemoryUserService implements IUserService {
    private Map<Long, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean saveUser(User user) {
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList(repository.values());
    }
}
