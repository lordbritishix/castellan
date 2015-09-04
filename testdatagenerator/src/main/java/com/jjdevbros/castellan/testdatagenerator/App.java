package com.jjdevbros.castellan.testdatagenerator;

import com.jjdevbros.castellan.common.model.DailySession;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {
    public static void main(String[] args) {
        log.info("hey!");
        System.out.println("Hello World!");

        DailySession session = new DailySession(Instant.now().toEpochMilli());
        System.out.println(session.isInSession(Instant.now().toEpochMilli()));
    }
}
