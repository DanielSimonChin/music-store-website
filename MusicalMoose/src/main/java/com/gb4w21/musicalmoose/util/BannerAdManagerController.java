package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Bannerad;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author victo
 */
@Named
@SessionScoped
public class BannerAdManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(BannerAdManagerController.class);
    
    @Inject
    private BanneradJpaController bannerAdJpaController;
    
    private List<Bannerad> bannerAds;
    private List<Bannerad> selectedBannerAds;
    private Bannerad selectedBannerAd;
    
    
    /**
     * The data table should be filled with all the ad entity objects from
     * the database.
     */
    @PostConstruct
    public void init() {
        this.bannerAds = bannerAdJpaController.findBanneradEntities();
    }
    
    public List<Bannerad> getBannerAds() {
        return this.bannerAds;
    }
    
    public void setBannerAds(List<Bannerad> bannerAds) {
        this.bannerAds = bannerAds;
    }
    
    public List<Bannerad> getSelectedBannerAds() {
        return this.selectedBannerAds;
    }
    
    public void setSelectedBannerAds(List<Bannerad> selectedBannerAds) {
        this.selectedBannerAds = selectedBannerAds;
    }
    
    public Bannerad getSelectedBannerAd() {
        return this.selectedBannerAd;
    }
    
    public void setSelectedBannerAd(Bannerad selectedBannerAd) {
        this.selectedBannerAd = selectedBannerAd;
    }
    
    public void openNew() {
        this.selectedBannerAd = new Bannerad();
    }
    
    /**
     * Check if the selected bannerAds is empty
     *
     * @return true if it is not empty
     */
    public boolean hasSelectedBannerAds() {
        return this.selectedBannerAds != null && !this.selectedBannerAds.isEmpty();
    }
    
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
    public void removeSelectedBannerAds() throws Exception {
        for (Bannerad bannerAd : this.selectedBannerAds) {
            bannerAd.setDisplayed(Boolean.FALSE);
            this.bannerAdJpaController.edit(bannerAd);
        }

        this.selectedBannerAds = null;
        FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "adSetNotDisplayed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
    }

    /**
     * Set the selected ad's available field to false.
     *
     * @throws Exception
     */
    public void removeBannerAd() throws Exception {
        this.selectedBannerAd.setDisplayed(Boolean.FALSE);
        this.bannerAdJpaController.edit(this.selectedBannerAd);
        this.selectedBannerAd = null;
        FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "adSetNotDisplayed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * Update the selected ad and display a message for the user.
     *
     * @throws Exception
     */
    public void saveBannerAd() throws Exception {
        if (checkValidAd()) {
            // If this is a new track
            if (this.selectedBannerAd.getBanneraddid() == null) {
                LOG.info("CREATING A NEW BANNER AD ENTITY");
                this.bannerAdJpaController.create(this.selectedBannerAd);
                this.bannerAds.add(this.selectedBannerAd);
                FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "adCreated"));
                //A currently existing ad that was edited.
            } else {
                LOG.info("EDITING AN AD");
                this.bannerAdJpaController.edit(this.selectedBannerAd);
                FacesContext.getCurrentInstance().addMessage(null, createMsg("confirmation", "adUpdated"));
            }
        }
        else {
            selectedBannerAd.setDisplayed(Boolean.FALSE);
            FacesContext.getCurrentInstance().addMessage(null, createMsg("invalid", "adInvalid"));
        }
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
    
    /**
     * Checks if the ad created/update is valid with the other ads
     * 
     * @return boolean
     */
    private boolean checkValidAd() {
        if (selectedBannerAd.getDisplayed())
            for (int i = 0; i < bannerAds.size(); i++) {
                if (bannerAds.get(i).getBanneraddid() != selectedBannerAd.getBanneraddid()) {
                    if (bannerAds.get(i).getDisplayed() && bannerAds.get(i).getPageposition() == selectedBannerAd.getPageposition()) {
                        return false;
                    }
                }
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
    public void cancelBannerAdForm() throws NonexistentEntityException {
        this.bannerAds = this.bannerAdJpaController.findBanneradEntities();
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
}
