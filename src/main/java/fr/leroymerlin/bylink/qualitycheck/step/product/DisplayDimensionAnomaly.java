package fr.leroymerlin.bylink.qualitycheck.step.product;

public class DisplayDimensionAnomaly {

    public String refLm;
    public String ckbDisplayWidthValue;
    public String stepDisplayWidthValue;
    public String ckbDisplayHeightValue;
    public String stepDisplayHeightValue;
    public String ckbDisplayDepthValue;
    public String stepDisplayDepthValue;

    public DisplayDimensionAnomaly(String refLm, String ckbDisplayWidthValue, String stepDisplayWidthValue, String ckbDisplayHeightValue, String stepDisplayHeightValue, String ckbDisplayDepthValue, String stepDisplayDepthValue) {
        this.refLm = refLm;
        this.ckbDisplayWidthValue = ckbDisplayWidthValue;
        this.stepDisplayWidthValue = stepDisplayWidthValue;
        this.ckbDisplayHeightValue = ckbDisplayHeightValue;
        this.stepDisplayHeightValue = stepDisplayHeightValue;
        this.ckbDisplayDepthValue = ckbDisplayDepthValue;
        this.stepDisplayDepthValue = stepDisplayDepthValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbDisplayWidthValue + ";" + stepDisplayWidthValue + ";" + ckbDisplayHeightValue + ";" + stepDisplayHeightValue + ";" + ckbDisplayDepthValue + ";" + stepDisplayDepthValue;
    }
}
