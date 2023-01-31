package org.stonexthree.persistence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.DiscussionTopic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Component
@Slf4j
public class PersistenceManager {
    String baseDirName;

    public PersistenceManager(@Value("${app-config.storage.base-dir}")String baseDirName) {
        //File projectPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
        this.baseDirName = baseDirName;
    }

    public File getBaseDir(){
        return new File(baseDirName);
    }

    /**
     * 创建一个持久化处理对象
     * @param saveFileName 持久化到本地到文件名称，这个文件会放在 getBaseDir() 对应到路径下
     * @param supplier 当从文件中读取对象失败时，返回一个默认的对象的逻辑
     * @return
     * @param <T>
     * @throws IOException
     */
    public <T> ObjectPersistenceHandler<T> getHandler(String saveFileName, Supplier<T> supplier) throws IOException {
        return new ObjectPersistenceHandler<T>() {
            private  File saveFile ;
            {
                saveFile = new File(getBaseDir(),saveFileName);
                if(!saveFile.isFile()){
                    saveFile.createNewFile();
                }
            }
            @Override
            public void writeObject(T object) throws IOException {
                if(!saveFile.isFile()){
                    saveFile.createNewFile();
                }
                try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(saveFile))){
                    os.writeObject(object);
                }
            }

            @Override
            public T readObject() throws IOException {
                if(!saveFile.isFile()){
                    saveFile.createNewFile();
                    return supplier.get();
                }
                T result;
                try(ObjectInputStream is = new ObjectInputStream(new FileInputStream(saveFile))){
                    result =(T) is.readObject();
                }catch (EOFException eofException){
                    log.warn("空文件："+saveFileName);
                    result = supplier.get();
                }
                catch (Exception e){
                    e.printStackTrace();
                    result = supplier.get();
                }
                return result;
            }
        };
    }
}
