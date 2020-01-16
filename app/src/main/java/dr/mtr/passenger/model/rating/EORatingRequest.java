package dr.mtr.passenger.model.rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dr.mtr.passenger.utils.ObjectModel;

public class EORatingRequest extends ObjectModel {

    @Expose
    @SerializedName("tripid")
    private int tripid;
    @Expose
    @SerializedName("driverid")
    private int driverid;
    @Expose
    @SerializedName("rating")
    private double rating;
    @Expose
    @SerializedName("comment")
    private String comment;
    @Expose
    @SerializedName("pid")
    private int pid;
    @Expose
    @SerializedName("driverName")
    private String driverName;
    @Expose
    @SerializedName("driverPhoto")
    private String driverPhoto;

    public int getTripid() {
        return tripid;
    }

    public void setTripid(int tripid) {
        this.tripid = tripid;
    }

    public int getDriverid() {
        return driverid;
    }

    public void setDriverid(int driverid) {
        this.driverid = driverid;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }
}
