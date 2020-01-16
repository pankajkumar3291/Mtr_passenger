package dr.mtr.passenger.model.googleDistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dr.mtr.passenger.model.direction.Distance;
import dr.mtr.passenger.model.direction.Duration;

public class Elements {

    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("duration")
    private Duration duration;
    @Expose
    @SerializedName("distance")
    private Distance distance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }
}
