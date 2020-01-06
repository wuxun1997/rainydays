package com.wlf.order.api.util;

public enum LogisticsStatus {
    STORE_NOT_SEND("0"), STORE_SENDING("1"), STORE_HAS_SEDT("2"), STORE_RECEIVE("3"), STORE_OUT("4");

    private String logisticsStatus;

    LogisticsStatus(String logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public String getStatus() {
        return logisticsStatus;
    }
}
