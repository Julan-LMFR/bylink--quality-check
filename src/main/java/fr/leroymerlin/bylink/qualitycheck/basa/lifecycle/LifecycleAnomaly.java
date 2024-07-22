package fr.leroymerlin.bylink.qualitycheck.basa.lifecycle;

public class LifecycleAnomaly {

    public String refLm;
    public String ckbValue;
    public String basaValue;

    public LifecycleAnomaly(String refLm, String ckbValue, String basaValue) {
        this.refLm = refLm;
        this.ckbValue = ckbValue;
        this.basaValue = basaValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbValue + ";" + basaValue;
    }
}
