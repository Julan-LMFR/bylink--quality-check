package fr.leroymerlin.bylink.qualitycheck.datasource;

import fr.leroymerlin.bylink.qualitycheck.basa.BasaMNEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.filereader.BYLinkCSVReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BasaMNExport {

    public BYLinkCSVReader inputBYLinkCSVReader;

    public Map<Integer, String> outputBasaMNHeaders = new HashMap<>();
    public List<BasaMNEntry> outputBasaMNEntries = new LinkedList<>();

    public BasaMNExport(String sourceFile) {
        this.inputBYLinkCSVReader = new BYLinkCSVReader(sourceFile, true);
        this.mapData();
    }

    private void mapData() {
        // No change needed in headers
        this.outputBasaMNHeaders.putAll(inputBYLinkCSVReader.getHeaders());

        for (String[] lineData : inputBYLinkCSVReader.getLines()) {
            BasaMNEntry basaMNEntry = new BasaMNEntry();

            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(basaMNEntry, this.outputBasaMNHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            outputBasaMNEntries.add(basaMNEntry);
        }

        /** for (String line : inputBYLinkCSVReader.getLines()) {
            BasaMNEntry basaMNEntry = new BasaMNEntry();

            String[] lineData = line.split(";");
            for (int index = 0; index < lineData.length; index++) {
                try {
                    BeanUtils.setProperty(basaMNEntry, this.outputBasaMNHeaders.get(index), lineData[index]);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
            }

            outputBasaMNEntries.add(basaMNEntry);
        } */
    }
}
