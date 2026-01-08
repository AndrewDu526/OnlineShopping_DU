package com.andrewdu.onlineshopping_du.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public ElasticsearchClient esClient() {
        // 1. 创建低层 REST 客户端
        RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();

        // 2. 创建传输层（使用 Jackson 作为 JSON 映射器）
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // 3. 创建 ES 客户端
        return new ElasticsearchClient(transport);
    }
}