package dr.mtr.passenger.eventbus;

public class MapTypeEvent {

    private String mapTypeValue;

    public MapTypeEvent(String mapTypeValue) {
        this.mapTypeValue = mapTypeValue;
    }

    public String getMapTypeValue() {
        return this.mapTypeValue;
    }

}
