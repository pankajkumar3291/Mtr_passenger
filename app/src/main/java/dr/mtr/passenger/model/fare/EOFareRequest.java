package dr.mtr.passenger.model.fare;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOFareRequest {

    @SerializedName("clat")
    @Expose
    private String clat;
    @SerializedName("clon")
    @Expose
    private String clon;
    @SerializedName("dlat")
    @Expose
    private String dlat;
    @SerializedName("dlon")
    @Expose
    private String dlon;
    @SerializedName("custid")
    @Expose
    private String custid;

    public String getClat() {
        return clat;
    }

    public void setClat(String clat) {
        this.clat = clat;
    }

    public String getClon() {
        return clon;
    }

    public void setClon(String clon) {
        this.clon = clon;
    }

    public String getDlat() {
        return dlat;
    }

    public void setDlat(String dlat) {
        this.dlat = dlat;
    }

    public String getDlon() {
        return dlon;
    }

    public void setDlon(String dlon) {
        this.dlon = dlon;
    }

    public String getCustid() {
        return custid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

}
