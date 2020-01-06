package com.wlf.order.prize.dao;

import com.wlf.order.prize.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface OrderDao extends JpaRepository<OrderItem, Long> {
    OrderItem findByOrderId(String orderId);

    @Modifying
    @Query("update OrderItem o set o.address = ?1, o.timestamp = ?4 where o.orderId = ?2 and o.timestamp = ?3")
    void updateAddressByOrderId(String address, String orderId, Long oldTimestamp, Long newTimestamp);
}
