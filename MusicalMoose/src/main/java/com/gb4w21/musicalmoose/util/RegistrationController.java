/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.validator.ValidatorException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
@Named
@SessionScoped
public class RegistrationController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(LoginController.class);
    @Inject
    private ClientJpaController clientJpaController;
    @PersistenceContext
    private EntityManager entityManager;
    private Client registrationBean;
    private String fristPassword;
    private String confrimPassword;
    private boolean userRegistered;
    public RegistrationController() {

    }
    public String getFristPassword() {
        return fristPassword;
    }

    public void setFristPassword(String fristPassword) {
        this.fristPassword = fristPassword;
    }
    public String getConfrimPassword() {
        return confrimPassword;
    }
    public boolean isUserRegistered(){
        return userRegistered;
    }
    
    public void setConfrimPassword(String confrimPassword) {
        this.confrimPassword = confrimPassword;
    }
    public Client getRegistrationBean() {
        return registrationBean;
    }

    public void setRegistrationBean(Client registrationBean) {
        this.registrationBean = registrationBean;
    }
    public void addNewUser() throws RollbackFailureException{
        registrationBean.setPassword(fristPassword);
        clientJpaController.create(registrationBean);
        userRegistered=true;
    }
    public String registerUser(){
        registrationBean=new Client();
        fristPassword="";
        confrimPassword="";
        userRegistered=false;
        return "register";
    }
    public void validateUserNameError(FacesContext context, UIComponent component,
            Object value) {
        UIInput usernameInput = (UIInput) component.findComponent("cname");
        
        String username = ((String) usernameInput.getLocalValue());
        if (!checkUserName(username)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "notMatchPasswordError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
           
            throw new ValidatorException(message);
        }
    }
    private boolean checkUserName(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        // Use String to refernce a field
        cq.where(cb.equal(client.get("username"), username));

        TypedQuery<Client> query = entityManager.createQuery(cq);

        if(query.getResultList().isEmpty()){
            return true;
        }
        return false;

    }
     public void validatePasswordError(FacesContext context, UIComponent component,
            Object value) {
         UIInput passwordInput = (UIInput) component.findComponent("password1");
        UIInput confrimPasswordInput = (UIInput) component.findComponent("password2");

        String password = ((String) passwordInput.getLocalValue());
        String confrimPassword = ((String) confrimPasswordInput.getLocalValue());
        if (!password.equals(confrimPassword)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "notMatchPasswordError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
           
            throw new ValidatorException(message);
        }
    }
}
