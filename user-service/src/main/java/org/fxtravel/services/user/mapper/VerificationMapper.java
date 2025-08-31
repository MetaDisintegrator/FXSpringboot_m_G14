package org.fxtravel.services.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.fxtravel.services.user.entity.VerificationCode;

@Mapper
public interface VerificationMapper extends BaseMapper<VerificationCode> {

    @Select("select * from verification_code where code = #{code}")
    public VerificationCode getVerificationByCode(String code);

    @Insert("INSERT INTO verification_code (code, user_id) " +
            "VALUES (#{code}, #{userId})")
    @Options(useGeneratedKeys = true)
    public int insertVerificationCode(VerificationCode code);

}
