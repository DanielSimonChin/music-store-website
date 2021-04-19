package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NullCategoryException;
import com.gb4w21.musicalmoose.controller.exceptions.NullSearchValueException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

enum ReportCategory {
    TotalSales,
    SalesByClient,
    SalesByArtist,
    SalesByTrack,
    SalesByAlbum,
    TopSellers,
    TopClients,
    ZeroTracks,
    ZeroClients
}

/**
 * Controller for report manager allows a manager can get reports in the form of
 * a list of sales, clients, tracks or album that a user can specify to keep
 * track of current sales
 *
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class ReportManagementController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ReportManagementController.class);
    @PersistenceContext
    private EntityManager entityManager;
    private Date fromDate;
    private Date toDate;
    @Inject
    private ClientJpaController clientJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;

    private float totalProfit;
    private float totalCost;
    private float totalSales;
    private int toalNumberOfSales;
    private int totalNumberOfDownloads;
    private String reportCategory;
    private String specifiedSearch;
    private List<Invoicedetail> invoicedetails;

    private List<Invoicedetail> selectedInvoicedetails;
    private Invoicedetail selectedInvoicedetail;

    private List<Client> clients;
    private List<MusicTrack> tracks;
    private List<Album> albums;
    private boolean gettingSales;

    /**
     * Default constructor
     */
    public ReportManagementController() {

    }

    /**
     * Constructor that resets all page values
     *
     * @author Alessandro Dare
     */
    @PostConstruct
    public void init() {
        gettingSales = false;

        invoicedetails = new ArrayList<>();
        this.reportCategory = ReportCategory.TotalSales.toString();
        clients = new ArrayList<>();
        tracks = new ArrayList<>();
        albums = new ArrayList<>();
        selectedInvoicedetails = new ArrayList<>();
    }

    public Invoicedetail getSelectedInvoicedetail() {
        return selectedInvoicedetail;
    }

    public void setSelectedInvoicedetail(Invoicedetail selectedInvoicedetail) {
        this.selectedInvoicedetail = selectedInvoicedetail;
    }

    public boolean isGettingSales() {
        LOG.debug("getting sales:" + this.gettingSales);
        return gettingSales;
    }

    public List<Invoicedetail> getInvoicedetails() {
        return invoicedetails;
    }

    public void setInvoicedetails(List<Invoicedetail> invoicedetails) {
        this.invoicedetails = invoicedetails;
    }

    public List<Invoicedetail> getSelectedInvoicedetails() {
        return selectedInvoicedetails;
    }

    public void setSelectedInvoicedetails(List<Invoicedetail> selectedInvoicedetails) {
        this.selectedInvoicedetails = selectedInvoicedetails;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<MusicTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<MusicTrack> tracks) {
        this.tracks = tracks;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public String getSpecifiedSearch() {
        return specifiedSearch;
    }

    public void setSpecifiedSearch(String specifiedSearch) {
        this.specifiedSearch = specifiedSearch;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public float getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(float totalProfit) {
        this.totalProfit = totalProfit;
    }

    public float getTotalCost() {
        return this.totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public float getTotalSales() {
        return this.totalSales;
    }

    public void setTotalSales(float totalSales) {
        this.totalSales = totalSales;
    }

    public int getToalNumberOfSales() {
        return toalNumberOfSales;
    }

    public void setToalNumberOfSales(int toalNumberOfSales) {
        this.toalNumberOfSales = toalNumberOfSales;
    }

    public int getTotalNumberOfDownloads() {
        return totalNumberOfDownloads;
    }

    public void setTotalNumberOfDownloads(int totalNumberOfDownloads) {
        this.totalNumberOfDownloads = totalNumberOfDownloads;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return this.fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * If the date field is null change it to and invalid date
     *
     * @author Alessandro Dare
     */
    private void changeDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date invalidDate = null;
        try {
            invalidDate = sdf.parse("1950-01-01");
        } catch (ParseException ex) {
            LOG.info("erro with date conversion:" + ex.getLocalizedMessage());
        }
        if (this.toDate == null || this.fromDate == null) {
            invoicedetails = new ArrayList<>();
            clients = new ArrayList<>();
            tracks = new ArrayList<>();
            albums = new ArrayList<>();
        }

    }

    /**
     * clears all previous report values
     *
     * @author Alessandro Dare
     */
    private void clearAllValues() {
        gettingSales = false;
        invoicedetails = new ArrayList<>();
        clients = new ArrayList<>();
        tracks = new ArrayList<>();
        albums = new ArrayList<>();
        totalCost = 0;
        totalSales = 0;
        totalProfit = 0;
        toalNumberOfSales = 0;
        totalNumberOfDownloads = 0;
        selectedInvoicedetails = new ArrayList<>();
    }

    /**
     * checks if category or specific search or null if so throw and exception
     *
     * @author Alessandro Dare
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    private void checkNullValues() throws NullCategoryException, NullSearchValueException {
        if (this.reportCategory == null) {
            throw new NullCategoryException("Must always have a category to perform a search");

        }
        //checks if search is in a specific sale
        if ((this.reportCategory.equals(ReportCategory.SalesByAlbum.toString()) || this.reportCategory.equals(ReportCategory.SalesByArtist.toString())
                || this.reportCategory.equals(ReportCategory.SalesByClient.toString()) || this.reportCategory.equals(ReportCategory.SalesByTrack.toString()))
                && this.specifiedSearch == null) {

            throw new NullSearchValueException("This category requires a specified search field");
        }
    }

    /**
     * searches and displays the list of chosen sales, tracks, albums etc that
     * is requested for the report
     *
     * @author Alessandro Dare
     * @return String report page
     * @throws NullSearchValueException
     * @throws NullCategoryException
     */
    public String reportSearch() throws NullSearchValueException, NullCategoryException {

        clearAllValues();
        checkNullValues();
        LOG.info("Category Search:" + this.reportCategory);
        //different searches for different categories
        if (this.reportCategory.equals(ReportCategory.TotalSales.toString())) {

            gettingSales = true;
            invoicedetails = this.getAllSales();

        } else if (this.reportCategory.equals(ReportCategory.SalesByAlbum.toString())) {

            gettingSales = true;
            if (this.checkAlbum(specifiedSearch)) {
                invoicedetails = this.getSaleByAlbum(this.getAlbum(this.specifiedSearch).getAlbumid());
            }

        } else if (this.reportCategory.equals(ReportCategory.SalesByArtist.toString())) {

            gettingSales = true;
            invoicedetails = this.getSaleByArtist(this.specifiedSearch);
        } else if (this.reportCategory.equals(ReportCategory.SalesByClient.toString())) {

            gettingSales = true;
            invoicedetails = this.getSalesByClient(this.getUser(this.specifiedSearch));
        } else if (this.reportCategory.equals(ReportCategory.SalesByTrack.toString())) {
            gettingSales = true;
            if (this.checkTrack(specifiedSearch)) {
                invoicedetails = this.getSaleByTrack(this.getTrack(specifiedSearch).getInventoryid());
            }
        } else if (this.reportCategory.equals(ReportCategory.TopSellers.toString())) {

            tracks = this.getTopTarcks();
            albums = this.getTopAlbums();
        } else if (this.reportCategory.equals(ReportCategory.TopClients.toString())) {
            clients = this.getTopClients();

        } else if (this.reportCategory.equals(ReportCategory.ZeroClients.toString())) {
            clients = this.getUnsoldClients();

        } else if (this.reportCategory.equals(ReportCategory.ZeroTracks.toString())) {
            tracks = this.getUnsoldTarcks();
        }

        for (Invoicedetail iD : invoicedetails) {
            LOG.debug("ids" + iD.getInvoiceid());
        }
        changeDate();
        return "adminreport";
    }

    /**
     * Returns a list of clients who hadn't made a purchase in a specified date
     * range
     *
     * @author Alessandro Dare
     * @return List<Client> list of clients
     */
    private List<Client> getUnsoldClients() {
        List<Client> clients = this.clientJpaController.findClientEntities();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        cq.where(cb.equal(sale.get("saleremoved"), 0), cb.between(sale.get("saledate"), fromDate, toDate));
        cq.distinct(true);
        cq.select(client);
        TypedQuery<Client> query = entityManager.createQuery(cq);
        List<Client> clientsWhoPurhase = query.getResultList();
        for (Client selectedClient : clientsWhoPurhase) {
            if (clients.contains(selectedClient)) {
                clients.remove(selectedClient);
            }
        }
        LOG.info("Number of zero clients:" + clients.size());
        return clients;
    }

    /**
     * Returns a list of music tracks that haven't sold in the specified date
     * range
     *
     * @author Alessandro Dare
     * @return List<MusicTrack> list of music tracks
     */
    private List<MusicTrack> getUnsoldTarcks() {
        List<MusicTrack> tracks = this.musicTrackJpaController.findMusicTrackEntities();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.distinct(true);
        cq.select(musicTrack);

        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> musicTracksThatSold = query.getResultList();
        for (MusicTrack selectedTrack : musicTracksThatSold) {
            if (tracks.contains(selectedTrack)) {
                tracks.remove(selectedTrack);
            }
        }
        LOG.info("Number of zero tracks" + tracks.size());
        return tracks;
    }

    /**
     * Returns a list of clients ordered by total sale that have made a purchase
     * in the specified date range
     *
     * @author Alessandro Dare
     * @return List<Client> client list
     */
    private List<Client> getTopClients() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        cq.where(cb.equal(sale.get("saleremoved"), 0), cb.between(sale.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(client.get("saleList"))));
        cq.distinct(true);
        cq.select(client);
        TypedQuery<Client> query = entityManager.createQuery(cq);

        List<Client> clients = query.getResultList();
        for (Client selectedClient : clients) {
            LOG.info("track:" + selectedClient.getUsername());
            LOG.info("number of client sales" + selectedClient.getSaleList().size());
            if (selectedClient.getSaleList() == null || selectedClient.getSaleList().isEmpty()) {
                clients.remove(selectedClient);
            }
        }
        LOG.info("number of top clients:" + query.getResultList().size());
        return clients;
    }

    /**
     * Returns a list of tracks ordered by total sale that have been purchased
     * in the specified date range
     *
     * @author Alessandro Dare
     * @return List<MusicTrack> track list
     */
    private List<MusicTrack> getTopTarcks() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(musicTrack.get("invoicedetailList"))));
        cq.distinct(true);
        cq.select(musicTrack);

        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> musicTracks = query.getResultList();

        for (MusicTrack selectedmusicTrack : musicTracks) {
            LOG.info("track:" + selectedmusicTrack.getTracktitle());
            LOG.info("number of track sales" + selectedmusicTrack.getInvoicedetailList().size());
            if (selectedmusicTrack.getInvoicedetailList() == null || selectedmusicTrack.getInvoicedetailList().isEmpty()) {
                musicTracks.remove(selectedmusicTrack);
            }
        }
        LOG.info("number of top tracks:" + musicTracks.size());
        return musicTracks;
    }

    /**
     * Returns a list of albums ordered by total sale that have been purchased
     * in the specified date range
     *
     * @author Alessandro Dare
     * @return List<Album> album list
     */
    private List<Album> getTopAlbums() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        Join invoicedetail = album.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(album.get("invoicedetailList"))));
        cq.distinct(true);
        cq.select(album);
        TypedQuery<Album> query = entityManager.createQuery(cq);
        List<Album> albums = query.getResultList();

        for (Album selectedAlbum : albums) {
            LOG.info("album:" + selectedAlbum.getAlbumtitle());
            LOG.info("number of album sales" + selectedAlbum.getInvoicedetailList().size());
            if (selectedAlbum.getInvoicedetailList() == null || selectedAlbum.getInvoicedetailList().isEmpty()) {
                albums.remove(selectedAlbum);
            }
        }
        LOG.info("number of top albums:" + albums.size());
        return albums;
    }

    /**
     * Gets all sales of the given track made in the specified date range
     *
     * @author Alessandro Dare
     * @param trackid int
     * @return List<Invoicedetail> sale list
     */
    private List<Invoicedetail> getSaleByTrack(int trackid) {
        LOG.info("track id:" + trackid);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("inventoryid"), trackid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        LOG.info("number of sales associated with track:" + invoicedetails.size());
        setTotals(invoicedetails);
        return invoicedetails;
    }

    /**
     * Gets all sales of the given album made in the specified date range
     *
     * @author Alessandro Dare
     * @param albumid int
     * @return List<Invoicedetail> sale list
     */
    private List<Invoicedetail> getSaleByAlbum(int albumid) {
        LOG.info("Album id:" + albumid);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        Join invoicedetail = album.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("albumid"), albumid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        LOG.info("number of sales associated with album:" + invoicedetails.size());
        setTotals(invoicedetails);
        return invoicedetails;
    }

    /**
     * Gets a sale list of all sale form a certain artist in the specified date
     * range
     *
     * @author Alessandro Dare
     * @param artist String
     * @return List<Invoicedetail> sale list
     */
    private List<Invoicedetail> getSaleByArtist(String artist) {
        LOG.info("Artist:" + artist);
        //getting albums for artist
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        LOG.info("ablums associted with artist:" + invoicedetails.size());
        //getting tracks from artist
        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        invoicedetail = album.join("invoicedetailList");
        sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        query = entityManager.createQuery(cq);
        LOG.info("tracks associted with artist:" + query.getResultList().size());
        invoicedetails.addAll(query.getResultList());

        setTotals(invoicedetails);
        return invoicedetails;
    }

    /**
     * get a list of all sale made in a certain date range
     *
     * @author Alessandro Dare
     * @return List<Invoicedetail> sale list
     */
    private List<Invoicedetail> getAllSales() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Invoicedetail> invoicedetail = cq.from(Invoicedetail.class);
        Join sale = invoicedetail.join("saleid");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoiceDetails = query.getResultList();

        setTotals(invoiceDetails);
        LOG.info("List of Sales:" + invoiceDetails.size());
        return invoiceDetails;
    }

    /**
     * get all sales that where made by a chosen client in the specified date
     * range
     *
     * @author Alessandro Dare
     * @param chosenClient
     * @return List<Invoicedetail> the list of sale
     */
    private List<Invoicedetail> getSalesByClient(Client chosenClient) {
        LOG.info("Client:" + chosenClient.getUsername());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        Join invoicedetail = sale.join("invoicedetailList");

        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(client.get("clientid"), chosenClient.getClientid()));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoiceDetails = query.getResultList();
        setTotals(invoiceDetails);
        LOG.info("Sales in reference to client:" + invoiceDetails.size());
        return invoiceDetails;
    }

    /**
     * Calculates all the totals when the user requests a sales report
     *
     * @author Alessandro Dare
     * @param invoiceDetails List<Invoicedetail>
     */
    private void setTotals(List<Invoicedetail> invoiceDetails) {
        this.toalNumberOfSales = invoiceDetails.size();
        for (Invoicedetail selectedInvoicedetail : invoiceDetails) {
            this.totalProfit += selectedInvoicedetail.getProfit();
            totalSales += selectedInvoicedetail.getCurrentcost();
            if (this.reportCategory.equals(ReportCategory.SalesByTrack.toString()) || this.reportCategory.equals(ReportCategory.SalesByAlbum.toString())) {
                this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
            }
            //adds cost of the track/ablum for each sale
            if (selectedInvoicedetail.getInventoryid() != null) {
                this.totalCost += selectedInvoicedetail.getInventoryid().getCostprice();
            }
            if (selectedInvoicedetail.getAlbumid() != null) {
                this.totalCost += selectedInvoicedetail.getAlbumid().getCostprice();
            }
        }
        LOG.info("totalCost:" + totalCost);
        LOG.info("totalProfit:" + totalProfit);
        LOG.info("totalSales:" + totalSales);
        LOG.info("toalNumberOfSales:" + toalNumberOfSales);
        LOG.info("totalNumberOfDownloads:" + totalNumberOfDownloads);
    }

    /**
     * Checks to see if the track, album, artists , or client exists before
     * doing a specified search if it does it throws an error
     *
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
    public void validateSpecificSearch(FacesContext context, UIComponent component,
            Object value) {
        UIInput selectInput = (UIInput) component.findComponent("criteria");
        //checks if search is in a specific category
        if (selectInput.getValue().equals(ReportCategory.SalesByAlbum.toString())
                || selectInput.getValue().equals(ReportCategory.SalesByArtist.toString())
                || selectInput.getValue().equals(ReportCategory.SalesByClient.toString())
                || selectInput.getValue().equals(ReportCategory.SalesByTrack.toString())) {
            LOG.info("Category:" + selectInput.getValue());

            if (value == null || value.toString().isEmpty() || value.toString().isBlank()) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "specificNotNullError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            LOG.info("Specific Search:" + value.toString());
            //different validation and message for each category
            if (selectInput.getLocalValue().equals(ReportCategory.SalesByAlbum.toString()) && (!checkAlbum(value.toString()))) {

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "specificAlbumError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (selectInput.getLocalValue().equals(ReportCategory.SalesByClient.toString()) && (!checkUser(value.toString()))) {

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "specificClientError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (selectInput.getLocalValue().equals(ReportCategory.SalesByTrack.toString()) && (!checkTrack(value.toString()))) {

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "specificTrackError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (selectInput.getLocalValue().equals(ReportCategory.SalesByArtist.toString()) && (!checkArtists(value.toString()))) {

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "specificArtistError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
        }
    }

    /**
     * checks if track or album with specified artist exists
     *
     * @author Alessandro Dare
     * @param artist String
     * @return true if it exists false if not
     */
    private boolean checkArtists(String artist) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("artist"), artist));
        TypedQuery<MusicTrack> queryTrack = entityManager.createQuery(cq);
        List<MusicTrack> musicTracks = queryTrack.getResultList();
        LOG.info("music tracks related to artist" + musicTracks.size());
        if (musicTracks != null && musicTracks.size() > 0) {
            return true;
        }
        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery();
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        cq.where(cb.equal(album.get("artist"), artist));
        TypedQuery<Album> queryAlbum = entityManager.createQuery(cq);
        List<Album> albums = queryAlbum.getResultList();
        LOG.info("albums related to artist" + albums.size());
        LOG.info("");
        if (albums != null && albums.size() > 0) {
            return true;
        }
        return false;

    }

    /**
     * checks if album with specified name exists
     *
     * @author Alessandro Dare
     * @param albumTitle String
     * @return true if it exists false if not
     */
    private boolean checkAlbum(String albumTitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        // Use String to refernce a field
        cq.where(cb.equal(album.get("albumtitle"), albumTitle));
        TypedQuery<Album> query = entityManager.createQuery(cq);
        List<Album> albums = query.getResultList();
        LOG.info("albums related to title" + albumTitle + albums.size());
        if (albums == null || albums.isEmpty()) {

            return false;
        }

        return true;
    }

    /**
     * checks if client with specified name exists
     *
     * @author Alessandro Dare
     * @param userName String
     * @return true if it exists false if not
     */
    private boolean checkUser(String userName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.equal(client.get("username"), userName));
        TypedQuery<Client> query = entityManager.createQuery(cq);

        try {
            LOG.info("clients related to username" + userName + " valid");
            query.getSingleResult();
            return true;
        } catch (javax.persistence.NoResultException NoResultException) {
            return false;
        }
    }

    /**
     * checks if track with specified name exists
     *
     * @author Alessandro Dare
     * @param trackname String
     * @return true if it exists false if not
     */
    private boolean checkTrack(String trackname) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("tracktitle"), trackname));
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> tracks = query.getResultList();
        LOG.info("tracks related to title" + trackname + tracks.size());
        if (tracks == null || tracks.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * gets track with specified track name
     *
     * @author Alessandro Dare
     * @param trackname String
     * @return MusicTrack specified track
     */
    private MusicTrack getTrack(String trackname) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("tracktitle"), trackname));
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        try {
            LOG.info("get track unsucessful:" + trackname);
            return query.getSingleResult();

        } catch (javax.persistence.NoResultException NoResultException) {
            return null;
        }
    }

    /**
     * gets album with specified album title
     *
     * @author Alessandro Dare
     * @param albumName String
     * @return Album album with specified title
     */
    private Album getAlbum(String albumName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        cq.where(cb.equal(album.get("albumtitle"), albumName));
        TypedQuery<Album> query = entityManager.createQuery(cq);

        try {
            return query.getSingleResult();

        } catch (javax.persistence.NoResultException NoResultException) {
            LOG.info("get album unsucessful:" + albumName);
            return null;
        }
    }

    /**
     * get client with specified username
     *
     * @author Alessandro Dare
     * @param userName String
     * @return Client client with username
     */
    private Client getUser(String userName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.equal(client.get("username"), userName));
        TypedQuery<Client> query = entityManager.createQuery(cq);
        try {

            return query.getSingleResult();

        } catch (javax.persistence.NoResultException NoResultException) {
            LOG.info("get user unsucessful:" + userName);
            return null;
        }
    }

}
