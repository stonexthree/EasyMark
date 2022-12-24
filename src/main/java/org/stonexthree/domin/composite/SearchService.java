package org.stonexthree.domin.composite;

import org.stonexthree.domin.model.DocDTO;

import java.util.Set;

public interface SearchService {
    Set<DocDTO> searchDoc(String scope,String keyword);
}
