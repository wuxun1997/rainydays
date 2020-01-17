package com.wlf.order.prize.dao;

import com.wlf.order.prize.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductDao extends JpaRepository<ProductItem, Long> {

    List<ProductItem> findAllByOrderId(String orderId);

    @Modifying
    @Query("update ProductItem p set p.equipmentId = ?1, p.timestamp = ?5 where p.orderId = ?2 and " +
            "p.productId = ?3 and p.timestamp = ?4")
    void updateEquitmentIdByOrderIdAndProductId(String equimentId, String orderId, String productId,
                                                Long oldTimestamp, Long newTimestamp);
}
