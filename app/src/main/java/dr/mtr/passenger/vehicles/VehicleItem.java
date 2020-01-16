package dr.mtr.passenger.vehicles;

public class VehicleItem {

    private int any;
    private int three_car;
    private String s;
    private boolean isSelected;

    public VehicleItem(int any, int three_car, String s, boolean isSelected) {
        this.any = any;
        this.three_car = three_car;
        this.s = s;
        this.isSelected = isSelected;
    }

    public int getAny() {
        return any;
    }

    public void setAny(int any) {
        this.any = any;
    }

    public int getThree_car() {
        return three_car;
    }

    public void setThree_car(int three_car) {
        this.three_car = three_car;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }


}
