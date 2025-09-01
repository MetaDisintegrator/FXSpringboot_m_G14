package org.fxtravel.services.train.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import org.fxtravel.services.train.common.E_PaymentStatus;
import org.fxtravel.services.train.entitiy.TrainMealOrder;

import java.util.List;

@Mapper
public interface TrainMealOrderMapper extends BaseMapper<TrainMealOrder> {
    @Select("SELECT * FROM train_meal_order WHERE user_id = #{userId}")
    List<TrainMealOrder> selectByUser(Integer userId);

    @Select("SELECT * FROM train_meal_order WHERE seat_order_id = #{seatOrderId}")
    List<TrainMealOrder> selectBySeatOrder(Integer seatOrderId);

    @Select("SELECT EXISTS(SELECT 1 FROM train_meal_order WHERE seat_order_id = #{seatOrderId} AND status = 'COMPLETED')")
    boolean existsBySeatOrderId(Integer seatOrderId);

    // 更新订单状态
    @Update("UPDATE train_meal_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(Integer id, E_PaymentStatus status);
}