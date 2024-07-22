package fr.leroymerlin.bylink.qualitycheck;

import fr.leroymerlin.bylink.qualitycheck.basa.BasaPCMDEntry;
import fr.leroymerlin.bylink.qualitycheck.basa.lifecycle.LifeCycleEnum;
import fr.leroymerlin.bylink.qualitycheck.datasource.*;
import fr.leroymerlin.bylink.qualitycheck.datasource.filewriter.ByLinkCSVWriter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class GeneralChecker extends Checker {

    public CKBExport ckbExport;
    public StepExport stepExport;
    public BasaPCMDExport basaPCMDExport;
    public ByLinkExport byLinkExport;
    public ImportantExport importantExport;

    public String reportingFolder;
    public int filePartitionSize;

    public GeneralChecker(String ckbExportFile, String stepExportFile, String basaPCMDExportFile, String importantFile, boolean withDetailedReporting, int filePartitionSize) {
        this.ckbExport = DataSourceFactory.getCkbExport(ckbExportFile);
        this.stepExport = DataSourceFactory.getStepExport(stepExportFile);
        this.basaPCMDExport = DataSourceFactory.getBasaPcmdExport(basaPCMDExportFile);
        this.importantExport = DataSourceFactory.getImportantExport(importantFile);
        this.reportingFolder = this.defineReportingFolder(ckbExportFile);
        this.filePartitionSize = filePartitionSize;

        this.reportUnknownProducts();
        this.reportByLifecycle(LifeCycleEnum.TRADE_ON.getName(), false);
        this.reportByLifecycle(LifeCycleEnum.INTERNAL_SERVICE_ON.getName(),false);
        this.reportByLifecycle(LifeCycleEnum.INIT.getName(),false);
        this.reportByLifecycle(LifeCycleEnum.SCHEDULED_FOR_DELETE.getName(),false);
        this.reportByLifecycle(LifeCycleEnum.DELETE_IN_PROGRESS.getName(),false);
        this.reportByLifecycle(LifeCycleEnum.DELETED.getName(),false);
        this.reportByLifecycle(LifeCycleEnum.PRE_INIT.getName(),false);

        this.reportByLifecycle(LifeCycleEnum.TRADE_ON.getName(), true);
        this.reportByLifecycle(LifeCycleEnum.INTERNAL_SERVICE_ON.getName(),true);
        this.reportByLifecycle(LifeCycleEnum.INIT.getName(),true);
        this.reportByLifecycle(LifeCycleEnum.SCHEDULED_FOR_DELETE.getName(),true);
        this.reportByLifecycle(LifeCycleEnum.DELETE_IN_PROGRESS.getName(),true);
        this.reportByLifecycle(LifeCycleEnum.DELETED.getName(),true);
        this.reportByLifecycle(LifeCycleEnum.PRE_INIT.getName(),true);
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

    private void reportUnknownProducts() {
        TreeSet<String> unknownProducts = ckbExport.outputCKBEntries.keySet().stream()
                .filter(key -> !basaPCMDExport.getOutputBasaPCMDEntries().containsKey(key) || !stepExport.getOutputStepEntries().containsKey(key))
                .collect(Collectors.toCollection(TreeSet::new));

        TreeSet<String> knownProducts = ckbExport.outputCKBEntries.keySet().stream()
                .filter(key -> basaPCMDExport.getOutputBasaPCMDEntries().containsKey(key) && stepExport.getOutputStepEntries().containsKey(key))
                .collect(Collectors.toCollection(TreeSet::new));

        System.out.println("Il y a " + importantExport.outputImportantEntries.size() + " produits importants");
        System.out.println(unknownProducts.size() + " / " + ckbExport.getOutputCKBEntries().size() + " produits de CKB sont inconnus dans Step ou Basa");
        System.out.println(knownProducts.size() + " / " + ckbExport.getOutputCKBEntries().size() + " produits de CKB connus dans Step et Basa");

        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "unknown-products-of-ckb-in-lm-repositories.csv");
        byLinkCsvWriter.writeListOfRefsInCSV(unknownProducts);
    }

    private void reportByLifecycle(String lifecycle, boolean important) {
        TreeSet<String> knownProducts;

        if(important) {
            knownProducts = importantExport.outputImportantEntries.stream()
                    .filter(key -> basaPCMDExport.getOutputBasaPCMDEntries().containsKey(key) && stepExport.getOutputStepEntries().containsKey(key))
                    .collect(Collectors.toCollection(TreeSet::new));

            // System.out.println("Il y a " + knownProducts.size() + " produits importants  de CKB connus dans Step et Basa");
        } else {
            TreeSet<String> importantProducts = importantExport.outputImportantEntries.stream()
                    .filter(key -> basaPCMDExport.getOutputBasaPCMDEntries().containsKey(key) && stepExport.getOutputStepEntries().containsKey(key))
                    .collect(Collectors.toCollection(TreeSet::new));

            knownProducts = ckbExport.outputCKBEntries.keySet().stream()
                    .filter(key -> basaPCMDExport.getOutputBasaPCMDEntries().containsKey(key) && stepExport.getOutputStepEntries().containsKey(key) && !importantExport.outputImportantEntries.contains(key))
                    .collect(Collectors.toCollection(TreeSet::new));

            // System.out.println("Il y a " + importantProducts.size() + " produits importants  de CKB connus dans Step et Basa");
            // System.out.println("Il reste " + knownProducts.size() + " sans les produits importants  de CKB connus dans Step et Basa");
        }

        TreeSet<String> filteredToLifecycle = basaPCMDExport.getOutputBasaPCMDEntries().values().stream().filter(entry -> knownProducts.contains(entry.getProductReferenceBU()) && entry.getProductActiveLifeCycle().equals(lifecycle)).map(BasaPCMDEntry::getProductReferenceBU).collect(Collectors.toCollection(TreeSet::new));

        System.out.println(filteredToLifecycle.size() + " produits " + (important ? "importants " : "") + "ont le cycle de vie : " + lifecycle);

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(filteredToLifecycle, this.filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + LifeCycleEnum.getLevelByName(lifecycle) + " - " + LifeCycleEnum.getDescByName(lifecycle) + File.separator + "products-" + (important ? "important-" : "") + lifecycle + "-" + partitionIndex + ".csv");
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
}
