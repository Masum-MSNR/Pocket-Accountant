package com.me.pa.models;

public class UserAccount {
    String phoneNumber, fullName, language, image;
    boolean completeAccount;

    public UserAccount() {
    }

    public UserAccount(String phoneNumber, String fullName, String language, String image, boolean completeAccount) {
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.language = language;
        this.image = image;
        this.completeAccount = completeAccount;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isCompleteAccount() {
        return completeAccount;
    }

    public void setCompleteAccount(boolean completeAccount) {
        this.completeAccount = completeAccount;
    }
}

