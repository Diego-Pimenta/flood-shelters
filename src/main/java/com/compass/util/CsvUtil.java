package com.compass.util;

import com.compass.exception.CsvReaderException;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtil {

    public static List<Map<String, String>> readCsv(String filePath) {
        List<Map<String, String>> lines = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] headers = csvReader.readNext();
            String[] columns;

            while ((columns = csvReader.readNext()) != null) {
                Map<String, String> fields = new HashMap<>();

                for (int i = 0; i < columns.length; i++) {
                    fields.put(headers[i], columns[i]);
                }
                lines.add(fields);
            }
        } catch (CsvValidationException | IOException e) {
            throw new CsvReaderException(e.getMessage());
        }
        return lines;
    }
}
