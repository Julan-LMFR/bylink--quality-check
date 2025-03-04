package fr.leroymerlin.bylink.qualitycheck.step.product;

public class SuggestedRestockingAnomaly {

    public String refLm;
    public String ckbValue;
    public String stepValue;

    public SuggestedRestockingAnomaly(String refLm, String ckbValue, String stepValue) {
        this.refLm = refLm;
        this.ckbValue = ckbValue;
        this.stepValue = stepValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbValue + ";" + stepValue;
    }
}
