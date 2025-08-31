package org.fxtravel.services.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.fxtravel.services.hotel.entitiy.Hotel;

import java.util.List;

public interface HotelMapper extends BaseMapper<Hotel> {

    @Select("SELECT * FROM hotel WHERE destination = #{destination} ORDER BY rating DESC")
    List<Hotel> findByDest(@Param("destination") String destination);

    @Select("SELECT * FROM hotel WHERE destination = #{destination} AND name LIKE CONCAT('%', #{name}, '%') ORDER BY rating DESC")
    List<Hotel> findByDestAndName(@Param("destination") String destination, @Param("name") String name);
}