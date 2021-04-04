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

    public void saveSurvey() {
        try {
            if (checkSurveyExists()) {
                this.selectedSurvey.setSurveytitle(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 9));
                this.surveys.add(this.selectedSurvey);
                surveyJpaController.create(selectedSurvey);
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "createdSurvey", null);
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                surveyJpaController.edit(selectedSurvey);
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "editedSurvey", null);
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
            return true;
        }
        return false;
    }
}
