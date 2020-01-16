package dr.mtr.passenger.model.fare;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOFareResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("fare")
    @Expose
    private Double fare;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

}
