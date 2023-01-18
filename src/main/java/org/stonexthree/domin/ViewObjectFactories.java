package org.stonexthree.domin;

import org.stonexthree.domin.model.DocDTO;
import org.stonexthree.domin.model.DocVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class ViewObjectFactories {
    /**
     * @param docDTO   dto对象
     * @param nickname 文档作者的昵称名
     * @return
     */
    public static DocVO toVO(DocDTO docDTO, String nickname) {
        return new DocVO(docDTO.getDocId(), docDTO.getDocName(), docDTO.getDocLocation(), docDTO.getDocAuthor(),
                nickname, docDTO.getUpdateTimestamp());

    }

    /**
     * @param docDTOCollection dto集合
     * @param nicknames        用户名和昵称的映射
     * @return
     */
    public static List<DocVO> batchToVO(Collection<DocDTO> docDTOCollection, Map<String, String> nicknames) {
        List<DocVO> result = new ArrayList<>();
        docDTOCollection.stream().forEach((docDTO) -> {
            String nickname = nicknames.get(docDTO.getDocAuthor());
            nickname = nickname == null ? "用户" : nickname;
            DocVO vo = toVO(docDTO, nickname);
            result.add(vo);
        });
        return result;
    }
}
