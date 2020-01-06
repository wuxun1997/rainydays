package com.wlf.order.prize.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderException extends RuntimeException {

    private int code;

    private String msg;

    public OrderException(){
        super();
    }

    public OrderException(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

}
