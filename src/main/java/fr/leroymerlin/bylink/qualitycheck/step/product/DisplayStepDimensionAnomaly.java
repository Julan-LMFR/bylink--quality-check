package fr.leroymerlin.bylink.qualitycheck.step.product;

public class DisplayStepDimensionAnomaly {

    public String refLm;
    public String ckbDisplayStepWidthValue;
    public String stepDisplayWidthValue;
    public String ckbDisplayStepHeightValue;
    public String stepDisplayHeightValue;
    public String ckbDisplayStepDepthValue;
    public String stepDisplayDepthValue;

    public DisplayStepDimensionAnomaly(String refLm, String ckbDisplayStepWidthValue, String stepDisplayWidthValue, String ckbDisplayStepHeightValue, String stepDisplayHeightValue, String ckbDisplayStepDepthValue, String stepDisplayDepthValue) {
        this.refLm = refLm;
        this.ckbDisplayStepWidthValue = ckbDisplayStepWidthValue;
        this.stepDisplayWidthValue = stepDisplayWidthValue;
        this.ckbDisplayStepHeightValue = ckbDisplayStepHeightValue;
        this.stepDisplayHeightValue = stepDisplayHeightValue;
        this.ckbDisplayStepDepthValue = ckbDisplayStepDepthValue;
        this.stepDisplayDepthValue = stepDisplayDepthValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbDisplayStepWidthValue + ";" + stepDisplayWidthValue + ";" + ckbDisplayStepHeightValue + ";" + stepDisplayHeightValue + ";" + ckbDisplayStepDepthValue + ";" + stepDisplayDepthValue;
    }
}
