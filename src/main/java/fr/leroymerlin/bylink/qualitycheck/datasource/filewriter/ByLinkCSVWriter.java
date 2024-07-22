package fr.leroymerlin.bylink.qualitycheck.datasource.filewriter;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import fr.leroymerlin.bylink.qualitycheck.basa.offerconstruction.ProductRankingAnomaly;
import fr.leroymerlin.bylink.qualitycheck.basa.offerconstruction.RangeLetterAnomaly;
import org.apache.commons.lang3.StringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ByLinkCSVWriter {

    private final String filePath;
    public List<String> headers;

    public ByLinkCSVWriter(String filePath) {
        this.filePath = filePath;
        headers = new LinkedList<>();
    }

    public void writeListOfRefsInCSV(Set<String> values) {
        Iterator<String> iterator = values.iterator();

        try (FileWriter writer = new FileWriter(this.filePath)) {
            while (iterator.hasNext()) {
                writer.write(iterator.next());
                if(iterator.hasNext()) {
                    writer.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void writeElementsInCSV(List<String> headers, Collection<T> values, Class<T> className) {
        try (FileWriter writer = new FileWriter(this.filePath)) {
            // headers
            writer.write(String.join(",", headers));
            writer.write("\n");

            ColumnPositionMappingStrategy<T> mappingStrategy = new ColumnPositionMappingStrategy<>();
            mappingStrategy.setType(className);

            String[] columns = headers.toArray(String[]::new);
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<T> beanWriter = new StatefulBeanToCsvBuilder<T>(writer).withMappingStrategy(mappingStrategy).build();

            beanWriter.write(values.stream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        } catch (CsvDataTypeMismatchException e) {
            throw new RuntimeException(e);
        }

//        try (FileWriter writer = new FileWriter(filePath)) {
//
//            if(headers != null) {
//                for (Iterator<String> iterator = headers.iterator(); iterator.hasNext();) {
//                    writer.append(iterator.next());
//                    if(iterator.hasNext()) {
//                        writer.append(";");
//                    } else {
//                        writer.append("\n");
//                    }
//                }
//            }
//
//            for (Iterator<Object> iterator = values.iterator(); iterator.hasNext();) {
//                writer.append(iterator.next().toString());
//                if(iterator.hasNext()) {
//                    writer.append("\n");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
