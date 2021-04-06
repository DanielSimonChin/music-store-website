/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    @Inject
    private LoginController loginController;
    @PersistenceContext
    private EntityManager entityManager;
    private Client registrationBean;
    private String fristPassword;
    private String confrimPassword;
    private boolean userRegistered;
    private String lastPageRegister;
    private static final Pattern VALID_EMAIL_ADDRESS_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$", Pattern.CASE_INSENSITIVE);
    public RegistrationController() {

    }

    public String getLastPageRegister() {
        return lastPageRegister;
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

    public boolean isUserRegistered() {
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

    public String addNewUser() throws RollbackFailureException {
        registrationBean.setPassword(fristPassword);
        clientJpaController.create(registrationBean);
        loginController.getLoginBean().setLoggedIn(true);
        loginController.getLoginBean().setId(clientJpaController.findUser(registrationBean.getUsername(), registrationBean.getPassword()).getClientid());
        loginController.getLoginBean().setPassword(registrationBean.getPassword());
        loginController.getLoginBean().setUsername(registrationBean.getUsername());
        return lastPageRegister;
    }

    public String registerUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        lastPageRegister = context.getViewRoot().getViewId();
        lastPageRegister = lastPageRegister.substring(1, lastPageRegister.length() - 6);
        if (lastPageRegister.equals("register") || lastPageRegister.equals("login")) {
            lastPageRegister = loginController.getLoginLastPage();
        }
        registrationBean = new Client();
        fristPassword = "";
        confrimPassword = "";
        userRegistered = false;
        return "register";
    }

    public void validateEmailError(FacesContext context, UIComponent component,
            Object value) {
        String email = value.toString();
        Matcher matcher = VALID_EMAIL_ADDRESS_PATTERN.matcher(email);
        if (!matcher.find()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "emailError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    public void validateUserNameError(FacesContext context, UIComponent component,
            Object value) {
        String username = value.toString();
        if (!checkUserName(username)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "usernameTakenError", null);
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

        if (query.getResultList().isEmpty()) {
            return true;
        }
        return false;

    }

    public void validatePasswordError(FacesContext context, UIComponent component,
            Object value) {
        String confrimPassword = value.toString();
        if (!fristPassword.equals(confrimPassword)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "notMatchPasswordError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    public void validateAddress(FacesContext context, UIComponent component,
            Object value) {
        String address1 = registrationBean.getAddress1();
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

    public void validateHomePhoneNumber(FacesContext context, UIComponent component,
            Object value) {
        String phoneNumber = value.toString();
        Matcher matcher = VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber);
        if (!matcher.find()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "phoneNumberFormatError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }
    public void validateCellPhoneNumber(FacesContext context, UIComponent component,
            Object value) {
        String cellPhoneNumber = value.toString();
        String homePhoneNumber = ""+registrationBean.getHometelephone();
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
}
