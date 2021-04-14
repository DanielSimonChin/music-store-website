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
import java.util.Random;
import java.util.ResourceBundle;
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
import javax.persistence.criteria.Root;
/**
 * Controller for survey manager allows the creation and editing of existing surveys
 * @author Alessandro Dare
 * @version 1.0
 */
@Named
@SessionScoped
public class SurveyManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SurveyManagerController.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private SurveyJpaController surveyJpaController;

    private List<Survey> surveys;


    private Survey bigSurvey;

    private List<Survey> selectedSurveys;
    /**
     * Default constructor
     */
    public SurveyManagerController() {

    }
    
    public Survey getBigSurvey() {
        return bigSurvey;
    }

    public void setBigSurvey(Survey bigSurvey) {
        this.bigSurvey = bigSurvey;
    }

 
    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public void setSelectedSurveys(List<Survey> selectedSurveys) {
        this.selectedSurveys = selectedSurveys;
    }


    public List<Survey> getSurveys() {
        return this.surveys;
    }

    public List<Survey> getSelectedSurveys() {
        return this.selectedSurveys;
    }
    /**
     * creates a new survey
     */
    public void createNewSurvey() {

        this.bigSurvey = new Survey();
        this.bigSurvey.setDatesurveyrcreated(new Date());
    }
    /**
     * takes the user to the survey management page resets all values
     */
    @PostConstruct
    public void init() {
        surveys = surveyJpaController.findSurveyEntities();
        bigSurvey = null;
        selectedSurveys = new ArrayList<>();
    }
    /**
     * takes the user to the survey management page resets all values
     * @return String survey page
     */
    public String toSurveryManagement() {
        surveys = surveyJpaController.findSurveyEntities();
        bigSurvey = null;
        selectedSurveys = new ArrayList<>();
        return "adminsurvey";
    }
    /**
     * saves current survey information if a survey is changed to be active the current survey on the front page will be taken down
     */
    public void saveSurvey() {
       
        if (bigSurvey.getSurveryinuse()) {
            
            takeDonwCurrentSurvey();
            
        }
        try {
            
            if (this.bigSurvey.getSurveyid() == null) {
                
                Random random = new Random();
               

                int id = random.nextInt(1000000);
                while (surveyExists(id)) {
                    id = random.nextInt(1000000);
                }
               
                bigSurvey.setSurveyid(id);
              
                setAnswerVotesCreate();
                this.surveys.add(this.bigSurvey);
               
                surveyJpaController.create(bigSurvey);
               
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "createdSurvey", null);
                
                FacesContext.getCurrentInstance().addMessage(null, message);
              
            } else {
                
                setAnswerVotesEdit();
                surveyJpaController.edit(bigSurvey);
              
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "editedSurvey", null);
          
                FacesContext.getCurrentInstance().addMessage(null, message);
   
            }
        } catch (RollbackFailureException ex) {

            LOG.info("Trouble creating survey" + ex.getLocalizedMessage());

        } catch (Exception ex) {

            LOG.info("Trouble editing survey" + ex.getLocalizedMessage());
        }
     
        surveys = surveyJpaController.findSurveyEntities();
 
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
    
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
    /**
     * set votes to zero and not null is a new answer was created
     */
    private void setAnswerVotesCreate() {
        if (this.bigSurvey.getAnserw1() != null && (!this.bigSurvey.getAnserw1().isEmpty())) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2() != null && (!this.bigSurvey.getAnserw2().isEmpty())) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3() != null && (!this.bigSurvey.getAnserw3().isEmpty())) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4() != null && (!this.bigSurvey.getAnserw4().isEmpty())) {
            this.bigSurvey.setAnserw4votes(0);
        }
    }
    /**
     * resets the votes for a given survey to zero if a question was edited or removed
     */
    private void setAnswerVotesEdit() {
        Survey currentSurvey = this.surveyJpaController.findSurvey(this.bigSurvey.getSurveyid());
        if (this.bigSurvey.getAnserw1() == null && (this.bigSurvey.getAnserw1().isEmpty())) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2() == null && (this.bigSurvey.getAnserw2().isEmpty())) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3() == null && (this.bigSurvey.getAnserw3().isEmpty())) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4() == null && (this.bigSurvey.getAnserw4().isEmpty())) {
            this.bigSurvey.setAnserw4votes(0);
        }

        if (this.bigSurvey.getAnserw1() != null && currentSurvey.getAnserw1() != null && (!currentSurvey.getAnserw1().equals(this.bigSurvey.getAnserw1()))) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2() != null && currentSurvey.getAnserw2() != null && (!currentSurvey.getAnserw2().equals(this.bigSurvey.getAnserw2()))) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3() != null && currentSurvey.getAnserw3() != null && (!currentSurvey.getAnserw3().equals(this.bigSurvey.getAnserw3()))) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4() != null && currentSurvey.getAnserw4() != null && (!currentSurvey.getAnserw4().equals(this.bigSurvey.getAnserw4()))) {
            this.bigSurvey.setAnserw4votes(0);
        }

    }
    /**
     * checks to see if specified survey exists
     * @param id int
     * @return boolean true if it exits false if not
     */
    private boolean surveyExists(int id) {
        if (this.surveyJpaController.findSurvey(id) != null) {
            return true;
        }
        return false;
    }
    /**
     * removes the current survey being used from the home page
     */
    private void takeDonwCurrentSurvey() {
        Survey runningSurvey = surveyJpaController.getRunningSurvey();
        if (runningSurvey != null) {
            runningSurvey.setSurveryinuse(false);
            runningSurvey.setDatelastused(new Date());

            try {
                surveyJpaController.edit(runningSurvey);
            } catch (Exception ex) {

                LOG.error("error in taking down the current survey:" + ex.getLocalizedMessage());
            }
        }
    }
    /**
     * Checks to see if the survey question given was already taken if so it returns an error
     * @param context FacesContext
     * @param component UIComponent
     * @param value  Object
     */
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
     /**
     * checks to see if the question for the survey was found in the database
     * @param question String
     * @return boolean true if survey question isn't found false if it is
     */
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
        if (this.bigSurvey.getSurveyid() != null && this.bigSurvey.getSurveyid() == query.getResultList().get(0).getSurveyid()) {
            return true;
        }
        return false;

    }
    /**
     * Checks to see if the survey title given was already taken if so it returns an error
     * @param context FacesContext
     * @param component UIComponent
     * @param value  Object
     */
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
    /**
     * checks to see if the title for the survey was found in the database
     * @param surveytitle String
     * @return boolean true if survey title isn't found false if it is
     */
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
        if (this.bigSurvey.getSurveyid() != null && this.bigSurvey.getSurveyid() == query.getResultList().get(0).getSurveyid()) {
            return true;
        }
        return false;

    }
}
