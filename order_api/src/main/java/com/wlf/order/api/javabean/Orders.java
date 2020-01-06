package com.wlf.order.api.javabean;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Orders {
    List<Order> orders;
    int orderCount;
}
