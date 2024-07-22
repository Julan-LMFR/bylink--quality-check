package fr.leroymerlin.bylink.qualitycheck.basa.offerconstruction;

import fr.leroymerlin.bylink.qualitycheck.Checker;
import fr.leroymerlin.bylink.qualitycheck.basa.BasaPCMDEntry;
import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.BasaPCMDExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.ByLinkExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.CKBExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.DataSourceFactory;
import fr.leroymerlin.bylink.qualitycheck.datasource.filewriter.ByLinkCSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OfferConstructionChecker extends Checker {

    public CKBExport ckbExport;
    public BasaPCMDExport basaPCMDExport;
    public ByLinkExport byLinkExport;

    public String reportingFolder;
    public int filePartitionSize;

    public SortedMap<String, RangeLetterAnomaly> rangeLetterAnomalies = new TreeMap<>();
    public SortedMap<String, ProductRankingAnomaly> productRankingAnomalies = new TreeMap<>();

    public SortedMap<String, RangeLetterAnomaly> businessRuleRG_LG = new TreeMap<>();

    public OfferConstructionChecker(String ckbExportFile, String basaPCMDExportFile, boolean withDetailedReporting, int filePartitionSize) {
        this.ckbExport = DataSourceFactory.getCkbExport(ckbExportFile);
        this.basaPCMDExport = DataSourceFactory.getBasaPcmdExport(basaPCMDExportFile);
        this.reportingFolder = this.defineReportingFolder(ckbExportFile);
        this.filePartitionSize = filePartitionSize;

        this.compare();
        this.reportDuplicates(); // N'a rien à faire ici en fait
        this.reportRangeLetterAnomalies(withDetailedReporting);
        this.reportRG_LG_anomalies(withDetailedReporting);
        this.reportProductRankingAnomalies(withDetailedReporting);
        this.reportAllAnomalies(this.rangeLetterAnomalies.keySet(), this.productRankingAnomalies.keySet());
    }

    private void compare() {
        for (Map.Entry<String, CKBEntry> entry : this.ckbExport.getOutputCKBEntries().entrySet()) {
            CKBEntry ckbEntry = entry.getValue();

            if (super.isRefLmProcessable(ckbEntry)) {
                BasaPCMDEntry basaPCMDEntry = this.basaPCMDExport.getOutputBasaPCMDEntries().get(ckbEntry.getRefLm());
                if (basaPCMDEntry != null) {
                    CKBEntry transformedBasaPCMDEntry = basaPCMDEntry.toCKBEntry();

                    this.checkIsRangeLetterCorrect(ckbEntry, transformedBasaPCMDEntry);
                    this.checkIsProductRankingCorrect(ckbEntry, transformedBasaPCMDEntry);
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

    private void checkIsRangeLetterCorrect(CKBEntry ckbEntry, CKBEntry transformedBasaPCMDEntry) {
        if (!StringUtils.equals(ckbEntry.getLettreDeGamme(), transformedBasaPCMDEntry.getLettreDeGamme())) {
            // RG_LG : Si Basa envoi une lettre de gamme vide, celle-ci n'est pas prise en compte dans ByLink
            if (StringUtils.isEmpty(transformedBasaPCMDEntry.getLettreDeGamme()) && StringUtils.isNotEmpty(ckbEntry.getLettreDeGamme())) {
                businessRuleRG_LG.put(ckbEntry.getRefLm(), new RangeLetterAnomaly(ckbEntry.getRefLm(), ckbEntry.getLettreDeGamme(), transformedBasaPCMDEntry.getLettreDeGamme()));
            } else {
                // System.out.println("Mauvais lettrage de gamme : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getLettreDeGamme() + " / Basa: " + transformedBasaPCMDEntry.getLettreDeGamme() + "]");
                this.rangeLetterAnomalies.put(ckbEntry.getRefLm(), new RangeLetterAnomaly(ckbEntry.getRefLm(), ckbEntry.getLettreDeGamme(), transformedBasaPCMDEntry.getLettreDeGamme()));
            }
        }
    }

    private void checkIsProductRankingCorrect(CKBEntry ckbEntry, CKBEntry transformedBasaPCMDEntry) {
        if (!StringUtils.equals(ckbEntry.getClassement(), transformedBasaPCMDEntry.getClassement())) {
            // System.out.println("Mauvais classement : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getClassement() + " / Basa: " + transformedBasaPCMDEntry.getClassement() + "]");
            this.productRankingAnomalies.put(ckbEntry.getRefLm(), new ProductRankingAnomaly(ckbEntry.getRefLm(), ckbEntry.getClassement(), transformedBasaPCMDEntry.getClassement()));
        }
    }

    public void reportDuplicates() {
        String reportingFileName = this.reportingFolder + "00 - Doublons CKB.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);
        byLinkCsvWriter.writeListOfRefsInCSV(ckbExport.duplicates);

        String reportingFileName2 = this.reportingFolder + "00 - Doublons Basa.csv";
        ByLinkCSVWriter byLinkCsvWriter2 = new ByLinkCSVWriter(reportingFileName2);
        byLinkCsvWriter2.writeListOfRefsInCSV(basaPCMDExport.duplicates);

        System.out.println("Nombre de doublons dans CKB : " + ckbExport.duplicates.size());
        System.out.println("Nombre de doublons dans Basa : " + basaPCMDExport.duplicates.size());
    }

    public void reportRangeLetterAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "01 - Gamme.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "basaValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.rangeLetterAnomalies.values(), RangeLetterAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.rangeLetterAnomalies.keySet());
        }

        System.out.println("[Offer Construction] Nombre d'écarts de lettrage de gamme : " + rangeLetterAnomalies.size());
    }

    public void reportRG_LG_anomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "01 - Gamme - RG_LG.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "basaValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.businessRuleRG_LG.values(), RangeLetterAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.businessRuleRG_LG.keySet());
        }

        System.out.println("[Offer Construction] Nombre d'écarts de lettrage de gamme lié à la règle de gestion RG_LG : " + businessRuleRG_LG.size());
    }

    public void reportProductRankingAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "02 - Classement.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "basaValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.productRankingAnomalies.values(), ProductRankingAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.productRankingAnomalies.keySet());
        }
        System.out.println("[Offer Construction] Nombre d'écarts de classement : " + productRankingAnomalies.size());
    }

    public void reportAllAnomalies(Set<String>... sets) {

        TreeSet<String> offerConstructionAnomalies = Stream.of(sets).flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));

        if (this.byLinkExport != null) {
            TreeSet<String> unknownProductInByLink = new TreeSet<>(offerConstructionAnomalies);
            unknownProductInByLink.removeAll(byLinkExport.getOutputByLinkEntries().keySet());

            if (!unknownProductInByLink.isEmpty()) {
                System.out.println("[Offer Construction] " + unknownProductInByLink.size() + " produits sont inconnus dans ByLink : " + unknownProductInByLink);
                ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "offer-construction-unknown-products.csv");
                byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(unknownProductInByLink));
            }

            offerConstructionAnomalies.removeAll(unknownProductInByLink);
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(offerConstructionAnomalies, this.filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "offer-construction-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }

        System.out.println("[Offer Construction] Nombre d'écarts dans Basa Offer Construction : " + offerConstructionAnomalies.size());
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
}
