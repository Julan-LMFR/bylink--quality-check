package fr.leroymerlin.bylink.qualitycheck.step.product;

public class SupplierAnomaly {

    public String refLm;
    public String ckbValue;
    public String stepValue;

    public SupplierAnomaly(String refLm, String ckbValue, String stepValue) {
        this.refLm = refLm;
        this.ckbValue = ckbValue;
        this.stepValue = stepValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbValue + ";" + stepValue;
    }
}
