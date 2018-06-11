package com.vn.ntsc.repository.model.chat;

/**
 * Created by ThoNh on 9/27/2017.
 */

public class FileMessage {
    public static final String FILE_PHOTO = "p";
    public static final String FILE_VIDEO = "v";
    public static final String FILE_AUDIO = "a";


    /**
     * Content of Message {@Link ChatMessage -> public String mContent;}
     */
    private String mContent;


    private String mName;
    private String mId;
    private String mType;
    private String mPath;
    /**
     * Nếu file là audio thì sẽ có thời gian
     */
    private long mAudioTime;

    /**
     * Truyền UserId vào đây để save file.
     * Nếu mình gửi thì truyền Id của người bên kia
     * Nếu người ta gửi thì truyền Id của mình
     * {@Link ChatMessage --> isOwn}
     */
    private String mUserId;
    public int progress = 0;


    public FileMessage() {

    }


    public FileMessage(String content, String userId) {
        this.mContent = content;
        this.mUserId = userId;
        parseFile(content);

    }

    // "content": "58cbc5a5e4b060cade54d9bb&59940baae4b0b096759cde29&20170927063933929|v|59cb47b5e4b0ae4d388dfdba|VIDEO0001.mp4", VIDEO
    // "content": "58cbc5a5e4b060cade54d9bb&59940baae4b0b096759cde29&20170927063925294|a|59cb479de4b0ae4d388dfdb8|5", AUDIO
    // "content": "58cbc5a5e4b060cade54d9bb&59940baae4b0b096759cde29&20170927045251679|p|59cb2ea6e4b0ae4d388dfdb7|1506487968706632395574.tmp", PHOTO

    private void parseFile(String content) {
        String[] split = content.split("\\|");
        mId = split[2];
//        mName = split[3];

        if (content.contains("|" + FILE_AUDIO + "|")) {
            mType = FILE_AUDIO;
            mAudioTime = Long.parseLong(split[3]) * 1000;

        } else if (content.contains("|" + FILE_PHOTO + "|")) {
            mType = FILE_PHOTO;

        } else if (content.contains("|" + FILE_VIDEO + "|")) {
            mType = FILE_VIDEO;
        }
    }

    public String getFileType() {
        return mType;
    }

    public String getFilePath() {
        return mPath;
    }

    public String getFileId() {
        return mId;
    }

    @Override
    public String toString() {
        return "FileMessage{" +
                "mContent='" + mContent + '\'' +
                ", mName='" + mName + '\'' +
                ", mId='" + mId + '\'' +
                ", mType='" + mType + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mAudioTime=" + mAudioTime +
                ", mUserId='" + mUserId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileMessage)) return false;

        FileMessage that = (FileMessage) o;

        if (mAudioTime != that.mAudioTime) return false;
        if (mContent != null ? !mContent.equals(that.mContent) : that.mContent != null)
            return false;
        if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
        if (mId != null ? !mId.equals(that.mId) : that.mId != null) return false;
        if (mType != null ? !mType.equals(that.mType) : that.mType != null) return false;
        if (mPath != null ? !mPath.equals(that.mPath) : that.mPath != null) return false;
        return mUserId != null ? mUserId.equals(that.mUserId) : that.mUserId == null;

    }

    @Override
    public int hashCode() {
        int result = mContent != null ? mContent.hashCode() : 0;
        result = 31 * result + (mName != null ? mName.hashCode() : 0);
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        result = 31 * result + (mType != null ? mType.hashCode() : 0);
        result = 31 * result + (mPath != null ? mPath.hashCode() : 0);
        result = 31 * result + (int) (mAudioTime ^ (mAudioTime >>> 32));
        result = 31 * result + (mUserId != null ? mUserId.hashCode() : 0);
        return result;
    }
}
