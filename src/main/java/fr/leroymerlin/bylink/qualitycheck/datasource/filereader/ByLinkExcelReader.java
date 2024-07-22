package fr.leroymerlin.bylink.qualitycheck.datasource.filereader;

import com.github.pjfanning.xlsx.StreamingReader;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ByLinkExcelReader {

    public Map<Integer, String> headers;
    public List<String> lines;
    private String filePath;

    public ByLinkExcelReader(String filePath) {
        this.filePath = filePath;
        this.headers = new HashMap<>();
        this.lines = new ArrayList<>();
        this.readExcel();
    }

    public void readExcel() {
        try {
            IOUtils.setByteArrayMaxOverride(300000000);

            InputStream file = new FileInputStream(new File(filePath));
            Workbook workbook = StreamingReader.builder().rowCacheSize(1000).bufferSize(4096).open(file);

            Sheet sheet = workbook.getSheetAt(0);

//            DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
//            decimalFormatSymbols.setDecimalSeparator('.');
//            DecimalFormat decimalFormat = new DecimalFormat("#.###############", decimalFormatSymbols);

            // Utilisé pour éviter de que les nombres de plus de caractères soient coupés avec un E+000
            DecimalFormat decimalFormat = new DecimalFormat("#.###############");

            DataFormatter formatter = new DataFormatter();

            boolean isFirstRow = true;

            for (Row row : sheet) {
                if (isFirstRow) {
                    for (Cell cell : row) {
                        this.headers.put(cell.getColumnIndex(), cell.getStringCellValue());
                    }
                    isFirstRow = false;
                } else {
                    StringBuilder text = new StringBuilder();

                    // Do not use iterator to loop over cells, because it skips empty cells (https://stackoverflow.com/questions/44733247/how-to-read-blank-cells-as-string-from-excel-file-using-apache-poi)
                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        if (!text.isEmpty()) {
                            text.append(";");
                        }
                        Cell cell = row.getCell(i);
                        if(cell != null) {
                            switch (cell.getCellType()) {
                                case STRING:
                                    text.append(formatter.formatCellValue(cell));
                                    break;
                                case NUMERIC:
                                    text.append(decimalFormat.format(cell.getNumericCellValue()));
                                    break;
                            }
                        }
                    }
                    this.lines.add(text.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
