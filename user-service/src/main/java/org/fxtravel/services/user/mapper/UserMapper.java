package org.fxtravel.services.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import org.fxtravel.services.user.entity.User;

@Mapper
public interface UserMapper  extends BaseMapper<User> {
    //根据邮箱查询用户
    @Select("select * from user where email = #{email}")
    public User getUserByEmail(String email);

    //查看用户邮箱是否被注册
    default boolean existsByEmail(String email) {
        return getUserByEmail(email) != null;
    }
}
