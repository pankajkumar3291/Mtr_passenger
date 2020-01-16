package dr.mtr.passenger.eventbus;

public class GetMqttEvent {

    private MqttComponentsEvent componentsEvent;

    public GetMqttEvent(MqttComponentsEvent componentsEvent) {
        this.componentsEvent = componentsEvent;
    }

    public MqttComponentsEvent getMqttEvent() {
        return this.componentsEvent;
    }
}
