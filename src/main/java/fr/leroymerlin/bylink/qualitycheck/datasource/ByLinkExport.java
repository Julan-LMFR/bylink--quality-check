package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.bylink.ByLinkEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.text.CaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.*;

@Getter
@Setter
public class ByLinkExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputByLinkHeaders = new HashMap<>();
    public SortedMap<String, ByLinkEntry> outputByLinkEntries = new TreeMap<>();
    public SortedSet<String> duplicates = new TreeSet<>();

    public ByLinkExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, false);
        this.mapData();
    }

    private void mapData() {
        // Headers of the original file could not be used as is
        this.inputBYLinkCSVReader.headers.forEach((key, value) -> this.outputByLinkHeaders.put(key, this.formatHeaderName(value)));

        int lineNumber = 0;
        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            ByLinkEntry byLinkEntry = new ByLinkEntry();

            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(byLinkEntry, this.outputByLinkHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    System.out.println("CSV line index : " + lineNumber + " / column index : " + index);
                    System.out.println("ByLink Export headers : " + this.outputByLinkHeaders);
                    System.out.println("Concerned header : " + this.outputByLinkHeaders.get(index));
                    throw new RuntimeException(e);
                }
            }

            if(byLinkEntry.getId() != null && outputByLinkEntries.containsKey(byLinkEntry.getId())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS BYLINK ------------------------------");
                }
                duplicates.add(byLinkEntry.getId());
            }

            outputByLinkEntries.put(byLinkEntry.getId(), byLinkEntry);
            lineNumber++;
        }
    }

    private String formatHeaderName(String headerName) {
        String camelCaseHeaderName = CaseUtils.toCamelCase(headerName, false, '-', '(', ')');
        return Normalizer.normalize(camelCaseHeaderName, Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{Z}]", "");
    }
}
