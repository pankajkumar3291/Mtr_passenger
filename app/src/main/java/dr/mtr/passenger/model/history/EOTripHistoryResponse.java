package dr.mtr.passenger.model.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EOTripHistoryResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<EOTripHistoryData> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EOTripHistoryData> getData() {
        return data;
    }

    public void setData(List<EOTripHistoryData> data) {
        this.data = data;
    }
}
