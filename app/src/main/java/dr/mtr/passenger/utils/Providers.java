package dr.mtr.passenger.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import dr.mtr.passenger.R;

public class Providers {

    public static String provideVehicleDrawable(int vehicleTypeSelection) {
        switch (vehicleTypeSelection) {
            case 0:
                return "Any";
            case 1:
                return "Sedan";
            case 2:
                return "SUV";
            case 3:
                return "Minivan";
            case 4:
                return "Moto";
        }
        return "Any";
    }

    public static BitmapDescriptor provideVehicleDrawable1(int vehicleTypeSelection) {
        switch (vehicleTypeSelection) {
            case 0:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_car_red);
            case 1:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_car_red);

            case 2:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_car_grey);

            case 3:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_car_almost_black);

            case 4:
                return BitmapDescriptorFactory.fromResource(R.drawable.ic_bike);

        }
        return BitmapDescriptorFactory.fromResource(R.drawable.ic_car_red);
    }

    public static Drawable provideImageForLocationType(Context context, int locationType) {
        switch (locationType) {
            case 1:
                return ContextCompat.getDrawable(context, R.drawable.ic_home);
            case 2:
                return ContextCompat.getDrawable(context, R.drawable.ic_office);

            case 3:
                return ContextCompat.getDrawable(context, R.drawable.ic_airport);

            case 4:
                return ContextCompat.getDrawable(context, R.drawable.ic_healthcare);

            case 5:
                return ContextCompat.getDrawable(context, R.drawable.ic_map_marker_grey_def);

        }
        return ContextCompat.getDrawable(context, R.drawable.ic_map_marker_grey_def);
    }


}
