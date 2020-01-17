package com.wlf.order.prize.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wlf.order.api.javabean.*;
import com.wlf.order.prize.model.InfoSourceItem;
import com.wlf.order.prize.model.LogisticsItem;
import com.wlf.order.prize.model.OrderItem;
import com.wlf.order.prize.model.ReturnOrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象转换帮助类
 *
 * @author wulinfeng
 * @since 2019/12/16
 */
public class BeanConvert {

    /**
     * 接口请求参数转为数据库表字段
     *
     * @param order
     * @param timestamp
     * @return
     */
    public static OrderItem getOrderItem(Order order, Long timestamp) {
        if (order == null || order.getOrderId() == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getOrderId());
        orderItem.setServernum(order.getServernum());
        orderItem.setArea(order.getArea());
        orderItem.setName(order.getName());
        orderItem.setLinkphone(order.getLinkphone());
        orderItem.setAddress(order.getAddress());
        orderItem.setAccessChannel(order.getAccessChannel());
        orderItem.setOprtime(order.getOprtime());
        orderItem.setChannel(order.getChannel());
        orderItem.setOprcode(order.getOprcode());
        orderItem.setTimestamp(timestamp);

        return orderItem;
    }

    /**
     * 物流信息同步
     *
     * @param logistics
     * @param timestamp
     * @return
     */
    public static LogisticsItem getLogisticsItem(Logistics logistics, Long timestamp) {
        if (logistics == null || logistics.getOrderId() == null) {
            return null;
        }

        LogisticsItem logisticsItem = new LogisticsItem();
        logisticsItem.setOrderId(logistics.getOrderId());
        logisticsItem.setServiceNumber(logistics.getServiceNumber());
        logisticsItem.setLogisticsOrderNumber(logistics.getLogisticsOrderNumber());
        logisticsItem.setLogisticsCompanyName(logistics.getLogisticsCompanyName());
        logisticsItem.setLogisticsStatus(logistics.getLogisticsStatus());
        logisticsItem.setInstallStatus(logistics.getInstallStatus());
        logisticsItem.setSatisfication(logistics.getSatisfication());
        logisticsItem.setTimestamp(timestamp);

        return logisticsItem;
    }


    /**
     * 退货信息同步
     *
     * @param returnOrder
     * @param timestamp
     * @return
     */
    public static List<ReturnOrderItem> getReturnOrderItem(ReturnOrder returnOrder, Long timestamp) {
        if (returnOrder == null || returnOrder.getOrderId() == null || returnOrder.getProductinfo() == null
                || returnOrder.getProductinfo().size() <= 0) {
            return null;
        }

        List<ReturnOrderItem> returnOrderItems = new ArrayList<>();

        List<ReturnProduct> returnProducts = returnOrder.getProductinfo();
        for (ReturnProduct returnProduct : returnProducts) {
            ReturnOrderItem returnOrderItem = new ReturnOrderItem();
            returnOrderItem.setOrderId(returnOrder.getOrderId());
            returnOrderItem.setServernum(returnOrder.getServernum());

            returnOrderItem.setProductgroup(returnProduct.getProductgroup());
            returnOrderItem.setProductid(returnProduct.getProductid());
            returnOrderItem.setProductname(returnProduct.getProductname());
            returnOrderItem.setProducttype(returnProduct.getProducttype());
            returnOrderItem.setOrdertype(returnProduct.getOrdertype());
            returnOrderItem.setTimestamp(timestamp);
            returnOrderItems.add(returnOrderItem);
        }

        return returnOrderItems;
    }

    /**
     * 订单表字段转为请求实体类
     *
     * @param orderItem
     * @param products
     * @return
     */
    public static Order getOrder(OrderItem orderItem, List<Product> products) {
        if (orderItem == null || orderItem.getOrderId() == null) {
            return null;
        }

        Order order = new Order();
        order.setOrderId(orderItem.getOrderId());
        order.setServernum(orderItem.getServernum());
        order.setArea(orderItem.getArea());
        order.setProducts(products);
        order.setName(orderItem.getName());
        order.setAddress(orderItem.getAddress());
        order.setLinkphone(orderItem.getLinkphone());
        order.setAccessChannel(orderItem.getAccessChannel());
        order.setOprtime(orderItem.getOprtime());
        order.setOprcode(orderItem.getOprcode());
        order.setChannel(orderItem.getChannel());
        return order;
    }

    /**
     * 对象转json
     *
     * @param logisticsOrders
     * @return
     * @throws JsonProcessingException
     */
    public static String getLogisticsJson(LogisticsOrders logisticsOrders) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(logisticsOrders);
    }

    /**
     * 对象转json
     *
     * @param returnOrder
     * @return
     * @throws JsonProcessingException
     */
    public static String getReturnOrderJson(ReturnOrder returnOrder) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(returnOrder);
    }

    /**
     * 对象转json
     *
     * @param result
     * @return
     */
    public static String getResultJson(Result result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(result);
    }

    /**
     * 生成表实体
     *
     * @param timestamp
     * @param o
     * @param request
     * @param prizeResponse
     * @return
     */
    public static InfoSourceItem getInfoSourceItem(Long timestamp, Logistics o, PrizeRequest request,
                                                   PrizeResponse prizeResponse) {
        InfoSourceItem infoSourceItem = new InfoSourceItem();
        infoSourceItem.setTimestamp(timestamp);
        infoSourceItem.setOrderId(o.getOrderId());
        infoSourceItem.setUserId(o.getServiceNumber());
        infoSourceItem.setFailedCount(1);
        if (request != null) {
            infoSourceItem.setActivityId(request.getActivityId());
            infoSourceItem.setRequestSeq(request.getRequestSeq());
        }

        if (prizeResponse != null) {
            infoSourceItem.setErrorMsg(prizeResponse.getResultCode());
            infoSourceItem.setErrorMsg(prizeResponse.getResultMsg().length() > 200 ?
                    prizeResponse.getResultMsg().substring(0, 200) : prizeResponse.getResultMsg());
        } else {
            infoSourceItem.setResultCode("59999");
            infoSourceItem.setErrorMsg("runtime exception.");
        }

        return infoSourceItem;
    }
}
