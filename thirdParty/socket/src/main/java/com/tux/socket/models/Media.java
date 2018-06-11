package com.tux.socket.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by dev22 on 11/25/17.
 * send audio, video, photo
 */
@Keep
public class Media extends Message {
    public static final String TYPE_FILE = "FILE";

    /**
     * thumbnail_height : 3.2
     * original_url : http://10.64.100.22:81/image/original_image/201712/01/4d83699c-4092-4548-926f-2b364ce53f35.jpg
     * thumbnail_width : 3.2
     * thumbnail_url : http://10.64.100.22:81/image/thumbnail/201712/01/e449850a-d246-493e-9820-a7499b33c058.jpg
     * file_id : 5a20d14941afe76c691addbc
     * original_height : 960.0
     * original_width : 960.0
     * file_type : image
     */
    @Keep
    public static class FileBean implements Parcelable {
        public static final String FILE_TYPE_IMAGE = "image";
        public static final String FILE_TYPE_AUDIO = "audio";
        public static final String FILE_TYPE_VIDEO = "video";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({FILE_TYPE_IMAGE, FILE_TYPE_AUDIO, FILE_TYPE_VIDEO})
        public @interface FileType {
        }

        /**
         * origin for image
         */
        @SerializedName("original_url")
        public String originalUrl;
        @SerializedName("thumbnail_url")
        public String thumbnailUrl;
        @SerializedName("file_id")
        public String fileId;
        /**
         * show audio and video source
         */
        @FileType
        @SerializedName("file_type")
        public String fileType;
        @SerializedName("file_url")
        public String fileUrl;
        /**
         * duration of video, audio
         */
        @SerializedName("file_duration")
        public int fileDuration;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.originalUrl);
            dest.writeString(this.thumbnailUrl);
            dest.writeString(this.fileId);
            dest.writeString(this.fileType);
            dest.writeString(this.fileUrl);
            dest.writeInt(this.fileDuration);
        }

        public FileBean() {
        }

        protected FileBean(Parcel in) {
            this.originalUrl = in.readString();
            this.thumbnailUrl = in.readString();
            this.fileId = in.readString();
            this.fileType = in.readString();
            this.fileUrl = in.readString();
            this.fileDuration = in.readInt();
        }

        public static final Creator<FileBean> CREATOR = new Creator<FileBean>() {
            @Override
            public FileBean createFromParcel(Parcel source) {
                return new FileBean(source);
            }

            @Override
            public FileBean[] newArray(int size) {
                return new FileBean[size];
            }
        };

        /**
         * @return origin url of {@link #FILE_TYPE_IMAGE}
         * @see #getThumbnailUrl()
         */
        public String getOriginalUrl() {
            return originalUrl;
        }

        /**
         * @return thumbnail url of {@link #FILE_TYPE_IMAGE}
         */
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        /**
         * @return file id when upload success
         */
        public String getFileId() {
            return fileId;
        }

        /**
         * @return file type {@link #FILE_TYPE_IMAGE},{@link #FILE_TYPE_AUDIO}, {@link #FILE_TYPE_VIDEO}
         */
        @FileType
        public String getFileType() {
            return fileType;
        }

        /**
         * @return direct link to file {@link #FILE_TYPE_VIDEO}, {@link #FILE_TYPE_AUDIO}
         */
        public String getFileUrl() {
            return fileUrl;
        }


        @Override
        public String toString() {
            return "FileBean{" +
                    "originalUrl='" + originalUrl + '\'' +
                    ", thumbnailUrl='" + thumbnailUrl + '\'' +
                    ", fileId='" + fileId + '\'' +
                    ", fileType='" + fileType + '\'' +
                    ", fileUrl='" + fileUrl + '\'' +
                    '}';
        }
    }

    public Media(String senderId, @NonNull String receiverId, List<FileBean> listFile) {
        super(null, senderId, receiverId, TYPE_FILE);
        setListFile(listFile);
    }
}
