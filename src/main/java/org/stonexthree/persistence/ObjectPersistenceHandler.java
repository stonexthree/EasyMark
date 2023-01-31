package org.stonexthree.persistence;

import java.io.IOException;

/**
 * 持久化工具
 * @author stonexthree
 * @param <T> 要持久化的对象的类型
 */
public interface ObjectPersistenceHandler<T> {
    /**
     * 写入对象到文件
     * @param object
     */
    void writeObject(T object) throws IOException;

    /**
     * 从文件读取对象
     * @return
     */
    T readObject() throws IOException;
}
