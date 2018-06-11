package com.vn.ntsc.repository.model.chat;

/**
 * passing data into adapter for display view
 */
class Message {
    /**
     * to identify message
     */
    private int id;
    /**
     * avatar url of sender
     */
    private String avatarUrl;
    /**
     * name of sender
     */
    private String name;
    /**
     * last message
     */
    private String lastMessage;
    /**
     * when last message come
     */
    private String lastMessageTime;
    /**
     * unread messages number
     */
    private int bagde;
    /**
     * true: last message from me
     */
    private boolean isFromMe;

    public Message(int id, String avatarUrl, String name, String lastMessage, String lastMessageTime, int bagde, boolean isFromMe) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.bagde = bagde;
        this.isFromMe = isFromMe;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getBagde() {
        return bagde;
    }

    public void setBagde(int bagde) {
        this.bagde = bagde;
    }

    public boolean isFromMe() {
        return isFromMe;
    }

    public void setFromMe(boolean fromMe) {
        isFromMe = fromMe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;

        Message message = (Message) o;

        return id == message.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
