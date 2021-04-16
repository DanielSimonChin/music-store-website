/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.NewsJpaController;
import com.gb4w21.musicalmoose.entities.News;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author victo
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
     * The data table should be filled with all the ad entity objects from
     * the database.
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
    public boolean hasSelectedBannerAds() {
        return this.selectedNewsList != null && !this.selectedNewsList.isEmpty();
    }

//    /**
//     * Set the available table field for each selected bannerAd to false.
//     *
//     * @throws Exception
//     */
//    public void removeSelectedBannerAds() throws Exception {
//        for (News news : this.selectedNewsList) {
//            news.setDisplayed(Boolean.FALSE);
//            this.bannerAdJpaController.edit(bannerAd);
//        }
//
//        this.selectedBannerAds = null;
//        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
//                "com.gb4w21.musicalmoose.bundles.messages", "adSetNotDisplayed", null));
//        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
//        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
//    }
//
//    /**
//     * Set the selected ad's available field to false.
//     *
//     * @throws Exception
//     */
//    public void removeBannerAd() throws Exception {
//        this.selectedBannerAd.setDisplayed(Boolean.FALSE);
//        this.bannerAdJpaController.edit(this.selectedBannerAd);
//        this.selectedBannerAd = null;
//        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
//                "com.gb4w21.musicalmoose.bundles.messages", "adSetNotDisplayed", null));
//        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
//    }
//
//    /**
//     * Update the selected ad and display a message for the user.
//     *
//     * @throws Exception
//     */
//    public void saveBannerAd() throws Exception {
//        if (checkValidAd()) {
//            // If this is a new track
//            if (this.selectedBannerAd.getBanneraddid() == null) {
//                LOG.info("CREATING A NEW BANNER AD ENTITY");
//                this.bannerAdJpaController.create(this.selectedBannerAd);
//                this.bannerAds.add(this.selectedBannerAd);
//                FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
//                        "com.gb4w21.musicalmoose.bundles.messages", "adCreated", null));
//                //A currently existing ad that was edited.
//            } else {
//                LOG.info("EDITING AN AD");
//                this.bannerAdJpaController.edit(this.selectedBannerAd);
//                FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
//                        "com.gb4w21.musicalmoose.bundles.messages", "adUpdated", null));
//            }
//        }
//        else {
//            FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
//                    "com.gb4w21.musicalmoose.bundles.messages", "adInvalid", null));
//        }
//        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
//        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
//    }
//    
//    /**
//     * Checks if the ad created/update is valid with the other ads
//     * 
//     * @return boolean
//     */
//    private boolean checkValidAd() {
//        if (selectedBannerAd.getDisplayed() && selectedBannerAd.getPageposition() > 0)
//        for (int i = 0; i < bannerAds.size(); i++) {
//            if (bannerAds.get(i).getDisplayed() && bannerAds.get(i).getPageposition() == selectedBannerAd.getPageposition()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * When the cancel button is clicked for the management form, all changes
//     * made before the cancel button was clicked will not affect the datatable
//     * or the database. Reset the datatable values.
//     *
//     * @throws NonexistentEntityException
//     */
//    public void cancelBannerAdForm() throws NonexistentEntityException {
//        this.bannerAds = this.bannerAdJpaController.findBanneradEntities();
//        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
//        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
//    }
}
