package dr.mtr.passenger.model.newTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dr.mtr.passenger.utils.ObjectModel;

public class EONewTripRequest extends ObjectModel {

    @Expose
    @SerializedName("vehicle")
    private String vehicle;
    @Expose
    @SerializedName("droplng")
    private String droplng;
    @Expose
    @SerializedName("droplat")
    private String droplat;
    @Expose
    @SerializedName("dropaddress")
    private String dropaddress;
    @Expose
    @SerializedName("picklng")
    private String picklng;
    @Expose
    @SerializedName("picklat")
    private String picklat;
    @Expose
    @SerializedName("pickaddress")
    private String pickaddress;
    @Expose
    @SerializedName("userid")
    private String userid;
    @Expose
    @SerializedName("contact_number")
    private String contact_number;
    @Expose
    @SerializedName("isreserved")
    private String isreserved;
    @Expose
    @SerializedName("reservationtime")
    private String reservationtime;
    @Expose
    @SerializedName("domainid")
    private String domainid;
    @Expose
    @SerializedName("paymenttype")
    private String paymenttype;

    public EONewTripRequest() {
    }

    public String getDomainid() {
        return domainid;
    }

    public void setDomainid(String domainid) {
        this.domainid = domainid;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDroplng() {
        return droplng;
    }

    public void setDroplng(String droplng) {
        this.droplng = droplng;
    }

    public String getDroplat() {
        return droplat;
    }

    public void setDroplat(String droplat) {
        this.droplat = droplat;
    }

    public String getDropaddress() {
        return dropaddress;
    }

    public void setDropaddress(String dropaddress) {
        this.dropaddress = dropaddress;
    }

    public String getPicklng() {
        return picklng;
    }

    public void setPicklng(String picklng) {
        this.picklng = picklng;
    }

    public String getPicklat() {
        return picklat;
    }

    public void setPicklat(String picklat) {
        this.picklat = picklat;
    }

    public String getPickaddress() {
        return pickaddress;
    }

    public void setPickaddress(String pickaddress) {
        this.pickaddress = pickaddress;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getIsreserved() {
        return isreserved;
    }

    public void setIsreserved(String isreserved) {
        this.isreserved = isreserved;
    }

    public String getReservationtime() {
        return reservationtime;
    }

    public void setReservationtime(String reservationtime) {
        this.reservationtime = reservationtime;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

}
