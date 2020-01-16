package dr.mtr.passenger.model.activeTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOActiveTripResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<EOActiveTripData> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EOActiveTripData> getData() {
        return data;
    }

    public void setData(List<EOActiveTripData> data) {
        this.data = data;
    }

}
