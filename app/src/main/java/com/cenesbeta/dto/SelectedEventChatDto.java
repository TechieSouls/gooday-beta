package com.cenesbeta.dto;

public class SelectedEventChatDto {

    private boolean showChatWindow;
    private Integer eventChatId;
    private String message;

    public boolean isShowChatWindow() {
        return showChatWindow;
    }

    public void setShowChatWindow(boolean showChatWindow) {
        this.showChatWindow = showChatWindow;
    }

    public Integer getEventChatId() {
        return eventChatId;
    }

    public void setEventChatId(Integer eventChatId) {
        this.eventChatId = eventChatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
