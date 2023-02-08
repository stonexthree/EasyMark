package org.stonexthree.domin.statistics;

import org.stonexthree.domin.model.Document;

import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 保存统计数据，以及排行榜
 */
public class CountDataHolder implements Serializable {
    /**
     * 排行榜分类
     */
    public enum ChartType{
        /**
         * 文档对浏览量排行
         */
        VIEWS,
        /**
         * 标签使用排行
         */
        LABEL_USED,
        /**
         * 用户创建文档排行
         */
        USER_CREATE_DOC
    }

    /**
     * 浏览量统计,记录每个文档的浏览量，实时更新
     */
    private HashMap<String,Integer> docViewCount;

    /**
     * 文档浏览量排行，更根据 docViewCount 定时统计；
     */
    private ArrayList< AbstractMap.SimpleEntry<String,Integer>> docViewsCharts;

    /**
     * 热门标签，定时统计。根据各标签被文档标记的次数排行
     */
    private ArrayList< AbstractMap.SimpleEntry<String,Integer>> labelUsedCharts;

    /**
     * 用户创建文档数排行。定时刷新。
     */
    private ArrayList< AbstractMap.SimpleEntry<String,Integer>> userCreateDocCharts;

    public CountDataHolder() {
        this.docViewCount = new HashMap<>();
        this.docViewsCharts = new ArrayList<>();
        this.labelUsedCharts = new ArrayList<>();
        this.userCreateDocCharts = new ArrayList<>();
    }

    /**
     * 加载浏览量数据
     * @param param
     */
    public void loadDocViewCount(Map<String,Integer> param){
        this.docViewCount = new HashMap<>();
        docViewCount.putAll(param);
    }

    /**
     * 设置排行榜数据
     * @param type
     * @param array
     */
    public void loadCharts(ChartType type,Map.Entry<String,Integer>[] array){
        ArrayList< AbstractMap.SimpleEntry<String,Integer>> target;
        switch (type){
            case VIEWS -> target = this.docViewsCharts;
            case LABEL_USED -> target = this.labelUsedCharts;
            case USER_CREATE_DOC -> target = this.userCreateDocCharts;
            default -> throw new IllegalArgumentException("");
        }
        target.clear();
        for(Map.Entry<String,Integer> entry: array){
            target.add(new AbstractMap.SimpleEntry<>(entry));
        }
    }

    /**
     * 获取排行榜信息
     * @param type 排行榜类型
     * @param top 排行榜长度
     * @return 返回排行榜的前 top 位
     */
    public Map.Entry[] getCharts(ChartType type,int top){
        ArrayList< AbstractMap.SimpleEntry<String,Integer>> target;
        switch (type){
            case VIEWS -> target = this.docViewsCharts;
            case LABEL_USED -> target = this.labelUsedCharts;
            case USER_CREATE_DOC -> target = this.userCreateDocCharts;
            default -> throw new IllegalArgumentException("");
        }
        return target.stream().limit(top).toArray(Map.Entry[]::new);
    }

    /**
     * 增加浏览量
     * @param docId
     */
    synchronized public void viewAdd(String docId){
        Integer counts = docViewCount.get(docId);
        if(counts!=null){
            docViewCount.put(docId,counts+1);
            return;
        }
        docViewCount.put(docId,1);
    }

    /**
     * 当移除文档时，调用次方法。会在统计信息中移除对应的文档信息
     * @param document
     */
    synchronized public void removeDoc(Document document){
        docViewCount.remove(document);
    }

    /**
     * 获取浏览量对象
     * @return
     */
    public HashMap<String, Integer> getDocViewCount() {
        return docViewCount;
    }
}
