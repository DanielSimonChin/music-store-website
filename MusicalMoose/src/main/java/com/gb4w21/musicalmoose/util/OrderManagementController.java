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

    public OrderManagementController() {

    }

    @PostConstruct
    public void init() {
        this.addRemoved = true;
        selectedSale = null;
        sales = saleJpaController.findSaleEntities();
        LOG.debug("THIS WAS CALLED MORE THEN ONCE1");
        LOG.debug("THIS WAS CALLED MORE THEN ONCE1");
        LOG.debug("THIS WAS CALLED MORE THEN ONCE1");
        LOG.debug("THIS WAS CALLED MORE THEN ONCE1");
        LOG.debug("THIS WAS CALLED MORE THEN ONCE1");
    }

    public boolean isAddRemoved() {
        return addRemoved;
    }

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

    public boolean isAblum(Invoicedetail invoicedetail) {
        return invoicedetail.getAlbumid() != null;
    }

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

    public int totalNumberOfTracks(Sale sale) {
        int trackNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                trackNumber += invoicedetail.getProfit();
            } else {
                if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getInventoryid() != null) {
                    trackNumber++;
                }
            }
        }
        return trackNumber;
    }

    public int totalNumberOfAlbums(Sale sale) {
        int albumNumber = 0;
        for (Invoicedetail invoicedetail : sale.getInvoicedetailList()) {
            if (addRemoved) {
                albumNumber += invoicedetail.getProfit();
            } else {
                if (!invoicedetail.getInvoicedetailremoved() && invoicedetail.getAlbumid() != null) {
                    albumNumber++;
                }
            }
        }
        return albumNumber;
    }

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

    public void validateSaleDate(FacesContext context, UIComponent component, Object value) {
      
       
        if (value != null) {
            Date saleDate = (Date) value;
            if (checkDateInFutre(saleDate)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "saleDateError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
        }
    }

    public void validateInvoiceDate(FacesContext context, UIComponent component, Object value) {

        if (value != null) {
            Date invoiceDate = (Date) value;
            if (checkDateInFutre(invoiceDate)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "saleDateError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            /**
            if (invoiceDate.compareTo(this.selectedSale.getSaledate()) > 0) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "invoiceDateError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }*/

        }
    }

    /**
     * checks to see if the chosen date is in the future
     *
     * @param chosenDate Date
     * @return boolean true if the date is in the future false if not
     */
    private boolean checkDateInFutre(Date chosenDate) {
        Date currentDate = new Date();
        LOG.info("Date:" + chosenDate.toString());
        return chosenDate.compareTo(currentDate) > 0;

    }
}
