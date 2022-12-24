package org.stonexthree.domin.model;
import lombok.Data;
import lombok.Value;
import java.io.Serializable;

/**
 * @author stonexthree
 */
@Data
public class LabelDTO implements Serializable {
    private Integer id;
    private String name;
    @Override
    public String toString() {
        return "LabelDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
