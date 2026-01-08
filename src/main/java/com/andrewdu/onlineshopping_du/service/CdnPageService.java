package com.andrewdu.onlineshopping_du.service;

import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CdnPageService {

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private OnlineShoppingCommodityDao commodityDao;

    public void createHtml(long commodityId) {
        PrintWriter writer = null;
        try {
            log.info("========== 开始生成静态页面 ==========");
            log.info("商品ID: {}", commodityId);

            // 1. 查询商品
            OnlineShoppingCommodity commodity = commodityDao.selectCommodityById(commodityId);

            if (commodity == null) {
                log.error("商品不存在: {}", commodityId);
                return;
            }

            log.info("查询到商品: {}", commodity.getCommodityName());

            // 2. 准备数据
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("commodity", commodity);

            Context context = new Context();
            context.setVariables(resultMap);

            // 3. 创建文件
            File file = new File("src/main/resources/templates/item_detail_" + commodityId + ".html");

            // 打印绝对路径
            log.info("文件路径: {}", file.getAbsolutePath());

            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (!parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                log.info("创建目录: {} -> {}", parentDir.getAbsolutePath(), created);
            }

            // 4. 写入文件
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item_detail", context, writer);
            writer.flush();  // 强制刷新到磁盘

            log.info("静态页面生成成功！");
            log.info("文件大小: {} 字节", file.length());
            log.info("检查目录: {}", file.getParentFile().getAbsolutePath());
            log.info("========== 生成完成 ==========");

        } catch (Exception e) {
            log.error("页面静态化异常 [commodityId={}]", commodityId, e);
            e.printStackTrace();  // 打印完整堆栈
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}