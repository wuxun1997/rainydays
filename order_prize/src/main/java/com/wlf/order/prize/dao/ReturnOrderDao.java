package com.wlf.order.prize.dao;

import com.wlf.order.prize.model.ReturnOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReturnOrderDao extends JpaRepository<ReturnOrderItem, Long> {

    List<ReturnOrderItem> findAllByOrderId(String orderId);

    @Modifying
    @Query("update ReturnOrderItem r set r.ordertype = ?1, r.timestamp = ?4 where r.orderId = ?2 " +
            "and r.timestamp = ?3")
    void updateOrderTypeByOrderId(String orderType, String orderId, Long oldTimestamp, Long newTimestamp);
}
