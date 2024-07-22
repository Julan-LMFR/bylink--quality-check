package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import org.apache.commons.text.CaseUtils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ImportantExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputImportantHeaders = new HashMap<>();
    public SortedSet<String> outputImportantEntries = new TreeSet<>();

    public ImportantExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, false);
        this.mapData();
    }

    private void mapData() {
        this.inputBYLinkCSVReader.headers.forEach((key, value) -> this.outputImportantHeaders.put(key, this.formatHeaderName(value)));

        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            for (int index = 0; index < lineData.length; index++) {
                this.outputImportantEntries.add(lineData[0]);
            }
        }
    }

    private String formatHeaderName(String headerName) {
        String camelCaseHeaderName = CaseUtils.toCamelCase(headerName, false, '-', '(', ')');
        return Normalizer.normalize(camelCaseHeaderName, Normalizer.Form.NFD).replaceAll("[^\\p{L}\\p{Z}]", "");
    }
}
