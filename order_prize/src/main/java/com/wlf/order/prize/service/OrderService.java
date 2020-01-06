package com.wlf.order.prize.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wlf.order.api.util.LogisticsStatus;
import com.wlf.order.api.util.OrderErrorCode;
import com.wlf.order.prize.dao.*;
import com.wlf.order.prize.exception.OrderException;
import com.wlf.order.api.javabean.*;
import com.wlf.order.prize.model.*;
import com.wlf.order.prize.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 业务逻辑实现类
 *
 * @author wulinfeng
 * @since 2019/12/13
 */
@Slf4j
@Service
public class OrderService {

    @Value("${ng_apiSecret}")
    private String ng_apiSecret;

    @Value("${dy_apiSecret}")
    private String dy_apiSecret;

    @Value("${orders_max}")
    private int orders_max;

    @Value("${store_out_max}")
    private int store_out_max;

    @Value("${expire_second}")
    private int expire_second;

    @Value("${dyUrl}")
    private String dyUrl;

    @Value("${ngUrl}")
    private String ngUrl;

    @Value("${isUrl}")
    private String isUrl;

    @Value("${info_source_appID}")
    private String info_source_appID;

    @Value("${info_source_passwd}")
    private String info_source_passwd;

    @Value("${info_source_activityId1}")
    private String info_source_activityId1;

    @Value("${info_source_activityId2}")
    private String info_source_activityId2;

    @Autowired
    OrderDao orderDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    LogisticsDao logisticsDao;

    @Autowired
    ReturnOrderDao returnOrderDao;

    @Autowired
    InfoSourceDao infoSourceDao;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 插入/更新订单数据
     *
     * @param orders
     * @param timestamp
     * @throws OrderException
     */
    @Transactional
    public void insertOrders(Orders orders, Long timestamp) throws OrderException {

        // 请求消息体校验
        if (orders == null || orders.getOrders() == null) {
            log.error("orders are empty.");
            throw new OrderException(OrderErrorCode.ORDER_EMPTY.getCode(),
                    OrderErrorCode.ORDER_EMPTY.getMsg());
        } else if (orders.getOrderCount() <= 0 ||
                orders.getOrderCount() > orders_max ||
                orders.getOrderCount() != orders.getOrders().size()) {
            log.error("orderCount error, orderCount : {}", orders.getOrderCount());
            throw new OrderException(OrderErrorCode.ORDER_COUNT_ERROR.getCode(),
                    OrderErrorCode.ORDER_COUNT_ERROR.getMsg());
        }


        // 遍历同步过来的数据
        for (Order order : orders.getOrders()) {

            // 校验空记录、空订单号记录
            if (order == null || order.getOrderId() == null ||
                    order.getOrderId().trim().equals("")) {
                log.error("order or orderId is null.");
                throw new OrderException(OrderErrorCode.ORDER_ERROR.getCode(),
                        OrderErrorCode.ORDER_ERROR.getMsg());
            }

            // 订单表实体映射
            OrderItem orderItem = BeanConvert.getOrderItem(order, timestamp);
            if (orderItem == null) {
                log.error("orderItem is null.");
                throw new OrderException(OrderErrorCode.ORDER_ERROR.getCode(),
                        OrderErrorCode.ORDER_ERROR.getMsg());
            }

            // 先查询订单ID，已存在数据则不入库
            OrderItem tmp = orderDao.findByOrderId(order.getOrderId());
            if (tmp != null) {
                log.warn("orderItem has exist.");
                continue;
            }

            // 1、订单入库
            try {
                orderDao.save(orderItem);
            } catch (Exception e) {
                log.error("db error : {}", e.getMessage());
                throw new OrderException(OrderErrorCode.DB_ERROR.getCode(),
                        OrderErrorCode.DB_ERROR.getMsg());
            }

            // 2、产品对象入库
            List<Product> products = order.getProducts();
            if (products != null && products.size() > 0) {
                for (Product product : products) {
                    if (product == null || product.getProductid() == null ||
                            product.getProductid().trim().equals("")) {
                        log.error("product data error，orderId is : {}", order.getOrderId());
                        throw new OrderException(OrderErrorCode.PRODUCT_ERROR.getCode(),
                                OrderErrorCode.PRODUCT_ERROR.getMsg()
                                        + order.getOrderId());
                    }

                    // 对象转换
                    ProductItem productItem = new ProductItem();
                    productItem.setOrderId(order.getOrderId());
                    productItem.setProductId(product.getProductid());
                    productItem.setProductName(product.getProductname());
                    productItem.setTimestamp(timestamp);

                    try {
                        productDao.save(productItem);
                    } catch (Exception e) {
                        log.error("db error : {}", e.getMessage());
                        throw new OrderException(OrderErrorCode.DB_ERROR.getCode(),
                                OrderErrorCode.DB_ERROR.getMsg());
                    }
                }
            } else {
                log.error("products are empty，orderId is : {}", order.getOrderId());
                throw new OrderException(OrderErrorCode.PRODUCT_ERROR.getCode(),
                        OrderErrorCode.PRODUCT_ERROR.getMsg()
                                + order.getOrderId());
            }
        }
    }

    /**
     * 修改订单地址
     *
     * @param orderAddress
     * @param timestamp
     */
    @Transactional
    public void modifyOrderAddress(OrderAddress orderAddress, Long timestamp) {
        if (orderAddress == null || orderAddress.getOrderId() == null ||
                orderAddress.getOrderId().trim().equals("") ||
                orderAddress.getAddress() == null ||
                orderAddress.getAddress().trim().equals("")) {
            log.error("orderAddress is null. orderAddress : {}", orderAddress);
            throw new OrderException(OrderErrorCode.ADDRESS_ERROR.getCode(),
                    OrderErrorCode.ADDRESS_ERROR.getMsg());
        }

        // 先查询，已存在数据则更新
        OrderItem tmp = orderDao.findByOrderId(orderAddress.getOrderId());
        if (tmp == null) {
            log.error("orderAddress has not insert.");
            throw new OrderException(OrderErrorCode.ADDRESS_EMPTY.getCode(),
                    OrderErrorCode.ADDRESS_EMPTY.getMsg());
        }

        // 更新数据
        try {
            orderDao.updateAddressByOrderId(orderAddress.getAddress(), orderAddress.getOrderId(), tmp.getTimestamp(), timestamp);
        } catch (Exception e) {
            log.error("db error : {}", e.getMessage());
            throw new OrderException(OrderErrorCode.DB_ERROR.getCode(),
                    OrderErrorCode.DB_ERROR.getMsg());
        }
    }

    /**
     * 出库/更新订单状态
     *
     * @param logisticsOrders
     * @param timestamp
     * @throws OrderException
     */
    @Transactional
    public void updateOrderStatus(LogisticsOrders logisticsOrders, Long timestamp) {

        // 数据校验
        if (logisticsOrders == null || logisticsOrders.getOrders() == null) {
            log.error("logistics orders are empty.");
            throw new OrderException(OrderErrorCode.STORE_EMPTY.getCode(),
                    OrderErrorCode.STORE_EMPTY.getMsg());
        } else if (logisticsOrders.getOrderCount() <= 0 ||
                logisticsOrders.getOrderCount() > store_out_max ||
                logisticsOrders.getOrderCount() !=
                        logisticsOrders.getOrders().size()) {
            log.error("logistics ordersCount error.");
            throw new OrderException(OrderErrorCode.STORE_COUNT_ERROR.getCode(),
                    OrderErrorCode.STORE_COUNT_ERROR.getMsg());
        }

        // 区分是申请发货还是更新订单状态
        String storeStatus = getStoreStatus(logisticsOrders);
        boolean isStoreOut = storeStatus.equals(LogisticsStatus.STORE_OUT.getStatus());

        for (Logistics order : logisticsOrders.getOrders()) {
            if (order == null || order.getOrderId() == null ||
                    order.getOrderId().trim().equals("") ||
                    order.getLogisticsStatus() == null) {
                log.error("logistics order data error, order : {}", order);
                throw new OrderException(OrderErrorCode.STORE_ORDER_ERROR.getCode(),
                        OrderErrorCode.STORE_ORDER_ERROR.getMsg());
            }

            // 所有状态必须都相同
            if (!order.getLogisticsStatus().equals(storeStatus)) {
                log.error("not every logistics status is same.");
                throw new OrderException(OrderErrorCode.UPDATE_STATUS_ERROR.getCode(),
                        OrderErrorCode.UPDATE_STATUS_ERROR.getMsg());
            }

            // 确认该物流信息订单已存在，否则不予出库
            OrderItem orderItem = orderDao.findByOrderId(order.getOrderId());
            if (orderItem == null) {
                log.error("logistics orderId not exist in t_order.");
                throw new OrderException(OrderErrorCode.UPDATE_NO_ORDER.getCode(),
                        OrderErrorCode.UPDATE_NO_ORDER.getMsg() + order.getOrderId());
            }

            // 先查该物流信息是否已存在，存在则更新，否则新增
            LogisticsItem tmp = logisticsDao.findByOrderId(order.getOrderId());
            if (isStoreOut && tmp != null) {
                log.error("store out data has exist.");
                throw new OrderException(OrderErrorCode.REPEAT_ORDER_CHANGE.getCode(),
                        OrderErrorCode.REPEAT_ORDER_CHANGE.getMsg());
            } else if (!isStoreOut && tmp == null) {
                log.error("change logistics status, but out store has not insert.");
                throw new OrderException(OrderErrorCode.UPDATE_ORDER_ERROR.getCode(),
                        OrderErrorCode.UPDATE_ORDER_ERROR.getMsg());
            } else if (!isStoreOut && tmp != null && tmp.getLogisticsStatus().equals(storeStatus)) {
                log.error("change logistics status, but the status is same with original, storeStatus: {}," +
                        "logistics status : {}", storeStatus, tmp.getLogisticsStatus());
                throw new OrderException(OrderErrorCode.REPEAT_ORDER_CHANGE.getCode(),
                        OrderErrorCode.REPEAT_ORDER_CHANGE.getMsg());
            }

            // 对象转换
            LogisticsItem logisticsItem = BeanConvert.getLogisticsItem(order, timestamp);
            if (logisticsItem == null) {
                log.error("logistics order data error.");
                throw new OrderException(OrderErrorCode.STORE_ORDER_ERROR.getCode(),
                        OrderErrorCode.STORE_ORDER_ERROR.getMsg());
            }

            // 申请发货时insert，更新订单状态时update
            try {
                if (isStoreOut) {
                    logisticsDao.save(logisticsItem);
                } else {
                    logisticsDao.updateStatusByOrderId(storeStatus, order.getOrderId(), tmp.getTimestamp(), timestamp);
                }
            } catch (Exception e) {
                log.error("db error : {}", e.getMessage());
                throw new OrderException(OrderErrorCode.DB_ERROR.getCode(),
                        OrderErrorCode.DB_ERROR.getMsg());
            }
        }

        // 出库则准备通知点壹，更新订单状态则通知NG
        if (isStoreOut) {
            storeOutNotifyDY(logisticsOrders);
        } else {
            // 已收货，开通权限
            if (storeStatus.equals(LogisticsStatus.STORE_HAS_SEDT.getStatus())) {
                try {
                    openPrize(logisticsOrders.getOrders(), timestamp);
                } catch (Exception e) {
                    log.error("open prize failed, error : {}", e.getMessage());
                    throw new OrderException(OrderErrorCode.PRIZE_ERROR.getCode(),
                            OrderErrorCode.PRIZE_ERROR.getMsg());
                }
            }

            // 通知NG
            statusChangeNotifyNG(logisticsOrders);
        }
    }

    /**
     * 退货申请/退货完成
     *
     * @param returnOrder
     * @param timestamp
     * @throws OrderException
     */
    @Transactional
    public void insertReturnOrder(ReturnOrder returnOrder, Long timestamp) {

        if (returnOrder == null || returnOrder.getOrderId() == null ||
                returnOrder.getOrderId().trim().equals("") ||
                returnOrder.getProductinfo() == null ||
                returnOrder.getProductinfo().size() <= 0) {
            log.error("returnOrder error. returnOrder : {}", returnOrder);
            throw new OrderException(OrderErrorCode.RETURN_ORDER_ERROR.getCode(),
                    OrderErrorCode.RETURN_ORDER_ERROR.getMsg());
        }

        // 是否退货
        boolean isReturnOrder = isReturnOrder(returnOrder);

        // 退货状态校验
        for (ReturnProduct returnProduct : returnOrder.getProductinfo()) {
            if (returnProduct == null || returnProduct.getProducttype() == null
                    || returnProduct.getProductid() == null ||
                    returnProduct.getProductid().trim().equals("") ||
                    returnProduct.getOrdertype() == null ||
                    returnProduct.getOrdertype().trim().equals("")) {
                log.error("returnProduct data error. returnProduct : {}", returnProduct);
                throw new OrderException(OrderErrorCode.RETURN_ORDER_ERROR.getCode(),
                        OrderErrorCode.RETURN_ORDER_ERROR.getMsg());
            }

            if (isReturnOrder && !returnProduct.getOrdertype().equals("2")) {
                log.error("returnProduct ordertype error. returnProduct : {}", returnProduct.getOrdertype());
                throw new OrderException(OrderErrorCode.RETURN_ORDERTYPE_ERROR.getCode(),
                        OrderErrorCode.RETURN_ORDERTYPE_ERROR.getMsg() + returnProduct.getProductid());
            }
        }

        // 确认退货订单信息存在
        OrderItem orderItem = orderDao.findByOrderId(returnOrder.getOrderId());
        if (orderItem == null) {
            log.error("return orderId not exist in t_order.");
            throw new OrderException(OrderErrorCode.RETURN_ORDER_EMPTY.getCode(),
                    OrderErrorCode.RETURN_ORDER_EMPTY.getMsg());
        }

        // 确认产品信息存在
        List<ProductItem> productItems = productDao.findAllByOrderId(returnOrder.getOrderId());
        if (productItems == null || productItems.size() <= 0) {
            log.error("products not exist in t_product.");
            throw new OrderException(OrderErrorCode.RETURN_PRODUCT_ERROR.getCode(),
                    OrderErrorCode.RETURN_PRODUCT_ERROR.getMsg());
        } else {
            List<String> productIds = new ArrayList<>();
            for (ProductItem productItem : productItems) {
                productIds.add(productItem.getProductId());
            }

            for (ReturnProduct returnProduct : returnOrder.getProductinfo()) {
                if (!productIds.contains(returnProduct.getProductid())) {
                    log.error("productid {} not exist in t_product.", returnProduct.getProductid());
                    throw new OrderException(OrderErrorCode.RETURN_ORDER_EMPTY.getCode(),
                            OrderErrorCode.RETURN_ORDER_EMPTY.getMsg());
                }
            }
        }

        // 确认退货数据正确
        List<ReturnOrderItem> tmp = returnOrderDao.findAllByOrderId(returnOrder.getOrderId());

        if (tmp == null || tmp.size() <= 0) {
            if (!isReturnOrder) {
                log.error("return order has not exist.");
                throw new OrderException(OrderErrorCode.RETURN_FINISH_ERROR.getCode(),
                        OrderErrorCode.RETURN_FINISH_ERROR.getMsg());
            }
        } else {
            List<String> returnOrderProductIds = new ArrayList<>();
            for (ReturnOrderItem returnOrderItem : tmp) {
                returnOrderProductIds.add(returnOrderItem.getProductid());
            }

            for (ReturnProduct returnProduct : returnOrder.getProductinfo()) {
                if (isReturnOrder && returnOrderProductIds.contains(returnProduct.getProductid())) {
                    log.error("return order product {} has exist.", returnProduct.getProductid());
                    throw new OrderException(OrderErrorCode.RETURN__ERROR.getCode(),
                            OrderErrorCode.RETURN__ERROR.getMsg());
                } else if (!isReturnOrder && !returnOrderProductIds.contains(returnProduct.getProductid())) {
                    log.error("return order product {} has not exist.", returnProduct.getProductid());
                    throw new OrderException(OrderErrorCode.RETURN_FINISH_ERROR.getCode(),
                            OrderErrorCode.RETURN_FINISH_ERROR.getMsg());
                }
            }
        }

        // 对象转换
        List<ReturnOrderItem> returnOrderItems = BeanConvert.getReturnOrderItem(returnOrder, timestamp);
        if (returnOrderItems == null || returnOrderItems.size() <= 0) {
            log.error("returnOrderItems is empty.");
            throw new OrderException(OrderErrorCode.RETURN_ORDER_ERROR.getCode(),
                    OrderErrorCode.RETURN_ORDER_ERROR.getMsg());
        }

        try {
            // 申请退货则insert
            if (isReturnOrder) {
                returnOrderDao.saveAll(returnOrderItems);
            } else {
                // 完成退货时则update
                returnOrder.getProductinfo().forEach(r -> {
                    returnOrderDao.updateOrderTypeByOrderId(r.getOrdertype(), returnOrder.getOrderId(),
                            tmp.get(0).getTimestamp(), timestamp);
                });
            }
        } catch (
                Exception e) {
            log.error("db error: {}", e.getMessage());
            throw new OrderException(OrderErrorCode.DB_ERROR.getCode(),
                    OrderErrorCode.DB_ERROR.getMsg());
        }

        // 发起退货，则通知给点壹平台；退货完成，则通知NG
        if (isReturnOrder) {
            returnOrderNotifyDY(returnOrder);
        } else {
            finishReturnNotifyNG(returnOrder);
        }
    }

    /**
     * 是否申请退货
     *
     * @param returnOrder
     * @return
     */
    private boolean isReturnOrder(ReturnOrder returnOrder) {
        for (ReturnProduct returnProduct : returnOrder.getProductinfo()) {
            if (returnProduct.getOrdertype().equals("2")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 本次请求的订单状态
     *
     * @param logisticsOrders
     * @return
     */
    private String getStoreStatus(LogisticsOrders logisticsOrders) {
        return logisticsOrders.getOrders().get(0).getLogisticsStatus();
    }


    /**
     * 请求鉴权
     *
     * @param url       服务url
     * @param timestamp 时间戳
     * @param sign      调用方签名
     * @param arg       请求消息体
     * @param apiSecret 秘钥
     * @throws OrderException
     */
    public void signAuth(String url, Long timestamp, String sign, String arg, String apiSecret) {

        String seed = url + timestamp + arg + apiSecret;
        String mySign = getSign(seed);
        if (!mySign.equals(sign)) {
            log.error("request sign != md5 sign. requestSign : {}, mySign : {}, url : {}", sign, mySign, url);
            throw new OrderException(OrderErrorCode.TOKEN_ERROR.getCode(),
                    OrderErrorCode.TOKEN_ERROR.getMsg());
        }

        long currentTime = System.currentTimeMillis();
        if ((timestamp + expire_second * 1000) < currentTime) {
            log.error("The token has expired.request time is {}, currentTime is : {}", timestamp, currentTime);
            throw new OrderException(OrderErrorCode.TIME_EXPIRE.getCode(),
                    OrderErrorCode.TIME_EXPIRE.getMsg());
        }
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getSign(String seed) {
        String sign;
        try {
            sign = MD5Util.md5Sum(seed);
        } catch (NoSuchAlgorithmException e) {
            log.error("md5 error : {}", e.getMessage());
            throw new OrderException(OrderErrorCode.MD5_ERROR.getCode(),
                    OrderErrorCode.MD5_ERROR.getMsg());
        }
        return sign;
    }

    /**
     * 出库时调用点壹接口
     *
     * @param logisticsOrders
     */
    private void storeOutNotifyDY(LogisticsOrders logisticsOrders) {

        // 构造请求消息体
        List<Order> orderList = new ArrayList<>();

        logisticsOrders.getOrders().forEach(l -> {

            // 查出订单信息
            OrderItem orderItem = orderDao.findByOrderId(l.getOrderId());

            // 查出产品信息
            List<ProductItem> productItems = productDao.findAllByOrderId(l.getOrderId());
            List<Product> products = new ArrayList<>();
            productItems.forEach(p -> {
                Product product = new Product();
                product.setProductid(p.getProductId());
                product.setProductname(p.getProductName());
                products.add(product);
            });

            orderList.add(BeanConvert.getOrder(orderItem, products));
        });

        // 构造请求消息体
        Orders orders = new Orders();
        orders.setOrderCount(logisticsOrders.getOrderCount());
        orders.setOrders(orderList);

        // 构造消息头
        String url = dyUrl + "order/my/status";
        String currentTime = String.valueOf(System.currentTimeMillis());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("timestamp", currentTime);
        httpHeaders.add("sign", getSign(url + currentTime + logisticsOrders.getOrderCount() + dy_apiSecret));

        HttpEntity<Orders> httpEntity = new HttpEntity<Orders>(orders, httpHeaders);

        // 开始调用点壹平台出库通知接口
        httpCall(url, httpEntity);
    }

    /**
     * 申请退货通知点壹
     *
     * @param returnOrder
     */
    private void returnOrderNotifyDY(ReturnOrder returnOrder) {
        String url = dyUrl + "order/status";
        HttpHeaders httpHeaders = new HttpHeaders();
        String currentTime = String.valueOf(System.currentTimeMillis());
        httpHeaders.add("timestamp", currentTime);
        httpHeaders.add("sign", getSign(url + currentTime + returnOrder.getOrderId() + dy_apiSecret));

        HttpEntity<ReturnOrder> httpEntity = new HttpEntity<ReturnOrder>(returnOrder, httpHeaders);

        // 调用点壹平台退订接口
        httpCall(url, httpEntity);
    }

    /**
     * 变更订单状态时调用NG接口
     *
     * @param logisticsOrders
     */
    private void statusChangeNotifyNG(LogisticsOrders logisticsOrders) {
        // 同步订单状态给NG
        String url = ngUrl + "sale/ng/servicesale/sale/province/order/notify";

        String requestBodyStr;
        try {
            requestBodyStr = BeanConvert.getLogisticsJson(logisticsOrders);
        } catch (JsonProcessingException e) {
            log.error("convert logisticsOrders to json error, error : {}", e.getMessage());
            throw new OrderException(OrderErrorCode.STORE_ORDER_ERROR.getCode(),
                    OrderErrorCode.STORE_ORDER_ERROR.getMsg());
        }

        // 构造消息头
        HttpHeaders httpHeaders = createHttpHead("/sale/ng/servicesale/sale/province/order/notify", requestBodyStr);

        HttpEntity<LogisticsOrders> httpEntity = new HttpEntity<LogisticsOrders>(logisticsOrders, httpHeaders);

        // 开始调用NG状态同步通知接口
        httpCall(url, httpEntity);
    }

    /**
     * 完成退货通知NG
     *
     * @param returnOrder
     */
    private void finishReturnNotifyNG(ReturnOrder returnOrder) {

        String url = ngUrl + "sale/ng/servicesale/sale/province/order/return/notify";

        String requestBodyStr;
        try {
            requestBodyStr = BeanConvert.getReturnOrderJson(returnOrder);
        } catch (JsonProcessingException e) {
            log.error("convert logisticsOrders to json error, error : {}", e.getMessage());
            throw new OrderException(OrderErrorCode.RETURN_ORDER_ERROR.getCode(),
                    OrderErrorCode.RETURN_ORDER_ERROR.getMsg());
        }

        // 构造消息头
        HttpHeaders httpHeaders = createHttpHead("/sale/ng/servicesale/sale/province/order/return/notify", requestBodyStr);

        HttpEntity<ReturnOrder> httpEntity = new HttpEntity<ReturnOrder>(returnOrder, httpHeaders);

        // 开始调用NG状态同步通知接口
        httpCall(url, httpEntity);
    }

    /**
     * 构造消息头
     *
     * @param url
     * @param requestBodyStr
     * @return
     */
    private HttpHeaders createHttpHead(String url, String requestBodyStr) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("sign", "shumei");
        httpHeaders.add("token", getSign(getSign(url + requestBodyStr + ng_apiSecret)));
        return httpHeaders;
    }

    /**
     * http调用
     *
     * @param url
     * @param httpEntity
     */
    private void httpCall(String url, HttpEntity httpEntity) {

        log.info("call {} begin, httpEntity : {}", url, httpEntity);

        Result result = null;
        try {
            result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Result.class).getBody();
        } catch (Exception e) {
            log.error("call {} failed, error : {}", url, e.getMessage());
            throw new OrderException(OrderErrorCode.NOTIFY_ERROR.getCode(),
                    OrderErrorCode.NOTIFY_ERROR.getMsg());
        }

        if (result.getCode() != 0) {
            log.error("return error : {}, msg : {}", result.getCode(), result.getMsg());
            throw new OrderException(OrderErrorCode.NOTIFY_ERROR.getCode(),
                    OrderErrorCode.NOTIFY_ERROR.getMsg());
        }

        log.info("call {} end, result: {}", url, result);
    }

    /**
     * 批量开通权限
     *
     * @param orders
     * @param timestamp
     */
    private void openPrize(List<Logistics> orders, Long timestamp) {
        final String url = isUrl + "is/getUserCollectionActivities";

        orders.forEach(o -> {
            // 先确认是否需要重试
            List<InfoSourceItem> infoSourceItems = infoSourceDao.findAllByOrderId(o.getOrderId());

            // 重试调用，更新权益开通失败表
            PrizeResponse prizeResponse = null;
            if (infoSourceItems != null && infoSourceItems.size() > 0) {
                retryCallAndUpdate(timestamp, o, url, infoSourceItems);
            } else {
                // 首次调用，失败则插入权益开通失败表
                callInfoSource(o, url, info_source_activityId1, true, timestamp);
                callInfoSource(o, url, info_source_activityId2, true, timestamp);
            }
        });
    }

    /**
     * 重试权益开通接口
     *
     * @param o
     * @param url
     * @param info_source_activityId1
     * @param oldTimestamp
     * @param timestamp
     * @param failedCount
     */
    private void retryCallInfoSource(Logistics o, String url, String info_source_activityId1, Long oldTimestamp,
                                     Long timestamp, int failedCount) {
        try {
            callInfoSource(o, url, info_source_activityId1, false, timestamp);
        } catch (OrderException e) {
            log.warn("retry call infosource failed, activityId : {}, timestamp : {}", info_source_activityId1,
                    timestamp);
            try {
                infoSourceDao.updateResultCodeByOrderIdAndActivityId(info_source_activityId1, o.getOrderId(),
                        oldTimestamp, timestamp, String.valueOf(e.getCode()), e.getMsg().length() > 200 ?
                                e.getMsg().substring(0, 200) : e.getMsg(), ++failedCount);
            } catch (Exception e1) {
                log.warn("update t_infosource failed, error : {}", e1);
            }

            throw e;
        }

        // 成功则更新异常表
        try {
            infoSourceDao.updateResultCodeByOrderIdAndActivityId(info_source_activityId1, o.getOrderId(),
                    oldTimestamp, timestamp, "00000", "success", failedCount);
        } catch (Exception e) {
            log.warn("update t_infosource failed, error : {}", e);
        }
    }

    /**
     * 调用信源开通权益
     *
     * @param o
     * @param url
     * @param activityId
     * @param isFirstCall
     * @param timestamp
     */
    private void callInfoSource(Logistics o, String url, String activityId, boolean isFirstCall, Long timestamp) {
        PrizeResponse prizeResponse = null;

        // 构造信源支撑平台2.13用户权益开通接口请求消息体
        PrizeRequest request = getPrizeRequest(o.getServiceNumber(), activityId);

        log.info("call {} begin, request : {}", url, request);

        try {
            prizeResponse = restTemplate.postForObject(url,
                    request, PrizeResponse.class);
        } catch (Exception e) {
            log.error("get prizeResponse failed, error : {}", e.getMessage());

            // 准备告警信息
            mailRecored(request, prizeResponse, timestamp);

            // 异常入库
            if (isFirstCall) {
                insertInfoSource(o, request, prizeResponse, timestamp);
            }
            throw new OrderException(59999, e.getMessage());
        }

        if (!prizeResponse.getResultCode().equals("00000")) {
            log.error("call infoSource failed, prizeResponse : {}",
                    prizeResponse);

            // 准备告警信息
            mailRecored(request, prizeResponse, timestamp);

            if (isFirstCall) {
                insertInfoSource(o, request, prizeResponse, timestamp);
            }
            throw new OrderException(Integer.valueOf(prizeResponse.getResultCode()), prizeResponse.getResultMsg());
        }

        log.info("call {} end, result : {}", url, prizeResponse);

    }

    /**
     * 准备告警信息
     *
     * @param request
     * @param prizeResponse
     * @param timestamp
     */
    private void mailRecored(PrizeRequest request, PrizeResponse prizeResponse, Long timestamp) {
        Map<String, String> strMap = ThreadLocalUtil.getMap();
        StringBuilder sb = new StringBuilder();
        sb.append("prizeRequest : ");
        sb.append(request.toString());
        sb.append(System.getProperty("line.separator", "\n"));
        sb.append("prizeResponse : ");
        sb.append(prizeResponse == null ? "null" : prizeResponse.toString());
        sb.append(System.getProperty("line.separator", "\n"));
        sb.append("timestamp : ");
        sb.append(timestamp);
        strMap.put("openPrize", sb.toString());
    }

    /**
     * 首次调用失败，异常数据入库
     *
     * @param o
     * @param request
     * @param prizeResponse
     * @param timestamp
     */
    private void insertInfoSource(Logistics o, PrizeRequest request, PrizeResponse prizeResponse, Long timestamp) {
        log.warn("first call infosource failed and insert into t_infosource, timestamp : {}", timestamp);
        try {
            InfoSourceItem infoSourceItem = BeanConvert.getInfoSourceItem(timestamp, o, request, prizeResponse);
            infoSourceDao.save(infoSourceItem);
        } catch (Exception e) {
            log.warn("insert into t_infosource failed, error : {}", e);
        }
    }

    /**
     * 调用并更新信源
     *
     * @param timestamp
     * @param o
     * @param url
     * @param infoSourceItems
     */
    private void retryCallAndUpdate(Long timestamp, Logistics o, String url, List<InfoSourceItem> infoSourceItems) {
        infoSourceItems.forEach(i -> {
            int failedCount = i.getFailedCount();
            Long oldTimestamp = i.getTimestamp();

            // 对第一个权益重试
            if (i.getActivityId().equals(info_source_activityId1)) {

                // 更新失败则继续抛异常
                retryCallInfoSource(o, url, info_source_activityId1, oldTimestamp, timestamp, failedCount);

                // 继续调用第二个权益开通接口
                callInfoSource(o, url, info_source_activityId2, true, timestamp);
            } else {
                // 第二个权益重试
                retryCallInfoSource(o, url, info_source_activityId2, oldTimestamp, timestamp, failedCount);
            }
        });
    }

    /**
     * 权益开通请求消息体构造
     *
     * @param serviceNumber
     * @param info_source_activityId
     * @return
     */
    private PrizeRequest getPrizeRequest(String serviceNumber, String info_source_activityId) {

        String sequenceNo = UUIDUtil.getSequence();
        String seed;
        try {
            seed = MD5Util.md5Sum(info_source_passwd + sequenceNo);
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5 failed, error : {}", e.getMessage());
            throw new OrderException(59999, e.getMessage());
        }

        PrizeRequest prizeRequest = new PrizeRequest();
        prizeRequest.setRequestSeq(sequenceNo);
        prizeRequest.setAppId(info_source_appID);
        prizeRequest.setPassword(seed.toUpperCase());
        prizeRequest.setActivityId(info_source_activityId);
        prizeRequest.setUserId(serviceNumber);
        prizeRequest.setEquipmentId(serviceNumber);
        return prizeRequest;
    }

}