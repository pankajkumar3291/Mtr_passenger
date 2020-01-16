package dr.mtr.passenger.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOLoginData implements Serializable {

    @Expose
    @SerializedName("standing")
    private String mStanding;
    @Expose
    @SerializedName("voucher_import")
    private String mVoucher_import;
    @Expose
    @SerializedName("company")
    private String mCompany;
    @Expose
    @SerializedName("gender")
    private String mGender;
    @Expose
    @SerializedName("dob")
    private String mDob;
    @Expose
    @SerializedName("passenger_current_version")
    private String mPassenger_current_version;
    @Expose
    @SerializedName("app_id")
    private String mApp_id;
    @Expose
    @SerializedName("SMSAlerts")
    private String mSMSAlerts;
    @Expose
    @SerializedName("registed_at")
    private String mRegisted_at;
    @Expose
    @SerializedName("available_credit")
    private String mAvailable_credit;
    @Expose
    @SerializedName("promotional_credit")
    private String mPromotional_credit;
    @Expose
    @SerializedName("simno")
    private String mSimno;
    @Expose
    @SerializedName("mobilenumber")
    private String mMobilenumber;
    @Expose
    @SerializedName("eminumber")
    private String mEminumber;
    @Expose
    @SerializedName("state")
    private String mState;
    @Expose
    @SerializedName("city")
    private String mCity;
    @Expose
    @SerializedName("status")
    private String mStatus;
    @Expose
    @SerializedName("token_code")
    private String mToken_code;
    @Expose
    @SerializedName("devicetype")
    private String mDevicetype;
    @Expose
    @SerializedName("domainid")
    private String mDomainid;
    @Expose
    @SerializedName("is_looged_in")
    private String mIs_looged_in;
    @Expose
    @SerializedName("profile_image")
    private String mProfile_image;
    @Expose
    @SerializedName("device_token")
    private String mDevice_token;
    @Expose
    @SerializedName("current_longitude")
    private String mCurrent_longitude;
    @Expose
    @SerializedName("current_latitude")
    private String mCurrent_latitude;
    @Expose
    @SerializedName("mobile_number")
    private String mMobile_number;
    @Expose
    @SerializedName("fav_driver")
    private String mFav_driver;
    @Expose
    @SerializedName("address")
    private String mAddress;
    @Expose
    @SerializedName("password")
    private String mPassword;
    @Expose
    @SerializedName("passenger_lname")
    private String mPassenger_lname;
    @Expose
    @SerializedName("passanger_name")
    private String mPassanger_name;
    @Expose
    @SerializedName("email_id")
    private String mEmail_id;
    @Expose
    @SerializedName("passanger_info_id")
    private String mPassanger_info_id;
    @Expose
    @SerializedName("allowCredit")
    private String allowCredit;

    public String getAllowCredit() {
        return allowCredit;
    }

    public void setAllowCredit(String allowCredit) {
        this.allowCredit = allowCredit;
    }

    public String getStanding() {
        return mStanding;
    }

    public void setStanding(String mStanding) {
        this.mStanding = mStanding;
    }

    public String getVoucher_import() {
        return mVoucher_import;
    }

    public void setVoucher_import(String mVoucher_import) {
        this.mVoucher_import = mVoucher_import;
    }

    public String getCompany() {
        return mCompany;
    }

    public void setCompany(String mCompany) {
        this.mCompany = mCompany;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public String getDob() {
        return mDob;
    }

    public void setDob(String mDob) {
        this.mDob = mDob;
    }

    public String getPassenger_current_version() {
        return mPassenger_current_version;
    }

    public void setPassenger_current_version(String mPassenger_current_version) {
        this.mPassenger_current_version = mPassenger_current_version;
    }

    public String getApp_id() {
        return mApp_id;
    }

    public void setApp_id(String mApp_id) {
        this.mApp_id = mApp_id;
    }

    public String getSMSAlerts() {
        return mSMSAlerts;
    }

    public void setSMSAlerts(String mSMSAlerts) {
        this.mSMSAlerts = mSMSAlerts;
    }

    public String getRegisted_at() {
        return mRegisted_at;
    }

    public void setRegisted_at(String mRegisted_at) {
        this.mRegisted_at = mRegisted_at;
    }

    public String getAvailable_credit() {
        return mAvailable_credit;
    }

    public void setAvailable_credit(String mAvailable_credit) {
        this.mAvailable_credit = mAvailable_credit;
    }

    public String getPromotional_credit() {
        return mPromotional_credit;
    }

    public void setPromotional_credit(String mPromotional_credit) {
        this.mPromotional_credit = mPromotional_credit;
    }

    public String getSimno() {
        return mSimno;
    }

    public void setSimno(String mSimno) {
        this.mSimno = mSimno;
    }

    public String getMobilenumber() {
        return mMobilenumber;
    }

    public void setMobilenumber(String mMobilenumber) {
        this.mMobilenumber = mMobilenumber;
    }

    public String getEminumber() {
        return mEminumber;
    }

    public void setEminumber(String mEminumber) {
        this.mEminumber = mEminumber;
    }

    public String getState() {
        return mState;
    }

    public void setState(String mState) {
        this.mState = mState;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getToken_code() {
        return mToken_code;
    }

    public void setToken_code(String mToken_code) {
        this.mToken_code = mToken_code;
    }

    public String getDevicetype() {
        return mDevicetype;
    }

    public void setDevicetype(String mDevicetype) {
        this.mDevicetype = mDevicetype;
    }

    public String getDomainid() {
        return mDomainid;
    }

    public void setDomainid(String mDomainid) {
        this.mDomainid = mDomainid;
    }

    public String getIs_looged_in() {
        return mIs_looged_in;
    }

    public void setIs_looged_in(String mIs_looged_in) {
        this.mIs_looged_in = mIs_looged_in;
    }

    public String getProfile_image() {
        return mProfile_image;
    }

    public void setProfile_image(String mProfile_image) {
        this.mProfile_image = mProfile_image;
    }

    public String getDevice_token() {
        return mDevice_token;
    }

    public void setDevice_token(String mDevice_token) {
        this.mDevice_token = mDevice_token;
    }

    public String getCurrent_longitude() {
        return mCurrent_longitude;
    }

    public void setCurrent_longitude(String mCurrent_longitude) {
        this.mCurrent_longitude = mCurrent_longitude;
    }

    public String getCurrent_latitude() {
        return mCurrent_latitude;
    }

    public void setCurrent_latitude(String mCurrent_latitude) {
        this.mCurrent_latitude = mCurrent_latitude;
    }

    public String getMobile_number() {
        return mMobile_number;
    }

    public void setMobile_number(String mMobile_number) {
        this.mMobile_number = mMobile_number;
    }

    public String getFav_driver() {
        return mFav_driver;
    }

    public void setFav_driver(String mFav_driver) {
        this.mFav_driver = mFav_driver;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getPassenger_lname() {
        return mPassenger_lname;
    }

    public void setPassenger_lname(String mPassenger_lname) {
        this.mPassenger_lname = mPassenger_lname;
    }

    public String getPassanger_name() {
        return mPassanger_name;
    }

    public void setPassanger_name(String mPassanger_name) {
        this.mPassanger_name = mPassanger_name;
    }

    public String getEmail_id() {
        return mEmail_id;
    }

    public void setEmail_id(String mEmail_id) {
        this.mEmail_id = mEmail_id;
    }

    public String getPassanger_info_id() {
        return mPassanger_info_id;
    }

    public void setPassanger_info_id(String mPassanger_info_id) {
        this.mPassanger_info_id = mPassanger_info_id;
    }
}
