package com.wlf.order.prize.dao;

import com.wlf.order.prize.model.LogisticsItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LogisticsDao extends JpaRepository<LogisticsItem, Long> {

    LogisticsItem findByOrderId(String orderId);

    @Modifying
    @Query("update LogisticsItem l set l.logisticsStatus = ?1, l.timestamp = ?4 where l.orderId = ?2 and l.timestamp = ?3")
    void updateStatusByOrderId(String logisticsStatus, String orderId, Long oldTimestamp, Long newTimestamp);
}
