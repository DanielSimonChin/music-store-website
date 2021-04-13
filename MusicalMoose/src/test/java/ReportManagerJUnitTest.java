
import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.NewsJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NullCategoryException;
import com.gb4w21.musicalmoose.controller.exceptions.NullSearchValueException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.converters.AlbumConverter;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Sale;
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
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Ignore;

/**
 * Arquillian unit tests for the MusicTrackController's methods that involve
 * CrtieriaBuilder queries.
 *
 * @author Daniel
 */
@Ignore
@RunWith(Arquillian.class)
public class ReportManagerJUnitTest {

    private final static Logger LOG = LoggerFactory.getLogger(ReportManagerJUnitTest.class);

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

    public ReportManagerJUnitTest() {
    }

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

    @Test
    public void testTotalSalesAllSales() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        assertEquals(reportController.getInvoicedetails().size(), 37);
    }

    @Test
    public void testTotalSaleParcial() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = sdf.parse("2020-03-13");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        assertEquals(reportController.getInvoicedetails().size(), 13);
    }

    @Test
    public void testTotalSaleTotalProfit() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        float answer = (float) 434.06;

        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);
    }
    @Test
    public void testTotalSalesFromAllSales() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        assertEquals(checkForTotalSale(reportController.getInvoicedetails(), toDate, fromDate), true);
    }

    private boolean checkForTotalSale(List<Invoicedetail> invoicedetails, Date toDate, Date fromDate) {
        for (Invoicedetail invoicedetail : invoicedetails) {
            if (toDate.compareTo(invoicedetail.getSaledate()) <= 0 && fromDate.compareTo(invoicedetail.getSaledate()) >= 0) {
                return false;
            }
        }

        return true;
    }
    @Test
    public void testSaleByClientAll() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 5);

    }
   @Test
    public void testSaleByClientAllProfit() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        float answer = (float) 65.57;
        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);

    }
    @Test
    public void testSaleByClientAllFromClient() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        assertEquals(checkForClient(reportController.getInvoicedetails(), testClient), true);

    }

    private boolean checkForClient(List<Invoicedetail> invoiceList, Client client) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (!invoicedetail.getSaleid().getClientid().equals(client)) {
                return false;
            }
        }

        return true;
    }
   @Test
    public void testSaleByClientDifferentDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = sdf.parse("2020-03-13");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 3);

    }
    @Test
    public void testSaleByClientDifferentClient() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(2);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 2);

    }
   @Test
    public void testSaleByArtistAll() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 4);

    }
    @Test
    public void testSaleByArtistAllProfit() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        float answer = (float) 35.34;
        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);

    }
    @Test
    public void testSaleByArtistAllFromArtist() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        assertEquals(checkForArtist(reportController.getInvoicedetails(), artist), true);

    }

    private boolean checkForArtist(List<Invoicedetail> invoiceList, String artist) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getInventoryid() != null) {
                if (!invoicedetail.getInventoryid().getArtist().equals(artist)) {
                    return false;
                }
            }
            if (invoicedetail.getAlbumid() != null) {
                if (!invoicedetail.getAlbumid().getArtist().equals(artist)) {
                    return false;
                }
            }

        }

        return true;
    }
   @Test
    public void testSaleByArtistDifferentDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 3);

    }

    @Test
    public void testSaleByArtistDifferentArtist() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Fallout Boy";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 3);

    }

    @Test
    public void testSaleByTrackAll() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 6);

    }

    @Test
    public void testSaleByTrackAllDownloads() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        assertEquals(reportController.getTotalNumberOfDownloads(), 10);

    }

    @Test
    public void testSaleByTrackAllProfit() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        float answer = (float) 38.57;
        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);

    }

    @Test
    public void testSaleByTrackDifferentDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 3);

    }

    @Test
    public void testSaleByTrackDifferentTrack() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(43);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 2);

    }

    @Test
    public void testSaleByTrackAllFromTrack() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        assertEquals(checkForTrack(reportController.getInvoicedetails(), track), true);
    }

    private boolean checkForTrack(List<Invoicedetail> invoiceList, MusicTrack track) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getInventoryid() == null) {
                return false;
            }

            if (!invoicedetail.getInventoryid().getInventoryid().equals(track.getInventoryid())) {
                return false;
            }

        }

        return true;
    }

   
    @Test
    public void testSaleByAlbumAll() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 5);

    }

    @Test
    public void testSaleByAlbumAllProfit() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        float answer = (float)86.28;
        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);

    }

    @Test
    public void testSaleByAlbumAllDownloads() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        assertEquals(reportController.getTotalNumberOfDownloads(), 6);

    }

    @Test
    public void testSaleByAlbumAllFromAlbum() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        assertEquals(checkForAlbum(reportController.getInvoicedetails(), album), true);

    }

    private boolean checkForAlbum(List<Invoicedetail> invoiceList, Album album) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getAlbumid() == null) {
                return false;
            }

            if (!invoicedetail.getAlbumid().getAlbumid().equals(album.getAlbumid())) {
                return false;
            }

        }

        return true;
    }

   @Test
    public void testSaleByAlbumDifferentDate() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 2);

    }

    @Test
    public void testSaleByAlbumDifferentAlbum() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(21);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        assertEquals(reportController.getInvoicedetails().size(), 2);

    }


    @Test
    public void testZeroCleintsAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 3);
    }

    @Test
    public void testZeroCleintsAllDatesFromZeroClients() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(checkForeroClients(reportController.getClients(), toDate, fromDate), true);
    }

    private boolean checkForeroClients(List<Client> clients, Date toDate, Date fromDate) {
        for (Client client : clients) {
            if (client.getSaleList() != null) {
                for (Sale sale : client.getSaleList()) {
                    for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
                        if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    @Test
    public void testZeroCleintsPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 6);
    }


    @Test
    public void testZeroTracksAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals(reportController.getTracks().size(), 87);
    }

  
    @Test
    public void testZeroTracksAllDatesFromZeroTracks() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals(checkForZeroTracks(reportController.getTracks(), toDate, fromDate), true);
    }

    private boolean checkForZeroTracks(List<MusicTrack> tracks, Date toDate, Date fromDate) {
        for (MusicTrack musicTrack : tracks) {
            if (musicTrack.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : musicTrack.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

   
    @Test
    public void testZeroTracksPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals(reportController.getTracks().size(), 90);
    }

   
    @Test
    public void testTopClientsAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 19);
    }

    
    @Test
    public void testTopClientsAllDatesFromTopClients() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(checkForTopClients(reportController.getClients(), toDate, fromDate), true);
    }

    private boolean checkForTopClients(List<Client> clients, Date toDate, Date fromDate) {

        for (Client client : clients) {
            if (client.getSaleList() != null) {
                for (Sale sale : client.getSaleList()) {
                    for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
                        LOG.debug("DATE:" + invoicedetail.getSaledate().toString());
                        LOG.debug("DATE:" + toDate.compareTo(invoicedetail.getSaledate()));
                        LOG.debug("DATE:" + fromDate.compareTo(invoicedetail.getSaledate()));
                        if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

  
    @Test
    public void testTopClientsParcialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(reportController.getClients().size(), 16);
    }

   
    @Test
    public void testTopSellersAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals((reportController.getTracks().size() + reportController.getAlbums().size()), 25);
    }

   
    @Test
    public void testTopSellersAllDatesFromTopSellers() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals((checkForTopTracks(reportController.getTracks(), toDate, fromDate) && checkForTopAlbums(reportController.getAlbums(), toDate, fromDate)), true);
    }

    private boolean checkForTopTracks(List<MusicTrack> tracks, Date toDate, Date fromDate) {
        for (MusicTrack musicTrack : tracks) {
            if (musicTrack.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : musicTrack.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkForTopAlbums(List<Album> albums, Date toDate, Date fromDate) {
        for (Album album : albums) {
            if (album.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : album.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

   
    @Test
    public void testTopSellersPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals((reportController.getTracks().size() + reportController.getAlbums().size()), 20);
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
