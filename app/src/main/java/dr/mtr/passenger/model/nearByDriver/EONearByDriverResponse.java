package dr.mtr.passenger.model.nearByDriver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EONearByDriverResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<EONearByDriverData> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EONearByDriverData> getData() {
        return data;
    }

    public void setData(List<EONearByDriverData> data) {
        this.data = data;
    }

}
