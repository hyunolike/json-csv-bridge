package com.jsoncsvbridge;

import com.jsoncsvbridge.csv.CsvCreator;
import com.jsoncsvbridge.csv.FormattingOptions;
import com.jsoncsvbridge.csv.MergeCsvCreator;
import com.jsoncsvbridge.filter.Condition;
import com.jsoncsvbridge.filter.FilterCriteria;
import com.jsoncsvbridge.json.JsonToCsvCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.generateCsv;
import static com.jsoncsvbridge.factory.DefaultCsvCreatorFactory.generateMergeCsv;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Java 에서 호출했을 때의 공개 API 모양을 고정한다.
 * Kotlin 기본 인자에 기대지 않고도 쓸 수 있어야 한다.
 */
class JavaInteropTest {

    private static final String JSON_INPUT = """
            [
                {"name": "Hyunho", "age": 30, "city": "Seoul"},
                {"name": "Bob", "age": 25, "city": "Los Angeles"}
            ]
            """;

    @Test
    void convertsJsonWithoutOptions(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("java_basic.csv");

        CsvCreator creator = generateCsv("json");
        creator.createCsv(JSON_INPUT, output.toString());

        assertEquals(
                List.of("name,age,city", "Hyunho,30,Seoul", "Bob,25,Los Angeles"),
                Files.readAllLines(output));
    }

    @Test
    void convertsJsonWithOptions(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("java_options.csv");

        // 앞쪽 인자만 넘기는 축약형도 Java 에서 쓸 수 있어야 한다.
        generateCsv("json", new FormattingOptions(';')).createCsv(JSON_INPUT, output.toString());

        assertEquals("name;age;city", Files.readAllLines(output).get(0));
    }

    @Test
    void filtersRecords(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("java_filtered.csv");

        FilterCriteria criteria = FilterCriteria.of(new Condition("city", "Seoul"));
        JsonToCsvCreator creator = (JsonToCsvCreator) generateCsv("json");
        creator.createCsv(JSON_INPUT, output.toString(), criteria);

        assertEquals(List.of("name,age,city", "Hyunho,30,Seoul"), Files.readAllLines(output));
    }

    @Test
    void mergesDocuments(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("java_merged.csv");

        MergeCsvCreator creator = generateMergeCsv();
        creator.createMergedCsv("""
                [{"a": 1}]
                """, """
                {"b": 2}
                """, output.toString());

        assertEquals(List.of("a,b", "1,", ",2"), Files.readAllLines(output));
    }
}
