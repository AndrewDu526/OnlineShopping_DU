package com.andrewdu.onlineshopping_du.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.DynamicTemplate;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import com.alibaba.fastjson.JSON;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EsService {

    public static final String COMMODITY_INDEX = "commodity";
    @Resource
    private ElasticsearchClient esClient;

    /**
     * 添加商品到 ES
     * 1. 检查 commodity 索引是否存在
     * 2. 不存在则创建索引（配置 IK 分词动态模板）
     * 3. 将商品文档插入 ES
     *
     * @param onlineShoppingCommodity 商品对象
     * @return HTTP 状态码（200=成功, 500=失败）
     */
    public int addCommodity(OnlineShoppingCommodity onlineShoppingCommodity) throws IOException {
        try {
            // 1. 检查索引是否存在
            boolean isExist = esClient.indices()
                    .exists(e -> e.index(COMMODITY_INDEX))  // e 是 ExistsRequest.Builder
                    .value();

            if (!isExist) {
                // 2. 创建索引
                Map<String, DynamicTemplate> templates = new HashMap<>();
                templates.put("strings", DynamicTemplate.of(dt -> dt
                        .matchMappingType("string")
                        .mapping(mp -> mp.text(t -> t.analyzer("ik_smart")))
                ));

                CreateIndexResponse response = esClient.indices().create(idx -> idx
                        .index(COMMODITY_INDEX)
                        .mappings(m -> m.dynamicTemplates(templates))
                );

                if (!response.acknowledged()) {
                    log.error("Failed to create ES Index: {}", COMMODITY_INDEX);
                    return 500;
                }

                log.info("Created ES Index: {}", COMMODITY_INDEX);
            }

            //3. 插入文档
            // 序列化商品对象为 JSON
            String jsonDoc = JSON.toJSONString(onlineShoppingCommodity);

            IndexResponse response = esClient.index(i -> i
                    .index(COMMODITY_INDEX)  // 索引名
                    .id(onlineShoppingCommodity.getCommodityId().toString())  // 文档 ID
                    .withJson(new StringReader(jsonDoc))  // JSON 数据（通过 Reader）
            );

            log.info("AddCommodity To ES, Commodity: {}, Result: {}", jsonDoc, response.result());
            // 返回状态码
            return response.result().jsonValue().equals("created") ? 201 : 200;

        } catch (Exception e) {
            log.error("Failed to add commodity to ES", e);
            return 500;
        }
    }

    /**
     * 搜索商品
     *
     * 逻辑：
     * 1. 使用 multi_match 查询（搜索 commodityName 和 commodityDesc）
     * 2. 分页返回结果
     * 3. 解析结果为商品对象列表
     *
     * @param keyword 搜索关键词
     * @param from 起始位置
     * @param size 返回数量
     * @return 商品列表
     */
    public List<OnlineShoppingCommodity> searchCommodity(String keyword, int from, int size) throws IOException {

        try {
            // 1. 构建并执行搜索
            SearchResponse<OnlineShoppingCommodity> response = esClient.search(s -> s
                            .index(COMMODITY_INDEX)  // 索引名
                            .query(q -> q  // 查询条件
                                    .multiMatch(m -> m  // 多字段匹配
                                            .query(keyword)  // 搜索关键词
                                            .fields("commodityName", "commodityDesc")  // 搜索字段
                                    )
                            )
                            .from(from)  // 分页起始位置
                            .size(size),  // 返回数量
                    OnlineShoppingCommodity.class  // 目标类型
            );

            List<OnlineShoppingCommodity> result = new ArrayList<>();

            for (Hit<OnlineShoppingCommodity> hit : response.hits().hits()) {
                OnlineShoppingCommodity commodity = hit.source();  // 直接获取对象
                result.add(commodity);
            }

            log.info("Search keyword: {}, Result count: {}", keyword, result.size());
            return result;

        } catch (Exception e) {
            log.error("Failed to search commodities", e);
            return new ArrayList<>();  // 返回空列表
        }
    }
}