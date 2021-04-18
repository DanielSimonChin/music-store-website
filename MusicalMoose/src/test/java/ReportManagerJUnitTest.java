
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
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Ignore;

/**
 * Arquillian unit tests for the report controller makes sure that every search
 * interacting with the data base is working properly regardless of the values
 * given so long and the required parameters are met
 *
 * @author Alessandro Dare
 * @version 1.0
 */
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
                .addPackage(Email.class.getPackage())

                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/payara-resources.xml"), "payara-resources.xml")
                .addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
                .addAsResource(new File("src/main/resources/log4j2.xml"), "log4j2.xml")
                .addAsResource("createMusicTable.sql")
                .addAsLibraries(dependencies);

        return webArchive;
    }

    /**
     * Test that all sales from the database are given when searching using a
     * wide enough date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
    @Test
    public void testTotalSalesAllSales() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");

        assertEquals(37, reportController.getInvoicedetails().size());
    }

    /**
     * Test that only specific sales from the database are given when searching
     * using a specific date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testTotalSaleParcial() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = sdf.parse("2020-03-13");
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        assertEquals(13, reportController.getInvoicedetails().size());
    }

    /**
     * Test that the total profit is given when selecting all sales
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
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
        LOG.info("testTotalSaleTotalCurrentCost TOTAL Profit:" + reportController.getTotalProfit());
        assertEquals(Float.compare(answer, reportController.getTotalProfit()), 0);
    }

    /**
     * Test that the total current cost is given when selecting all sales
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testTotalSaleTotalCurrentCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        float answer = (float) 593.99005;
        LOG.info("testTotalSaleTotalCurrentCost TOTAL CURRENT COST:" + reportController.getTotalSales());

        assertEquals(0, Float.compare(answer, reportController.getTotalSales()));
    }

    /**
     * Test that the total cost is given when selecting all sales
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testTotalSaleTotalCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        float answer = (float) 172.17;
        LOG.info("testTotalSaleTotalCost TOTAL COST:" + reportController.getTotalCost());
        assertEquals(0, Float.compare(answer, reportController.getTotalCost()));
    }

    /**
     * test that the sale list generates from total sales fits in the specified
     * date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
    @Test
    public void testTotalSalesFromAllSales() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.reportSearch();
        reportController.setReportCategory("TotalSales");
        assertEquals(true, checkForTotalSale(reportController.getInvoicedetails(), toDate, fromDate));
    }

    /**
     * checks to see if all sales if with the specified date range
     *
     * @param invoicedetails List<Invoicedetail>
     * @param toDate Date
     * @param fromDate Date
     * @return boolean true if all sales are in the date range false if not
     */
    private boolean checkForTotalSale(List<Invoicedetail> invoicedetails, Date toDate, Date fromDate) {
        for (Invoicedetail invoicedetail : invoicedetails) {
            if (toDate.compareTo(invoicedetail.getSaledate()) <= 0 && fromDate.compareTo(invoicedetail.getSaledate()) >= 0) {
                LOG.info("Different Sale Date:"+invoicedetail.getSaledate());
                return false;
            }
        }

        return true;
    }

    /**
     * tests the correct number of sales is generated when searching for sales
     * from a selected client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals(5, reportController.getInvoicedetails().size());

    }

    /**
     * tests the total profit is generated for all the sales made by a certain
     * client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
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
        LOG.info("testSaleByClientAllCurrentCost TOTAL PROFIT:" + reportController.getTotalProfit());
        assertEquals(0, Float.compare(answer, reportController.getTotalProfit()));

    }

    /**
     * tests the total current cost is generated for all the sales made by a
     * certain client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByClientAllCurrentCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        float answer = (float) 88.409996;
        LOG.info("testSaleByClientAllCurrentCost TOTAL CURRENT COST:" + reportController.getTotalSales());
        assertEquals(0, Float.compare(answer, reportController.getTotalSales()));

    }

    /**
     * tests the total cost is generated for all the sales made by a certain
     * client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByClientAllCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByClient");
        Client testClient = this.clientJpaController.findClient(1);
        reportController.setSpecifiedSearch(testClient.getUsername());
        reportController.reportSearch();
        float answer = (float) 27.13;
        LOG.info("testSaleByClientAllCost TOTAL COST:" + reportController.getTotalCost());
        assertEquals(0, Float.compare(answer, reportController.getTotalCost()));

    }

    /**
     * test that all sales generated by a specific client where made by that
     * client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
 
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
        assertEquals(true, checkForClient(reportController.getInvoicedetails(), testClient));

    }

    /**
     * checks to see if all sales where made by the selected client
     *
     * @param invoiceList List<Invoicedetail>
     * @param client Client
     * @return true if all sales matched the client false if not
     */
    private boolean checkForClient(List<Invoicedetail> invoiceList, Client client) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (!invoicedetail.getSaleid().getClientid().equals(client)) {
                 LOG.info("Different Client id:"+invoicedetail.getSaleid().getClientid());
                return false;
            }
        }

        return true;
    }

    /**
     * tests that only selected list of sales is generated for selected client
     * while searching for a specific date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals(3, reportController.getInvoicedetails().size());

    }

    /**
     * test that the correct number of sales is generated when searching for
     * sales selected by a different client
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals(2, reportController.getInvoicedetails().size());

    }

    /**
     * tests the correct number of sales is generated when searching for sales
     * from a selected artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals(4, reportController.getInvoicedetails().size());

    }

    /**
     * tests the total profit is generated for all the sales made by a certain
     * artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
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
        LOG.info("testSaleByArtistAllProfit TOTAL profit:" + reportController.getTotalProfit());
        assertEquals(0, Float.compare(answer, reportController.getTotalProfit()));

    }

    /**
     * tests the total current cost is generated for all the sales made by a
     * certain artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByArtistAllCurrentCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        float answer = (float) 52.590004;
        LOG.info("testSaleByArtistAllCurrentCost TOTAL CURRENT COST:" + reportController.getTotalSales());
        assertEquals(0, Float.compare(answer, reportController.getTotalSales()));

    }

    /**
     * tests the total cost is generated for all the sales made by a certain
     * artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByArtistAllCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        String artist = "Imagine Dragons";
        reportController.setReportCategory("SalesByArtist");
        reportController.setSpecifiedSearch(artist);
        reportController.reportSearch();
        float answer = (float) 10.69;
        LOG.info("testSaleByArtistAllCost TOTAL COST:" + reportController.getTotalCost());
        assertEquals(0, Float.compare(answer, reportController.getTotalCost()));

    }

    /**
     * test that all sales generated by a specific artist where made by that
     * artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals( true,checkForArtist(reportController.getInvoicedetails(), artist));

    }

    /**
     * checks to see if all sales where made by the selected artist
     *
     * @param invoiceList List<Invoicedetail>
     * @param artist String
     * @return true if all sales matched the artist false if not
     */
    private boolean checkForArtist(List<Invoicedetail> invoiceList, String artist) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getInventoryid() != null) {
                if (!invoicedetail.getInventoryid().getArtist().equals(artist)) {
                    LOG.info("Artist:"+invoicedetail.getInventoryid().getArtist());
                    return false;
                }
            }
            if (invoicedetail.getAlbumid() != null) {
                if (!invoicedetail.getAlbumid().getArtist().equals(artist)) {
                    LOG.info("Different Artist:"+invoicedetail.getAlbumid().getArtist());
                    return false;
                }
            }

        }

        return true;
    }

    /**
     * tests that only selected list of sales is generated for selected artist
     * while searching for a specific date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */

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
        assertEquals(3,reportController.getInvoicedetails().size());

    }

    /**
     * test that the correct number of sales is generated when searching for
     * sales selected by a different artist
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals( 3,reportController.getInvoicedetails().size());

    }

    /**
     * tests the correct number of sales is generated when searching for sales
     * from a selected track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals( 6,reportController.getInvoicedetails().size());

    }

    /**
     * tests the total downloads is generated for all the sales made by a
     * certain track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals(10,reportController.getTotalNumberOfDownloads());

    }

    /**
     * tests the total profit is generated for all the sales made by a certain
     * track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
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
        LOG.info("testSaleByTrackAllProfit TOTAL PROFIT:" + reportController.getTotalProfit());
        assertEquals(0,Float.compare(answer, reportController.getTotalProfit()));

    }

    /**
     * tests the total current cost is generated for all the sales made by a
     * certain track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByTrackAllCurrentCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        float answer = (float) 65.84999;
        LOG.info("testSaleByTrackAllCurrentCost TOTAL CURRENT COST:" + reportController.getTotalSales());
        assertEquals( 0,Float.compare(answer, reportController.getTotalSales()));

    }

    /**
     * tests the total cost is generated for all the sales made by a certain
     * track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByTrackAllCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByTrack");
        MusicTrack track = this.musicTrackJpaController.findMusicTrack(71);
        reportController.setSpecifiedSearch(track.getTracktitle());
        reportController.reportSearch();
        float answer = (float) 8.7;
        LOG.info("testSaleByTrackAllCost TOTAL COST:" + reportController.getTotalCost());
        assertEquals(0,Float.compare(answer, reportController.getTotalCost()));

    }

    /**
     * tests that only selected list of sales is generated for selected track
     * while searching for a specific date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals( 3,reportController.getInvoicedetails().size());

    }

    /**
     * test that the correct number of sales is generated when searching for
     * sales selected by a different track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
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
        assertEquals(2,reportController.getInvoicedetails().size());

    }

    /**
     * test that all sales generated by a specific track where sold with that
     * track
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
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
        assertEquals(true,checkForTrack(reportController.getInvoicedetails(), track));
    }

    /**
     * checks to see if all sales where sold with the selected track
     *
     * @param invoiceList List<Invoicedetail>
     * @param track MusicTrack
     * @return true if all sales matched the track false if not
     */
    private boolean checkForTrack(List<Invoicedetail> invoiceList, MusicTrack track) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getInventoryid() == null) {
                LOG.info("NULL");
                return false;
            }

            if (!invoicedetail.getInventoryid().getInventoryid().equals(track.getInventoryid())) {
                LOG.info("Different track:"+invoicedetail.getInventoryid().getInventoryid());
                return false;
            }

        }

        return true;
    }

    /**
     * tests the correct number of sales is generated when searching for sales
     * from a selected album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals(5,reportController.getInvoicedetails().size());

    }

    /**
     * tests the total profit is generated for all the sales made by a certain
     * album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
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
        float answer = (float) 86.28;
        LOG.info("testSaleByAlbumAllProfit TOTAL PROFIT:" + reportController.getTotalProfit());
        assertEquals(0,Float.compare(answer, reportController.getTotalProfit()));

    }

    /**
     * tests the total current cost is generated for all the sales made by a
     * certain album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByAlbumAllCurrentCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        float answer = (float) 109.53999;
        LOG.info("testSaleByAlbumAllCurrentCost TOTAL CURRENT COST:" + reportController.getTotalSales());
        assertEquals(0,Float.compare(answer, reportController.getTotalSales()));

    }

    /**
     * tests the total cost is generated for all the sales made by a certain
     * album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    @Test
    public void testSaleByAlbumAllCost() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("SalesByAlbum");
        Album album = this.albumJpaController.findAlbum(1);
        reportController.setSpecifiedSearch(album.getAlbumtitle());
        reportController.reportSearch();
        float answer = (float) 39.949997;
        LOG.info("testSaleByAlbumAllCost TOTAL COST:" + reportController.getTotalCost());
        assertEquals(0,Float.compare(answer, reportController.getTotalCost()));

    }

    /**
     * tests the total downloads is generated for all the sales made by a
     * certain album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
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
       
        assertEquals(6,reportController.getTotalNumberOfDownloads());

    }

    /**
     * test that all sales generated by a specific track where sold with that
     * album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals( true,checkForAlbum(reportController.getInvoicedetails(), album));

    }

    /**
     * checks to see if all sales where sold with the selected album
     *
     * @param invoiceList List<Invoicedetail>
     * @param album Album
     * @return true if all sales matched the track false if not
     */
    private boolean checkForAlbum(List<Invoicedetail> invoiceList, Album album) {
        for (Invoicedetail invoicedetail : invoiceList) {
            if (invoicedetail.getAlbumid() == null) {
                LOG.info("NULL");
                return false;
            }

            if (!invoicedetail.getAlbumid().getAlbumid().equals(album.getAlbumid())) {
                LOG.info("Different album id:"+invoicedetail.getAlbumid().getAlbumid());
                return false;
            }

        }

        return true;
    }

    /**
     * tests that only selected list of sales is generated for selected album
     * while searching for a specific date
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
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
        assertEquals(2,reportController.getInvoicedetails().size());

    }

    /**
     * test that the correct number of sales is generated when searching for
     * sales selected by a different album
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
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
        assertEquals(2,reportController.getInvoicedetails().size());

    }

    /**
     * tests the correct number of clients is generated when searching for all
     * clients who had never made a sale
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testZeroCleintsAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(3,reportController.getClients().size());
    }

    /**
     * test that all clients generated by search by zero clients have actually
     * made no sales
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
    @Test
    public void testZeroCleintsAllDatesFromZeroClients() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(true,checkForeroClients(reportController.getClients(), toDate, fromDate));
    }

    /**
     * check's for all clients in list have never made a sale in the specified
     * date range
     *
     * @param clients List<Client>
     * @param toDate Date
     * @param fromDate Date
     * @return true if the client made no purchases false if not
     */
    private boolean checkForeroClients(List<Client> clients, Date toDate, Date fromDate) {
        for (Client client : clients) {
            if (client.getSaleList() != null) {
                for (Sale sale : client.getSaleList()) {
                    for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
                        if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                            LOG.info("client has made purcahse:"+client.getUsername());
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * tests that all clients generated by zero clients made no purchases in the
     * specified date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
    @Test
    public void testZeroCleintsPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroClients");
        reportController.reportSearch();
        assertEquals(6,reportController.getClients().size());
    }

    /**
     * tests the correct number of tracks is generated when searching for all
     * tracks who had never sold
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testZeroTracksAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals( 87,reportController.getTracks().size());
    }

    /**
     * test that all tracks generated by search by zero tracks have actually
     * never sold
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testZeroTracksAllDatesFromZeroTracks() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals( true,checkForZeroTracks(reportController.getTracks(), toDate, fromDate));
    }

    /**
     * check's for all tracks in list have never been sold in the specified date
     * range
     *
     * @param tracks List<MusicTrack>
     * @param toDate Date
     * @param fromDate Date
     * @return true if the track did not sale false if not
     */
    private boolean checkForZeroTracks(List<MusicTrack> tracks, Date toDate, Date fromDate) {
        for (MusicTrack musicTrack : tracks) {
            if (musicTrack.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : musicTrack.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                       LOG.info("Track has sold:"+musicTrack.getTracktitle());
                        return false;
                    }
                }
            }
        }

        return true;
    }
    
    /**
     * tests that all tracks generated by zero tracks have not sold in the
     * specified date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testZeroTracksPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("ZeroTracks");
        reportController.reportSearch();
        assertEquals(90,reportController.getTracks().size());
    }

    /**
     * tests the correct number of clients is generated when searching for all
     * clients that have made a purchase
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
    @Test
    public void testTopClientsAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(19,reportController.getClients().size());
    }

    /**
     * test that all clients generated by search by top clients have actually
     * made a purchase
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
   
    @Test
    public void testTopClientsAllDatesFromTopClients() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(true,checkForTopClients(reportController.getClients(), toDate, fromDate));
    }

    /**
     * check's for all clients in list have actually made a sale in the
     * specified date range
     *
     * @param clients List<Client>
     * @param toDate Date
     * @param fromDate Date
     * @return true if the client made purchases false if not
     */
    private boolean checkForTopClients(List<Client> clients, Date toDate, Date fromDate) {

        for (Client client : clients) {
           if(!checkClientHasBought(client, toDate,fromDate)){
               return false;
           }
        }

        return true;
    }
     /**
     * Goes through a specific MusicTrack to check if it was sold it a given date range 
     * @param client Client
     * @param toDate Date
     * @param fromDate Date
     * @return boolean true it it has been sold false if not
     */
    private boolean checkClientHasBought(Client client,Date toDate, Date fromDate){
       if (client.getSaleList() != null) {
                for (Sale sale : client.getSaleList()) {
                    for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
             
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return true;
                    }
                }
            }
       }
       LOG.info("Client hasn't bought:"+client.getUsername());
        return false;
    }

    /**
     * tests that all clients generated by top clients have made a purchase in
     * the specified date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
    @Test
    public void testTopClientsParcialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopClients");
        reportController.reportSearch();
        assertEquals(16,reportController.getClients().size());
    }

    /**
     * tests that the correct number of tracks and albums have been generated
     * when searching for all tracks that have sold
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    
    @Test
    public void testTopSellersAllDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals(25,(reportController.getTracks().size() + reportController.getAlbums().size()));
    }

    /**
     * test that all albums and tracks generated by search by top sellers have
     * actually been purchased
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
    @Test
    public void testTopSellersAllDatesFromTopSellers() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2000-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals( true,(checkForTopTracks(reportController.getTracks(), toDate, fromDate) && checkForTopAlbums(reportController.getAlbums(), toDate, fromDate)));
    }

    /**
     * check's for all tracks in a list have actually been purchased in the
     * specified date range
     *
     * @param tracks List<MusicTrack> tracks
     * @param toDate Date
     * @param fromDate Date
     * @return true if the tracks been purchased false if not
     */
    private boolean checkForTopTracks(List<MusicTrack> tracks, Date toDate, Date fromDate) {
        for (MusicTrack musicTrack : tracks) {
            if(!checkTrackHasSold(musicTrack,toDate,fromDate)){
                return false;
            }
        }

        return true;
    }
    /**
     * Goes through a specific MusicTrack to check if it was sold it a given date range 
     * @param track MusicTrack
     * @param toDate Date
     * @param fromDate Date
     * @return boolean true it it has been sold false if not
     */
    private boolean checkTrackHasSold(MusicTrack track,Date toDate, Date fromDate){
        if (track.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : track.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return true;
                    }
                }
            }
        LOG.info("Track has sold:"+track.getTracktitle());
        return false;
    }
    /**
     * check's for all albums in a list have actually been purchased in the
     * specified date range
     *
     * @param albums List<Album>
     * @param toDate Date
     * @param fromDate Date
     * @return true if the albums have been purchased false if not
     */
    private boolean checkForTopAlbums(List<Album> albums, Date toDate, Date fromDate) {
        for (Album album : albums) {
            if(!checkAlbumHasSold(album,toDate,fromDate)){
                return false;
            }
        }

        return true;
    }
    /**
     * Goes through a specific album to check if it was sold it a given date range 
     * @param album Album
     * @param toDate Date
     * @param fromDate Date
     * @return boolean true it it has been sold false if not
     */
    private boolean checkAlbumHasSold(Album album,Date toDate, Date fromDate){
        if (album.getInvoicedetailList() != null) {
                for (Invoicedetail invoicedetail : album.getInvoicedetailList()) {
                    if (toDate.compareTo(invoicedetail.getSaledate()) >= 0 && fromDate.compareTo(invoicedetail.getSaledate()) <= 0) {
                        return true;
                    }
                }
            }
        LOG.info("Get album title:"+album.getAlbumtitle());
        return false;
    }
    /**
     * tests that all tracks and albums generated by top sellers that have been
     * purchased in the specified date range
     *
     * @throws ParseException
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
  
    @Test
    public void testTopSellersPracialDates() throws ParseException, NullSearchValueException, NullCategoryException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse("2021-01-01");
        Date toDate = new Date();
        reportController.setFromDate(fromDate);
        reportController.setToDate(toDate);
        reportController.setReportCategory("TopSellers");
        reportController.reportSearch();
        assertEquals(20,(reportController.getTracks().size() + reportController.getAlbums().size()));
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
