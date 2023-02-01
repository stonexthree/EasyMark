package org.stonexthree.domin.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author stonexthree
 */
@Data
public class Document implements Serializable {
    private String docId;
    private String docName;
    private String docLocation;
    private String docAuthor;
    private Long updateTimestamp;
    private boolean draft;

    private Document(String docId, String docName, String docLocation, String docAuthor, Long updateTimestamp,Boolean draft) {
        this.docId = docId;
        this.docName = docName;
        this.docLocation = docLocation;
        this.docAuthor = docAuthor;
        this.updateTimestamp = updateTimestamp;
        this.draft = draft;
    }

    public static Document createDocument(String docId, String docName, String docLocation, String docAuthor, Long updateTimestamp){
        return new Document(docId,docName,docLocation,docAuthor,updateTimestamp,false);
    }

    public static Document createDraft(String docId, String docName, String docLocation, String docAuthor, Long updateTimestamp){
        return new Document(docId,docName,docLocation,docAuthor,updateTimestamp,true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Document document = (Document) o;
        return Objects.equals(docId, document.docId);
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
