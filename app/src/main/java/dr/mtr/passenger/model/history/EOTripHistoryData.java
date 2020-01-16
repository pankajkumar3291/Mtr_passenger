package dr.mtr.passenger.model.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOTripHistoryData implements Serializable {

    @SerializedName("pickupaddress")
    @Expose
    private String pickupaddress;
    @SerializedName("dropoffaddress")
    @Expose
    private String dropoffaddress;
    @SerializedName("pickuptime")
    @Expose
    private String pickuptime;
    @SerializedName("requestedcar")
    @Expose
    private String requestedcar;
    @SerializedName("driverno")
    @Expose
    private Object driverno;
    @SerializedName("driverimg")
    @Expose
    private String driverimg;
    @SerializedName("plateno")
    @Expose
    private String plateno;
    @SerializedName("Fare")
    @Expose
    private String fare;
    @SerializedName("status")
    @Expose
    private String status;

    public String getPickupaddress() {
        return pickupaddress;
    }

    public void setPickupaddress(String pickupaddress) {
        this.pickupaddress = pickupaddress;
    }

    public String getDropoffaddress() {
        return dropoffaddress;
    }

    public void setDropoffaddress(String dropoffaddress) {
        this.dropoffaddress = dropoffaddress;
    }

    public String getPickuptime() {
        return pickuptime;
    }

    public void setPickuptime(String pickuptime) {
        this.pickuptime = pickuptime;
    }

    public String getRequestedcar() {
        return requestedcar;
    }

    public void setRequestedcar(String requestedcar) {
        this.requestedcar = requestedcar;
    }

    public Object getDriverno() {
        return driverno;
    }

    public void setDriverno(Object driverno) {
        this.driverno = driverno;
    }

    public String getDriverimg() {
        return driverimg;
    }

    public void setDriverimg(String driverimg) {
        this.driverimg = driverimg;
    }

    public String getPlateno() {
        return plateno;
    }

    public void setPlateno(String plateno) {
        this.plateno = plateno;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
