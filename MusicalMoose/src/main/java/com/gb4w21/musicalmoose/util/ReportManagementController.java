package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import javax.faces.application.Application;
import com.gb4w21.musicalmoose.controller.ReviewJpaController;
import com.gb4w21.musicalmoose.controller.SurveyJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Review;
import com.gb4w21.musicalmoose.entities.Survey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.PrimeFaces;
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
    private int totalPiceNetValue;
    private int totalPiceGrossValue;
    private int toalNumberOfSales;
    private int totalNumberOfDownloads;
    private String reportCategory;
    private String specifiedSearch;
    private List<Invoicedetail> invoicedetails;

    private List<Invoicedetail> selectedInvoicedetails;
    private Invoicedetail selectedInvoicedetail;

    private List<Invoicedetail> manyInvoiceDetails;

    private List<Client> clients;
    private List<MusicTrack> tracks;
    private List<Album> albums;
    private boolean gettingSales;

    public ReportManagementController() {

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

    public List<Invoicedetail> getManyInvoiceDetails() {
        return this.manyInvoiceDetails;
    }

    public void setManyInvoiceDetails(List<Invoicedetail> manyInvoiceDetails) {
        this.manyInvoiceDetails = manyInvoiceDetails;
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

    public int getTotalPiceNetValue() {
        return totalPiceNetValue;
    }

    public void setTotalPiceNetValue(int totalPiceNetValue) {
        this.totalPiceNetValue = totalPiceNetValue;
    }

    public int getTotalPiceGrossValue() {
        return totalPiceGrossValue;
    }

    public void setTotalPiceGrossValue(int totalPiceGrossValue) {
        this.totalPiceGrossValue = totalPiceGrossValue;
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

    public void save() {

    }

    public String reportSearch() {
        gettingSales = false;
        invoicedetails = new ArrayList<>();
        clients = new ArrayList<>();
        tracks = new ArrayList<>();
        albums = new ArrayList<>();
        totalPiceNetValue=0;
        totalPiceGrossValue=0;
        toalNumberOfSales=0;
        totalNumberOfDownloads=0;
        selectedInvoicedetails = new ArrayList<>();
       
        if (this.reportCategory.equals(ReportCategory.TotalSales.toString())) {
            LOG.debug("TOTAL SALES");
            LOG.debug("TOTAL SALES");
            LOG.debug("TOTAL SALES");
            LOG.debug("TOTAL SALES");
            gettingSales = true;
            invoicedetails = this.getTotalSales();
            LOG.debug("INVOICE DETAILS:" + invoicedetails.size());

            LOG.debug("TOTAL SALES" + totalPiceNetValue);
            LOG.debug("TOTAL SALES" + totalPiceGrossValue);
            LOG.debug("TOTAL SALES" + toalNumberOfSales);
            LOG.debug("TOTAL SALES" + totalNumberOfDownloads);
        } else if (this.reportCategory.equals(ReportCategory.SalesByAlbum.toString())) {
            gettingSales = true;
            invoicedetails = this.getSaleByAlbum(this.getAlbum(this.specifiedSearch).getAlbumid());
        } else if (this.reportCategory.equals(ReportCategory.SalesByArtist.toString())) {
            gettingSales = true;
            invoicedetails = this.getSaleByArtist(this.specifiedSearch);
        } else if (this.reportCategory.equals(ReportCategory.SalesByClient.toString())) {
            gettingSales = true;
            invoicedetails = this.getSalesByClient(this.getUser(this.specifiedSearch));
        } else if (this.reportCategory.equals(ReportCategory.SalesByTrack.toString())) {
            gettingSales = true;
            invoicedetails = this.getSaleByTrack(this.getTrack(specifiedSearch).getInventoryid());
        } else if (this.reportCategory.equals(ReportCategory.TopSellers.toString())) {
           
            tracks = this.getTopTarcks();
            albums = this.getTopAlbums();
        } else if (this.reportCategory.equals(ReportCategory.TopClients.toString())) {
            clients = this.getTopClients();
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
            LOG.debug("clients:"+clients.size());
        } else if (this.reportCategory.equals(ReportCategory.ZeroClients.toString())) {
            clients = this.getUnsoldClients();
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
            LOG.debug("clientsW:"+clients.size());
        } else if (this.reportCategory.equals(ReportCategory.ZeroTracks.toString())) {
            tracks = this.getUnsoldTarcks();
        }
        LOG.debug("TOTAL SALES1" + gettingSales);
        LOG.debug("TOTAL SALES1" + invoicedetails.size());
        for (Invoicedetail iD : invoicedetails) {
            LOG.debug("ids" + iD.getInvoiceid());
        }
        LOG.debug("TOTAL SALES1" + totalPiceNetValue);
        LOG.debug("TOTAL SALES1" + totalPiceGrossValue);
        LOG.debug("TOTAL SALES1" + toalNumberOfSales);
        LOG.debug("TOTAL SALES1" + totalNumberOfDownloads);
        return "adminreport";
    }

    public String toReportPage() {
        LOG.info("TOREPORTPAGE!!!!!!!!!!!!!!!!!!!!");
        gettingSales = false;
        manyInvoiceDetails = null;

        invoicedetails = new ArrayList<>();
        this.reportCategory = ReportCategory.TotalSales.toString();
        clients = new ArrayList<>();
        tracks = new ArrayList<>();
        albums = new ArrayList<>();
        selectedInvoicedetails = new ArrayList<>();
        return "adminreport";
    }

    private List<Client> getUnsoldClients() {
        List<Client> clients = this.clientJpaController.findClientEntities();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        cq.where(cb.equal(sale.get("saleremoved"), 0),cb.between(sale.get("saledate"), fromDate, toDate));
        cq.distinct(true);
        cq.select(client);
        TypedQuery<Client> query = entityManager.createQuery(cq);
        List<Client> clientsWhoPurhase = query.getResultList();
        for (Client selectedClient : clientsWhoPurhase) {
            if (clients.contains(selectedClient)) {
                clients.remove(selectedClient);
            }
        }
        return clients;
    }

    private List<MusicTrack> getUnsoldTarcks() {
        List<MusicTrack> tracks = this.musicTrackJpaController.findMusicTrackEntities();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.distinct(true);
        cq.select(musicTrack);
        
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> musicTracksThatSold = query.getResultList();
        for (MusicTrack selectedTrack : musicTracksThatSold) {
            if (tracks.contains(selectedTrack)) {
                tracks.remove(selectedTrack);
            }
        }
        return tracks;
    }

    private List<Client> getTopClients() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        cq.where(cb.equal(sale.get("saleremoved"), 0),cb.between(sale.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(client.get("saleList"))));
        cq.distinct(true);
        cq.select(client);
        TypedQuery<Client> query = entityManager.createQuery(cq);
     
        List<Client> clients = query.getResultList();
        for (Client selectedClient : clients) {
            if (selectedClient.getSaleList() == null || selectedClient.getSaleList().isEmpty()) {
                clients.remove(selectedClient);
            }
        }
        return clients;
    }

    private List<MusicTrack> getTopTarcks() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(musicTrack.get("invoicedetailList"))));
        cq.distinct(true);
        cq.select(musicTrack);
        
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> musicTracks = query.getResultList();
        for (MusicTrack selectedmusicTrack : musicTracks) {
            if (selectedmusicTrack.getInvoicedetailList() == null || selectedmusicTrack.getInvoicedetailList().isEmpty()) {
                musicTracks.remove(selectedmusicTrack);
            }
        }
        return musicTracks;
    }

    private List<Album> getTopAlbums() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        Join invoicedetail = album.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(album.get("invoicedetailList"))));
        cq.distinct(true);
        cq.select(album);
        TypedQuery<Album> query = entityManager.createQuery(cq);
        List<Album> albums = query.getResultList();
        for (Album selectedAlbum : albums) {
            if (selectedAlbum.getInvoicedetailList() == null || selectedAlbum.getInvoicedetailList().isEmpty()) {
                albums.remove(selectedAlbum);
            }
        }
        return albums;
    }

    private List<Invoicedetail> getSaleByTrack(int trackid) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("inventoryid"), trackid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        this.toalNumberOfSales = invoicedetails.size();
        for (Invoicedetail selectedInvoicedetail : invoicedetails) {
            this.totalPiceGrossValue += selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue += selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }

    private List<Invoicedetail> getSaleByAlbum(int albumid) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        Join invoicedetail = album.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("albumid"), albumid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        this.toalNumberOfSales = invoicedetails.size();
        for (Invoicedetail selectedInvoicedetail : invoicedetails) {
            this.totalPiceGrossValue += selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue += selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }

    private List<Invoicedetail> getSaleByArtist(String artist) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();

        cb = entityManager.getCriteriaBuilder();
        cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        invoicedetail = album.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        query = entityManager.createQuery(cq);
        invoicedetails.addAll(query.getResultList());

        this.toalNumberOfSales = invoicedetails.size();
        for (Invoicedetail selectedInvoicedetail : invoicedetails) {
            this.totalPiceGrossValue += selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue += selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }

    private List<Invoicedetail> getTotalSales() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Invoicedetail> invoicedetail = cq.from(Invoicedetail.class);
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0),cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();

        this.toalNumberOfSales = invoicedetails.size();
        for (Invoicedetail selectedInvoicedetail : invoicedetails) {
            this.totalPiceGrossValue += selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue += selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }

    private List<Invoicedetail> getSalesByClient(Client chosenClient) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        Join invoicedetail = sale.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(client.get("clientid"), chosenClient.getClientid()));
        cq.distinct(true);
        cq.select(invoicedetail);
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoiceDetails = query.getResultList();
        this.toalNumberOfSales = invoiceDetails.size();
        for (Invoicedetail selectedInvoicedetail : invoiceDetails) {
            this.totalPiceGrossValue += selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue += selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads += selectedInvoicedetail.getProductdownloaded();
        }
        return invoiceDetails;
    }

    public void validateSpecificSearch(FacesContext context, UIComponent component,
            Object value) {
        UIInput selectInput = (UIInput) component.findComponent("criteria");

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

    private boolean checkArtists(String artist) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("artist"), artist));
        TypedQuery<MusicTrack> queryTrack = entityManager.createQuery(cq);
        List<MusicTrack> musicTracks = queryTrack.getResultList();
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
        if (albums != null && albums.size() > 0) {
            return true;
        }
        return false;

    }

    private boolean checkAlbum(String albumTitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        // Use String to refernce a field
        cq.where(cb.equal(album.get("albumtitle"), albumTitle));
        TypedQuery<Album> query = entityManager.createQuery(cq);
        List<Album> albums = query.getResultList();
        if (albums == null || albums.isEmpty()) {

            return false;
        }
       
        return true;
    }

    private boolean checkUser(String userName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.equal(client.get("username"), userName));
        TypedQuery<Client> query = entityManager.createQuery(cq);
        try {
            query.getSingleResult();
            return true;
        } catch (javax.persistence.NoResultException NoResultException) {
            return false;
        }
    }

    private boolean checkTrack(String trackname) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("tracktitle"), trackname));
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        List<MusicTrack> tracks = query.getResultList();
        if (tracks == null || tracks.isEmpty()) {
            return false;
        }
        return true;
    }

    private MusicTrack getTrack(String trackname) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        cq.where(cb.equal(musicTrack.get("tracktitle"), trackname));
        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);
        try {
            return query.getSingleResult();

        } catch (javax.persistence.NoResultException NoResultException) {
            return null;
        }
    }

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
            return null;
        }
    }

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
            return null;
        }
    }
}
