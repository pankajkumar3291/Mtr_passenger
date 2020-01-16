package dr.mtr.passenger.model.nearByDriver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EONearByDriverRequest implements Serializable {

    @SerializedName("pid")
    @Expose
    private String pid;
    @SerializedName("vtype")
    @Expose
    private String vtype;
    @SerializedName("domainid")
    @Expose
    private String domainid;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("lat")
    @Expose
    private String lat;

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setVtype(String vtype) {
        this.vtype = vtype;
    }

    public void setDomainid(String domainid) {
        this.domainid = domainid;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPid() {
        return pid;
    }

    public String getVtype() {
        return vtype;
    }

    public String getDomainid() {
        return domainid;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

}
