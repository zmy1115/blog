package com.zmy.service.serviceImpl;

import com.zmy.dao.UserRepository;
import com.zmy.po.User;
import com.zmy.service.UserService;
import com.zmy.util.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user;
    }
}
