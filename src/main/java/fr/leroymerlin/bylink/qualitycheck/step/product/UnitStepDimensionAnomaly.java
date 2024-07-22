package fr.leroymerlin.bylink.qualitycheck.step.product;

public class UnitStepDimensionAnomaly {

    public String refLm;
    public String ckbUnitStepWidthValue;
    public String stepUnitWidthValue;
    public String ckbUnitStepHeightValue;
    public String stepUnitHeightValue;
    public String ckbUnitStepDepthValue;
    public String stepUnitDepthValue;

    public UnitStepDimensionAnomaly(String refLm, String ckbUnitStepWidthValue, String stepUnitWidthValue, String ckbUnitStepHeightValue, String stepUnitHeightValue, String ckbUnitStepDepthValue, String stepUnitDepthValue) {
        this.refLm = refLm;
        this.ckbUnitStepWidthValue = ckbUnitStepWidthValue;
        this.stepUnitWidthValue = stepUnitWidthValue;
        this.ckbUnitStepHeightValue = ckbUnitStepHeightValue;
        this.stepUnitHeightValue = stepUnitHeightValue;
        this.ckbUnitStepDepthValue = ckbUnitStepDepthValue;
        this.stepUnitDepthValue = stepUnitDepthValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbUnitStepWidthValue + ";" + stepUnitWidthValue + ";" + ckbUnitStepHeightValue + ";" + stepUnitHeightValue + ";" + ckbUnitStepDepthValue + ";" + stepUnitDepthValue;
    }
}
