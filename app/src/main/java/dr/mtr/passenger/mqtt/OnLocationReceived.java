package dr.mtr.passenger.mqtt;


import dr.mtr.passenger.model.driverLocation.EODriverCurrentLocation;

public interface OnLocationReceived {

    void onDriverLocationUpdate(EODriverCurrentLocation driverCurrentLocation);

}
