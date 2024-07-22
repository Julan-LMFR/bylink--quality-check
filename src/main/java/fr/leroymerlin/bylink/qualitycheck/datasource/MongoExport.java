package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.bylink.MongoEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.text.CaseUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class MongoExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputMongoHeaders = new HashMap<>();
    public SortedMap<String, MongoEntry> outputMongoEntries = new TreeMap<>();

    public MongoExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, false);
        this.mapData();
    }

    private void mapData() {
        this.inputBYLinkCSVReader.headers.forEach((key, value) -> this.outputMongoHeaders.put(key, this.formatHeaderName(value)));

        int lineNumber = 0;
        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            MongoEntry mongoEntry = new MongoEntry();

            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(mongoEntry, this.outputMongoHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    System.out.println("CSV line index : " + lineNumber + " / column index : " + index);
                    System.out.println("Step Export headers : " + this.outputMongoHeaders);
                    System.out.println("Concerned header : " + this.outputMongoHeaders.get(index));
                    throw new RuntimeException(e);
                }
            }

            outputMongoEntries.put(mongoEntry.getId(), mongoEntry);
            lineNumber++;
        }
    }

    private String formatHeaderName(String headerName) {
        return Normalizer.normalize(headerName, Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{Z}]", "");
    }
}
