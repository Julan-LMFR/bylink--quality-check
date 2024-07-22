package fr.leroymerlin.bylink.qualitycheck.bylink;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MongoEntry {

    public String id;
    public String redwoodLastUpdatedDate;
    public String offerConstructionLastUpdatedDate;
    public String nomenclatureLastUpdatedDate;
    public String lifeCycleLastUpdatedDate;
    public String opusLastUpdatedDate;
    public String purchaseLastUpdatedDate;

    @Override
    public String toString() {
        return "MongoEntry{" +
                "id='" + id + '\'' +
                ", redwoodLastUpdatedDate='" + redwoodLastUpdatedDate + '\'' +
                ", offerConstructionLastUpdatedDate='" + offerConstructionLastUpdatedDate + '\'' +
                ", nomenclatureLastUpdatedDate='" + nomenclatureLastUpdatedDate + '\'' +
                ", lifeCycleLastUpdatedDate='" + lifeCycleLastUpdatedDate + '\'' +
                ", opusLastUpdatedDate='" + opusLastUpdatedDate + '\'' +
                ", purchaseLastUpdatedDate='" + purchaseLastUpdatedDate + '\'' +
                '}';
    }
}
