package nl.earnit.helpers;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.svgsupport.BatikSVGDrawer;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;
import nl.earnit.Constants;
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
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static nl.earnit.Constants.getName;

/**
 * The type Invoice pdf handler.
 */
public class InvoicePDFHandler {
    /**
     * Create single invoice streaming output.
     *
     * @param invoiceInformation the invoice information
     * @return the streaming output
     */
    public static StreamingOutput createSingleInvoice(InvoiceInformation invoiceInformation) {
        return output -> {
            try {
                createInvoiceStream(output, invoiceInformation);
            } catch (Exception e) {
                throw new WebApplicationException("File Not Found !!");
            }
        };
    }

    /**
     * Create invoices streaming output.
     *
     * @param invoiceInformationList the invoice information list
     * @return the streaming output
     */
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

    /**
     * Create invoice stream.
     *
     * @param outputStream       the output stream
     * @param invoiceInformation the invoice information
     * @throws IOException the io exception
     */
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

        html = html.replaceAll("&lt;week_number&gt;", String.valueOf(invoiceInformation.week));

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

    /**
     * The type Invoice information.
     */
    public static class InvoiceInformation {
        /**
         * From worked week invoice information.
         *
         * @param workedWeek the worked week
         * @return the invoice information
         */
        public static InvoiceInformation fromWorkedWeek(WorkedWeekDTO workedWeek) {
            return new InvoiceInformation(workedWeek.getCompany().getName(),
                workedWeek.getCompany().getAddress(),
                workedWeek.getCompany().getKvk(),
                String.format("%04d", new Random().nextInt(10000)), // This is currently just a random number.
                ISOWeek.getNextMonday(workedWeek.getYear(), workedWeek.getWeek()).format(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                getNameFromWorkedWeek(workedWeek),
                workedWeek.getUser().getAddress(),
                workedWeek.getUser().getKvk(),
                workedWeek.getUser().getBtw(),
                workedWeek.getContract().getRole(),
                workedWeek.getContract().getDescription(),
                workedWeek.getWeek(),
                workedWeek.getYear(),
                workedWeek.getTotalMinutes(),
                workedWeek.getUserContract().getHourlyWage());
        }

        /**
         * Gets name from worked week.
         *
         * @param workedWeek the worked week
         * @return the name from worked week
         */
        public static String getNameFromWorkedWeek(WorkedWeekDTO workedWeek) {
            return getName(workedWeek.getUser().getFirstName(), workedWeek.getUser().getLastNamePrefix(), workedWeek.getUser().getLastName());
        }

        /**
         * Gets invoice name from worked week.
         *
         * @param workedWeek the worked week
         * @return the invoice name from worked week
         */
        public static String getInvoiceNameFromWorkedWeek(WorkedWeekDTO workedWeek) {
            return "invoice-%s-%s-%s-%s.pdf".formatted(workedWeek.getYear(), workedWeek.getWeek(), getNameFromWorkedWeek(workedWeek).replaceAll(" ", "-"), workedWeek.getContract().getRole());
        }

        /**
         * Gets invoice name from invoice information.
         *
         * @param invoiceInformation the invoice information
         * @return the invoice name from invoice information
         */
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


        /**
         * Instantiates a new Invoice information.
         *
         * @param companyName       the company name
         * @param companyAddress    the company address
         * @param companyKVK        the company kvk
         * @param invoiceNumber     the invoice number
         * @param invoiceDate       the invoice date
         * @param studentName       the student name
         * @param studentAddress    the student address
         * @param studentKVK        the student kvk
         * @param studentBTW        the student btw
         * @param role              the role
         * @param description       the description
         * @param week              the week
         * @param year              the year
         * @param minutesWorked     the minutes worked
         * @param hourlyWageInCents the hourly wage in cents
         */
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

        /**
         * Gets company name.
         *
         * @return the company name
         */
        public String getCompanyName() {
            return companyName;
        }

        /**
         * Gets company address.
         *
         * @return the company address
         */
        public String getCompanyAddress() {
            return companyAddress;
        }

        /**
         * Gets company kvk.
         *
         * @return the company kvk
         */
        public String getCompanyKVK() {
            return companyKVK;
        }

        /**
         * Gets invoice number.
         *
         * @return the invoice number
         */
        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        /**
         * Gets invoice date.
         *
         * @return the invoice date
         */
        public String getInvoiceDate() {
            return invoiceDate;
        }

        /**
         * Gets student name.
         *
         * @return the student name
         */
        public String getStudentName() {
            return studentName;
        }

        /**
         * Gets student address.
         *
         * @return the student address
         */
        public String getStudentAddress() {
            return studentAddress;
        }

        /**
         * Gets student kvk.
         *
         * @return the student kvk
         */
        public String getStudentKVK() {
            return studentKVK;
        }

        /**
         * Gets student btw.
         *
         * @return the student btw
         */
        public String getStudentBTW() {
            return studentBTW;
        }

        /**
         * Gets role.
         *
         * @return the role
         */
        public String getRole() {
            return role;
        }

        /**
         * Gets description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets week.
         *
         * @return the week
         */
        public int getWeek() {
            return week;
        }

        /**
         * Gets minutes worked.
         *
         * @return the minutes worked
         */
        public int getMinutesWorked() {
            return minutesWorked;
        }

        /**
         * Gets hourly wage in cents.
         *
         * @return the hourly wage in cents
         */
        public int getHourlyWageInCents() {
            return hourlyWageInCents;
        }

        /**
         * Gets year.
         *
         * @return the year
         */
        public int getYear() {
            return year;
        }
    }
}
