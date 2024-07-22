package fr.leroymerlin.bylink.qualitycheck.datasource.filereader;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.Getter;
import lombok.Setter;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.txt.UniversalEncodingDetector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BYLinkCSVReader {

    public Map<Integer, String> headers;
    // public List<String> lines;
    public List<String[]> lines;
    private String filePath;

    public BYLinkCSVReader(String filePath, boolean removeQuotationMarks) {
        this.filePath = filePath;
        this.headers = new HashMap<>();
        this.lines = new ArrayList<>();
        this.readCSV(removeQuotationMarks);
    }

    public void readCSV(boolean removeQuotationMarks) {

        EncodingDetector encodingDetector = new UniversalEncodingDetector();
        Charset detectedCharset = StandardCharsets.ISO_8859_1;
        try {
            byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
            detectedCharset = encodingDetector.detect(inputStream, new Metadata());
            // System.out.println("file=" + filePath + " / detectedCharset=" + detectedCharset.displayName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (Reader reader = Files.newBufferedReader(Path.of(this.filePath), detectedCharset)) {
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader).withCSVParser(parser);

            try (CSVReader csvReader = csvReaderBuilder.build()) {
                int lineIndex = 0;

                String[] line;
                while ((line = csvReader.readNext()) != null) {
                    if (lineIndex == 0) {
                        for (int headerIndex = 0; headerIndex < line.length; headerIndex++) {
                            this.headers.put(headerIndex, line[headerIndex]);
                        }
                    } else {
                        this.lines.add(line);
                    }

                    lineIndex++;
                }
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.ISO_8859_1))) {
//
//            String headers = br.readLine();
//            String[] headersTable = headers.split(";");
//            for(int index = 0; index < headersTable.length; index++) {
//                if(removeQuotationMarks) {
//                    this.headers.put(index, removeQuotationMarks(headersTable[index]));
//                } else {
//                    this.headers.put(index, headersTable[index]);
//                }
//            }
//
//            String line;
//            while ((line = br.readLine()) != null) {
//                if(removeQuotationMarks) {
//                    this.lines.add(removeQuotationMarks(line));
//                } else {
//                    this.lines.add(line);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    public String removeQuotationMarks(String entry) {
//        return entry.replace("\"", "");
//    }
}
