package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import guru.qa.model.Delivery;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ParseFilesHW {

    ClassLoader cl = ParseFilesHW.class.getClassLoader();

    @DisplayName("Checking PDF-file from ZIP")
    @Test
    void testPdfInZip() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/Archive_test.zip"));
        try (InputStream is = cl.getResourceAsStream("Archive_test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry)) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf.author).isEqualTo("Dmitry");
                        assertThat(pdf.numberOfPages).isEqualTo(2);
                    }
                }

            }
        }
    }

    @DisplayName("Checking XLS-file from ZIP")
    @Test
    void zipXlsTest() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/Archive_test.zip"));
        try (InputStream is = cl.getResourceAsStream("Archive_test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".xls")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry)) {
                        XLS xls = new XLS(inputStream);
                        AssertionsForClassTypes.assertThat(
                                xls.excel.getSheetAt(0)
                                        .getRow(2)
                                        .getCell(1).getStringCellValue()
                        ).isEqualTo("000000000002013197");
                    }
                }
            }
        }
    }

    @DisplayName("Checking CSV-file from ZIP")
    @Test
    void zipCsvTest() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/Archive_test.zip"));
        try (InputStream is = cl.getResourceAsStream("Archive_test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".csv")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry);
                         CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
                        List<String[]> content = reader.readAll();
                        String[] row = content.get(1);
                        assertThat(row[0]).isEqualTo("3-01-01-P2-11");
                        assertThat(row[1]).isEqualTo("000000000003347866");
                        assertThat(row[2]).isEqualTo("2205060611");
                        assertThat(row[3]).isEqualTo("REGULAR");
                        assertThat(row[4]).isEqualTo("FF");
                        assertThat(row[5]).isEqualTo("100");
                    }
                }
            }
        }
    }

    @DisplayName("Checking JSON-file with model")
    @Test
    void jsonJackson() throws Exception {
        InputStream is = cl.getResourceAsStream("delivery.json");
        Gson gson = new Gson();
        Delivery delivery = gson.fromJson(new InputStreamReader(is), Delivery.class);
        assertThat(delivery.deliveryNumber).isEqualTo(1488);
        assertThat(delivery.inboundTime).isEqualTo("10.10.22 16:40:00");
        assertThat(delivery.vendorName).isEqualTo("Nestle");
        assertThat(delivery.warehouse.get(0)).isEqualTo("DC01");
        assertThat(delivery.warehouse.get(1)).isEqualTo("DC02");
        assertThat(delivery.items.amount).isEqualTo(30);
        assertThat(delivery.items.price).isEqualTo(20.6);
        assertThat(delivery.items.productId).isEqualTo(544331);
        assertThat(delivery.items.productName).isEqualTo("Молочная смесь");
        assertThat(delivery.items.isActive).isTrue();
    }
}
