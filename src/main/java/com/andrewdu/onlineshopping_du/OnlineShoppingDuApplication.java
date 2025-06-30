package com.andrewdu.onlineshopping_du;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.andrewdu.onlineshopping_du.db.mappers")
public class OnlineShoppingDuApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingDuApplication.class, args);
    }

}
