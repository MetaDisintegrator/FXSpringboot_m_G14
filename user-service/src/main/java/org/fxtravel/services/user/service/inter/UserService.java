package org.fxtravel.services.user.service.inter;
import org.fxtravel.services.user.dto.RegisterRequest;
import org.fxtravel.services.user.entity.User;

public interface UserService {
    /**
     * 根据邮箱和密码注册用户账号
     */
    public User register(RegisterRequest request);

    public boolean verifyCode(String code);

    public User login(String email, String password);

    public boolean resetPasswordByVerificationCode(String email,String code, String password);

    public boolean resetPasswordByOldPassword(String email, String oldPassword, String newPassword);
}




