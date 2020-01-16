package dr.mtr.passenger.model.firebase;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOFireBaseTokenRequest {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("userid")
    @Expose
    private String userid;

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
