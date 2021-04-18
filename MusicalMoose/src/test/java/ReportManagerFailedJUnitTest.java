
import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.NewsJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NullCategoryException;
import com.gb4w21.musicalmoose.controller.exceptions.NullSearchValueException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;

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
import jodd.mail.Email;
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
import org.junit.Ignore;

/**
 * Arquillian unit tests for the report controller test as failed instants where
 * it returns no results or is supposed to throw and error
 *
 * @author Alessandro Dare
 * @version 1.0
 */

@Ignore
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
                
                .addPackage(JavaEE8Resource.class.getPackage())
                .addPackage(LocaleChanger.class.getPackage())
                .addPackage(ClientJpaController.class.getPackage())
                .addPackage(ReportManagementController.class.getPackage())
                .addPackage(ReportManagerJUnitTest.class.getPackage())
                .addPackage(RollbackFailureException.class.getPackage())
                .addPackage(Client.class.getPackage())
                .addPackage(Invoicedetail.class.getPackage())
                .addPackage(Album.class.getPackage())
                .addPackage(Email.class.getPackage())
                .addPackage(MusicTrack.class.getPackage())
                .addPackage(Email.class.getPackage()) 
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

    /**
     * test at getting a list of all sales will return 0 results if given an
     * invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTotalSalesInvalidDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TotalSales");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    /**
     * throws an exception if the category field for any search is null
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testInvlidCategoryNull() throws ParseException, NullSearchValueException, NullCategoryException {
        thrown.expect(NullCategoryException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory(null);
        reportController.reportSearch();

    }

    /**
     * test at getting a list of sales for any search will return 0 results if
     * given an invalid category
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testInvlidCategory() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    /**
     * test at getting a list of sales for a specified client will return 0
     * results if given an null date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTotalSalesInvalidDateNull() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TotalSales");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    /**
     * test at getting a list of sales for a specified client will return 0
     * results if given an invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByClientInvalidDate() throws ParseException, NullSearchValueException, NullCategoryException {
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

    /**
     * throws an exception if the specified field for an client search is null
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByClientInvalidClientNull() throws ParseException, NullSearchValueException, NullCategoryException {
        thrown.expect(NullSearchValueException.class);
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

    /**
     * test at getting a list of sales for a specified client will return 0
     * results if given an invalid client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByClientInvalidClient() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setToDate(toDate);
        reportController.setSpecifiedSearch("");
        reportController.setReportCategory("SalesByArtist");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    /**
     * test at getting a list of sales for a specified artist will return 0
     * results if given an invalid track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByArtistInvalidDate() throws ParseException, NullSearchValueException, NullCategoryException {
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

    /**
     * throws an exception if the specified field for an artist search is null
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByArtistInvalidArtistNull() throws ParseException, NullSearchValueException, NullCategoryException {
        thrown.expect(NullSearchValueException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setToDate(toDate);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SalesByArtist");
        reportController.reportSearch();

    }

    /**
     * test at getting a list of sales for a specified artist will return 0
     * results if given an invalid artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByArtistInvalidArtist() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setReportCategory("SalesByClient");
        reportController.setToDate(toDate);
        reportController.setSpecifiedSearch("");
        reportController.setReportCategory("SalesByArtist");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);

    }

    /**
     * test at getting a list of sales for a specified track will return 0
     * results if given an invalid track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByTrackDate() throws ParseException, NullSearchValueException, NullCategoryException {
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

    /**
     * throws an exception if the specified field for an track search is null
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByTrackInvalidTrackNull() throws ParseException, NullSearchValueException, NullCategoryException {
        thrown.expect(NullSearchValueException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SalesByTrack");
        reportController.reportSearch();

    }

    /**
     * test at getting a list of sales for a specified track will return 0
     * results if given an invalid track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSalesByTrackInvalidTrack() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch("");
        reportController.setReportCategory("SalesByTrack");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);

    }

    /**
     * test at getting a list of sales for a specified album will return 0
     * results if given an invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSaleByAlbumInvalidDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.setReportCategory("SalesByAlbum");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);
    }

    /**
     * throws an exception if the specified field for an album search is null
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSaleByAlbumInvalidAlbumNull() throws ParseException, NullSearchValueException, NullCategoryException {
        thrown.expect(NullSearchValueException.class);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(null);
        reportController.setReportCategory("SalesByAlbum");
        reportController.reportSearch();

    }

    /**
     * test at getting a list of sales for a specified album will return 0
     * results if given an invalid album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testSaleByAlbumInvalidAlbum() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch("");
        reportController.setReportCategory("SalesByAlbum");
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 0);

    }

    /**
     * test at getting a list of top selling clients will return 0 results if
     * given an invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTopClientsDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);
    }

    /**
     * test at getting a list of top selling clients will return 0 results if
     * given an invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTopClientsDateNull() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);
    }

    /**
     * test at getting a list of top selling clients will return 0 results if
     * given an invalid date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTopSellersInvalidDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("1970-01-01");
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals((reportController.getTracks().size() + reportController.getAlbums().size()), 0);
    }

    /**
     * test at getting a list of top selling clients will return 0 results if
     * given an null date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testTopSellersInvalidDateNull() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = sdf.parse("1990-01-01");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);

    }

    /**
     * test at getting a list of zero selling tracks will return 0 results if
     * given an null date
     *
     * @throws ParseException
     * @throws NullCategoryException
     * @throws NullSearchValueException
     * @author Alessandro Dare
     */
    @Test
    public void testZeroTracksAllDates() throws ParseException, NullCategoryException, NullSearchValueException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals(reportController.getTracks().size(), 0);

    }

    /**
     * test at getting a list of zero selling clients will return 0 results if
     * given an mull date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     * @author Alessandro Dare
     */
    @Test
    public void testZeroCleintsAllDates() throws ParseException, NullSearchValueException, NullCategoryException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = null;
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 0);
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
