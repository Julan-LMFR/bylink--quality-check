package fr.leroymerlin.bylink.qualitycheck.basa.lifecycle;

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

public class LifecycleChecker extends Checker {

    public CKBExport ckbExport;
    public BasaPCMDExport basaPCMDExport;
    public ByLinkExport byLinkExport;

    public String reportingFolder;
    public int filePartitionSize;

    public SortedMap<String, LifecycleAnomaly> lifecycleAnomalies = new TreeMap<>();

    public LifecycleChecker(String ckbExportFile, String basaPCMDExportFile, boolean withDetailedReporting, int filePartitionSize) {
        this.ckbExport = DataSourceFactory.getCkbExport(ckbExportFile);
        this.basaPCMDExport = DataSourceFactory.getBasaPcmdExport(basaPCMDExportFile);
        this.reportingFolder = this.defineReportingFolder(ckbExportFile);
        this.filePartitionSize = filePartitionSize;

        this.compare();
        this.reportLifecycleAnomalies(withDetailedReporting);
        this.reportAllAnomalies(this.lifecycleAnomalies.keySet());
    }

    private void compare() {
        for (Map.Entry<String, CKBEntry> entry : this.ckbExport.getOutputCKBEntries().entrySet()) {
            CKBEntry ckbEntry = entry.getValue();

            if (super.isRefLmProcessable(ckbEntry)) {
                BasaPCMDEntry basaPCMDEntry = this.basaPCMDExport.getOutputBasaPCMDEntries().get(ckbEntry.getRefLm());
                if (basaPCMDEntry != null) {

                    CKBEntry transformedBasaPCMDEntry = basaPCMDEntry.toCKBEntry();

                    this.checkIsLifecycleCorrect(ckbEntry, transformedBasaPCMDEntry);
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

    private void checkIsLifecycleCorrect(CKBEntry ckbEntry, CKBEntry transformedBasaPCMDEntry) {
        if (!StringUtils.equals(ckbEntry.getCycleDeVie(), transformedBasaPCMDEntry.getCycleDeVie())) {
            // System.out.println("Mauvais cycle de vie : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getCycleDeVie() + " / Basa: " + transformedBasaPCMDEntry.getCycleDeVie() + "]");
            this.lifecycleAnomalies.put(ckbEntry.getRefLm(), new LifecycleAnomaly(ckbEntry.getRefLm(), ckbEntry.getCycleDeVie(), transformedBasaPCMDEntry.getCycleDeVie()));
        }
    }

    public void reportLifecycleAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "12 - Cycle de vie.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbValue", "basaValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.lifecycleAnomalies.values(), LifecycleAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.lifecycleAnomalies.keySet());
        }
        System.out.println("[Lifecycle] Nombre d'écarts de cycle de vie : " + lifecycleAnomalies.size());
    }

    public void reportAllAnomalies(Set<String>... sets) {
        TreeSet<String> lifecycleAnomalies = Stream.of(sets).flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));

        if (this.byLinkExport != null) {
            TreeSet<String> unknownProductInByLink = new TreeSet<>(lifecycleAnomalies);
            unknownProductInByLink.removeAll(byLinkExport.getOutputByLinkEntries().keySet());

            if (!unknownProductInByLink.isEmpty()) {
                System.out.println("[Lifecycle] " + unknownProductInByLink.size() + " produits sont inconnus dans ByLink : " + unknownProductInByLink);
                ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "lifecycle-unknown-products.csv");
                byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(unknownProductInByLink));
            }

            lifecycleAnomalies.removeAll(unknownProductInByLink);
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(lifecycleAnomalies, this.filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "lifecycle-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }

        System.out.println("[Lifecycle] Nombre d'écarts dans Basa Lifecycle : " + lifecycleAnomalies.size());
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
