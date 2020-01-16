package dr.mtr.passenger.model.history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOTripHistoryRequest {

    @SerializedName("pid")
    @Expose
    private String pid;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

}
