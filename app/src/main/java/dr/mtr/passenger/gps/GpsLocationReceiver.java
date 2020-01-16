package dr.mtr.passenger.gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import dr.mtr.passenger.application.GlobalApplication;

import static android.content.Context.LOCATION_SERVICE;

public class GpsLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null)
            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                // react on GPS provider change action
                try {
                    LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                    if (locationManager != null)
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            ((GlobalApplication) context.getApplicationContext()).getBus().send(new GpsLocationProvider(true));
                        } else {
                            ((GlobalApplication) context.getApplicationContext()).getBus().send(new GpsLocationProvider(false));
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }


}
