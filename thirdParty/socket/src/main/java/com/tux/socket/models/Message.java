package com.tux.socket.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.tux.socket.TimeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.List;

import static com.tux.socket.models.Message.MessageType.NOTI_BUZZ;

/**
 * Created by dev22 on 11/24/17.
 * base message struct
 */
@Keep
public class Message {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({Auth.TYPE_AUTH, Gift.TYPE_GIFT, Media.TYPE_FILE, MessageDeliveringState.TYPE_DELIVERING_STATE, Sticker.TYPE_STICKER, Text.TYPE_TEXT, Typing.TYPE_WRITING, NOTI_BUZZ})
    public @interface MessageType {
        String NOTI_BUZZ = "NOTIBUZZ";
    }

    private static Gson gson = null;

    /**
     * send msg to server
     */
    public static final String TO_SERVER = "server";

    /**
     * message type
     */
    @SerializedName("msg_type")
    private String messageType;
    /**
     * <p>- auth: token</p>
     * <p>- text: content of message</p>
     * <p>- start typing: sw</p>
     * <p>- stop typing: wt</p>
     * ............................
     */
    @SerializedName("value")
    private Object value;

    /**
     * sender id
     */
    @SerializedName("from")
    private String senderId;
    /**
     * receiver id
     */
    @SerializedName("to")
    private String receiverId;
    /**
     * current GMT time  in yyyyMMddHHmmssSSS
     */
    @SerializedName("origin_time")
    private String originTime;
    /**
     * message id format: from&to&originTime
     */
    @SerializedName("msg_id")
    private String id;

    /**
     * only type {@link MessageDeliveringState#TYPE_DELIVERING_STATE MDS}, to notify status of message
     *
     * @see MessageDeliveringState#VALUE_SENT_MESSAGE_SUCCESS
     */

    /**
     * only type {@link MessageDeliveringState#TYPE_DELIVERING_STATE}, to notify user login same account with you sent message success
     */
    @SerializedName("message_content")
    private String messageContent;

    @SerializedName("sticker_url")
    private String stickerUrl;

    @SerializedName("cat_id")
    private String catId;

    @SerializedName("files")
    private List<Media.FileBean> listFile;

    @SerializedName("total_unread")
    private int totalUnread;

    private String rawText;

    public Message() {

    }

    /**
     * create base message
     *
     * @param value      message value
     * @param senderId   user id of sender
     * @param receiverId receiver id
     */
    public Message(@Nullable String value, @NonNull String senderId, @NonNull String receiverId, @NonNull String messageType) {
        this.messageType = messageType;
        this.value = value;
        this.senderId = senderId;
        this.receiverId = receiverId;

        originTime = TimeUtils.getGMTDateTime();
        id = senderId + "&" + receiverId + "&" + originTime;
    }


    public Message(String stickerUrl, @Nullable String value, @NonNull String senderId, @NonNull String receiverId, @NonNull String messageType, String caiId) {
        this.messageType = messageType;
        this.stickerUrl = stickerUrl;
        this.catId = caiId;
        this.value = value;
        this.senderId = senderId;
        this.receiverId = receiverId;

        originTime = TimeUtils.getGMTDateTime();
        id = senderId + "&" + receiverId + "&" + originTime;
    }


    @MessageType
    public String getMessageType() {
        return messageType;
    }

    public String getStickerUrl() {
        return stickerUrl;
    }

    public void setStickerUrl(String stickerUrl) {
        this.stickerUrl = stickerUrl;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    @Nullable
    public String getValue() {
        return value!= null ? value.toString() : null;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getOriginTime() {
        return originTime;
    }

    public String getId() {
        return id;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public void setOriginTime(String originTime) {
        this.originTime = originTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setListFile(List<Media.FileBean> listFile) {
        this.listFile = listFile;
    }

    public List<Media.FileBean> getListFile() {
        return listFile;
    }

    public int getTotalUnread() {
        return totalUnread;
    }

    public void setTotalUnread(int totalUnread) {
        this.totalUnread = totalUnread;
    }

    @Nullable
    public String getMessageContent() {
        return messageContent;
    }

    public String toJson() {
        return getGsonInstance().toJson(this);
    }

    public static Message parse(String json) {
        return getGsonInstance().fromJson(json, Message.class);
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    /**
     * @return single instance of gson
     */
    private static Gson getGsonInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    // case field 'value' may be String or Object
                    .registerTypeAdapter(Object.class, new JsonDeserializer<Object>() {
                        @Override
                        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                            if (jsonElement.isJsonObject()) {
                                return jsonDeserializationContext.deserialize(jsonElement, Object.class);
                            } else {
                                return jsonElement.getAsString();
                            }
                        }
                    })
                    .create();
        }
        return gson;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType='" + messageType + '\'' +
                ", value='" + value + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", originTime='" + originTime + '\'' +
                ", id='" + id + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", listFile=" + listFile +
                ", total_Unread=" + totalUnread +
                '}';
    }
}
