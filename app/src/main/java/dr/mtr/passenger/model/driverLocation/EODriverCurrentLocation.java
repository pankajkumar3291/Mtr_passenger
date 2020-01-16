package dr.mtr.passenger.model.driverLocation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EODriverCurrentLocation implements Serializable {

    @SerializedName("JsonObject")
    @Expose
    private EODriverLocationData jsonObject;

    public EODriverLocationData getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(EODriverLocationData jsonObject) {
        this.jsonObject = jsonObject;
    }

}
