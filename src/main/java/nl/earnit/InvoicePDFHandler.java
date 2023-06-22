package nl.earnit;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.resources.companies.ISOWeek;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class InvoicePDFHandler {
    public static StreamingOutput createSingleInvoice(InvoiceInformation invoiceInformation) {
        return output -> {
            try {
                createInvoiceStream(output, invoiceInformation);
            } catch (Exception e) {
                throw new WebApplicationException("File Not Found !!");
            }
        };
    }

    public static StreamingOutput createInvoices(List<InvoiceInformation> invoiceInformationList) {
        return outputStream -> {
            ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(outputStream));

            for (InvoiceInformation invoiceInformation : invoiceInformationList) {
                ZipEntry zipEntry = new ZipEntry(InvoiceInformation.getInvoiceNameFromInvoiceInformation(invoiceInformation));
                zipOut.putNextEntry(zipEntry);

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                InvoicePDFHandler.createInvoiceStream(os, invoiceInformation);

                zipOut.write(os.toByteArray());
                zipOut.closeEntry();
            }

            zipOut.close();
            outputStream.flush();
            outputStream.close();
        };
    }

    public static void createInvoiceStream(OutputStream outputStream, InvoiceInformation invoiceInformation) throws IOException {
        String html = Constants.INVOICE_TEMPLATE;

        html = html.replaceAll("&lt;company_name&gt;", invoiceInformation.getCompanyName());
        html = html.replaceAll("&lt;company_address&gt;", invoiceInformation.getCompanyAddress());
        html = html.replaceAll("&lt;company_kvk&gt;", invoiceInformation.getCompanyKVK());

        html = html.replaceAll("&lt;invoice_number&gt;", invoiceInformation.getInvoiceNumber());
        html = html.replaceAll("&lt;date&gt;", invoiceInformation.getInvoiceDate());

        html = html.replaceAll("&lt;student_name&gt;", invoiceInformation.getStudentName());
        html = html.replaceAll("&lt;student_address&gt;", invoiceInformation.getStudentAddress());
        html = html.replaceAll("&lt;student_kvk&gt;", invoiceInformation.getStudentKVK());
        html = html.replaceAll("&lt;student_btw&gt;", invoiceInformation.getStudentBTW());

        html = html.replaceAll("&lt;week_number&gt;", String.valueOf(invoiceInformation));

        float salary = invoiceInformation.getHourlyWageInCents() / 100f * invoiceInformation.getMinutesWorked() / 60f;
        html = html.replaceAll("&lt;role&gt;", invoiceInformation.getRole());
        html = html.replaceAll("&lt;value&gt;", String.valueOf(round(salary, 2)));
        html = html.replaceAll("&lt;description&gt;", invoiceInformation.getDescription());

        float tax = 0.21f * salary;

        html = html.replaceAll("&lt;tax_value&gt;", String.valueOf(round(tax, 2)));
        html = html.replaceAll("&lt;total_value&gt;", String.valueOf(round(salary + tax, 2)))
        ;

        final Document document = Jsoup.parse(html);

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.useSVGDrawer(new BatikSVGDrawer());
        builder.withW3cDocument(new W3CDom().fromJsoup(document), "");
        builder.toStream(outputStream);
        builder.run();
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public static class InvoiceInformation {
        public static InvoiceInformation fromWorkedWeek(WorkedWeekDTO workedWeek) {
            /** @TODO: missing information */
            return new InvoiceInformation(workedWeek.getCompany().getName(),
                "An address",
                "12345678",
                "1234", // This is currently just fixed.
                ISOWeek.getNextMonday(workedWeek.getYear(), workedWeek.getWeek()).format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                getNameFromWorkedWeek(workedWeek),
                "An address too",
                "12345678",
                "NL123456789B01",
                workedWeek.getContract().getRole(),
                workedWeek.getContract().getDescription(),
                workedWeek.getWeek(),
                workedWeek.getYear(),
                workedWeek.getTotalMinutes(),
                workedWeek.getUserContract().getHourlyWage());
        }

        public static String getNameFromWorkedWeek(WorkedWeekDTO workedWeek) {
            String spacing = (workedWeek.getUser().getLastNamePrefix() == null || workedWeek.getUser().getLastNamePrefix().trim().length() < 1) ? "" : workedWeek.getUser().getLastNamePrefix() + " ";
            return workedWeek.getUser().getFirstName() + " " + (spacing) + workedWeek.getUser().getLastName();
        }

        public static String getInvoiceNameFromWorkedWeek(WorkedWeekDTO workedWeek) {
            return "invoice-%s-%s-%s-%s.pdf".formatted(workedWeek.getYear(), workedWeek.getWeek(), getNameFromWorkedWeek(workedWeek).replaceAll(" ", "-"), workedWeek.getContract().getRole());
        }

        public static String getInvoiceNameFromInvoiceInformation(InvoiceInformation invoiceInformation) {
            return "invoice-%s-%s-%s-%s.pdf".formatted(invoiceInformation.getYear(), invoiceInformation.getWeek(), invoiceInformation.getStudentName().replaceAll(" ", "-"), invoiceInformation.getRole());
        }

        private final String companyName;
        private final String companyAddress;
        private final String companyKVK;
        private final String invoiceNumber;
        private final String invoiceDate;
        private final String studentName;
        private final String studentAddress;
        private final String studentKVK;
        private final String studentBTW;
        private final String role;
        private final String description;

        private final int week;
        private final int year;
        private final int minutesWorked;
        private final int hourlyWageInCents;


        public InvoiceInformation(String companyName, String companyAddress, String companyKVK,
                                  String invoiceNumber, String invoiceDate, String studentName,
                                  String studentAddress, String studentKVK, String studentBTW,
                                  String role, String description, int week, int year,
                                  int minutesWorked, int hourlyWageInCents) {
            this.companyName = companyName;
            this.companyAddress = companyAddress;
            this.companyKVK = companyKVK;
            this.invoiceNumber = invoiceNumber;
            this.invoiceDate = invoiceDate;
            this.studentName = studentName;
            this.studentAddress = studentAddress;
            this.studentKVK = studentKVK;
            this.studentBTW = studentBTW;
            this.role = role;
            this.description = description;
            this.week = week;
            this.year = year;
            this.minutesWorked = minutesWorked;
            this.hourlyWageInCents = hourlyWageInCents;
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getCompanyAddress() {
            return companyAddress;
        }

        public String getCompanyKVK() {
            return companyKVK;
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getStudentAddress() {
            return studentAddress;
        }

        public String getStudentKVK() {
            return studentKVK;
        }

        public String getStudentBTW() {
            return studentBTW;
        }

        public String getRole() {
            return role;
        }

        public String getDescription() {
            return description;
        }

        public int getWeek() {
            return week;
        }

        public int getMinutesWorked() {
            return minutesWorked;
        }

        public int getHourlyWageInCents() {
            return hourlyWageInCents;
        }

        public int getYear() {
            return year;
        }
    }
}
