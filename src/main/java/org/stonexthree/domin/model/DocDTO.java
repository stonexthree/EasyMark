package org.stonexthree.domin.model;

import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * @author stonexthree
 */
@Data
public class DocDTO implements Serializable {
    private String docId;
    private String docName;
    private String docLocation;
    private String docAuthor;
    private Long updateTimestamp;

    public DocDTO(String docId, String docName, String docLocation, String docAuthor,Long updateTimestamp) {
        this.docId = docId;
        this.docName = docName;
        this.docLocation = docLocation;
        this.docAuthor = docAuthor;
        this.updateTimestamp = updateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        DocDTO docDTO = (DocDTO) o;
        return Objects.equals(docId, docDTO.docId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId);
    }

    @Override
    public String toString() {
        return "DocDTO{" +
                "docId='" + docId + '\'' +
                ", docName='" + docName + '\'' +
                ", docLocation='" + docLocation + '\'' +
                ", docAuthor='" + docAuthor + '\'' +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }
}
