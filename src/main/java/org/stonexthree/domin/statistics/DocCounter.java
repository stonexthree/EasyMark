package org.stonexthree.domin.statistics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.DocService;
import org.stonexthree.domin.LabelService;
import org.stonexthree.domin.model.Document;

import java.time.Instant;
import java.util.*;

/**
 * 进行文档相关的统计
 */
@Slf4j
@Component
public class DocCounter {
    private DocService docService;
    private LabelService labelService;

    public DocCounter(DocService docService,LabelService labelService) {
        this.docService = docService;
        this.labelService = labelService;
    }

    /**
     * 初始化浏览量统计
     */
    @Async
    public void initDocViewCount(CountDataHolder countDataHolder) {
        Set<Document> documents = docService.listAllDoc();
        Map<String, Integer> count = new HashMap<>(documents.size());
        documents.forEach(document -> count.put(document.getDocId(), 0));
        countDataHolder.loadDocViewCount(count);
    }

    /**
     * 浏览量增加
     */
    @Async
    public void viewAdd(CountDataHolder countDataHolder, String docId) {
        countDataHolder.viewAdd(docId);
    }

    /**
     * 更新浏览量排行,更新的时候会移除已经不在系统中的文档的信息
     */
    @Async
    public void refreshDocViewCharts(CountDataHolder countDataHolder) {
        String start = Instant.now().toString();
        HashMap<String, Integer> docViewCountMap = countDataHolder.getDocViewCount();
        docViewCountMap.keySet().retainAll(docService.listAllDocId());
        Map.Entry<String, Integer>[] result = countDataHolder.getDocViewCount().entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                .toArray(Map.Entry[]::new);
        countDataHolder.loadCharts(CountDataHolder.ChartType.VIEWS, result);
        String end = Instant.now().toString();
        log.info("任务：refreshDocViewList：\n" + start + " -- " + end);
    }
    /**
     * 更新标签使用排行
     */
    @Async
    public void refreshLabelUsedCharts(CountDataHolder countDataHolder) {
        String start = Instant.now().toString();
      Map.Entry<String,Integer>[] result = labelService.getLabelUsedCount().entrySet().stream()
                .sorted((entry1,entry2)-> entry2.getValue()-entry1.getValue())
                .toArray(Map.Entry[]::new);
        countDataHolder.loadCharts(CountDataHolder.ChartType.LABEL_USED,result);
        String end = Instant.now().toString();
        log.info("任务：refreshLabelUsedList：\n" + start + " -- " + end);
    }

    /**
     * 更新用户创建文档数排行
     * @param countDataHolder
     */
    @Async
    public void refreshUserCreateDocCharts(CountDataHolder countDataHolder) {
        String start = Instant.now().toString();
        Map.Entry<String,Integer>[] result = docService.listUserCreateDocCount().entrySet().stream()
                .sorted((entry1,entry2)-> entry2.getValue()-entry1.getValue())
                .toArray(Map.Entry[]::new);
        countDataHolder.loadCharts(CountDataHolder.ChartType.USER_CREATE_DOC,result);
        String end = Instant.now().toString();
        log.info("任务：refreshUserCreateDocList：\n" + start + " -- " + end);
    }
}
