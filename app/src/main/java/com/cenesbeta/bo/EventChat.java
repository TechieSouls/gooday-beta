package com.cenesbeta.bo;

import com.google.gson.annotations.SerializedName;

public class EventChat {

    private Integer eventChatId;

    private String chat;

    private Integer eventId;

    private Integer senderId;

    private Long createdAt;

    private String chatStatus = "Sent";

    private String chatEdited = "No";

    private User user;

    public Integer getEventChatId() {
        return eventChatId;
    }

    public void setEventChatId(Integer eventChatId) {
        this.eventChatId = eventChatId;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(String chatStatus) {
        this.chatStatus = chatStatus;
    }

    public String getChatEdited() {
        return chatEdited;
    }

    public void setChatEdited(String chatEdited) {
        this.chatEdited = chatEdited;
    }
}
