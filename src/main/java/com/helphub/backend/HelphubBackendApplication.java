package com.helphub.backend;

import com.helphub.backend.common.util.DateTimeUtils;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelphubBackendApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone(DateTimeUtils.VN_ZONE));
        SpringApplication.run(HelphubBackendApplication.class, args);
    }
}
