package org.stonexthree.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.UserService;
import org.stonexthree.domin.statistics.StatisticsService;
import org.stonexthree.utils.FieldMapper;
import org.stonexthree.web.utils.CommonResponse;
import org.stonexthree.web.utils.ErrorCodeUtil;
import org.stonexthree.web.utils.RestResponseFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计功能相关的控制器
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private StatisticsService statisticsService;
    private UserService userService;
    private DocService docService;

    public StatisticsController(StatisticsService statisticsService,
                                UserService userService,
                                DocService docService) {
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.docService = docService;
    }

    /**
     * 获取站内文档总数
     *
     * @return
     */
    @GetMapping("/doc-num")
    public CommonResponse getDocNums() {
        return RestResponseFactory.createSuccessResponseWithData(docService.listAllDoc().size());
    }

    /**
     * 获取排行榜前10名
     *
     * @param name
     * @return
     */
    @GetMapping("/charts/{type}")
    public CommonResponse getLabelCharts(@PathVariable("type") String name,
                                         @RequestParam(value = "useDictionary",
                                                 required = false,
                                                 defaultValue = "false") String useDictionaryString) {
        Map.Entry<String, Integer>[] charts;
        Map<String, String> dictionary;
        boolean useDictionary = Boolean.parseBoolean(useDictionaryString);
        switch (name) {
            case "label" -> {
                charts = statisticsService.getLabelUsedCharts(10);
                dictionary = new HashMap<>();
            }
            case "doc-view" -> {
                charts = statisticsService.getDocViewCharts(10);
                dictionary = docService.getIdNameMap();
            }
            case "doc-create" -> {
                charts = statisticsService.getUserCreateDocCharts(10);
                dictionary = userService.getUserNicknameMap();
            }
            default -> throw new IllegalArgumentException("不支持的排行榜类型");
        }
        if (useDictionary) {
            return RestResponseFactory.createSuccessResponseWithData(FieldMapper.entryArrayKeyMapper(charts, dictionary));
        }
        return RestResponseFactory.createSuccessResponseWithData(charts);
    }

    /**
     * 手动刷新排行榜
     *
     * @return
     */
    @PostMapping("/refresh")
    public CommonResponse refreshCharts() {
        if (!userService.hasRoleAdmin()) {
            throw new AccessDeniedException("请求的操作仅管理员可用");
        }
        statisticsService.refreshDocCount();
        return RestResponseFactory.createSuccessResponse();
    }

    /**
     * 手动落盘排行榜数据
     *
     * @return
     * @throws IOException
     */
    @PostMapping("/save")
    public CommonResponse save() throws IOException {
        if (!userService.hasRoleAdmin()) {
            throw new AccessDeniedException("请求的操作仅管理员可用");
        }
        statisticsService.saveData();
        return RestResponseFactory.createSuccessResponse();
    }

}
