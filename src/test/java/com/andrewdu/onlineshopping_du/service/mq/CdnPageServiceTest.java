package com.andrewdu.onlineshopping_du.service.mq;

import com.andrewdu.onlineshopping_du.service.CdnPageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CdnPageServiceTest {
    @Resource
    CdnPageService cdnPageService;
    @Test
    void createHtml() {cdnPageService.createHtml(1);
    }
}