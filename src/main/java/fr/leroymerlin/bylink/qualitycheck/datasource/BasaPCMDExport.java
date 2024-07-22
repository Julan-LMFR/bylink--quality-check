package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.basa.BasaPCMDEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Getter
@Setter
public class BasaPCMDExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputBasaPCMDHeaders = new HashMap<>();
    public SortedMap<String, BasaPCMDEntry> outputBasaPCMDEntries = new TreeMap<>();
    public SortedSet<String> duplicates = new TreeSet<>();

    public BasaPCMDExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, true);
        this.mapData();
    }

    private void mapData() {

        // No change needed in headers
        this.outputBasaPCMDHeaders.putAll(inputBYLinkCSVReader.getHeaders());

        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            BasaPCMDEntry basaPCMDEntry = new BasaPCMDEntry();

            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(basaPCMDEntry, this.outputBasaPCMDHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            if(basaPCMDEntry.getProductReferenceBU() != null && outputBasaPCMDEntries.containsKey(basaPCMDEntry.getProductReferenceBU())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS BASA ------------------------------");
                }
                duplicates.add(basaPCMDEntry.getProductReferenceBU());
                // System.out.println(basaPCMDEntry.getProductReferenceBU());
                // System.out.println("\t→ Actuel: " + outputBasaPCMDEntries.get(basaPCMDEntry.getProductReferenceBU()));
                // System.out.println("\t→ Nouveau: " + basaPCMDEntry);
            }

            outputBasaPCMDEntries.put(basaPCMDEntry.getProductReferenceBU(), basaPCMDEntry);
        }

        /**for (String line : inputBYLinkCSVReader.getLines()) {
            BasaPCMDEntry basaPCMDEntry = new BasaPCMDEntry();

            String[] lineData = line.split(";");
            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(basaPCMDEntry, this.outputBasaPCMDHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            if(basaPCMDEntry.getProductReferenceBU() != null && outputBasaPCMDEntries.containsKey(basaPCMDEntry.getProductReferenceBU())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS BASA ------------------------------");
                }
                duplicates.add(basaPCMDEntry.getProductReferenceBU());
                // System.out.println(basaPCMDEntry.getProductReferenceBU());
                // System.out.println("\t→ Actuel: " + outputBasaPCMDEntries.get(basaPCMDEntry.getProductReferenceBU()));
                // System.out.println("\t→ Nouveau: " + basaPCMDEntry);
            }

            outputBasaPCMDEntries.put(basaPCMDEntry.getProductReferenceBU(), basaPCMDEntry);
        }*/
    }
}
