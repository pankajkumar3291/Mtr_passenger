package dr.mtr.passenger.model.activeTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOActiveTripData implements Serializable {

    @SerializedName("tripid")
    @Expose
    private String tripid;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("taxi_plate_number")
    @Expose
    private String taxiPlateNumber;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("driverid")
    @Expose
    private String driverid;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_rating")
    @Expose
    private String driverRating;
    @SerializedName("estimated_fare")
    @Expose
    private String estimatedFare;
    @SerializedName("driver_image")
    @Expose
    private String driverImage;
    @SerializedName("driver_phone_number")
    @Expose
    private String driverPhoneNumber;
    @SerializedName("driver_current_lat")
    @Expose
    private String driverCurrentLat;
    @SerializedName("driver_current_lng")
    @Expose
    private String driverCurrentLng;
    @SerializedName("plat")
    @Expose
    private String plat;
    @SerializedName("plong")
    @Expose
    private String plong;
    @SerializedName("dlat")
    @Expose
    private String dlat;
    @SerializedName("dlong")
    @Expose
    private String dlong;

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaxiPlateNumber() {
        return taxiPlateNumber;
    }

    public void setTaxiPlateNumber(String taxiPlateNumber) {
        this.taxiPlateNumber = taxiPlateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDriverid() {
        return driverid;
    }

    public void setDriverid(String driverid) {
        this.driverid = driverid;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(String estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public String getDriverImage() {
        return driverImage;
    }

    public void setDriverImage(String driverImage) {
        this.driverImage = driverImage;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverCurrentLat() {
        return driverCurrentLat;
    }

    public void setDriverCurrentLat(String driverCurrentLat) {
        this.driverCurrentLat = driverCurrentLat;
    }

    public String getDriverCurrentLng() {
        return driverCurrentLng;
    }

    public void setDriverCurrentLng(String driverCurrentLng) {
        this.driverCurrentLng = driverCurrentLng;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getPlong() {
        return plong;
    }

    public void setPlong(String plong) {
        this.plong = plong;
    }

    public String getDlat() {
        return dlat;
    }

    public void setDlat(String dlat) {
        this.dlat = dlat;
    }

    public String getDlong() {
        return dlong;
    }

    public void setDlong(String dlong) {
        this.dlong = dlong;
    }

}
