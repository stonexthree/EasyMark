package org.stonexthree.domin.statistics;

import java.io.IOException;
import java.util.Map;

/**
 * 总的统计服务
 */
public interface StatisticsService {
    /**
     * 刷新文档相关的统计
     */
    void refreshDocCount();

    /**
     * 获取统计数据存储对象
     * @return
     */
    CountDataHolder getDataHolder();

    /**
     * 获取文档统计执行对象
     * @return
     */
    DocCounter getDocCounter();

    /**
     * 获取标签使用排行
     * @param top
     * @return
     */
    Map.Entry<String,Integer>[] getLabelUsedCharts(int top);

    /**
     * 获取浏览量排行
     * @param top
     * @return
     */
    Map.Entry<String,Integer>[] getDocViewCharts(int top);

    /**
     * 获取用户创建文档排行
     * @param top
     * @return
     */
    Map.Entry<String,Integer>[] getUserCreateDocCharts(int top);

    /**
     * 移除文档相关的统计数据
     * @param docId
     */
    void removeDoc(String docId);

    /**
     * 统计数据持久化
     * @throws IOException
     */
    void saveData() throws IOException;
}
