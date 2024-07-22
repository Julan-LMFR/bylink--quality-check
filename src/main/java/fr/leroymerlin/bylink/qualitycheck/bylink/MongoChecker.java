package fr.leroymerlin.bylink.qualitycheck.bylink;

import fr.leroymerlin.bylink.qualitycheck.Checker;
import fr.leroymerlin.bylink.qualitycheck.datasource.*;
import fr.leroymerlin.bylink.qualitycheck.datasource.filewriter.ByLinkCSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class MongoChecker extends Checker {

    public CKBExport ckbExport;
    public StepExport stepExport;
    public BasaPCMDExport basaPCMDExport;
    public ImportantExport importantExport;
    public MongoExport mongoExport;

    public int importantRefIntegrated = 0;

    public String reportingFolder;
    public int filePartitionSize;

    public MongoChecker(String ckbExportFile, String stepExportFile, String basaPCMDExportFile, String importantFile, String mongoExportFile, int filePartitionSize) {
        this.importantExport = DataSourceFactory.getImportantExport(importantFile);
        this.mongoExport = DataSourceFactory.getMongoExport(mongoExportFile);
        this.reportingFolder = this.defineReportingFolder(mongoExportFile);
        this.filePartitionSize = filePartitionSize;

        this.reportNumberOfImportantProducts(); // Should be the first step
        this.reportMissingOfferConstruction(5000, false);
        this.reportMissingNomenclature(5000, false);
        this.reportMissingRedwood(1000, false);
        this.reportMissingLifecycle(5000, false);
        this.reportMissingPurchase(5000, false);
        this.reportMissingOpus(5000, false);
    }

    private String defineReportingFolder(String mongoExportFile) {
        File inputFile = new File(mongoExportFile);
        File parentDirectory = inputFile.getParentFile().getParentFile();

        if (parentDirectory != null) {
            return parentDirectory.getAbsolutePath() + File.separator + "Missing Mongo" + File.separator;
        } else {
            System.err.println("Impossible de récupérer le répertoire parent pour le chemin : " + mongoExportFile);
            return null;
        }
    }

    private void reportNumberOfImportantProducts() {
        TreeSet<String> important = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        this.importantRefIntegrated = important.size();

        System.out.println("[Mongo] " + important.size() + " important products are in MongoDB");
    }

    private void reportMissingOfferConstruction(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getOfferConstructionLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have offer-construction information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getOfferConstructionLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have offer-construction information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-offer-construction-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
    }

    private void reportMissingRedwood(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getRedwoodLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have redwood information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getRedwoodLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have redwood information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-redwood-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
    }

    private void reportMissingLifecycle(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getLifeCycleLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have lifecycle information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getLifeCycleLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have lifecycle information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-lifecycle-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
    }

    private void reportMissingOpus(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getOpusLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have opus information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getOpusLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have opus information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-opus-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
    }

    private void reportMissingPurchase(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getPurchaseLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have purchase information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getPurchaseLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have purchase information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-purchase-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
    }

    private void reportMissingNomenclature(int filePartitionSize, boolean important) {
        TreeSet<String> missing = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getNomenclatureLastUpdatedDate())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (!important) {
            System.out.println("[Mongo] " + missing.size() + " / " + mongoExport.outputMongoEntries.size() + " products does not have nomenclature information");
        }

        TreeSet<String> missingImportant = mongoExport.outputMongoEntries.values().stream().filter(mongoEntry -> StringUtils.isEmpty(mongoEntry.getNomenclatureLastUpdatedDate()) && importantExport.outputImportantEntries.contains(mongoEntry.getId())).map(MongoEntry::getId).collect(Collectors.toCollection(TreeSet::new));
        if (important) {
            System.out.println("[Mongo] " + missingImportant.size() + " / " + this.importantRefIntegrated + " important products does not have nomenclature information");
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(getFileToProcess(missing, missingImportant, important), filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "mongo-missing-nomenclature-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }
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

    private TreeSet<String> getFileToProcess(TreeSet<String> missing, TreeSet<String> missingImportant, boolean important) {
        return important ? missingImportant : missing;
    }
}
