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
    public ReportManagementController() {

    }
    public String getReportCategory(){
        return reportCategory;
    }
    public void setReportCategory(String reportCategory){
        this.reportCategory=reportCategory;
    }
    public int getTotalPiceNetValue() {
        return totalPiceNetValue;
    }
     public void setTotalPiceNetValue(int totalPiceNetValue) {
        this.totalPiceNetValue= totalPiceNetValue;
    }
     public int getTotalPiceGrossValue() {
        return totalPiceGrossValue;
    }
     public void setTotalPiceGrossValue(int totalPiceGrossValue) {
        this.totalPiceGrossValue= totalPiceGrossValue;
    }
    public int getToalNumberOfSales() {
        return toalNumberOfSales;
    }
    public void setToalNumberOfSales(int toalNumberOfSales) {
        this.toalNumberOfSales= toalNumberOfSales;
    }
    public int getTotalNumberOfDownloads(){
        return totalNumberOfDownloads;
    }
    public void setTotalNumberOfDownloads(int totalNumberOfDownloads) {
        this.totalNumberOfDownloads= totalNumberOfDownloads;
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

    private List<Client> getUnsoldClients() {
        List<Client> clients = this.clientJpaController.findClientEntities();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");

        cq.where(cb.between(sale.get("saledate"), fromDate, toDate));

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
        Join sale = musicTrack.join("invoicedetailList");

        cq.where(cb.between(sale.get("saledate"), fromDate, toDate));

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
        cq.where(cb.between(sale.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(client.get("saleList"))));
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
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(musicTrack.get("invoicedetailList"))));
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
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(cb.size(album.get("invoicedetailList"))));
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
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("inventoryid"), trackid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        this.toalNumberOfSales=invoicedetails.size();
        for(Invoicedetail selectedInvoicedetail:invoicedetails){
            this.totalPiceGrossValue+=selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue+=selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads+=selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }
    private List<Invoicedetail> getSaleByAlbum(int albumid) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        Join invoicedetail = album.join("invoicedetailList");
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("albumid"), albumid));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        this.toalNumberOfSales=invoicedetails.size();
        for(Invoicedetail selectedInvoicedetail:invoicedetails){
            this.totalPiceGrossValue+=selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue+=selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads+=selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }
    private List<Invoicedetail> getSaleByArtist(String artist) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        Join invoicedetail = musicTrack.join("invoicedetailList");
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(musicTrack.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        
         cb = entityManager.getCriteriaBuilder();
         cq = cb.createQuery(Invoicedetail.class);
        Root<Album> album = cq.from(Album.class);
        invoicedetail = album.join("invoicedetailList");
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(album.get("artist"), artist));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        query = entityManager.createQuery(cq);
         invoicedetails.addAll(query.getResultList());
        
        
        this.toalNumberOfSales=invoicedetails.size();
        for(Invoicedetail selectedInvoicedetail:invoicedetails){
            this.totalPiceGrossValue+=selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue+=selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads+=selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }
    private List<Invoicedetail> getTotalSales(){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Invoicedetail> invoicedetail = cq.from(Invoicedetail.class);
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate));
        cq.orderBy(cb.desc(invoicedetail.get("saledate")));
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoicedetails = query.getResultList();
        
        this.toalNumberOfSales=invoicedetails.size();
        for(Invoicedetail selectedInvoicedetail:invoicedetails){
            this.totalPiceGrossValue+=selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue+=selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads+=selectedInvoicedetail.getProductdownloaded();
        }
        return invoicedetails;
    }
    public List<Invoicedetail> getSalesByClient(Client chosenClient){
  
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoicedetail> cq = cb.createQuery(Invoicedetail.class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        Join invoicedetail = sale.join("invoicedetailList");
        cq.where(cb.between(invoicedetail.get("saledate"), fromDate, toDate), cb.equal(client.get("clientid"), chosenClient.getClientid()));
        cq.multiselect(invoicedetail.get("totalgrossvalue"));
        TypedQuery<Invoicedetail> query = entityManager.createQuery(cq);
        List<Invoicedetail> invoiceDetails = query.getResultList();
        this.toalNumberOfSales=invoiceDetails.size();
        for(Invoicedetail selectedInvoicedetail:invoiceDetails){
            this.totalPiceGrossValue+=selectedInvoicedetail.getTotalnetvalue();
            this.totalPiceNetValue+=selectedInvoicedetail.getTotalgrossvalue();
            this.totalNumberOfDownloads+=selectedInvoicedetail.getProductdownloaded();
        }
        return invoiceDetails;
    }
}
