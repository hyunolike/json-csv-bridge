package com.example.springbootjava;

import com.jsoncsvbridge.csv.FlattenMode;
import com.jsoncsvbridge.csv.FormattingOptions;
import com.jsoncsvbridge.filter.Condition;
import com.jsoncsvbridge.filter.FilterCriteria;
import com.jsoncsvbridge.json.JsonToCsvCreator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.generateCsv;
import static com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.generateJson;
import static com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.generateMergeCsv;

@SpringBootApplication
public class SpringBootJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootJavaApplication.class, args);
    }
}

@Component
class CsvCreatorRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        File outputDir = new File("csv_output");

        // 기능1. JSON 형식 문자열을 CSV 파일로 변환
        String jsonInput = """
                    [
                        {"name": "Hyunho", "age": 30, "city": "New York"},
                        {"name": "Bob", "age": 25, "city": "Los Angeles"},
                        {"name": "Charlie", "age": 35, "city": "Chicago"}
                    ]
                """;

        String jsonOutputPath = new File(outputDir, "output_json.csv").getAbsolutePath();
        generateCsv("json").createCsv(jsonInput, jsonOutputPath);
        System.out.println("JSON to CSV conversion completed. File saved at: " + jsonOutputPath);

        // 기능2. 2개 JSON 형식 문자열을 CSV 파일 하나로 병합
        String data1 = """
                    [
                        {"name": "John", "age": 30, "city": "New York", "country": "USA"}
                    ]
                """;
        String data2 = """
                    [
                        {"name": "Alice", "age": 25, "city": "London", "occupation": "Engineer"},
                        {"name": "Bob", "age": 35, "city": "Paris", "hobby": "Photography"}
                    ]
                """;

        String mergeJsonOutputPath = new File(outputDir, "output_merge_json.csv").getAbsolutePath();
        generateMergeCsv().createMergedCsv(data1, data2, mergeJsonOutputPath);
        System.out.println("Merged JSON to CSV conversion completed. File saved at: " + mergeJsonOutputPath);

        // 기능3. CSV 포맷 지정 (구분자, 행 구분자, 빈 값 표기, Excel 용 BOM)
        FormattingOptions options = new FormattingOptions(';', "UTF-8", "\r\n", '"', "N/A", true);

        String formattedOutputPath = new File(outputDir, "output_formatted.csv").getAbsolutePath();
        generateCsv("json", options).createCsv(jsonInput, formattedOutputPath);
        System.out.println("Formatted CSV saved at: " + formattedOutputPath);

        // 기능4. 조건에 맞는 레코드만 변환
        FilterCriteria criteria = FilterCriteria.of(new Condition("city", "Chicago"));

        String filteredOutputPath = new File(outputDir, "output_filtered.csv").getAbsolutePath();
        JsonToCsvCreator creator = (JsonToCsvCreator) generateCsv("json");
        creator.createCsv(jsonInput, filteredOutputPath, criteria);
        System.out.println("Filtered CSV saved at: " + filteredOutputPath);

        // 기능5. 중첩 값을 열로 펼치기
        String nestedInput = """
                    [
                        {"id": 1, "addr": {"city": "Seoul"}, "tags": ["a", "b"]}
                    ]
                """;

        FormattingOptions flatten = new FormattingOptions(
                ',', "UTF-8", "\n", '"', "", false, FlattenMode.BRACKET);
        System.out.println("Flattened CSV:\n" + generateCsv("json", flatten).convertToString(nestedInput));

        // 기능6. 파일을 거치지 않고 CSV 문자열로 받기
        String csv = generateCsv("json").convertToString(jsonInput);
        System.out.println("CSV as a string:\n" + csv);

        // 기능7. CSV 를 다시 JSON 으로 되돌리기
        System.out.println("Back to JSON: " + generateJson().toJsonString(csv));
    }
}
