package org.stonexthree.domin.statistics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewAdd {
    /**
     * 代表docId的参数名
     * @return
     */
    String id();
}
