package com.v5.test.worker.bean;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Created by roger on 14-3-4.
 */
public class User {
    private String id;

    private String nickname;

    private String mobile;

    private Integer sex;

    private String avatar;

    private String avatar_url;


    private String regSource;

    private String language;

    private Integer userType;

    private String hideTime;

    private String countrycode;


    private Integer mobileVerify;


    private Date createTime;


    private Timestamp lastLoginTime;


    private Long lastUpdateTime;

    private int appId;

    private String publicKey;

    private String cert;//证书

    private Integer conversation;

    public Integer getMobileVerify() {
        return mobileVerify;
    }

    public void setMobileVerify(Integer mobileVerify) {
        this.mobileVerify = mobileVerify;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getHideTime() {
        return hideTime;
    }

    public void setHideTime(String hideTime) {
        this.hideTime = hideTime;
    }

    private String[] tcpServer;
    private String[] fileServer;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    private String sessionId;

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String[] getTcpServer() {
        return tcpServer;
    }

    public void setTcpServer(String[] tcpServer) {
        this.tcpServer = tcpServer;
    }

    public String[] getFileServer() {
        return fileServer;
    }

    public void setFileServer(String[] fileServer) {
        this.fileServer = fileServer;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRegSource() {
        return regSource;
    }

    public void setRegSource(String regSource) {
        this.regSource = regSource;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public static String genPassword(String password, String salt) {
        return DigestUtils.md5Hex(password + salt);
    }

    public static String genSalt() {
        return DigestUtils.sha256Hex(Long.toHexString(System.currentTimeMillis()));
    }

    public boolean equals(Object o) {
        if(!(o instanceof User)) return false;
        User u = (User)o;

        return this.id.equals(u.getId());
    }

    public Integer getConversation() {
        return conversation;
    }

    public void setConversation(Integer conversation) {
        this.conversation = conversation;
    }

    public Locale getLocale() {
        return Locale.ENGLISH;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", sex=" + sex +
                ", avatar='" + avatar + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", regSource='" + regSource + '\'' +
                ", language='" + language + '\'' +
                ", userType=" + userType +
                ", hideTime='" + hideTime + '\'' +
                ", countrycode='" + countrycode + '\'' +
                ", mobileVerify=" + mobileVerify +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", appId=" + appId +
                ", publicKey='" + publicKey + '\'' +
                ", cert='" + cert + '\'' +
                ", conversation=" + conversation +
                ", tcpServer=" + Arrays.toString(tcpServer) +
                ", fileServer=" + Arrays.toString(fileServer) +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
