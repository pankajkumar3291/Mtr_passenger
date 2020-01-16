package dr.mtr.passenger.model.cancelTrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripCancellation {

    @Expose
    @SerializedName("isvisible")
    private int mIsvisible;
    @Expose
    @SerializedName("vouchertype")
    private int mVouchertype;
    @Expose
    @SerializedName("tripid")
    private int mTripid;
    @Expose
    @SerializedName("status")
    private int mStatus;

    public int getIsvisible() {
        return mIsvisible;
    }

    public void setIsvisible(int mIsvisible) {
        this.mIsvisible = mIsvisible;
    }

    public int getVouchertype() {
        return mVouchertype;
    }

    public void setVouchertype(int mVouchertype) {
        this.mVouchertype = mVouchertype;
    }

    public int getTripid() {
        return mTripid;
    }

    public void setTripid(int mTripid) {
        this.mTripid = mTripid;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

}
