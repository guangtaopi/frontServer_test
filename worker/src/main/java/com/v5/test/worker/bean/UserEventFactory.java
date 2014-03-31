package com.v5.test.worker.bean;

import com.lmax.disruptor.EventFactory;

/**
 * Created by piguangtao on 14-2-19.
 */
public class UserEventFactory implements EventFactory<String> {


    @Override
    public String newInstance() {
        return null;
    }
}
