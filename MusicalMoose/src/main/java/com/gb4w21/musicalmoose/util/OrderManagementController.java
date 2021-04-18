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
import org.primefaces.component.calendar.Calendar;
/**
 * The controller to manage the sales page use it to edit any sales 
 * changes made by the manager and allows the sale information to be displayed
 * @author Alessandro Dare
 * @version 1.0
 */
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
    private boolean addRemoved;
    /**
     * Default constructor
     */
    public OrderManagementController() {

    }
    /**
     * post constructor used to reset values
     * @author Alessandro Dare
     */
    @PostConstruct
    public void init() {
        this.addRemoved = true;
        selectedSale = null;
        sales = saleJpaController.findSaleEntities();
       
    }

    public boolean isAddRemoved() {
        return addRemoved;
    }
    /**
     * changes table to show or exclude sales that have been removed
     * @author Alessandro Dare
     * @return String admin page
     */
    public String changeTable() {
        sales = saleJpaController.findSaleEntities();
        if (addRemoved) {
            addRemoved = false;
            removeAdded();
        } else {
            addRemoved = true;
        }

        return "adminsale";
    }
    /**
     * removes all invoices that have been marked as removed from the sale
     * @author Alessandro Dare
     */
    private void removeAdded() {
        List<Sale> removedSale = new ArrayList<>();
        for (Sale sale : this.sales) {
            if (sale.getSaleremoved()) {
                removedSale.add(sale);

            }
        }
        this.sales.removeAll(removedSale);
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
    /**
     * Shows weather an invoice is for an album or track
     * @author Alessandro Dare
     * @param invoicedetail Invoicedetail
     * @return boolean album if true track if false
     */
    public boolean isAblum(Invoicedetail invoicedetail) {
        return invoicedetail.getAlbumid() != null;
    }
   /**
     * calculates the total amount of profit the website made on the sale
     * @author Alessandro Dare
     * @param sale Sale
     * @return  float total profit
     */
    public float totalProfit(Sale sale) {
        float totalProfit = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                totalProfit += invoicedetail.getProfit();
            } else {
                if (!invoicedetail.getInvoicedetailremoved()) {
                    totalProfit += invoicedetail.getProfit();
                }

            }
        }
        return totalProfit;
    }
 /**
     * calculates the total amount the use sent on the sale
     * @author Alessandro Dare
     * @param sale Sale
     * @return  float total current cost
     */
    public float totalCurrentCost(Sale sale) {
        float totalCurrentCost = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                totalCurrentCost += invoicedetail.getCurrentcost();
            } else {
                if (!invoicedetail.getInvoicedetailremoved()) {
                    totalCurrentCost += invoicedetail.getCurrentcost();
                }
            }
        }
        return totalCurrentCost;
    }
     /**
     * calculates the total number of tracks for a specific sale
     * @author Alessandro Dare
     * @param sale Sale
     * @return  int number of tracks
     */
    public int totalNumberOfTracks(Sale sale) {
        int trackNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                trackNumber ++;
            } else {
                if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getInventoryid() != null) {
                    trackNumber++;
                }
            }
        }
        return trackNumber;
    }
    /**
     * calculates the total number of albums for a specific sale
     * @author Alessandro Dare
     * @param sale Sale
     * @return  int number of albums
     */
    public int totalNumberOfAlbums(Sale sale) {
        int albumNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                albumNumber ++;
            } else {
                if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getAlbumid() != null) {
                    albumNumber++;
                }
            }
        }
        return albumNumber;
    }
    /**
     * saves the changes to the specified sale and it's related invoices
     * @author Alessandro Dare
     */
    public void saveSale() {

        try {

            if (this.selectedSale.getSaleid() != null) {

                for (Invoicedetail invoicedetail : selectedSale.getInvoicedetailList()) {
                    invoicedetailJpaController.edit(invoicedetail);
                }
                saleJpaController.edit(selectedSale);

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "saleEdited", null);

                FacesContext.getCurrentInstance().addMessage(null, message);

            } else {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "noSaleSelected", null);

                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            Sale s = saleJpaController.findSale(selectedSale.getSaleid());

        } catch (Exception ex) {

            LOG.info("Trouble editing sale" + ex.getLocalizedMessage());
        }

        sales = this.saleJpaController.findSaleEntities();
        if (!this.addRemoved) {
            this.removeAdded();
        }
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");

        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    

    /**
     * checks to see if the chosen date is in the future
     * @author Alessandro Dare
     * @param chosenDate Date
     * @return boolean true if the date is in the future false if not
     */
    private boolean checkDateInFutre(Date chosenDate) {
        Date currentDate = new Date();
        LOG.info("Date:" + chosenDate.toString());
        return chosenDate.compareTo(currentDate) > 0;

    }
}
