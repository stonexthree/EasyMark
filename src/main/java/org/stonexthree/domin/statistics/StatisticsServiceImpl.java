package org.stonexthree.domin.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stonexthree.persistence.ObjectPersistenceHandler;
import org.stonexthree.persistence.PersistenceManager;

import java.io.IOException;
import java.util.Map;

@Component
public class StatisticsServiceImpl implements StatisticsService{

    private ObjectPersistenceHandler<CountDataHolder> persistenceHandler;
    private CountDataHolder countDataHolder;

    private DocCounter docCounter;

    public StatisticsServiceImpl(@Value("${app-config.storage.persistence.file.count}") String saveFile,
                                 PersistenceManager persistenceManager,
                                 DocCounter docCounter
                                 ) throws IOException {
        this.persistenceHandler = persistenceManager.getHandler(saveFile,CountDataHolder::new);
        countDataHolder = persistenceHandler.readObject();
        this.docCounter = docCounter;
    }

    @Override
    @Scheduled(cron = "0 0 * * * ? ")//每小时刷新一次
    public void refreshDocCount() {
        this.docCounter.refreshDocViewCharts(countDataHolder);
        this.docCounter.refreshLabelUsedCharts(countDataHolder);
        this.docCounter.refreshUserCreateDocCharts(countDataHolder);
    }

    @Override
    public CountDataHolder getDataHolder() {
        return this.countDataHolder;
    }

    @Override
    public DocCounter getDocCounter() {
        return this.docCounter;
    }

    @Scheduled(cron = "0 * * * * ? ")//每分钟存储一次统计数据
    public void saveData() throws IOException {
        persistenceHandler.writeObject(countDataHolder);
    }


    /**
     * 获取标签使用排行
     * @return
     */
    @Override
    public Map.Entry<String,Integer>[] getLabelUsedCharts(int top){
        return countDataHolder.getCharts(CountDataHolder.ChartType.LABEL_USED,top);
    }

    /**
     * 获取文档浏览量排行
     * @return
     */
    @Override
    public Map.Entry<String,Integer>[] getDocViewCharts(int top){
        return countDataHolder.getCharts(CountDataHolder.ChartType.VIEWS,top);
    }

    /**
     * 获取用户创建文档排行
     * @return
     */
    @Override
    public Map.Entry<String,Integer>[] getUserCreateDocCharts(int top){
        return countDataHolder.getCharts(CountDataHolder.ChartType.USER_CREATE_DOC,top);
    }

    @Override
    public void removeDoc(String docId){
        this.docCounter.removeDoc(countDataHolder,docId);
    }
}
