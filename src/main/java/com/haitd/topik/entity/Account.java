package com.haitd.topik.entity;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;

import java.io.Serializable;

public class Account implements Serializable {
    private String level;
    private String emailName;
    private String emailDomain;
    private String fullName;
    private String koreanName;
    private String yearOfBirth;
    private String monthOfBirth;
    private String dayOfBirth;
    private String sex;
    private String sid;
    private String job;
    private String phone;
    private String address;
    private String imageId;
    private String password;
    private boolean isDone;
    private String status;
    private String ipAddress;
    private String instanceId;
    private BasicCookieStore basicCookieStore;
    private String captcha;
    private transient HttpPost httpPost;

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEmailName() {
        return this.emailName;
    }

    public void setEmailName(String emailName) {
        this.emailName = emailName;
    }

    public String getEmailDomain() {
        return this.emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getKoreanName() {
        return this.koreanName;
    }

    public void setKoreanName(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getYearOfBirth() {
        return this.yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getMonthOfBirth() {
        return this.monthOfBirth;
    }

    public void setMonthOfBirth(String monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public String getDayOfBirth() {
        return this.dayOfBirth;
    }

    public void setDayOfBirth(String dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageId() {
        return this.imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public HttpPost getHttpPost() {
        return this.httpPost;
    }

    public void setHttpPost(HttpPost httpPost) {
        this.httpPost = httpPost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public BasicCookieStore getBasicCookieStore() {
        return basicCookieStore;
    }

    public void setBasicCookieStore(BasicCookieStore basicCookieStore) {
        this.basicCookieStore = basicCookieStore;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String toString() {
        return "Account{level='" + this.level + "', emailName='" + this.emailName + "', emailDomain='" + this.emailDomain + "', fullName='" + this.fullName + "', koreanName='" + this.koreanName + "', yearOfBirth='" + this.yearOfBirth + "', monthOfBirth='" + this.monthOfBirth + "', dayOfBirth='" + this.dayOfBirth + "', sex='" + this.sex + "', sid='" + this.sid + "', job='" + this.job + "', phone='" + this.phone + "', address='" + this.address + "'}";
    }
}
