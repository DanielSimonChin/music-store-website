package com.gb4w21.musicalmoose.util;

import javax.faces.application.Application;
import com.gb4w21.musicalmoose.controller.ReviewJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Review;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
public class ReviewManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ReviewManagerController.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    private ReviewJpaController reviewJpaController;

    private List<Review> reviews;

    private Review selectedReview;

    private List<Review> selectedReviews;

    public ReviewManagerController() {

    }
    public List<Review> getReviews(){
        if(reviews==null||reviews.isEmpty()){
            reviews=reviewJpaController.findReviewEntities();
        }
        return reviews;
    }
    public void setReviews(List<Review> reviews){
         this.reviews=reviews;
    }
    public List<Review> getSelectedReviews(){
        return selectedReviews;
    }
    public void setSelectedReviews(List<Review> selectedReviews){
         this.selectedReviews=selectedReviews;
    }
    public Review getSelectedReview(){
        return selectedReview;
    }
    public void setSelectedReview(Review selectedReview){
        this.selectedReview=selectedReview;
    }
    public String toReviewPage(){
        reviews = new ArrayList<>();
        selectedReviews = new ArrayList<>();
        this.selectedReview = null;
        return "adminreview";
    }
     public void saveReview() {
       try{
        if (this.selectedReview.getClientid()== null) {
               LOG.info("Some one tried to edit a non exsitent review");
        } else {
            reviewJpaController.edit(selectedReview);
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "reviewUpdated", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
       } catch (Exception ex) {
            LOG.info("Error with editing:"+ex.getLocalizedMessage());
        }
      
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
}
