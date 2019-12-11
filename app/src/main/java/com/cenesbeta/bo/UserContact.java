package com.cenesbeta.bo;

public class UserContact {

    private Integer userContactId;

    private Integer userId;

    private Integer friendId;

    private String phone;

    private String name;

    private String cenesMember;

    public Integer getUserContactId() {
        return userContactId;
    }

    public void setUserContactId(Integer userContactId) {
        this.userContactId = userContactId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFriendId() {
        return friendId;
    }

    public void setFriendId(Integer friendId) {
        this.friendId = friendId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCenesMember() {
        return cenesMember;
    }

    public void setCenesMember(String cenesMember) {
        this.cenesMember = cenesMember;
    }
}
