package com.wlf.order.prize.util;

import java.util.LinkedList;

/**
 * 专门存放时间戳的缓存列表，先进先出队列
 *
 * @author wulinfeng
 * @since  2019/12/25
 * @param <Long>
 */
public class TimeStampList<Long> extends LinkedList<Long> {

    private int capacity;

    public TimeStampList(int capacity){
        super();
        this.capacity = capacity;
    }

    @Override
    public boolean add(Long timestamp){
        if(size() + 1 > capacity){
            super.removeFirst();
        }

        return super.add(timestamp);
    }
}
