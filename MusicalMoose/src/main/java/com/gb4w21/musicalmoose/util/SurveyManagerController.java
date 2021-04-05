/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.ClientJpaController;
import javax.faces.application.Application;
import com.gb4w21.musicalmoose.controller.ReviewJpaController;
import com.gb4w21.musicalmoose.controller.SurveyJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Review;
import com.gb4w21.musicalmoose.entities.Survey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Named
@SessionScoped
public class SurveyManagerController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(SurveyManagerController.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private SurveyJpaController surveyJpaController;
    
    private List<Survey> surveys;
    
    private Survey selectedSurvey;
    
    private List<Survey> selectedSurveys;
    
    public SurveyManagerController() {
        
    }
    
    public void setSelectedSurvey(Survey selectedSurvey) {
        this.selectedSurvey = selectedSurvey;
    }
    
    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }
    
    public void setSelectedSurveys(List<Survey> selectedSurveys) {
        this.selectedSurveys = selectedSurveys;
    }
    
    public Survey getSelectedSurvey() {
        return this.selectedSurvey;
    }
    
    public List<Survey> getSurveys() {
        return this.surveys;
    }
    
    public List<Survey> getSelectedSurveys() {
        return this.selectedSurveys;
    }

    public void createNewSurvey() {
        this.selectedSurvey = new Survey();
        this.selectedSurvey.setDatesurveyrcreated(new Date());
    }

    public void saveSurvey() {
        if (selectedSurvey.getSurveryinuse()) {
            takeDonwCurrentSurvey();
            
        }
        try {
            if (checkSurveyExists()) {
                surveyJpaController.edit(selectedSurvey);
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "editedSurvey", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
                
            } else {
                
                this.surveys.add(this.selectedSurvey);
                surveyJpaController.create(selectedSurvey);
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "createdSurvey", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
        } catch (RollbackFailureException ex) {
            LOG.info("Trouble creating survey" + ex.getLocalizedMessage());
        } catch (Exception ex) {
            LOG.info("Trouble editing survey" + ex.getLocalizedMessage());
        }
        
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
    
    private boolean checkSurveyExists() {
        Survey survey = surveyJpaController.findSurvey(this.selectedSurvey.getSurveyid());
        if (survey == null || (!survey.getSurveytitle().equals(this.selectedSurvey.getSurveytitle()))) {
            return false;
        }
        return true;
    }

    private void takeDonwCurrentSurvey() {
        Survey runningSurvey = surveyJpaController.getRunningSurvey();
        runningSurvey.setSurveryinuse(false);
        runningSurvey.setDatelastused(new Date());
        try {
            surveyJpaController.edit(runningSurvey);
        } catch (Exception ex) {
            LOG.error("error in taking dinw the current survey:" + ex.getLocalizedMessage());
        }
        
    }

    public void validateSurveyQuestion(FacesContext context, UIComponent component,
            Object value) {
        String question = value.toString();
        
        if (!checkQuestion(question)) {
            
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "surveyQuestionError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            
            throw new ValidatorException(message);
        }
    }

    private boolean checkQuestion(String question) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Survey> cq = cb.createQuery(Survey.class);
        Root<Survey> survey = cq.from(Survey.class);
        cq.select(survey);

        // Use String to refernce a field
        cq.where(cb.equal(survey.get("question"), question));
        
        TypedQuery<Survey> query = entityManager.createQuery(cq);
        
        if (query.getResultList().isEmpty()) {
            
            return true;
        }
        return false;
        
    }

    public void validateSurveyTitle(FacesContext context, UIComponent component,
            Object value) {
        String title = value.toString();
        if (!checkTile(title)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "surveyTitleError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            
            throw new ValidatorException(message);
        }
    }

    private boolean checkTile(String surveytitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Survey> cq = cb.createQuery(Survey.class);
        Root<Survey> survey = cq.from(Survey.class);
        cq.select(survey);
        // Use String to refernce a field
        cq.where(cb.equal(survey.get("surveytitle"), surveytitle));
        
        TypedQuery<Survey> query = entityManager.createQuery(cq);
        
        if (query.getResultList().isEmpty()) {
            return true;
        }
        return false;
        
    }
}
