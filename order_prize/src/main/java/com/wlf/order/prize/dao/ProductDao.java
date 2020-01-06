package com.wlf.order.prize.dao;

import com.wlf.order.prize.model.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<ProductItem, Long> {

    List<ProductItem> findAllByOrderId(String orderId);
}
