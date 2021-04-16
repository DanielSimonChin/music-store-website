package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.entities.Client;
import java.io.Serializable;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.primefaces.component.calendar.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for validation handles validations that are handled in multiple
 * pages
 *
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class ValidationController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ValidationController.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private ClientJpaController clientJpaController;
    private static final Pattern VALID_EMAIL_ADDRESS_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$", Pattern.CASE_INSENSITIVE);

    /**
     * Default constructor
     */
    public ValidationController() {

    }

    /**
     * Checks to make sure email given is the correct format if not it returns
     * and error
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
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

    /**
     * Checks to make sure the username given is unique and not used by another
     * users if not it returns false
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
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

    /**
     * Check in the database if specified user name was chosen
     *
     * @param username String
     * @return boolean true if the doesn't match false if not
     */
    private boolean checkUserName(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Client> cq = cb.createQuery(Client.class);
        Root<Client> client = cq.from(Client.class);
        cq.select(client);
        LOG.info("user error" + username);
        // Use String to refernce a field
        cq.where(cb.equal(client.get("username"), username));

        TypedQuery<Client> query = entityManager.createQuery(cq);

        if (query.getResultList().isEmpty()) {
            LOG.info("user error empty");
            return true;
        }
        return false;

    }

    /**
     * Checks to make sure the phone number given is the correct format if not
     * it throws an error
     *
     * @param context FacesContext
     * @param component UIComponent
     * @param value Object
     */
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

    /**
     * Validates the from date field checks to make sure the date wasn't in the
     * future or is after or matches the to field if so it returns and error
     *
     * @param context Object
     * @param component UIComponent
     * @param value FacesContext
     */
    public void validateDateFrom(FacesContext context, UIComponent component, Object value) {
        UIInput dateInput = (UIInput) component.findComponent("toSearch");
        Calendar calendar = (Calendar) dateInput;

        if (value != null) {
            Date fromDate = (Date) value;
            if (checkDateInFutre(fromDate)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "dateErrorFrom", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (calendar.getValue() != null) {
                Date toDate = (Date) calendar.getValue();

                if (fromDate.compareTo(toDate) >= 0) {
                    FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                            "com.gb4w21.musicalmoose.bundles.messages", "dateAfterError", null);
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);

                    throw new ValidatorException(message);
                }
            }
        }
    }

    /**
     * Validates the to date field checks to make sure the date wasn't in the
     * future or is before or matches the from field if so it returns and error
     *
     * @param context Object
     * @param component UIComponent
     * @param value FacesContext
     */
    public void validateDateTo(FacesContext context, UIComponent component, Object value) {
        LOG.debug("uiComponent:" + component.getClientId());
        LOG.debug("uiComponent:" + component.getId());
        LOG.debug("uiComponent:" + component.getFamily());
        LOG.debug("uiComponent:" + component.getChildren());
        UIInput dateInput = (UIInput) component.findComponent("fromSearch");
        LOG.debug("uiComponent:" + dateInput.getClientId());
        LOG.debug("uiComponent:" + dateInput.getId());
        LOG.debug("uiComponent:" + dateInput.getFamily());
        LOG.debug("uiComponent:" + dateInput.getChildren());
        Calendar calendar = (Calendar) dateInput;
        //calendar.getAttributes()

        if (value != null) {
            Date toDate = (Date) value;
            if (checkDateInFutre(toDate)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "dateErrorTo", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (calendar.getValue() != null) {
                Date fromDate = (Date) calendar.getValue();

                if (toDate.compareTo(fromDate) <= 0) {
                    FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                            "com.gb4w21.musicalmoose.bundles.messages", "dateBeforeError", null);
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);

                    throw new ValidatorException(message);
                }
            }
        }

    }

    public void validateDateSale(FacesContext context, UIComponent component, Object value) {
        FacesContext context1 = FacesContext.getCurrentInstance();

        UIInput dateInput = (UIInput) component.findComponent("saleDateInput");
        LOG.debug("uiComponentQQQQQQQQ:" + context1.getViewRoot().getChildren().size());
        LOG.debug("uiComponentQQQQQQQQQ:" + context1.getViewRoot().findComponent("body"));
        LOG.debug("uiComponentQQQQQQQQQ:" + context.getViewRoot().getChildren().size());
        LOG.debug("uiComponentQQQQQQQQQ:" +context.getViewRoot().getChildren().size());
        for(UIComponent uc:context.getViewRoot().getChildren()){
            LOG.debug("uiComponentZZZZZZZZZ:" + uc.getRendererType());
            LOG.debug("uiComponentZZZZZZZZZ:" + uc.toString());
            LOG.debug("uiComponentZZZZZZZZZ:" + uc.getFamily());
        }
        Calendar calendar = (Calendar) dateInput;

    }

    public void validateInvoiceDate(FacesContext context, UIComponent component, Object value) {
        for (UIComponent uiComponent : component.getChildren()) {
            LOG.debug("222uiComponent:" + uiComponent.getClientId());
            LOG.debug("222uiComponent:" + uiComponent.getId());
            LOG.debug("222uiComponent:" + uiComponent.getClientId());
            LOG.debug("222uiComponent:" + uiComponent.getId());
            LOG.debug("111uiComponent:" + uiComponent.getClientId());
            LOG.debug("111uiComponent:" + uiComponent.getId());
            LOG.debug("111uiComponent:" + uiComponent.getClientId());
            LOG.debug("111uiComponent:" + uiComponent.getId());
        }

        UIInput dateInput = (UIInput) component.findComponent("saleDateInput");
        LOG.debug("33uiComponent:" + dateInput);
        LOG.debug("33uiComponent:" + dateInput);
        LOG.debug("33uiComponent:" + dateInput);
        LOG.debug("33uiComponent:" + dateInput);
        Calendar calendar = (Calendar) dateInput;

        if (value != null) {
            Date invoiceDate = (Date) value;
            if (checkDateInFutre(invoiceDate)) {
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "saleDateError", null);
                message.setSeverity(FacesMessage.SEVERITY_ERROR);

                throw new ValidatorException(message);
            }
            if (calendar != null) {
                if (calendar.getValue() != null) {
                    Date saleDate = (Date) calendar.getValue();
                    if (invoiceDate.compareTo(saleDate) > 0) {
                        FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                                "com.gb4w21.musicalmoose.bundles.messages", "invoiceDateError", null);
                        message.setSeverity(FacesMessage.SEVERITY_ERROR);

                        throw new ValidatorException(message);
                    }
                }
            }
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
