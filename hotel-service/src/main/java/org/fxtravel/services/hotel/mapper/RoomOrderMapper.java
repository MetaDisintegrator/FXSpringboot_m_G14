package org.fxtravel.services.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.fxtravel.services.hotel.entitiy.RoomOrder;
import org.fxtravel.services.payment.common.E_PaymentStatus;


import java.util.List;

public interface RoomOrderMapper extends BaseMapper<RoomOrder> {
    @Select("SELECT * FROM room_order WHERE user_id = #{userID}")
    List<RoomOrder> findByUserID(Integer userID);

    // 更新订单状态
    @Update("UPDATE room_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(Integer id, E_PaymentStatus status);
}
