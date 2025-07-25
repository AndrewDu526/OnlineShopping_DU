package com.andrewdu.onlineshopping_du;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.andrewdu.onlineshopping_du.db.mappers")
public class OnlineShoppingDuApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShoppingDuApplication.class, args);
    }

}
