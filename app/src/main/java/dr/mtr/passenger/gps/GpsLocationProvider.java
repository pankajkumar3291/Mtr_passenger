package dr.mtr.passenger.gps;

public class GpsLocationProvider {

    private boolean isGpsLocationProviderEnabled;

    public GpsLocationProvider(boolean isGpsLocationProviderEnabled) {
        this.isGpsLocationProviderEnabled = isGpsLocationProviderEnabled;
    }

    public boolean isGpsLocationProviderEnabled() {
        return isGpsLocationProviderEnabled;
    }
}
