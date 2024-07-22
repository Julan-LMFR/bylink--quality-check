package fr.leroymerlin.bylink.qualitycheck.basa;

import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;

@Getter
@Setter
public class BasaPCMDEntry {

    public String businessUnitIdentifier;
    public String productReferenceBU;
    public String productReferenceADEO;
    public String departmentIdentifier;
    public String subDepartmentIdentifier;
    public String typeIdentifier;
    public String subTypeIdentifier;
    public String productRangeLetter;
    public String productRanking;
    public String initializedDate;
    public String activeInternalServiceDate;
    public String activeTradeDate;
    public String omnichannelSalesEndDate;
    public String deletedDate;
    public String customsCoefficient;
    public String activeTradeObjectiveDate;
    public String customsNomenclature;
    public String hyper100Indicator;
    public String purchasePriceChangeIndicator;
    public String salesPriceChangeIndicator;
    public String productActiveLifeCycle;

    private static String removeHtmlTags(String data) {
        return data != null ? Jsoup.parse(data).text() : null;
    }

    public CKBEntry toCKBEntry() {
        CKBEntry ckbEntry = new CKBEntry();
        ckbEntry.setRefLm(removeHtmlTags(this.getProductReferenceBU()));

        // Offer Construction
        ckbEntry.setLettreDeGamme(removeHtmlTags(this.getProductRangeLetter()));
        ckbEntry.setClassement(removeHtmlTags(this.getProductRanking()));

        // Lifecycle
        ckbEntry.setCycleDeVie(removeHtmlTags(computeLifecycle(this.getProductActiveLifeCycle())));

        return ckbEntry;
    }

    public CKBEntry toFullCKBEntry(BasaMNEntry nomenclature) {
        CKBEntry ckbEntry = this.toCKBEntry();

        // Nomenclature
        if (nomenclature != null) {
            ckbEntry.setRayon(removeHtmlTags(computeRayonNomenclature(nomenclature)));
            ckbEntry.setSousRayon(removeHtmlTags(computeSousRayonNomenclature(nomenclature)));
            ckbEntry.setType(removeHtmlTags(computeTypeNomenclature(nomenclature)));
            ckbEntry.setSousType(removeHtmlTags(computeSousTypeNomenclature(nomenclature)));
        }

        return ckbEntry;
    }

    private String computeLifecycle(String lifeCycle) {
        String computedLifecycle;

        switch (lifeCycle) {
            case "":
                computedLifecycle = "Pre-initialisé";
                break;
            case "INI":
                computedLifecycle = "Initialisé";
                break;
            case "AC":
                computedLifecycle = "Actif Commerce";
                break;
            case "ASI":
                computedLifecycle = "Actif Service Interne";
                break;
            case "SUPP":
                computedLifecycle = "Suppression Programmée";
                break;
            case "SUP":
                computedLifecycle = "Supprimée";
                break;
            case "ESUP":
                computedLifecycle = "En Suppression";
                break;
            case null, default:
                computedLifecycle = "Pre-initialisé";
                break;
        }
        return computedLifecycle;
    }

    private String computeRayonNomenclature(BasaMNEntry entry) {
        StringBuilder rayon = new StringBuilder();

        if (Integer.valueOf(entry.getDepartmentidentifier()) < 10) {
            rayon.append("0");
        }

        rayon.append(entry.getDepartmentidentifier() + " - " + entry.getDepartmentdescription());

        return rayon.toString();
    }

    private String computeSousRayonNomenclature(BasaMNEntry entry) {
        StringBuilder sousRayon = new StringBuilder();

        if (Integer.valueOf(entry.getSubdepartmentidentifier()) < 10) {
            sousRayon.append("0");
        }

        sousRayon.append(entry.getSubdepartmentidentifier() + " - " + entry.getSubdepartmentdescription());

        return sousRayon.toString();
    }

    private String computeTypeNomenclature(BasaMNEntry entry) {
        StringBuilder type = new StringBuilder();

        if (Integer.valueOf(entry.getTypeidentifier()) < 10) {
            type.append("0");
        }

        type.append(entry.getTypeidentifier() + " - " + entry.getTypedescription());

        return type.toString();
    }

    private String computeSousTypeNomenclature(BasaMNEntry entry) {
        StringBuilder sousType = new StringBuilder();

        if (Integer.valueOf(entry.getSubtypeidentifier()) < 10) {
            sousType.append("0");
        }

        sousType.append(entry.getSubtypeidentifier() + " - " + entry.getSubtypedescription());

        return sousType.toString();
    }

    @Override
    public String toString() {
        return "OfferConstructionEntry{" + "businessUnitIdentifier='" + businessUnitIdentifier + '\'' + ", productReferenceBU='" + productReferenceBU + '\'' + ", productReferenceADEO='" + productReferenceADEO + '\'' + ", departmentIdentifier='" + departmentIdentifier + '\'' + ", subDepartmentIdentifier='" + subDepartmentIdentifier + '\'' + ", typeIdentifier='" + typeIdentifier + '\'' + ", subTypeIdentifier='" + subTypeIdentifier + '\'' + ", productRangeLetter='" + productRangeLetter + '\'' + ", productRanking='" + productRanking + '\'' + ", initializedDate='" + initializedDate + '\'' + ", activeInternalServiceDate='" + activeInternalServiceDate + '\'' + ", activeTradeDate='" + activeTradeDate + '\'' + ", omnichannelSalesEndDate='" + omnichannelSalesEndDate + '\'' + ", deletedDate='" + deletedDate + '\'' + ", customsCoefficient='" + customsCoefficient + '\'' + ", activeTradeObjectiveDate='" + activeTradeObjectiveDate + '\'' + ", customsNomenclature='" + customsNomenclature + '\'' + ", hyper100Indicator='" + hyper100Indicator + '\'' + ", purchasePriceChangeIndicator='" + purchasePriceChangeIndicator + '\'' + ", salesPriceChangeIndicator='" + salesPriceChangeIndicator + '\'' + ", productActiveLifeCycle='" + productActiveLifeCycle + '\'' + '}';
    }
}
