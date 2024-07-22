package fr.leroymerlin.bylink.qualitycheck.ckb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CKBEntryOLD {

    public String idBy;
    public String refLm;
    public String gtinActifs;
    public String designationAdministrative;
    public String cycleDeVieId;
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
    public String produitEmballeHauteurEnCmHUnitStep;
    public String produitEmballeLargeurEnCmWUnitStep;
    public String produitEmballeProfondeurEnCmDUnitStep;
    public String hauteurEnCm;
    public String largeurEnCm;
    public String profondeurEnCm;
    public String hauteurEnCmHExpoStep;
    public String largeurEnCmWExpoStep;
    public String profondeurEnCmDExpoStep;
    public String premierPrix;

    @Override
    public String toString() {
        return "CKBEntry{" +
                "idBy='" + idBy + '\'' +
                ", refLm='" + refLm + '\'' +
                ", gtinActifs='" + gtinActifs + '\'' +
                ", designationAdministrative='" + designationAdministrative + '\'' +
                ", cycleDeVieId='" + cycleDeVieId + '\'' +
                ", cycleDeVie='" + cycleDeVie + '\'' +
                ", lettreDeGamme='" + lettreDeGamme + '\'' +
                ", precoTopReappro='" + precoTopReappro + '\'' +
                ", rayon='" + rayon + '\'' +
                ", sousRayon='" + sousRayon + '\'' +
                ", type='" + type + '\'' +
                ", sousType='" + sousType + '\'' +
                ", classement='" + classement + '\'' +
                ", nomsFournisseursCommerciauxBu='" + nomsFournisseursCommerciauxBu + '\'' +
                ", produitEmballeHauteurEnCm='" + produitEmballeHauteurEnCm + '\'' +
                ", produitEmballeLargeurEnCm='" + produitEmballeLargeurEnCm + '\'' +
                ", produitEmballeProfondeurEnCm='" + produitEmballeProfondeurEnCm + '\'' +
                ", produitEmballeHauteurEnCmHUnitStep='" + produitEmballeHauteurEnCmHUnitStep + '\'' +
                ", produitEmballeLargeurEnCmWUnitStep='" + produitEmballeLargeurEnCmWUnitStep + '\'' +
                ", produitEmballeProfondeurEnCmDUnitStep='" + produitEmballeProfondeurEnCmDUnitStep + '\'' +
                ", hauteurEnCm='" + hauteurEnCm + '\'' +
                ", largeurEnCm='" + largeurEnCm + '\'' +
                ", profondeurEnCm='" + profondeurEnCm + '\'' +
                ", hauteurEnCmHExpoStep='" + hauteurEnCmHExpoStep + '\'' +
                ", largeurEnCmWExpoStep='" + largeurEnCmWExpoStep + '\'' +
                ", profondeurEnCmDExpoStep='" + profondeurEnCmDExpoStep + '\'' +
                ", premierPrix='" + premierPrix + '\'' +
                '}';
    }
}
