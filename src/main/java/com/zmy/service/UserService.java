package com.zmy.service;

import com.zmy.po.User;


public interface UserService {

    User checkUser(String username, String password);
}
