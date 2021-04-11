/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class TrackSetSalesManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(TrackSetSalesManagerController.class);

    @Inject
    private MusicTrackJpaController trackController;

    //The global variables used for the track management page
    private List<MusicTrack> tracks;
    private MusicTrack selectedTrack;

    public TrackSetSalesManagerController() {

    }

    /**
     * The data table should be filled with all the track entity objects from
     * the database.
     */
    @PostConstruct
    public void init() {
        this.tracks = this.trackController.findMusicTrackEntities();
    }

    /**
     * @return the list of all tracks displayed in the data table
     */
    public List<MusicTrack> getTracks() {
        return this.tracks;
    }

    /**
     * @return the selected track that the user chose.
     */
    public MusicTrack getSelectedTrack() {
        return this.selectedTrack;
    }

    /**
     * When a track is clicked, it becomes the selected track
     *
     * @param musicTrack
     */
    public void setSelectedTrack(MusicTrack musicTrack) {
        this.selectedTrack = musicTrack;
    }

    public void saveProduct() throws Exception {
        this.trackController.edit(this.selectedTrack);
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "trackUpdated", null));

        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    public void cancelSalesEditForm() {
        this.tracks = this.trackController.findMusicTrackEntities();
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }
}
