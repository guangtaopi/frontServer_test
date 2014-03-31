package com.v5.test.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by piguangtao on 14-2-18.
 */
public class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws Exception {
        log.info("server main start.");

        AbstractXmlApplicationContext applicationContext =
                new ClassPathXmlApplicationContext("conf/main.xml");

        applicationContext.registerShutdownHook();

        log.info("server main ok.");
    }

}

