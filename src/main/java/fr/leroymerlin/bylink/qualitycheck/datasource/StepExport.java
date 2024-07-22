package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import fr.leroymerlin.bylink.qualitycheck.step.StepEntry;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.text.CaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.*;

@Getter
@Setter
public class StepExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputStepHeaders = new HashMap<>();
    public SortedMap<String, StepEntry> outputStepEntries = new TreeMap<>();
    public SortedSet<String> duplicates = new TreeSet<>();

    public StepExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, false);
        this.mapData();
    }

    private void mapData() {
        // Headers of the original file could not be used as is
        this.inputBYLinkCSVReader.headers.forEach((key, value) -> this.outputStepHeaders.put(key, this.formatHeaderName(value)));

        int lineNumber = 0;
        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            StepEntry stepEntry = new StepEntry();

            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(stepEntry, this.outputStepHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    System.out.println("CSV line index : " + lineNumber + " / column index : " + index);
                    System.out.println("Step Export headers : " + this.outputStepHeaders);
                    System.out.println("Concerned header : " + this.outputStepHeaders.get(index));
                    throw new RuntimeException(e);
                }
            }

            if(stepEntry.getRefLm() != null && outputStepEntries.containsKey(stepEntry.getRefLm())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS STEP ------------------------------");
                }
                duplicates.add(stepEntry.getRefLm());
                // System.out.println(basaPCMDEntry.getProductReferenceBU());
                // System.out.println("\t→ Actuel: " + outputBasaPCMDEntries.get(basaPCMDEntry.getProductReferenceBU()));
                // System.out.println("\t→ Nouveau: " + basaPCMDEntry);
            }

            outputStepEntries.put(stepEntry.getRefLm(), stepEntry);
            lineNumber++;
        }

        /** int lineNumber = 0;
        for (String line : inputBYLinkCSVReader.getLines()) {
            StepEntry stepEntry = new StepEntry();

            String[] lineData = line.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            for (int index = 0; index < lineData.length; index++) {
                lineData[index] = lineData[index].replace("\"","");
                try {
                    BeanUtils.setProperty(stepEntry, this.outputStepHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    System.out.println("CSV line index : " + lineNumber + " / column index : " + index);
                    System.out.println("Step Export headers : " + this.outputStepHeaders);
                    System.out.println("Concerned header : " + this.outputStepHeaders.get(index));
                    throw new RuntimeException(e);
                }
            }

            if(stepEntry.getRefLm() != null && outputStepEntries.containsKey(stepEntry.getRefLm())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS BASA ------------------------------");
                }
                duplicates.add(stepEntry.getRefLm());
                // System.out.println(basaPCMDEntry.getProductReferenceBU());
                // System.out.println("\t→ Actuel: " + outputBasaPCMDEntries.get(basaPCMDEntry.getProductReferenceBU()));
                // System.out.println("\t→ Nouveau: " + basaPCMDEntry);
            }

            outputStepEntries.put(stepEntry.getRefLm(), stepEntry);
            lineNumber++;
        }
        System.out.println("Read lines: " + lineNumber + "Number of rows: " + outputStepEntries.size());
         */
    }

    private String formatHeaderName(String headerName) {
        String camelCaseHeaderName = CaseUtils.toCamelCase(headerName, false, '-', '(', ')');
        return Normalizer.normalize(camelCaseHeaderName, Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{Z}]", "");
    }
}
