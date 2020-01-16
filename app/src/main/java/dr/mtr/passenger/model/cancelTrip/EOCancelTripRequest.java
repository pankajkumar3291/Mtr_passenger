package dr.mtr.passenger.model.cancelTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOCancelTripRequest {

    @SerializedName("tripid")
    @Expose
    private int tripid;
    @SerializedName("reason")
    @Expose
    private String reason;

    public int getTripid() {
        return tripid;
    }

    public void setTripid(int tripid) {
        this.tripid = tripid;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
