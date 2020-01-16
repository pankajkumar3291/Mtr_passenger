package dr.mtr.passenger.vehicles;

import java.util.Arrays;
import java.util.List;

import dr.mtr.passenger.R;

public class VehicleViewModel {

    public static VehicleViewModel get() {
        return new VehicleViewModel();
    }

    private VehicleViewModel() {
    }

    public List<VehicleItem> getVehicles() {
        return Arrays.asList(
                new VehicleItem(R.string.Any, R.drawable.three_car, "16", true),
                new VehicleItem(R.string.Sedan, R.drawable.car11, "14", false),
                new VehicleItem(R.string.Jeepeta, R.drawable.car12, "9", false),
                new VehicleItem(R.string.Minivan, R.drawable.car13, "18", false));
                /*new VehicleItem(R.string.Moto_Concho, R.drawable.bike, "6", false));*/

    }

}
