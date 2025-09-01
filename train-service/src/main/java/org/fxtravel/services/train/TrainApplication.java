package org.fxtravel.services.train;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TrainApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(TrainApplication.class, args);
    }
}
