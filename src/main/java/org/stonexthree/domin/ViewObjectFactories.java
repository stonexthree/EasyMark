package org.stonexthree.domin;

import org.stonexthree.domin.model.Document;
import org.stonexthree.domin.model.DocVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class ViewObjectFactories {
    /**
     * @param document   dto对象
     * @param nickname 文档作者的昵称名
     * @return
     */
    public static DocVO toVO(Document document, String nickname) {
        return new DocVO(document.getDocId(), document.getDocName(), document.getDocLocation(), document.getDocAuthor(),
                nickname, document.getUpdateTimestamp());

    }

    /**
     * @param documentCollection dto集合
     * @param nicknames        用户名和昵称的映射
     * @return
     */
    public static List<DocVO> batchToVO(Collection<Document> documentCollection, Map<String, String> nicknames) {
        List<DocVO> result = new ArrayList<>();
        documentCollection.stream().forEach((docDTO) -> {
            String nickname = nicknames.get(docDTO.getDocAuthor());
            nickname = nickname == null ? "用户" : nickname;
            DocVO vo = toVO(docDTO, nickname);
            result.add(vo);
        });
        return result;
    }
}
