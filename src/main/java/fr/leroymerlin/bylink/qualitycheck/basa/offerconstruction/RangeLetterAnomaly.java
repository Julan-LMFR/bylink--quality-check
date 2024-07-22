package fr.leroymerlin.bylink.qualitycheck.basa.offerconstruction;

public class RangeLetterAnomaly {

    public String refLm;
    public String ckbValue;
    public String basaValue;

    public RangeLetterAnomaly(String refLm, String ckbValue, String basaValue) {
        this.refLm = refLm;
        this.ckbValue = ckbValue;
        this.basaValue = basaValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbValue + ";" + basaValue;
    }
}
