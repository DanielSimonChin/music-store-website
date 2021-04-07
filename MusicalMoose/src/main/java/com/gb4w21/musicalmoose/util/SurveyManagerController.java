
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

    //private Survey selectedSurvey;
    private Survey bigSurvey;

    private List<Survey> selectedSurveys;

    public SurveyManagerController() {

    }

    public Survey getBigSurvey() {
        return bigSurvey;
    }

    public void setBigSurvey(Survey bigSurvey) {
        this.bigSurvey = bigSurvey;
    }

    //   public void setSelectedSurvey(Survey selectedSurvey) {
    //      this.selectedSurvey = selectedSurvey;
    //  }
    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public void setSelectedSurveys(List<Survey> selectedSurveys) {
        this.selectedSurveys = selectedSurveys;
    }

//    public Survey getSelectedSurvey() {
    //       return this.selectedSurvey;
//    }
    public List<Survey> getSurveys() {
        return this.surveys;
    }

    public List<Survey> getSelectedSurveys() {
        return this.selectedSurveys;
    }

    public void createNewSurvey() {

        this.bigSurvey = new Survey();
        this.bigSurvey.setDatesurveyrcreated(new Date());
    }

    public String toSurveryManagement() {
        surveys = surveyJpaController.findSurveyEntities();
        bigSurvey = null;
        bigSurvey = null;
        selectedSurveys = new ArrayList<>();
        return "adminsurvey";
    }

    public void saveSurvey() {
        LOG.info("ERROR1");
        if (bigSurvey.getSurveryinuse()) {
            LOG.info("ERROR2");
            takeDonwCurrentSurvey();
            LOG.info("ERROR3");
        }
        try {
            LOG.info("ERROR4");
            if (this.bigSurvey.getSurveyid() == null) {
                LOG.info("ERROR5");
                Random random = new Random();
                LOG.info("ERROR6");

                int id = random.nextInt(1000000);
                while (surveyExists(id)) {
                    id = random.nextInt(1000000);
                }
                LOG.info("ERROR7");
                bigSurvey.setSurveyid(id);
                LOG.info("ERROR8");
                setAnswerVotesCreate();
                this.surveys.add(this.bigSurvey);
                LOG.info("ERROR9");
                surveyJpaController.create(bigSurvey);
                LOG.info("ERROR10");
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "createdSurvey", null);
                LOG.info("ERROR11");
                FacesContext.getCurrentInstance().addMessage(null, message);
                LOG.info("ERROR12");
            } else {
                LOG.info("ERROR13");
                setAnswerVotesEdit();
                surveyJpaController.edit(bigSurvey);
                LOG.info("ERROR14");
                FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                        "com.gb4w21.musicalmoose.bundles.messages", "editedSurvey", null);
                LOG.info("ERROR15");
                FacesContext.getCurrentInstance().addMessage(null, message);
                LOG.info("ERROR16");
            }
        } catch (RollbackFailureException ex) {

            LOG.info("Trouble creating survey" + ex.getLocalizedMessage());

        } catch (Exception ex) {

            LOG.info("Trouble editing survey" + ex.getLocalizedMessage());
        }
        LOG.info("ERROR17");
        surveys = surveyJpaController.findSurveyEntities();
        LOG.info("ERROR18");
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        LOG.info("ERROR19");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    private void setAnswerVotesCreate() {
        if (this.bigSurvey.getAnserw1()!=null&&(!this.bigSurvey.getAnserw1().isEmpty())) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2()!=null&&(!this.bigSurvey.getAnserw2().isEmpty())) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3()!=null&&(!this.bigSurvey.getAnserw3().isEmpty())) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4()!=null&&(!this.bigSurvey.getAnserw4().isEmpty())) {
            this.bigSurvey.setAnserw4votes(0);
        }
    }
     private void setAnswerVotesEdit() {
        Survey currentSurvey= this.surveyJpaController.findSurvey(this.bigSurvey.getSurveyid());
        if (this.bigSurvey.getAnserw1()==null&&(this.bigSurvey.getAnserw1().isEmpty())) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2()==null&&(this.bigSurvey.getAnserw2().isEmpty())) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3()==null&&(this.bigSurvey.getAnserw3().isEmpty())) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4()==null&&(this.bigSurvey.getAnserw4().isEmpty())) {
            this.bigSurvey.setAnserw4votes(0);
        }
        
        if (this.bigSurvey.getAnserw1()!=null&&currentSurvey.getAnserw1()!=null&&(!currentSurvey.getAnserw1().equals(this.bigSurvey.getAnserw1()))) {
            this.bigSurvey.setAnserw1votes(0);
        }
        if (this.bigSurvey.getAnserw2()!=null&&currentSurvey.getAnserw2()!=null&&(!currentSurvey.getAnserw2().equals(this.bigSurvey.getAnserw2()))) {
            this.bigSurvey.setAnserw2votes(0);
        }
        if (this.bigSurvey.getAnserw3()!=null&&currentSurvey.getAnserw3()!=null&&(!currentSurvey.getAnserw3().equals(this.bigSurvey.getAnserw3()))) {
            this.bigSurvey.setAnserw3votes(0);
        }
        if (this.bigSurvey.getAnserw4()!=null&&currentSurvey.getAnserw4()!=null&&(!currentSurvey.getAnserw4().equals(this.bigSurvey.getAnserw4()))) {
            this.bigSurvey.setAnserw4votes(0);
        }
        
    }
    private boolean surveyExists(int id) {
        if (this.surveyJpaController.findSurvey(id) != null) {
            return true;
        }
        return false;
    }

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
        if (this.bigSurvey.getSurveyid() != null && this.bigSurvey.getSurveyid() == query.getResultList().get(0).getSurveyid()) {
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
        if (this.bigSurvey.getSurveyid() != null && this.bigSurvey.getSurveyid() == query.getResultList().get(0).getSurveyid()) {
            return true;
        }
        return false;

    }
}
