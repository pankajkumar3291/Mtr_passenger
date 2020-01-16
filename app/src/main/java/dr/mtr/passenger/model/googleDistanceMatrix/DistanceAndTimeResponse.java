package dr.mtr.passenger.model.googleDistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DistanceAndTimeResponse {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("rows")
    private List<Rows> rows;
    @Expose
    @SerializedName("origin_addresses")
    private List<String> origin_addresses;
    @Expose
    @SerializedName("destination_addresses")
    private List<String> destination_addresses;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public List<String> getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(List<String> origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public List<String> getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(List<String> destination_addresses) {
        this.destination_addresses = destination_addresses;
    }
}
