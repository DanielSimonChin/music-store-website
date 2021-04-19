/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller.management;

import com.gb4w21.musicalmoose.controller.NewsJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.News;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the controller used for the admin management of the news
 *
 * @author Victor Ouy
 */
@Named
@SessionScoped
public class NewsManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(NewsManagerController.class);

    @Inject
    private NewsJpaController newsJpaController;

    private List<News> newsList;
    private List<News> selectedNewsList;
    private News selectedNews;

    /**
     * The data table should be filled with all the ad entity objects from the
     * database.
     */
    @PostConstruct
    public void init() {
        this.newsList = newsJpaController.findNewsEntities();
    }

    public List<News> getNewsList() {
        return this.newsList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }

    public List<News> getSelectedNewsList() {
        return this.selectedNewsList;
    }

    public void setSelectedNewsList(List<News> selectedNewsList) {
        this.selectedNewsList = selectedNewsList;
    }

    public News getSelectedNews() {
        return this.selectedNews;
    }

    public void setSelectedNews(News selectedNews) {
        this.selectedNews = selectedNews;
    }

    public void openNew() {
        this.selectedNews = new News();
    }

    /**
     * Check if the selected news is empty
     *
     * @return true if it is not empty
     */
    public boolean hasSelectedNewsList() {
        return this.selectedNewsList != null && !this.selectedNewsList.isEmpty();
    }

    /**
     * Creates a proper message that notifies the user whenever they edit,
     * create or delete a news
     *
     * @param summary
     * @param detail
     * @return FaceMessage
     */
    private FacesMessage createMsg(String summary, String detail) {
        FacesMessage facesMsgDets = com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", summary, null);
        FacesMessage facesMsgSummary = com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", detail, null);
        facesMsgDets.setDetail(facesMsgSummary.getSummary());
        return facesMsgDets;
    }

    /**
     * Set the available table field for each selected bannerAd to false.
     *
     * @throws Exception
     */
    public void removeSelectedNewsList() throws Exception {
        for (News news : this.selectedNewsList) {
            news.setDisplayed(Boolean.FALSE);
            this.newsJpaController.edit(news);
        }

        this.selectedNewsList = null;
        FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "newsSetNotDisplayed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
        LOG.info("Removed selected news");
    }

    /**
     * Set the selected ad's available field to false.
     *
     * @throws Exception
     */
    public void removeNews() throws Exception {
        this.selectedNews.setDisplayed(Boolean.FALSE);
        this.newsJpaController.edit(this.selectedNews);
        this.selectedNews = null;
        FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "newsSetNotDisplayed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        LOG.info("Removed news");
    }

    /**
     * Update the selected news and display a message for the user.
     *
     * @throws Exception
     */
    public void saveNews() throws Exception {
        if (checkValidNews()) {
            // If this is a new track
            if (this.selectedNews.getNewsid() == null) {
                LOG.info("CREATING A NEW NEWS ENTITY");
                this.newsJpaController.create(this.selectedNews);
                this.newsList.add(this.selectedNews);
                FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "newsCreated"));
                //A currently existing ad that was edited.
            } else {
                LOG.info("EDITING AN AD");
                this.newsJpaController.edit(this.selectedNews);
                FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "newsUpdated"));
            }
        } else {
            this.selectedNews.setDisplayed(Boolean.FALSE);
            FacesMessage facesMsg = createMsg("invalid", "newsInvalid");
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, facesMsg);
            
            LOG.info("Invalid change");
        }
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * Checks if the ad created/update is valid with the other ads
     *
     * @return boolean
     */
    private boolean checkValidNews() {
        int displayedCounter = 0;
        if (selectedNews.getDisplayed()) {
            for (int i = 0; i < newsList.size(); i++) {
                if (newsList.get(i).getNewsid() != selectedNews.getNewsid()) {
                    if (newsList.get(i).getDisplayed()) {
                        displayedCounter++;
                    }
                }
            }
        }
        if (displayedCounter >= 3) {
            return false;
        }
        return true;
    }

    /**
     * When the cancel button is clicked for the management form, all changes
     * made before the cancel button was clicked will not affect the datatable
     * or the database. Reset the datatable values.
     *
     * @throws NonexistentEntityException
     */
    public void cancelNewsForm() throws NonexistentEntityException {
        this.newsList = this.newsJpaController.findNewsEntities();
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        LOG.info("Cancelled news form");
    }
}
