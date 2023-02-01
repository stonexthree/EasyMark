package org.stonexthree.domin.composite;

import org.stonexthree.domin.model.Document;

import java.util.Set;

public interface SearchService {
    Set<Document> searchDoc(String scope, String keyword);
}
