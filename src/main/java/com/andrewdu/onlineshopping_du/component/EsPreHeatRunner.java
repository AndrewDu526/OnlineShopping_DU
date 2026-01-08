package com.andrewdu.onlineshopping_du.component;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class EsPreHeatRunner implements ApplicationRunner {

    @Autowired
    private OnlineShoppingCommodityDao commodityDao;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========== Elasticsearch 预热 ==========");

        try {
            preHeatCommodityIndex();

        } catch (Exception e) {
            log.error("ES 预热失败", e);
        }
    }

    private void preHeatCommodityIndex() throws Exception {
        // 检查索引
        boolean exists = elasticsearchClient.indices()
                .exists(e -> e.index("commodity")).value();

        if (!exists) {
            elasticsearchClient.indices().create(c -> c.index("commodity"));
            log.info("开始创建 commodity 索引");
        }

        // 加载并索引数据
        List<OnlineShoppingCommodity> commodities = commodityDao.listCommodities();
        log.info("开始索引 {} 个商品", commodities.size());

        int count = 0;
        for (OnlineShoppingCommodity commodity : commodities) {
            if (commodity != null && commodity.getCommodityId() != null) {
                elasticsearchClient.index(i -> i
                        .index("commodity")
                        .id(String.valueOf(commodity.getCommodityId()))
                        .document(commodity)
                );
                count++;
            }
        }

        elasticsearchClient.indices().refresh(r -> r.index("commodity"));
        log.info("商品索引完成：{} 个", count);
    }
}