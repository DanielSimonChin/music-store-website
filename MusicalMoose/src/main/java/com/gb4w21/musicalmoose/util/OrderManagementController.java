package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.InvoicedetailJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import javax.faces.application.Application;
import com.gb4w21.musicalmoose.controller.ReviewJpaController;
import com.gb4w21.musicalmoose.controller.SaleJpaController;
import com.gb4w21.musicalmoose.controller.SurveyJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NullCategoryException;
import com.gb4w21.musicalmoose.controller.exceptions.NullSearchValueException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Review;
import com.gb4w21.musicalmoose.entities.Sale;
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
import javax.annotation.PostConstruct;
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

@Named
@SessionScoped
public class OrderManagementController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(OrderManagementController.class);
    @Inject
    private SaleJpaController saleJpaController;
    @Inject
    private InvoicedetailJpaController invoicedetailJpaController;
    @PersistenceContext
    private EntityManager entityManager;
    private Sale selectedSale;
    private List<Sale> sales;

    public OrderManagementController() {

    }
     @PostConstruct
    public void init() {
        selectedSale=null;
        sales = saleJpaController.findSaleEntities();
    }
    public Sale getSelectedSale() {
        return this.selectedSale;
    }

    public void setSelectedSale(Sale selectedSale) {
        this.selectedSale = selectedSale;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    public float totalProfit(Sale sale) {
        float totalProfit = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (!invoicedetail.getInvoicedetailremoved()) {
                totalProfit += invoicedetail.getProfit();
            }
        }
        return totalProfit;
    }

    public float totalCurrentCost(Sale sale) {
        float totalCurrentCost = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (!invoicedetail.getInvoicedetailremoved()) {
                totalCurrentCost += invoicedetail.getCurrentcost();
            }
        }
        return totalCurrentCost;
    }

    public int totalNumberOfTracks(Sale sale) {
        int trackNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getInventoryid() != null) {
                trackNumber++;
            }
        }
        return trackNumber;
    }

    public int totalNumberOfAlbums(Sale sale) {
        int albumNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getAlbumid() != null) {
                albumNumber++;
            }
        }
        return albumNumber;
    }
}
