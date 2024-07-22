package fr.leroymerlin.bylink.qualitycheck.step.product;

import fr.leroymerlin.bylink.qualitycheck.Checker;
import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.ByLinkExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.CKBExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.DataSourceFactory;
import fr.leroymerlin.bylink.qualitycheck.datasource.StepExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.filewriter.ByLinkCSVWriter;
import fr.leroymerlin.bylink.qualitycheck.step.StepEntry;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductChecker extends Checker {

    public boolean withoutDoubtingDimensions = true;    // Excel transforms 3 digits decimals as integer in CKB export...

    public CKBExport ckbExport;
    public StepExport stepExport;
    public ByLinkExport byLinkExport;

    public String reportingFolder;
    public int filePartitionSize;

    public SortedMap<String, SuggestedRestockingAnomaly> suggestedRestockingAnomalies = new TreeMap<>();
    public SortedMap<String, GtinAnomaly> gtinAnomalies = new TreeMap<>();
    public SortedMap<String, SupplierAnomaly> supplierAnomalies = new TreeMap<>();
    public SortedMap<String, FirstPriceAnomaly> firstPriceAnomalies = new TreeMap<>();
    public SortedMap<String, DisplayDimensionAnomaly> displayDimensionAnomalies = new TreeMap<>();
    public SortedMap<String, UnitDimensionAnomaly> unitDimensionAnomalies = new TreeMap<>();
    public SortedMap<String, DisplayStepDimensionAnomaly> displayStepDimensionAnomalies = new TreeMap<>();
    public SortedMap<String, UnitStepDimensionAnomaly> unitStepDimensionAnomalies = new TreeMap<>();

    public SortedMap<String, SuggestedRestockingAnomaly> businessRuleRG_TOP = new TreeMap<>();

    public ProductChecker(String ckbExportFile, String stepExportFile, boolean withDetailedReporting, int filePartitionSize) {
        this.ckbExport = DataSourceFactory.getCkbExport(ckbExportFile);
        this.stepExport = DataSourceFactory.getStepExport(stepExportFile);
        this.reportingFolder = this.defineReportingFolder(ckbExportFile);
        this.filePartitionSize = filePartitionSize;

        this.compare();
        this.reportDuplicates(); // N'a rien à faire ici en fait
        this.reportSuggestedRestockingAnomalies(withDetailedReporting);
        this.reportRG_TOP_anomalies(withDetailedReporting);
        this.reportGtinAnomalies(withDetailedReporting);
        this.reportSupplierAnomalies(withDetailedReporting);
        this.reportFirstPriceAnomalies(withDetailedReporting);
        this.reportDisplayDimensionAnomalies(withDetailedReporting);
        this.reportUnitDimensionAnomalies(withDetailedReporting);
        this.reportDisplayStepDimensionAnomalies(withDetailedReporting);
        this.reportUnitStepDimensionAnomalies(withDetailedReporting);

        // No need to report standard display and unit dimensions, as they can not be updated
        this.reportAllAnomalies(this.suggestedRestockingAnomalies.keySet(), this.gtinAnomalies.keySet(), this.supplierAnomalies.keySet(), this.firstPriceAnomalies.keySet(), this.displayStepDimensionAnomalies.keySet(), this.unitStepDimensionAnomalies.keySet());
        // this.reportAllAnomalies(this.suggestedRestockingAnomalies.keySet(), this.gtinAnomalies.keySet(), this.supplierAnomalies.keySet(), this.firstPriceAnomalies.keySet(), this.displayDimensionAnomalies.keySet(), this.unitDimensionAnomalies.keySet(), this.displayStepDimensionAnomalies.keySet(), this.unitStepDimensionAnomalies.keySet());
    }

    private void compare() {
        System.out.println("Compare - CKB: " + this.ckbExport.getOutputCKBEntries().size() + " / Step: " + this.stepExport.getOutputStepEntries().size());
        for (Map.Entry<String, CKBEntry> entry : this.ckbExport.getOutputCKBEntries().entrySet()) {
            CKBEntry ckbEntry = entry.getValue();

            if (super.isRefLmProcessable(ckbEntry)) {
                StepEntry stepEntry = this.stepExport.getOutputStepEntries().get(ckbEntry.getRefLm());
                if (stepEntry != null) {
                    CKBEntry transformedStepEntry = stepEntry.toCKBEntry();

                    this.checkIsSuggestedRestockingCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsGtinCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsSupplierCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsFirstPriceCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsDisplayDimensionCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsUnitDimensionCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsDisplayStepDimensionCorrect(ckbEntry, transformedStepEntry);
                    this.checkIsUnitStepDimensionCorrect(ckbEntry, transformedStepEntry);
                }
            }
        }
    }

    private String defineReportingFolder(String ckbExportFile) {
        File inputFile = new File(ckbExportFile);
        File parentDirectory = inputFile.getParentFile().getParentFile();

        if (parentDirectory != null) {
            return parentDirectory.getAbsolutePath() + File.separator;
        } else {
            System.err.println("Impossible de récupérer le répertoire parent pour le chemin : " + ckbExportFile);
            return null;
        }
    }

    private void checkIsSuggestedRestockingCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        if (!StringUtils.equals(ckbEntry.getPrecoTopReappro(), transformedStepEntry.getPrecoTopReappro())) {
            // RG_TOP : Si Redwood envoi un preco top-reappro vide, celle-ci n'est pas prise en compte dans ByLink
            if (StringUtils.isEmpty(transformedStepEntry.getPrecoTopReappro()) && StringUtils.isNotEmpty(ckbEntry.getPrecoTopReappro())) {
                businessRuleRG_TOP.put(ckbEntry.getRefLm(), new SuggestedRestockingAnomaly(ckbEntry.getRefLm(), ckbEntry.getPrecoTopReappro(), transformedStepEntry.getPrecoTopReappro()));
            } else {
                // System.out.println("Mauvais préco top réappro : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getPrecoTopReappro() + " / Step: " + transformedStepEntry.getPrecoTopReappro() + "]");
                this.suggestedRestockingAnomalies.put(ckbEntry.getRefLm(), new SuggestedRestockingAnomaly(ckbEntry.getRefLm(), ckbEntry.getPrecoTopReappro(), transformedStepEntry.getPrecoTopReappro()));
            }
        }
    }

    private void checkIsGtinCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        if (StringUtils.isNotEmpty(ckbEntry.getGtinActifs())) {
            // CKB automatically remove leading zeros, so we have to remove them for compare
            Set<String> gtinActifs = Arrays.stream(transformedStepEntry.getGtinActifs().split(";")).map(String::trim).map(s -> StringUtils.stripStart(s, "0")).collect(HashSet::new, HashSet::add, HashSet::addAll);

            if (!gtinActifs.contains(ckbEntry.getGtinActifs())) {
                // System.out.println("Mauvais UPC : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getGtinActifs() + " / Step: " + transformedStepEntry.getGtinActifs() + "]");
                this.gtinAnomalies.put(ckbEntry.getRefLm(), new GtinAnomaly(ckbEntry.getRefLm(), ckbEntry.getGtinActifs(), transformedStepEntry.getGtinActifs()));
            }
        }
    }

    private void checkIsSupplierCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        if (StringUtils.isNotEmpty(transformedStepEntry.getNomsFournisseursCommerciauxBu())) {
            if (StringUtils.isNotEmpty(ckbEntry.getNomsFournisseursCommerciauxBu())) {
                Set<String> nomsFournisseursCommerciauxBu = Arrays.stream(transformedStepEntry.getNomsFournisseursCommerciauxBu().split(";")).map(String::trim).collect(HashSet::new, HashSet::add, HashSet::addAll);

                if (!nomsFournisseursCommerciauxBu.contains(ckbEntry.getNomsFournisseursCommerciauxBu())) {
                    // System.out.println("Mauvais fournisseur : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getNomsFournisseursCommerciauxBu() + " / Step: " + transformedStepEntry.getNomsFournisseursCommerciauxBu() + "]");
                    this.supplierAnomalies.put(ckbEntry.getRefLm(), new SupplierAnomaly(ckbEntry.getRefLm(), ckbEntry.getNomsFournisseursCommerciauxBu(), transformedStepEntry.getNomsFournisseursCommerciauxBu()));
                }
            } else {
                this.supplierAnomalies.put(ckbEntry.getRefLm(), new SupplierAnomaly(ckbEntry.getRefLm(), ckbEntry.getNomsFournisseursCommerciauxBu(), transformedStepEntry.getNomsFournisseursCommerciauxBu()));
            }
        }
    }

    private void checkIsFirstPriceCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        if (!StringUtils.equals(ckbEntry.getPremierPrix(), transformedStepEntry.getPremierPrix())) {
            // System.out.println("Mauvais premier prix : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getPremierPrix() + " / Step: " + transformedStepEntry.getPremierPrix() + "]");
            this.firstPriceAnomalies.put(ckbEntry.getRefLm(), new FirstPriceAnomaly(ckbEntry.getRefLm(), ckbEntry.getPremierPrix(), transformedStepEntry.getPremierPrix()));
        }
    }

    private void checkIsDisplayDimensionCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        boolean hasIncorrectDimension = !this.areDimensionsEquals(ckbEntry.getHauteurEnCm(), transformedStepEntry.getHauteurEnCm());
        // System.out.println("Mauvaise hauteur dimension display : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getHauteurEnCm() + " / Step: " + transformedStepEntry.getHauteurEnCm() + "]");

        if (!this.areDimensionsEquals(ckbEntry.getLargeurEnCm(), transformedStepEntry.getLargeurEnCm())) {
            // System.out.println("Mauvaise largeur dimension display : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getLargeurEnCm() + " / Step: " + transformedStepEntry.getLargeurEnCm() + "]");
            hasIncorrectDimension = true;
        }

        if (!this.areDimensionsEquals(ckbEntry.getProfondeurEnCm(), transformedStepEntry.getProfondeurEnCm())) {
            // System.out.println("Mauvaise profondeur dimension display : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProfondeurEnCm() + " / Step: " + transformedStepEntry.getProfondeurEnCm() + "]");
            hasIncorrectDimension = true;
        }

        if (hasIncorrectDimension) {
            this.displayDimensionAnomalies.put(ckbEntry.getRefLm(), new DisplayDimensionAnomaly(ckbEntry.getRefLm(), ckbEntry.getHauteurEnCm(), transformedStepEntry.getHauteurEnCm(), ckbEntry.getLargeurEnCm(), transformedStepEntry.getLargeurEnCm(), ckbEntry.getProfondeurEnCm(), transformedStepEntry.getProfondeurEnCm()));
        }
    }

    private void checkIsUnitDimensionCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        boolean hasIncorrectDimension = !this.areDimensionsEquals(ckbEntry.getProduitEmballeHauteurEnCm(), transformedStepEntry.getProduitEmballeHauteurEnCm());
        // System.out.println("Mauvaise hauteur dimension unit : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeHauteurEnCm() + " / Step: " + transformedStepEntry.getProduitEmballeHauteurEnCm() + "]");

        if (!this.areDimensionsEquals(ckbEntry.getProduitEmballeLargeurEnCm(), transformedStepEntry.getProduitEmballeLargeurEnCm())) {
            // System.out.println("Mauvaise largeur dimension unit : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeLargeurEnCm() + " / Step: " + transformedStepEntry.getProduitEmballeLargeurEnCm() + "]");
            hasIncorrectDimension = true;
        }

        if (!this.areDimensionsEquals(ckbEntry.getProduitEmballeProfondeurEnCm(), transformedStepEntry.getProduitEmballeProfondeurEnCm())) {
            // System.out.println("Mauvaise profondeur dimension unit : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeProfondeurEnCm() + " / Step: " + transformedStepEntry.getProduitEmballeProfondeurEnCm() + "]");
            hasIncorrectDimension = true;
        }

        if (hasIncorrectDimension) {
            this.unitDimensionAnomalies.put(ckbEntry.getRefLm(), new UnitDimensionAnomaly(ckbEntry.getRefLm(), ckbEntry.getProduitEmballeHauteurEnCm(), transformedStepEntry.getProduitEmballeHauteurEnCm(), ckbEntry.getProduitEmballeLargeurEnCm(), transformedStepEntry.getProduitEmballeLargeurEnCm(), ckbEntry.getProduitEmballeProfondeurEnCm(), transformedStepEntry.getProduitEmballeProfondeurEnCm()));
        }
    }

    private void checkIsDisplayStepDimensionCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        boolean hasIncorrectDimension = !this.areDimensionsEquals(ckbEntry.getHauteurEnCmHExpoStep(), transformedStepEntry.getHauteurEnCmHExpoStep());
        // System.out.println("Mauvaise hauteur dimension display step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getHauteurEnCmHExpoStep() + " / Step: " + transformedStepEntry.getHauteurEnCmHExpoStep() + "]");

        if (!this.areDimensionsEquals(ckbEntry.getLargeurEnCmWExpoStep(), transformedStepEntry.getLargeurEnCmWExpoStep())) {
            // System.out.println("Mauvaise largeur dimension display step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getLargeurEnCmWExpoStep() + " / Step: " + transformedStepEntry.getLargeurEnCmWExpoStep() + "]");
            hasIncorrectDimension = true;
        }

        if (!this.areDimensionsEquals(ckbEntry.getProfondeurEnCmDExpoStep(), transformedStepEntry.getProfondeurEnCmDExpoStep())) {
            // System.out.println("Mauvaise profondeur dimension display step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProfondeurEnCmDExpoStep() + " / Step: " + transformedStepEntry.getProfondeurEnCmDExpoStep() + "]");
            hasIncorrectDimension = true;
        }

        if (hasIncorrectDimension) {
            this.displayStepDimensionAnomalies.put(ckbEntry.getRefLm(), new DisplayStepDimensionAnomaly(ckbEntry.getRefLm(), ckbEntry.getHauteurEnCmHExpoStep(), transformedStepEntry.getHauteurEnCmHExpoStep(), ckbEntry.getLargeurEnCmWExpoStep(), transformedStepEntry.getLargeurEnCmWExpoStep(), ckbEntry.getProfondeurEnCmDExpoStep(), transformedStepEntry.getProfondeurEnCmDExpoStep()));
        }
    }

    private void checkIsUnitStepDimensionCorrect(CKBEntry ckbEntry, CKBEntry transformedStepEntry) {
        boolean hasIncorrectDimension = !this.areDimensionsEquals(ckbEntry.getProduitEmballeHauteurEnCmHUnitStep(), transformedStepEntry.getProduitEmballeHauteurEnCmHUnitStep());
        // System.out.println("Mauvaise hauteur dimension unit step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeHauteurEnCmHUnitStep() + " / Step: " + transformedStepEntry.getProduitEmballeHauteurEnCmHUnitStep() + "]");

        if (!this.areDimensionsEquals(ckbEntry.getProduitEmballeLargeurEnCmWUnitStep(), transformedStepEntry.getProduitEmballeLargeurEnCmWUnitStep())) {
            // System.out.println("Mauvaise largeur dimension unit step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeLargeurEnCmWUnitStep() + " / Step: " + transformedStepEntry.getProduitEmballeLargeurEnCmWUnitStep() + "]");
            hasIncorrectDimension = true;
        }

        if (!this.areDimensionsEquals(ckbEntry.getProduitEmballeProfondeurEnCmDUnitStep(), transformedStepEntry.getProduitEmballeProfondeurEnCmDUnitStep())) {
            // System.out.println("Mauvaise profondeur dimension unit step : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getProduitEmballeProfondeurEnCmDUnitStep() + " / Step: " + transformedStepEntry.getProduitEmballeProfondeurEnCmDUnitStep() + "]");
            hasIncorrectDimension = true;
        }

        if (hasIncorrectDimension) {
            this.unitStepDimensionAnomalies.put(ckbEntry.getRefLm(), new UnitStepDimensionAnomaly(ckbEntry.getRefLm(), ckbEntry.getProduitEmballeHauteurEnCmHUnitStep(), transformedStepEntry.getProduitEmballeHauteurEnCmHUnitStep(), ckbEntry.getProduitEmballeLargeurEnCmWUnitStep(), transformedStepEntry.getProduitEmballeLargeurEnCmWUnitStep(), ckbEntry.getProduitEmballeProfondeurEnCmDUnitStep(), transformedStepEntry.getProduitEmballeProfondeurEnCmDUnitStep()));
        }
    }

    public void reportDuplicates() {
        String reportingFileName = this.reportingFolder + "00 - Doublons Step.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);
        byLinkCsvWriter.writeListOfRefsInCSV(stepExport.duplicates);

        System.out.println("[Product] Nombre de doublons dans Step : " + stepExport.duplicates.size());
    }

    public void reportSuggestedRestockingAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "04 - Top réappro.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "stepValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.suggestedRestockingAnomalies.values(), SuggestedRestockingAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.suggestedRestockingAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de préco top réappro : " + suggestedRestockingAnomalies.size());
    }

    public void reportRG_TOP_anomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "04 - Top réappro - RG_TOP.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "stepValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.businessRuleRG_TOP.values(), SuggestedRestockingAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.businessRuleRG_TOP.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de préco top réappro lié à la règle de gestion RG_TOP : " + businessRuleRG_TOP.size());
    }

    public void reportGtinAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "05 - UPC.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "stepValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.gtinAnomalies.values(), GtinAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.gtinAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts d'UPC : " + gtinAnomalies.size());
    }

    public void reportSupplierAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "06 - Fournisseur.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "stepValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.supplierAnomalies.values(), SupplierAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.supplierAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de fournisseur : " + supplierAnomalies.size());
    }

    public void reportFirstPriceAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "07 - 1er prix.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "stepValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.firstPriceAnomalies.values(), FirstPriceAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.firstPriceAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de 1er prix : " + firstPriceAnomalies.size());
    }

    public void reportDisplayDimensionAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "08 - Display.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbDisplayWidthValue", "stepDisplayWidthValue", "ckbDisplayHeightValue", "stepDisplayHeightValue", "ckbDisplayDepthValue", "stepDisplayDepthValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.displayDimensionAnomalies.values(), DisplayDimensionAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.displayDimensionAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de dimension Display : " + displayDimensionAnomalies.size());
    }

    public void reportUnitDimensionAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "09 - Unit.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbUnitWidthValue", "stepUnitWidthValue", "ckbUnitHeightValue", "stepUnitHeightValue", "ckbUnitDepthValue", "stepUnitDepthValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.unitDimensionAnomalies.values(), UnitDimensionAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.unitDimensionAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de dimension Unit : " + unitDimensionAnomalies.size());
    }

    public void reportDisplayStepDimensionAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "10 - Display STEP.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbDisplayStepWidthValue", "stepDisplayWidthValue", "ckbDisplayStepHeightValue", "stepDisplayHeightValue", "ckbDisplayStepDepthValue", "stepDisplayDepthValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.displayStepDimensionAnomalies.values(), DisplayStepDimensionAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.displayStepDimensionAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de dimension Display STEP : " + displayStepDimensionAnomalies.size());
    }

    public void reportUnitStepDimensionAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "11 - Unit STEP.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbUnitStepWidthValue", "stepUnitWidthValue", "ckbUnitStepHeightValue", "stepUnitHeightValue", "ckbUnitStepDepthValue", "stepUnitDepthValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.unitStepDimensionAnomalies.values(), UnitStepDimensionAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.unitStepDimensionAnomalies.keySet());
        }

        System.out.println("[Product] Nombre d'écarts de dimension Unit STEP : " + unitStepDimensionAnomalies.size());
    }

    public void reportAllAnomalies(Set<String>... sets) {
        TreeSet<String> productAnomalies = Stream.of(sets).flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));

        if (this.byLinkExport != null) {
            TreeSet<String> unknownProductInByLink = new TreeSet<>(productAnomalies);
            unknownProductInByLink.removeAll(byLinkExport.getOutputByLinkEntries().keySet());

            if (!unknownProductInByLink.isEmpty()) {
                System.out.println("[Product] " + unknownProductInByLink.size() + " produits sont inconnus dans ByLink : " + unknownProductInByLink);
                ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "product-unknown-products.csv");
                byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(unknownProductInByLink));
            }

            productAnomalies.removeAll(unknownProductInByLink);
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(productAnomalies, this.filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "product-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }

        System.out.println("[Product] Nombre d'écarts dans Redwood Product : " + productAnomalies.size());
    }

    private List<TreeSet<String>> splitTreeSet(TreeSet<String> setToSplit, int partitionSize) {
        List<TreeSet<String>> partitions = new ArrayList<>();
        TreeSet<String> currentSet = new TreeSet<>();

        int count = 0;
        for (String element : setToSplit) {
            currentSet.add(element);
            count++;

            if (count == partitionSize) {
                partitions.add(currentSet);
                currentSet = new TreeSet<>();
                count = 0;
            }
        }

        if (!currentSet.isEmpty()) {
            partitions.add(currentSet);
        }

        return partitions;
    }

    private boolean areDimensionsEquals(String ckbDimension, String stepDimension) {

        if (StringUtils.equals(ckbDimension, stepDimension)) {
            return true;
        } else if (StringUtils.isAnyEmpty(ckbDimension, stepDimension)) {
            return false;
        } else {
            float ckbFloatDimension = Float.parseFloat(ckbDimension.replace(",", "."));
            float stepFloatDimension = Float.parseFloat(stepDimension.replace(",", "."));

            if (ckbFloatDimension == stepFloatDimension) {
                return true;
            } else return withoutDoubtingDimensions && ckbFloatDimension / 1000 == stepFloatDimension;
        }
    }
}
