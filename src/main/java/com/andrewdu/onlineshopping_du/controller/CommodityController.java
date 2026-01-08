package com.andrewdu.onlineshopping_du.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.andrewdu.onlineshopping_du.db.dao.OnlineShoppingCommodityDao;
import com.andrewdu.onlineshopping_du.db.po.OnlineShoppingCommodity;
import com.andrewdu.onlineshopping_du.service.EsService;
import com.andrewdu.onlineshopping_du.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CommodityController {

    @Resource
    private OnlineShoppingCommodityDao commodityDao;
    @Resource
    SearchService searchService;
    @Resource
    EsService esService;

    @PostConstruct
    public void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();

        // 搜索接口限流
        FlowRule searchRule = new FlowRule();
        searchRule.setResource("searchRule");
        searchRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        searchRule.setCount(10);  // 每秒最多10次
        rules.add(searchRule);

        // 加载规则
        FlowRuleManager.loadRules(rules);
    }
    /**
     * 打开“新增商品的窗口”
     * GET /commodities/new
     */
    @GetMapping("/commodities/new")
    public String showCreateCommodityForm() {
        return "add_commodity";
    }

    /**
     * 新增商品
     * POST /commodities
     */
    @PostMapping("/commodities")
    public String createCommodity(
            @RequestParam("commodityId") long commodityId,
            @RequestParam("commodityName") String commodityName,
            @RequestParam("commodityDesc") String commodityDesc,
            @RequestParam("price") int price,
            @RequestParam("availableStock") int availableStock,
            @RequestParam("creatorUserId") long creatorUserId,
            Map<String, Object> model
    ) {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .totalStock(availableStock)
                .lockStock(0)
                .creatorUserId(creatorUserId)
                .build();

        commodityDao.insertCommodity(commodity);

        try {
            esService.addCommodity(commodity);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        model.put("commodity", commodity);

        return "add_commodity_success";
    }

    @GetMapping("searchAction")
    public String searchAction(@RequestParam("keyWord") String keyword, Map<String, Object> resultMap) throws IOException {

        try (Entry entry = SphU.entry("searchRule", EntryType.IN, 1, keyword)) {
            List<OnlineShoppingCommodity> commodityResult = searchService.searchCommodityWithEs(keyword);
            resultMap.put("itemList", commodityResult);
            return "search_items";

        }catch (BlockException e) {
            //被限流，返回提示
            resultMap.put("message", "搜索过于频繁，请稍后再试");
            return "wait";
        }
    }

    /**
     * 列出商品列表
     * GET /commodities
     * GET /commodities?sellerId=123
     */
    @GetMapping("/commodities")
    public String listCommodities(
            @RequestParam(value = "sellerId", required = false) Long sellerId,
            Map<String, Object> model
    ) {
        List<OnlineShoppingCommodity> commodityList;

        if (sellerId == null) {
            commodityList = commodityDao.listCommodities();
        } else {
            commodityList = commodityDao.listCommoditiesByUserId(sellerId);
        }

        model.put("commodityList", commodityList);
        return "list_commodities";
    }

    /**
     * 查看商品详细页
     * GET /commodities/{commodityId}
     */
    @GetMapping("/commodities/{commodityId}")
    public String getCommodityDetail(
            @PathVariable("commodityId") long commodityId,
            Map<String, Object> model
    ) {
        OnlineShoppingCommodity commodity = commodityDao.selectCommodityById(commodityId);

        model.put("commodity", commodity);
        return "commodity_detail";
    }

    @RequestMapping("/staticItem/{commodityId}")
    public String staticItemPage(@PathVariable("commodityId") long commodityId) {
        return "item_detail_" + commodityId;
    }
}
