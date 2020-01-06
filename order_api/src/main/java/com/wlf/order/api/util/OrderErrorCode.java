package com.wlf.order.api.util;

public enum OrderErrorCode {

    SUCCESS(0, "成功"),
    ORDER_EMPTY(11000, "录单时订单列表为空"),
    ORDER_COUNT_ERROR(11001, "录单时订单总数错误"),
    ORDER_ERROR(11002, "录单时订单数据错误"),
    PRODUCT_ERROR(11003, "录单时产品数据错误，该产品的订单编号为"),
    ADDRESS_ERROR(11004, "更新订单地址时订单数据错误"),
    ADDRESS_EMPTY(11005, "更新地址时订单尚未存在"),
    STORE_EMPTY(11006, "申请发货/更新订单状态时订单列表为空"),
    STORE_COUNT_ERROR(11007, "申请发货/更新订单状态时订单总数错误"),
    STORE_ORDER_ERROR(11008, "申请发货/更新订单状态时订单数据错误"),
    UPDATE_NO_ORDER(11009, "申请发货/更新订单状态时无该订单存在，订单编号为"),
    UPDATE_STATUS_ERROR(11010, "申请发货/更新订单状态时订单状态错误"),
    REPEAT_ORDER_CHANGE(11011, "同一订单重复申请发货/重复变更订单状态"),
    UPDATE_ORDER_ERROR(11012, "更新订单状态时无该订单存在"),
    RETURN_ORDER_EMPTY(11013, "申请退货/完成退货时订单数据不存在"),
    RETURN_ORDER_ERROR(11014, "申请退货/完成退货时订单数据错误"),
    RETURN_ORDERTYPE_ERROR(11015, "申请退货时订购类型错误，该产品编号为"),
    RETURN__ERROR(11016, "申请退货时已存在该退货记录"),
    RETURN_FINISH_ERROR(11017, "完成退货时无该退货记录"),
    TOKEN_ERROR(11018, "Token鉴权失败"),
    TIME_EXPIRE(11019, "请求时间戳过期"),
    MD5_ERROR(11020, "MD5生成失败"),
    DB_ERROR(11021, "数据库操作失败"),
    NOTIFY_ERROR(11022, "通知第三方失败"),
    PRIZE_ERROR(11023, "开通权益失败"),
    RETURN_PRODUCT_ERROR(11024, "申请退货/完成退货时产品数据不存在"),
    HEARD_EMPTY(11025, "请求头缺失"),
    TIMESTAMP_ERROR(11026, "时间戳格式错误"),
    REPEAT_REQUEST(11027, "重复请求");

    private Integer code;

    private String msg;

    OrderErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
