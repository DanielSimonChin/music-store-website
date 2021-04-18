/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 * Controller for client manager allows the editing of client registration 
 * information can also deactivate an account or promote a client to manager
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class ClientManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ClientManagerController.class);
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$", Pattern.CASE_INSENSITIVE);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private ClientJpaController clientJpaController;

    private List<Client> clients;

    private Client selectedClient;
    private String clientSearch;
    private List<Client> selectedClients;

    /**
     * Default constructor
     */
    public ClientManagerController() {

    }

    public List<Client> getSelectedClients() {
        return selectedClients;
    }

    public String getClientSearch() {
        return clientSearch;
    }

    public void setClientSearch(String clientSearch) {
        this.clientSearch = clientSearch;
    }

    public List<Client> getClients() {
        return clients;
    }

    public Client getSelectedClient() {
        return selectedClient;
    }

    public void setSelectedClients(List<Client> selectedClients) {
        this.selectedClients = selectedClients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
    }

    /**
     * creates a new client
     * @author Alessandro Dare
     */
    public void createNewClient() {
        this.selectedClient = new Client();

    }

    /**
     * takes use to the search page and resets all values
     * @author Alessandro Dare
     * @return String the search page
     */
    @PostConstruct
    public void init() {
        clients = clientJpaController.findClientEntities();
        selectedClients = new ArrayList<>();
        this.selectedClient = null;
    }


    /**
     * gives a list of all clients in the database
     * @author Alessandro Dare
     * @return String the search page
     */
    public String searchAllClients() {
        selectedClient = null;
        selectedClients = new ArrayList<>();
        clients = clientJpaController.findClientEntities();

        return "adminclient";
    }

    /**
     * searches the database for specified clients
     * @author Alessandro Dare
     * @return String the search page
     */
    public String searchClients() {
        selectedClient = null;
        selectedClients = new ArrayList<>();
        clients = getClientFromDatabase(clientSearch);

        return "adminclient";
    }

    /**
     * gets all clients that have the specified username
     * @author Alessandro Dare
     * @param clientSearch String
     * @return List<Client>
     */
    private List<Client> getClientFromDatabase(String clientSearch) {
        String clientSearchText = "%" + clientSearch + "%";

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.like(client.get("username"), clientSearchText));
        TypedQuery<Client> query = entityManager.createQuery(cq);
        List<Client> clients = query.getResultList();

        return clients;
    }

    /**
     * saves changes to client information
     * @author Alessandro Dare
     */
    public void saveClient() {
        try {
            if (this.selectedClient.getUsername() == null) {
                LOG.info("Soemone tried editing a nonexsitence client");
            } else {
                clientJpaController.edit(selectedClient);
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "clientUpdated", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (RollbackFailureException ex) {
            LOG.info("create exception:" + ex.getLocalizedMessage());
        } catch (Exception ex) {
            LOG.info("editing expection:" + ex.getLocalizedMessage());
        }
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * Validates the cellphone number to see if it matches the correct format
     * and doesn't match the home phone if either it throws an error
     * @author Alessandro Dare
     * @param context
     * @param component
     * @param value
     */
    public void validateCellPhoneNumber(FacesContext context, UIComponent component,
            Object value) {
        String cellPhoneNumber = value.toString();
        String homePhoneNumber = "" + selectedClient.getHometelephone();
        Matcher matcher = VALID_PHONE_NUMBER_PATTERN.matcher(cellPhoneNumber);
        if (!matcher.find()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberFormatError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
        if (cellPhoneNumber != null && (!cellPhoneNumber.equals("")) && homePhoneNumber.equals(cellPhoneNumber)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberIdenticalError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    /**
     * Validates the search to see if it returns no results if not it throws and
     * error
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
    public void validateClientSearch(FacesContext context, UIComponent component,
            Object value) {

        if (value == null || value.toString().equals("") || getClientFromDatabase(value.toString()).isEmpty()) {

            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "noResults", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);

        }
    }

    /**
     * calculates the total profit a given client has spent in the current date
     * range
     * @author Alessandro Dare
     * @param chosenClient Client
     * @return double total profit form client
     */
    public double getTotalSales(Client chosenClient) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Client> client = cq.from(Client.class);
        Join sale = client.join("saleList");
        Join invoicedetail = sale.join("invoicedetailList");
        cq.where(cb.equal(invoicedetail.get("invoicedetailremoved"), 0), cb.equal(sale.get("saleremoved"), 0),cb.equal(client.get("clientid"), chosenClient.getClientid()));
        cq.multiselect(invoicedetail.get("currentcost"));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> prices = query.getResultList();
        Float totalValue = 0.f;
        for (Object price : prices) {
            totalValue += (Float) price;
            LOG.info("" + price.toString());
        }
        return totalValue;
    }

    /**
     * Checks to make sure the username given is unique and not used by another
     * users if not it returns false
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
    public void validateClientUserNameError(FacesContext context, UIComponent component,
            Object value) {
        String username = value.toString();

        if (!this.selectedClient.getUsername().equals(username) && !checkUserName(username)) {

            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "usernameTakenError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    /**
     * Check in the database if specified user name was chosen
     * @author Alessandro Dare
     * @param username String
     * @return boolean true if the doesn't match false if not
     */
    private boolean checkUserName(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        cq.select(client);

        // Use String to refernce a field
        cq.where(cb.equal(client.get("username"), username));

        TypedQuery<Client> query = entityManager.createQuery(cq);

        if (query.getResultList().isEmpty()) {

            return true;
        }
        return false;

    }

    /**
     * Validates the address to make sure the second address doesn't match the
     * first
     * @author Alessandro Dare
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
    public void validateAddress(FacesContext context, UIComponent component,
            Object value) {
        String address1 = selectedClient.getAddress1();
        String address2 = value.toString();
        if (address2 != null && (!address2.equals(""))) {
            if (address1.equals(address2)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "addressError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
        }
    }
}
