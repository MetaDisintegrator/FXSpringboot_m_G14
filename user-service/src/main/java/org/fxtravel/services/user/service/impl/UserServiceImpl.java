package org.fxtravel.services.user.service.impl;

import org.fxtravel.services.user.dto.RegisterRequest;
import org.fxtravel.services.user.entity.VerificationCode;
import org.fxtravel.services.user.mapper.*;
import org.fxtravel.services.user.service.inter.UserService;
import org.fxtravel.services.user.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerificationMapper verificationMapper;

    @Override
    public User register(RegisterRequest request) {
        if (userMapper.existsByEmail(request.getEmail())) {
            return null;
        }
        System.out.println(request);

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setVerified(true);
        userMapper.insert(user);

        //发送验证邮件
//        String code = UUID.randomUUID().toString();
//        mailService.sendVerificationEmail(request.getEmail(), code, "注册验证邮件");
//
//        verificationService.save(code,user.getId());

        return user;
    }


    @Override
    public boolean verifyCode(String code) {
        VerificationCode verificationCode = verificationMapper.getVerificationByCode(code);
        if (verificationCode == null) {
            return false;
        }
        User user = userMapper.selectById(verificationCode.getUserId());
        if (user.isVerified()) {
            return false;
        }
        user.setVerified(true);

        userMapper.updateById(user);
        verificationMapper.deleteById(verificationCode);

        return true;
    }

    @Override
    public User login(String email, String password) {
        User user = userMapper.getUserByEmail(email);
        if (user == null || !user.getPassword().equals(password) || !user.isVerified()) {
            return null;
        }
        return user;
    }

    @Override
    public boolean resetPasswordByVerificationCode(String email, String code, String password) {
        VerificationCode verificationCode = verificationMapper.getVerificationByCode(code);
        if (verificationCode == null) {
            return false;
        }
        User user = userMapper.selectById(verificationCode.getUserId());
        if (user == null || !user.isVerified() || !user.getEmail().equals(email)) {
            return false;
        }
        user.setPassword(password);
        userMapper.updateById(user);
        verificationMapper.deleteById(verificationCode);
        return true;
    }

    @Override
    public boolean resetPasswordByOldPassword(String email, String oldPassword, String newPassword) {
        User user = userMapper.getUserByEmail(email);
        if (user == null || !user.isVerified() || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        user.setPassword(newPassword);
        userMapper.updateById(user);

        return true;
    }

}
