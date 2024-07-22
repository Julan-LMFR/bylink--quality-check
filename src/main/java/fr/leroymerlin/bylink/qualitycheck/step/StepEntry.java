package fr.leroymerlin.bylink.qualitycheck.step;

import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

@Getter
@Setter
public class StepEntry {

    public String refLm;
    public String gtinActifs;
    public String designationAdministrative;
    public String cycleDeVie;
    public String lettreDeGamme;
    public String precoTopReappro;
    public String rayon;
    public String sousRayon;
    public String type;
    public String sousType;
    public String classement;
    public String nomsFournisseursCommerciauxBu;
    public String produitEmballeHauteurEnCm;
    public String produitEmballeLargeurEnCm;
    public String produitEmballeProfondeurEnCm;
    public String hauteurEnCm;
    public String largeurEnCm;
    public String profondeurEnCm;
    public String politiquePrix;

    private static String removeHtmlTags(String data) {
        return data != null ? Jsoup.parse(data).text() : null;
    }

    public CKBEntry toCKBEntry() {
        CKBEntry ckbEntry = new CKBEntry();
        ckbEntry.setRefLm(removeHtmlTags(this.getRefLm()));

        // Product
        ckbEntry.setPrecoTopReappro(removeHtmlTags(this.getPrecoTopReappro()));
        ckbEntry.setGtinActifs(removeHtmlTags(this.getGtinActifs()));
        ckbEntry.setNomsFournisseursCommerciauxBu(removeHtmlTags(this.getNomsFournisseursCommerciauxBu()));
        ckbEntry.setPremierPrix(removeHtmlTags(computePremierPrix(this.getPolitiquePrix())));
        ckbEntry.setHauteurEnCm(removeHtmlTags(computeDimension(this.getHauteurEnCm())));
        ckbEntry.setLargeurEnCm(removeHtmlTags(computeDimension(this.getLargeurEnCm())));
        ckbEntry.setProfondeurEnCm(removeHtmlTags(computeDimension(this.getProfondeurEnCm())));
        ckbEntry.setProduitEmballeHauteurEnCm(removeHtmlTags(computeDimension(this.getProduitEmballeHauteurEnCm())));
        ckbEntry.setProduitEmballeLargeurEnCm(removeHtmlTags(computeDimension(this.getProduitEmballeLargeurEnCm())));
        ckbEntry.setProduitEmballeProfondeurEnCm(removeHtmlTags(computeDimension(this.getProduitEmballeProfondeurEnCm())));
        ckbEntry.setHauteurEnCmHExpoStep(removeHtmlTags(computeDimension(this.getHauteurEnCm())));
        ckbEntry.setLargeurEnCmWExpoStep(removeHtmlTags(computeDimension(this.getLargeurEnCm())));
        ckbEntry.setProfondeurEnCmDExpoStep(removeHtmlTags(computeDimension(this.getProfondeurEnCm())));
        ckbEntry.setProduitEmballeHauteurEnCmHUnitStep(removeHtmlTags(computeDimension(this.getProduitEmballeHauteurEnCm())));
        ckbEntry.setProduitEmballeLargeurEnCmWUnitStep(removeHtmlTags(computeDimension(this.getProduitEmballeLargeurEnCm())));
        ckbEntry.setProduitEmballeProfondeurEnCmDUnitStep(removeHtmlTags(computeDimension(this.getProduitEmballeProfondeurEnCm())));

        return ckbEntry;
    }

    private String computePremierPrix(String pricePolicy) {
        return (pricePolicy != null && pricePolicy.equals("1er prix")) ? "1" : "0";
    }

    private String computeDimension(String dimension) {
        if (StringUtils.isEmpty(dimension)) {
            return "0";
        } else {
            return dimension.replace(".", ",");
        }
    }

    @Override
    public String toString() {
        return "StepEntry{" + "refLm='" + refLm + '\'' + ", gtinActifs='" + gtinActifs + '\'' + ", designationAdministrative='" + designationAdministrative + '\'' + ", cycleDeVie='" + cycleDeVie + '\'' + ", lettreDeGamme='" + lettreDeGamme + '\'' + ", precoTopReappro='" + precoTopReappro + '\'' + ", rayon='" + rayon + '\'' + ", sousRayon='" + sousRayon + '\'' + ", type='" + type + '\'' + ", sousType='" + sousType + '\'' + ", classement='" + classement + '\'' + ", nomsFournisseursCommerciauxBu='" + nomsFournisseursCommerciauxBu + '\'' + ", produitEmballeHauteurEnCm='" + produitEmballeHauteurEnCm + '\'' + ", produitEmballeLargeurEnCm='" + produitEmballeLargeurEnCm + '\'' + ", produitEmballeProfondeurEnCm='" + produitEmballeProfondeurEnCm + '\'' + ", hauteurEnCm='" + hauteurEnCm + '\'' + ", largeurEnCm='" + largeurEnCm + '\'' + ", profondeurEnCm='" + profondeurEnCm + '\'' + ", politiquePrix='" + politiquePrix + '\'' + '}';
    }
}
