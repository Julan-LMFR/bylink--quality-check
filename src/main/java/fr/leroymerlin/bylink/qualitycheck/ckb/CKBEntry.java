package fr.leroymerlin.bylink.qualitycheck.ckb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CKBEntry {

    public String idBy;
    public String refLm;
    public String lettreDeGamme;
    public String classement;
    public String rayon;
    public String sousRayon;
    public String type;
    public String sousType;
    public String gtinActifs;
    public String designationAdministrative;
    public String nomDuProduit;
    public String marque;
    public String typeDeProduit;
    public String poidsBrut;
    public String produitEmballeHauteurEnCm;
    public String produitEmballeLargeurEnCm;
    public String produitEmballeProfondeurEnCm;
    public String produitEmballeHauteurEnCmHUnitStep;
    public String produitEmballeLargeurEnCmWUnitStep;
    public String produitEmballeProfondeurEnCmDUnitStep;
    public String poidsNet;
    public String typeDePackaging;
    public String paysDeFabrication;
    public String paysOrigineDuBois;
    public String niveauDeQualite;
    public String typeDeSupport;
    public String familleDeCouleur;
    public String couleur;
    public String destination;
    public String poidsDeLaPalette;
    public String dimensionPaletteHauteurEnCm;
    public String dimensionPaletteLargeurEnCm;
    public String dimensionPaletteProfondeurEnCm;
    public String dimensionPaletteHauteurEnCmHCaseStep;
    public String dimensionPaletteLargeurEnCmWCaseStep;
    public String dimensionPaletteProfondeurEnCmDCaseStep;
    public String hauteurEnCm;
    public String largeurEnCm;
    public String profondeurEnCm;
    public String hauteurEnCmHExpoStep;
    public String largeurEnCmWExpoStep;
    public String profondeurEnCmDExpoStep;
    public String blisterNombreDeTrous;
    public String produitEngage;
    public String brochable;
    public String usageDuProduit;
    public String contenance;
    public String aspect;
    public String nombreDeColis;
    public String premierPrix;
    public String uniteDeContenance;
    public String nomsFournisseursCommerciauxBu;
    public String prochaineLettreDeGamme;
    public String ancienneLettreDeGamme;
    public String precoTopReappro;
    public String marqueDesHabitants;
    public String marqueAdeo;
    public String cycleDeVie;
    public String cycleDeVieUnAuthorized;
    public String cycleDeVieId;
    public String dateDeFin;
    public String parCombien;
    public String critereDeTri1;
    public String critereDeTri2;
    public String critereDeTri3;
    public String critereDeTri4;
    public String critereDeTri5;
    public String critereDeTri6;
    public String critereDeTri7;
    public String critereDeTri8;
    public String critereDeTri9;
    public String critereDeTri10;

    @Override
    public String toString() {
        return "CKBEntry{" +
                "idBy='" + idBy + '\'' +
                ", refLm='" + refLm + '\'' +
                ", lettreDeGamme='" + lettreDeGamme + '\'' +
                ", classement='" + classement + '\'' +
                ", rayon='" + rayon + '\'' +
                ", sousRayon='" + sousRayon + '\'' +
                ", type='" + type + '\'' +
                ", sousType='" + sousType + '\'' +
                ", gtinActifs='" + gtinActifs + '\'' +
                ", designationAdministrative='" + designationAdministrative + '\'' +
                ", nomDuProduit='" + nomDuProduit + '\'' +
                ", marque='" + marque + '\'' +
                ", typeDeProduit='" + typeDeProduit + '\'' +
                ", poidsBrut='" + poidsBrut + '\'' +
                ", produitEmballeHauteurEnCm='" + produitEmballeHauteurEnCm + '\'' +
                ", produitEmballeLargeurEnCm='" + produitEmballeLargeurEnCm + '\'' +
                ", produitEmballeProfondeurEnCm='" + produitEmballeProfondeurEnCm + '\'' +
                ", produitEmballeHauteurEnCmHUnitStep='" + produitEmballeHauteurEnCmHUnitStep + '\'' +
                ", produitEmballeLargeurEnCmWUnitStep='" + produitEmballeLargeurEnCmWUnitStep + '\'' +
                ", produitEmballeProfondeurEnCmDUnitStep='" + produitEmballeProfondeurEnCmDUnitStep + '\'' +
                ", poidsNet='" + poidsNet + '\'' +
                ", typeDePackaging='" + typeDePackaging + '\'' +
                ", paysDeFabrication='" + paysDeFabrication + '\'' +
                ", paysOrigineDuBois='" + paysOrigineDuBois + '\'' +
                ", niveauDeQualite='" + niveauDeQualite + '\'' +
                ", typeDeSupport='" + typeDeSupport + '\'' +
                ", familleDeCouleur='" + familleDeCouleur + '\'' +
                ", couleur='" + couleur + '\'' +
                ", destination='" + destination + '\'' +
                ", poidsDeLaPalette='" + poidsDeLaPalette + '\'' +
                ", dimensionPaletteHauteurEnCm='" + dimensionPaletteHauteurEnCm + '\'' +
                ", dimensionPaletteLargeurEnCm='" + dimensionPaletteLargeurEnCm + '\'' +
                ", dimensionPaletteProfondeurEnCm='" + dimensionPaletteProfondeurEnCm + '\'' +
                ", dimensionPaletteHauteurEnCmHCaseStep='" + dimensionPaletteHauteurEnCmHCaseStep + '\'' +
                ", dimensionPaletteLargeurEnCmWCaseStep='" + dimensionPaletteLargeurEnCmWCaseStep + '\'' +
                ", dimensionPaletteProfondeurEnCmDCaseStep='" + dimensionPaletteProfondeurEnCmDCaseStep + '\'' +
                ", hauteurEnCm='" + hauteurEnCm + '\'' +
                ", largeurEnCm='" + largeurEnCm + '\'' +
                ", profondeurEnCm='" + profondeurEnCm + '\'' +
                ", hauteurEnCmHExpoStep='" + hauteurEnCmHExpoStep + '\'' +
                ", largeurEnCmWExpoStep='" + largeurEnCmWExpoStep + '\'' +
                ", profondeurEnCmDExpoStep='" + profondeurEnCmDExpoStep + '\'' +
                ", blisterNombreDeTrous='" + blisterNombreDeTrous + '\'' +
                ", produitEngage='" + produitEngage + '\'' +
                ", brochable='" + brochable + '\'' +
                ", usageDuProduit='" + usageDuProduit + '\'' +
                ", contenance='" + contenance + '\'' +
                ", aspect='" + aspect + '\'' +
                ", nombreDeColis='" + nombreDeColis + '\'' +
                ", premierPrix='" + premierPrix + '\'' +
                ", uniteDeContenance='" + uniteDeContenance + '\'' +
                ", nomsFournisseursCommerciauxBu='" + nomsFournisseursCommerciauxBu + '\'' +
                ", prochaineLettreDeGamme='" + prochaineLettreDeGamme + '\'' +
                ", ancienneLettreDeGamme='" + ancienneLettreDeGamme + '\'' +
                ", precoTopReappro='" + precoTopReappro + '\'' +
                ", marqueDesHabitants='" + marqueDesHabitants + '\'' +
                ", marqueAdeo='" + marqueAdeo + '\'' +
                ", cycleDeVie='" + cycleDeVie + '\'' +
                ", cycleDeVieUnAuthorized='" + cycleDeVieUnAuthorized + '\'' +
                ", cycleDeVieId='" + cycleDeVieId + '\'' +
                ", dateDeFin='" + dateDeFin + '\'' +
                ", parCombien='" + parCombien + '\'' +
                ", critereDeTri1='" + critereDeTri1 + '\'' +
                ", critereDeTri2='" + critereDeTri2 + '\'' +
                ", critereDeTri3='" + critereDeTri3 + '\'' +
                ", critereDeTri4='" + critereDeTri4 + '\'' +
                ", critereDeTri5='" + critereDeTri5 + '\'' +
                ", critereDeTri6='" + critereDeTri6 + '\'' +
                ", critereDeTri7='" + critereDeTri7 + '\'' +
                ", critereDeTri8='" + critereDeTri8 + '\'' +
                ", critereDeTri9='" + critereDeTri9 + '\'' +
                ", critereDeTri10='" + critereDeTri10 + '\'' +
                '}';
    }
}
