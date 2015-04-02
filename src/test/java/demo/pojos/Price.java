package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

/**
 * 价格
 */
@ExposeablePojo
public class Price {

    @Description("价格")
    private double value;

    @Description("单位")
    private String unit;

    @Description("计费类型")
    private String type;

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Price{" +
                "value=" + value +
                ", unit='" + unit + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
