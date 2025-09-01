package org.fxtravel.services.hotel;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HotelApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(HotelApplication.class, args);
    }
}
