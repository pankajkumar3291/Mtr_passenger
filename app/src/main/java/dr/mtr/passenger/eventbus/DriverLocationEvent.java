package dr.mtr.passenger.eventbus;

import dr.mtr.passenger.model.driverLocation.EODriverCurrentLocation;

public class DriverLocationEvent {

    private EODriverCurrentLocation driverCurrentLocation;

    public DriverLocationEvent(EODriverCurrentLocation driverCurrentLocation) {
        this.driverCurrentLocation = driverCurrentLocation;
    }

    public EODriverCurrentLocation getDriverCurrentLocation() {
        return this.driverCurrentLocation;
    }

}
