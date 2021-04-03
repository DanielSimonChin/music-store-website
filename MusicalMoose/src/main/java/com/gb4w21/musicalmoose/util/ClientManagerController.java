/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;
import com.gb4w21.musicalmoose.beans.SearchResult;
import javax.faces.application.Application;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Client_;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
@Named
@SessionScoped
public class ClientManagerController implements Serializable{
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
    public ClientManagerController(){
        
    }
    public List<Client> getSelectedClients(){
        if(selectedClients==null){
            selectedClients=new ArrayList<>();
        }
        return selectedClients;
    }
    public String getClientSearch(){
        return clientSearch;
    }
    public void setClientSearch(String clientSearch){
        this.clientSearch=clientSearch;
    }
    public List<Client> getClients(){
        if(clients==null){
            clients=new ArrayList<>();
        }
        LOG.info("clients size22:"+clients.size());
        LOG.info("clients size22:"+clients.size());
        LOG.info("clients size22:"+clients.size());
        LOG.info("clients size22:"+clients.size());
        return clients;
    }
    public Client getSelectedClient(){
        
        return selectedClient;
    }
     public void setSelectedClients(List<Client> selectedClients){
         this.selectedClients=selectedClients;
    }
    public void setClients(List<Client> clients){
         this.clients=clients;
    }
    public void setSelectedClient(Client selectedClient){
         this.selectedClient=selectedClient;
    }
    public void createNewClient() {
        this.selectedClient = new Client();
    
    }
    public String toClientManagement(){
        clients=new ArrayList<>();
        selectedClients=new ArrayList<>();
        this.selectedClient=null;
        return "adminclient";
    }
    public String searchAllClients(){
        selectedClient=null;
        selectedClients=new ArrayList<>();
        clients=clients=clientJpaController.findClientEntities();
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        return null;
    }
    public String searchClients(){
        selectedClient=null;
        selectedClients=new ArrayList<>();
        clients=getClientFromDatabase(clientSearch);
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        return "adminclient";
    }
    private List<Client> getClientFromDatabase(String clientSearch){
        String clientSearchText = "%" + clientSearch + "%";
        LOG.info("client search:"+clientSearch);
        LOG.info("client search:"+clientSearch);
        LOG.info("client search:"+clientSearch);
        LOG.info("client search:"+clientSearch);
        LOG.info("client search:"+clientSearch);
        LOG.info("client search:"+clientSearch);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        cq.where(cb.like(client.get("username"), clientSearchText));
        TypedQuery<Client> query = entityManager.createQuery(cq);
        List<Client> clients=query.getResultList();
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        LOG.info("clients size:"+clients.size());
        return clients;
    }
       public void saveClient() {
        if (this.selectedClient.getUsername()== null) {
            this.selectedClient.setUsername(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9));
            this.clients.add(this.selectedClient);
             FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "clientCreated", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
        else {
             FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "clientUpdated", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
       
       public void deleteClient() {
        this.clients.remove(this.selectedClient);
        try {
            clientJpaController.destroy(this.selectedClient.getClientid());
        } catch (RollbackFailureException ex ) {
          LOG.info("Destroyed error the client with the id "+selectedClient.getClientid()+" could not be deleted");
        } catch (Exception ex) {
            LOG.info("Destroyed error the client with the id "+selectedClient.getClientid()+" could not be deleted");
        }
        this.selectedClient = null;
        FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "clientRemvoed", null);
       
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
       
        public boolean hasSelectedClients() {
        return this.selectedClients != null && !this.selectedClients.isEmpty();
    }
    public String getDeleteClientButtonMessage() {
        if (hasSelectedClients()) {
            int size = this.selectedClients.size();
            FacesMessage messageProducts = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "productsSelected", null);
            FacesMessage messageOneProduct = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "opneProductSelected", null);
            return size > 1 ? size + messageProducts.getDetail() : messageOneProduct.getDetail();
        }
        FacesMessage messageDelete = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "delete", null);
        
        return messageDelete.getDetail();
    }
    public void deleteSelectedClients() {
        this.clients.removeAll(this.selectedClients);
        for(Client client :this.selectedClients){
            try {
                clientJpaController.destroy(client.getClientid());
            } catch (RollbackFailureException ex) {
                LOG.info("Destroyed error the client with the id "+selectedClient.getClientid()+" could not be deleted");
            } catch (Exception ex) {
                LOG.info("Destroyed error the client with the id "+selectedClient.getClientid()+" could not be deleted");
            }
        }
        this.selectedClients = null;
        FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "clientsRemvoed", null);
        FacesContext.getCurrentInstance().addMessage(null, message);
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
    }
    public void validateCellPhoneNumber(FacesContext context, UIComponent component,
            Object value) {
        String cellPhoneNumber = value.toString();
        String homePhoneNumber = ""+selectedClient.getHometelephone();
        Matcher matcher = VALID_PHONE_NUMBER_PATTERN.matcher(cellPhoneNumber);
        if (!matcher.find()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberFormatError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
        if(cellPhoneNumber!=null&&(!cellPhoneNumber.equals(""))&&homePhoneNumber.equals(cellPhoneNumber)){
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberIdenticalError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }
     public void validateClientSearch(FacesContext context, UIComponent component,
            Object value) {
         
        if (value==null||value.toString().equals("")||getClientFromDatabase(value.toString()).isEmpty()) {

                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "noResults", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            
        }
    }
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
