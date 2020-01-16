package dr.mtr.passenger.model.newTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EONewTripResponse implements Serializable {

    @Expose
    @SerializedName("tripid")
    private String mTripid;
    @Expose
    @SerializedName("message")
    private String mMessage;
    @Expose
    @SerializedName("status")
    private String mStatus;

    public String getTripid() {
        return mTripid;
    }

    public void setTripid(String mTripid) {
        this.mTripid = mTripid;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }


}
