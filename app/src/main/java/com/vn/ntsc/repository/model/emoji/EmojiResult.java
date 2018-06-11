package com.vn.ntsc.repository.model.emoji;

import java.util.List;

/**
 * Created by dev22 on 1/16/18.
 */

public class EmojiResult {
    public String categoryId;
    public String emojiId;
    public String emojiUrl;
    public String emojiPath;
    public List<String> codeLst;
    public String name;
    public String fileType;
    public int version;

    public EmojiResult(String categoryId, String emojiId, String emojiUrl, String emojiPath, List<String> codeLst, String fileType, String name, int version) {
        this.categoryId = categoryId;
        this.emojiId = emojiId;
        this.emojiUrl = emojiUrl;
        this.emojiPath = emojiPath;
        this.codeLst = codeLst;
        this.fileType = fileType;
        this.name = name;
        this.version = version;
    }
}
