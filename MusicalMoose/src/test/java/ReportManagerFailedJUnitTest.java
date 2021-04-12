
import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.NewsJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.converters.AlbumConverter;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.resources.JavaEE8Resource;
import com.gb4w21.musicalmoose.util.LocaleChanger;
import com.gb4w21.musicalmoose.util.ReportManagementController;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Arquillian unit tests for the MusicTrackController's methods that involve
 * CrtieriaBuilder queries.
 *
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class ReportManagerFailedJUnitTest {

    private final static Logger LOG = LoggerFactory.getLogger(ReportManagerFailedJUnitTest.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Inject
    private ReportManagementController reportController;
    @Inject
    private ClientJpaController clientJpaController;
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;

    @Resource(lookup = "java:app/jdbc/myMusic")
    private DataSource ds;

    @Deployment
    public static WebArchive deploy() {

        // Use an alternative to the JUnit assert library called AssertJ
        // Need to reference MySQL driver as it is not part of either
        // embedded or remote
        final File[] dependencies = Maven
                .resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.assertj:assertj-core",
                        "org.slf4j:slf4j-api",
                        "org.apache.logging.log4j:log4j-slf4j-impl",
                        "org.apache.logging.log4j:log4j-web"
                ).withTransitivity()
                .asFile();

        // The SQL script to create the database is in src/test/resources
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
                .addPackage(LoginBean.class.getPackage())
                .addPackage(PreRenderViewBean.class.getPackage())
                .addPackage(AlbumConverter.class.getPackage())
                .addPackage(JavaEE8Resource.class.getPackage())
                .addPackage(LocaleChanger.class.getPackage())
                .addPackage(ClientJpaController.class.getPackage())
                .addPackage(ReportManagementController.class.getPackage())
                .addPackage(ReportManagerJUnitTest.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Client.class.getPackage())
                .addPackage(Invoicedetail.class.getPackage())
                .addPackage(Album.class.getPackage())
                .addPackage(MusicTrack.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/main/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createMusicTable.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }

    public ReportManagerFailedJUnitTest() {

    }
    //thrown.expect(SQLException.class);

    @Test
    public void testTotalSalesInvalidDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TotalSales");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testTotalSalesInvalidDateNull() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TotalSales");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testSalesByClientInvalidDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        Client testClient = this.clientJpaController.findClient(2);
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testSalesByClientInvalidClient() throws ParseException {
        thrown.expect(NullPointerException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        Client testClient = this.clientJpaController.findClient(2);
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setSpecifiedSearch(null);
        reportController.reportSearch();

    }

    @Test
    public void testSalesByArtistInvalidDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setToDate(toDate);
        reportController.setSpecifiedSearch("Imagine Dragons");
        reportController.setReportCategory("SalesByArtist");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testSalesByArtistInvalidArtist() throws ParseException {
        //thrown.expect(NullPointerException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setToDate(toDate);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SalesByArtist");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);

    }

    @Test
    public void testSalesByTrackDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.setReportCategory("SalesByTrack");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testSalesByTrackInvalidTrack() throws ParseException {
        thrown.expect(NullPointerException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SalesByTrack");
        reportController.reportSearch();

    }

    @Test
    public void testSaleByAlbumDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.setReportCategory("SaleByAlbum");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    @Test
    public void testSaleByAlbumInvalidAlbum() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SaleByAlbum");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);

    }

    @Test
    public void testTopClientsDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);
    }

    @Test
    public void testTopClientsDateNull() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);
    }

    @Test
    public void testTopSellersInvalidDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals((reportController.getTracks().size() + reportController.getAlbums().size()), 0);
    }

    @Test
    public void testTopSellersInvalidDateNull() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);

    }

    @Test
    public void testZeroTracksAllDates() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();

    }

    @Test
    public void testZeroCleintsAllDates() throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();

    }

    /**
     * Restore the database to a known state before testing. This is important
     * if the test is destructive. This routine is courtesy of Bartosz Majsak
     * who also solved my Arquillian remote server problem
     */
    @Before
    public void seedDatabase() {
        final String seedDataScript = loadAsString("createMusicTable.sql");

        try ( Connection connection = ds.getConnection()) {
            for (String statement : splitStatements(new StringReader(
                    seedDataScript), ";")) {
                connection.prepareStatement(statement).execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed seeding database", e);
        }
    }

    /**
     * The following methods support the seedDatabse method
     */
    private String loadAsString(final String path) {
        try ( InputStream inputStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(path)) {
            return new Scanner(inputStream).useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new RuntimeException("Unable to close input stream.", e);
        }
    }

    private List<String> splitStatements(Reader reader,
            String statementDelimiter) {
        final BufferedReader bufferedReader = new BufferedReader(reader);
        final StringBuilder sqlStatement = new StringBuilder();
        final List<String> statements = new LinkedList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || isComment(line)) {
                    continue;
                }
                sqlStatement.append(line);
                if (line.endsWith(statementDelimiter)) {
                    statements.add(sqlStatement.toString());
                    sqlStatement.setLength(0);
                }
            }
            return statements;
        } catch (IOException e) {
            throw new RuntimeException("Failed parsing sql", e);
        }
    }

    private boolean isComment(final String line) {
        return line.startsWith("--") || line.startsWith("//")
                || line.startsWith("/*");
    }
}
