package fr.leroymerlin.bylink.qualitycheck.basa.nomenclature;

import fr.leroymerlin.bylink.qualitycheck.Checker;
import fr.leroymerlin.bylink.qualitycheck.basa.BasaMNEntry;
import fr.leroymerlin.bylink.qualitycheck.basa.BasaPCMDEntry;
import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.*;
import fr.leroymerlin.bylink.qualitycheck.datasource.filewriter.ByLinkCSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NomenclatureChecker extends Checker {

    public CKBExport ckbExport;
    public BasaPCMDExport basaPCMDExport;
    public BasaMNExport basaMNExport;
    public ByLinkExport byLinkExport;

    public Map<String, BasaMNEntry> nomenclatureCache = new HashMap<>();

    public String reportingFolder;
    public int filePartitionSize;

    public SortedMap<String, NomenclatureAnomaly> nomenclatureAnomalies = new TreeMap<>();

    public NomenclatureChecker(String ckbExportFile, String basaPCMDExportFile, String basaMNExportFile, boolean withDetailedReporting, int filePartitionSize) {
        this.ckbExport = DataSourceFactory.getCkbExport(ckbExportFile);
        this.basaPCMDExport = DataSourceFactory.getBasaPcmdExport(basaPCMDExportFile);
        this.basaMNExport = DataSourceFactory.getBasaMnExport(basaMNExportFile);
        this.reportingFolder = this.defineReportingFolder(ckbExportFile);
        this.filePartitionSize = filePartitionSize;

        this.compare();
        this.reportNomenclatureAnomalies(withDetailedReporting);
        this.reportAllAnomalies(nomenclatureAnomalies.keySet());
    }

    private void compare() {
        for (Map.Entry<String, CKBEntry> entry : this.ckbExport.getOutputCKBEntries().entrySet()) {
            CKBEntry ckbEntry = entry.getValue();

            if (super.isRefLmProcessable(ckbEntry)) {
                BasaPCMDEntry basaPCMDEntry = this.basaPCMDExport.getOutputBasaPCMDEntries().get(ckbEntry.getRefLm());
                if (basaPCMDEntry != null) {

                    BasaMNEntry basaMNEntry = findAssociatedNomenclature(basaPCMDEntry);
                    CKBEntry transformedBasaPCMDEntry = basaPCMDEntry.toFullCKBEntry(basaMNEntry);

                    this.checkIsNomenclatureCorrect(ckbEntry, basaPCMDEntry, transformedBasaPCMDEntry);
                }
            }
        }
    }

    private BasaMNEntry findAssociatedNomenclature(BasaPCMDEntry basaPCMDEntry) {

        BasaMNEntry basaMNEntry = this.nomenclatureCache.get(basaPCMDEntry.getDepartmentIdentifier() + "," + basaPCMDEntry.getSubDepartmentIdentifier() + "," + basaPCMDEntry.getTypeIdentifier() + "," + basaPCMDEntry.getSubTypeIdentifier());

        if (basaMNEntry == null) {
            try {
                basaMNEntry = basaMNExport.outputBasaMNEntries.stream().filter(nomenclature -> nomenclature.departmentidentifier.equals(basaPCMDEntry.getDepartmentIdentifier()) && nomenclature.subdepartmentidentifier.equals(basaPCMDEntry.getSubDepartmentIdentifier()) && nomenclature.typeidentifier.equals(basaPCMDEntry.getTypeIdentifier()) && nomenclature.subtypeidentifier.equals(basaPCMDEntry.getSubTypeIdentifier())).toList().getFirst();
                this.nomenclatureCache.put(basaPCMDEntry.getDepartmentIdentifier() + "," + basaPCMDEntry.getSubDepartmentIdentifier() + "," + basaPCMDEntry.getTypeIdentifier() + "," + basaPCMDEntry.getSubTypeIdentifier(), basaMNEntry);
            } catch (NoSuchElementException exception) {
                System.out.println("Pas de nomenclature trouvée pour : " + basaPCMDEntry.getProductReferenceBU() + " [Rayon: " + basaPCMDEntry.getDepartmentIdentifier() + "] [Sous-rayon: " + basaPCMDEntry.getSubDepartmentIdentifier() + "] [Type: " + basaPCMDEntry.getTypeIdentifier() + "] [Sous-type: " + basaPCMDEntry.getSubTypeIdentifier() + "]");
            }
        }

        return basaMNEntry;
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

    private void checkIsNomenclatureCorrect(CKBEntry ckbEntry, BasaPCMDEntry basaPCMDEntry, CKBEntry transformedBasaPCMDEntry) {
        boolean hasIncorrectNomenclature = !StringUtils.equals(ckbEntry.getRayon(), transformedBasaPCMDEntry.getRayon());

        // System.out.println("Mauvais rayon : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getRayon() + " / Basa: " + transformedBasaPCMDEntry.getRayon() + "]");
        if (!StringUtils.equals(ckbEntry.getSousRayon(), transformedBasaPCMDEntry.getSousRayon())) {
            // System.out.println("Mauvais sous-rayon : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getSousRayon() + " / Basa: " + transformedBasaPCMDEntry.getSousRayon() + "]");
            hasIncorrectNomenclature = true;
        }
        if (!StringUtils.equals(ckbEntry.getType(), transformedBasaPCMDEntry.getType())) {
            // System.out.println("Mauvais type : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getType() + " / Basa: " + transformedBasaPCMDEntry.getType() + "]");
            hasIncorrectNomenclature = true;
        }
        if (!StringUtils.equals(ckbEntry.getSousType(), transformedBasaPCMDEntry.getSousType())) {
            // System.out.println("Mauvais sous-type : " + ckbEntry.getRefLm() + " [CKB: " + ckbEntry.getSousType() + " / Basa: " + transformedBasaPCMDEntry.getSousType() + "]");
            hasIncorrectNomenclature = true;
        }

        if (hasIncorrectNomenclature) {
            this.nomenclatureAnomalies.put(ckbEntry.getRefLm(), new NomenclatureAnomaly(ckbEntry.getRefLm(), ckbEntry.getRayon(), transformedBasaPCMDEntry.getRayon(), ckbEntry.getSousRayon(), transformedBasaPCMDEntry.getSousRayon(), ckbEntry.getType(), transformedBasaPCMDEntry.getType(), ckbEntry.getSousType(), transformedBasaPCMDEntry.getSousType()));
        }
    }

    public void reportNomenclatureAnomalies(boolean withDetailedReporting) {
        String reportingFileName = this.reportingFolder + "03 - Nomenclature.csv";
        ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(reportingFileName);

        if (withDetailedReporting) {
            List<String> headers = Arrays.asList("refLm", "ckbDepartmentValue", "basaDepartmentValue", "ckbSubDepartmentValue", "basaSubDepartmentValue", "ckbTypeValue", "basaTypeValue", "ckbSubTypeValue", "basaSubTypeValue");
            byLinkCsvWriter.writeElementsInCSV(headers, this.nomenclatureAnomalies.values(), NomenclatureAnomaly.class);
        } else {
            byLinkCsvWriter.writeListOfRefsInCSV(this.nomenclatureAnomalies.keySet());
        }

        System.out.println("[Nomenclature] Nombre d'écarts de nomenclature : " + nomenclatureAnomalies.size());
    }

    public void reportAllAnomalies(Set<String>... sets) {

        TreeSet<String> nomenclatureAnomalies = Stream.of(sets).flatMap(Set::stream).collect(Collectors.toCollection(TreeSet::new));

        if (this.byLinkExport != null) {
            TreeSet<String> unknownProductInByLink = new TreeSet<>(nomenclatureAnomalies);
            unknownProductInByLink.removeAll(byLinkExport.getOutputByLinkEntries().keySet());

            if (!unknownProductInByLink.isEmpty()) {
                System.out.println("[Nomenclature] " + unknownProductInByLink.size() + " produits sont inconnus dans ByLink : " + unknownProductInByLink);
                ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "nomenclature-unknown-products.csv");
                byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(unknownProductInByLink));
            }

            nomenclatureAnomalies.removeAll(unknownProductInByLink);
        }

        int partitionIndex = 1;

        for (TreeSet<String> partition : splitTreeSet(nomenclatureAnomalies, this.filePartitionSize)) {
            ByLinkCSVWriter byLinkCsvWriter = new ByLinkCSVWriter(this.reportingFolder + "nomenclature-" + partitionIndex + ".csv");
            byLinkCsvWriter.writeListOfRefsInCSV(new TreeSet<>(partition));
            partitionIndex++;
        }

        System.out.println("[Nomenclature] Nombre d'écarts dans Basa Nomenclature : " + nomenclatureAnomalies.size());
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
