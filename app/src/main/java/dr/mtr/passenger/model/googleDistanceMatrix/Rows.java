package dr.mtr.passenger.model.googleDistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Rows {

    @Expose
    @SerializedName("elements")
    private List<Elements> elements;

    public List<Elements> getElements() {
        return elements;
    }

    public void setElements(List<Elements> elements) {
        this.elements = elements;
    }


}
