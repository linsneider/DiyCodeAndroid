package com.sneider.diycode.event;

public class GetUnreadCountEvent {

    public boolean hasUnread;

    public GetUnreadCountEvent(boolean hasUnread) {
        this.hasUnread = hasUnread;
    }
}
