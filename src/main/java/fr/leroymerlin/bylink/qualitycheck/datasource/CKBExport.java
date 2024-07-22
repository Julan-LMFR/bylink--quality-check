package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.ByLinkExcelReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.text.CaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.*;

@Getter
@Setter
public class CKBExport {

    public ByLinkExcelReader inputByLinkExcelReader;

    public Map<Integer, String> outputCKBHeaders = new HashMap<>();
    public SortedMap<String, CKBEntry> outputCKBEntries = new TreeMap<>();
    public SortedSet<String> duplicates = new TreeSet<>();

    public CKBExport(String sourceFile) {
        this.inputByLinkExcelReader = new ByLinkExcelReader(sourceFile);
        this.mapData();
    }

    private void mapData() {
        // Headers of the original file could not be used as is
        this.inputByLinkExcelReader.headers.forEach((key, value) -> this.outputCKBHeaders.put(key, this.formatHeaderName(value)));

        for (String line : inputByLinkExcelReader.getLines()) {
            CKBEntry ckbEntry = new CKBEntry();

            String[] lineData = line.split(";");
            if(lineData.length < 26) {
                System.out.println("Erreur " + lineData[0] + " " + lineData.length);
            }
            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(ckbEntry, this.outputCKBHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            if(ckbEntry.getRefLm() != null && outputCKBEntries.containsKey(ckbEntry.getRefLm())) {
                if(duplicates.isEmpty()) {
                    // System.out.println("\nDOUBLONS CKB ------------------------------");
                }
                duplicates.add(ckbEntry.getRefLm());
                // System.out.println(ckbEntry.getRefLm());
                // System.out.println("\t→ Actuel: " + outputCKBEntries.get(ckbEntry.getRefLm()));
                // System.out.println("\t→ Nouveau: " + ckbEntry);
            }

            outputCKBEntries.put(ckbEntry.getRefLm(), ckbEntry);
        }
    }

    private String formatHeaderName(String headerName) {
        String camelCaseHeaderName = CaseUtils.toCamelCase(headerName, false, '-', '(', ')');
        return Normalizer.normalize(camelCaseHeaderName, Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{Z}]", "").replace("erPrix", "premierPrix");
    }
}
