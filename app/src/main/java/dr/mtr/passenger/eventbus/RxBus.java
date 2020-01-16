package dr.mtr.passenger.eventbus;

import dr.mtr.passenger.gps.GpsLocationProvider;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public class RxBus {

    // TODO for AwsMqtt Status
    private BehaviorSubject<MqttComponentsEvent> mqttComponentSubject = BehaviorSubject.create();

    public Observable<MqttComponentsEvent> toObservableMqttComponents() {
        return mqttComponentSubject;
    }

    public void sendMqttComponents(MqttComponentsEvent mqttComponents) {
        //if (mqttComponentSubject.hasObservers())
        mqttComponentSubject.onNext(mqttComponents);
    }


    //TODO for GPS provider Update
    private PublishSubject<GpsLocationProvider> gpsProviderSubject = PublishSubject.create();

    public Observable<GpsLocationProvider> toObservableGpsProvider() {
        return gpsProviderSubject;
    }

    public void send(GpsLocationProvider gpsLocationProvider) {
        gpsProviderSubject.onNext(gpsLocationProvider);
    }

}
