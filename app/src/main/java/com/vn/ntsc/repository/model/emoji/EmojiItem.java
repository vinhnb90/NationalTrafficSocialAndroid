package com.vn.ntsc.repository.model.emoji;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ducng on 12/13/2017.
 */

public class EmojiItem {

    /**
     * code_lst : ["(ccc)",":D","$)"]
     * file_id : 5a41cf7441afe77e849b564f
     * name : ccc
     * emoji_id : 5a41cf7441afe777909b47a2
     * code : ["(ccc)",":D","$)"]
     * emoji_url : http://10.64.100.22:81/emoji/5a2ded8c41afe7398d1857bf/ccc.gif
     * file_type : gif
     */

    @SerializedName("file_id")
    private String fileId;
    @SerializedName("name")
    private String name;
    @SerializedName("emoji_id")
    private String emojiId;
    @SerializedName("code")
    private String code;
    @SerializedName("emoji_url")
    private String emojiUrl;
    @SerializedName("file_type")
    private String fileType;
    @SerializedName("code_lst")
    private List<String> codeLst;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(String emojiId) {
        this.emojiId = emojiId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<String> getCodeLst() {
        return codeLst;
    }

    public void setCodeLst(List<String> codeLst) {
        this.codeLst = codeLst;
    }
}
