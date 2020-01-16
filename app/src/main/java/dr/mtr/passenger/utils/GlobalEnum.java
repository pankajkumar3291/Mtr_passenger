package dr.mtr.passenger.utils;

import java.util.HashMap;
import java.util.Map;

public enum GlobalEnum {

    RECT_SHAPE("RECT_SHAPE"), ROUND_CORNER("ROUND_CORNER"), OVAL_SHAPE("OVAL_SHAPE"), RING_SHAPE("RING_SHAPE"),
    FONT_ENGLISH("FONT_ENGLISH"), FONT_ARABIC("FONT_ARABIC");

    private static final Map<String, GlobalEnum> enumMap = new HashMap<>();

    static {
        for (GlobalEnum s : GlobalEnum.values()) {
            enumMap.put(s.text, s);
        }
    }

    private final String text;

    GlobalEnum(final String text) {
        this.text = text;
    }

    public static GlobalEnum fromID(String id) {
        return enumMap.get(id);
    }

    public String toString() {
        return this.text;
    }

    public boolean isEqual(String status) {
        return status != null && status.equals(this.text);
    }

    public boolean isSame(GlobalEnum status) {
        return status != null && isEqual(status.toString());
    }
}
