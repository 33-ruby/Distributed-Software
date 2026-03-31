package org.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.mapper.UserMapper;
import org.example.pojo.User;
import org.example.common.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public boolean register(String nickname, String password, String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (userMapper.selectCount(wrapper) > 0) {
            return false;
        }
        User user = new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        return userMapper.insert(user) > 0;
    }

    public String login(String email, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return JwtUtil.generateToken(user.getId());
        }
        return null;
    }
}