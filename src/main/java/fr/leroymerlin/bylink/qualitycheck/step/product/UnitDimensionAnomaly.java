package fr.leroymerlin.bylink.qualitycheck.step.product;

public class UnitDimensionAnomaly {

    public String refLm;
    public String ckbUnitWidthValue;
    public String stepUnitWidthValue;
    public String ckbUnitHeightValue;
    public String stepUnitHeightValue;
    public String ckbUnitDepthValue;
    public String stepUnitDepthValue;

    public UnitDimensionAnomaly(String refLm, String ckbUnitWidthValue, String stepUnitWidthValue, String ckbUnitHeightValue, String stepUnitHeightValue, String ckbUnitDepthValue, String stepUnitDepthValue) {
        this.refLm = refLm;
        this.ckbUnitWidthValue = ckbUnitWidthValue;
        this.stepUnitWidthValue = stepUnitWidthValue;
        this.ckbUnitHeightValue = ckbUnitHeightValue;
        this.stepUnitHeightValue = stepUnitHeightValue;
        this.ckbUnitDepthValue = ckbUnitDepthValue;
        this.stepUnitDepthValue = stepUnitDepthValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbUnitWidthValue + ";" + stepUnitWidthValue + ";" + ckbUnitHeightValue + ";" + stepUnitHeightValue + ";" + ckbUnitDepthValue + ";" + stepUnitDepthValue;
    }
}
