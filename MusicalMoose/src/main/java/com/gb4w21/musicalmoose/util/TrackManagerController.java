/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.util.Date;
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
 * Controller methods for the adminmusic.xhtml page which allows us to search,
 * create and modify tracks.
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class TrackManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(TrackManagerController.class);

    @Inject
    private MusicTrackJpaController trackController;
    //The global variables used for the track management page
    private List<MusicTrack> tracks;
    private MusicTrack selectedTrack;
    private List<MusicTrack> selectedTracks;

    public TrackManagerController() {

    }

    /**
     * If a track's partofalbum field is set to false, then the albumid field
     * should be null.
     */
    public void setAlbumidToNull() {
        if (!this.selectedTrack.getPartofalbum()) {
            this.selectedTrack.setAlbumid(null);
        }
    }

    /**
     * The data table should be filled with all the track entity objects from
     * the database.
     */
    @PostConstruct
    public void init() {
        this.tracks = trackController.findMusicTrackEntities();
    }

    /**
     * @return the list of all tracks displayed in the data table
     */
    public List<MusicTrack> getTracks() {
        //init();
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

    /**
     * @return the list of all selected tracks
     */
    public List<MusicTrack> getSelectedTracks() {
        return this.selectedTracks;
    }

    /**
     * Set the list of selected tracks
     *
     * @param selectedTracks
     */
    public void setSelectedTracks(List<MusicTrack> selectedTracks) {
        this.selectedTracks = selectedTracks;
    }

    public MusicTrack getTrack(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("no id provided");
        }
        for (MusicTrack m : tracks) {
            if (id.equals(m.getInventoryid())) {
                return m;
            }
        }
        return null;
    }

    /**
     * When a user clicks on the create button, the selected track initializes a
     * new object
     */
    public void openNew() {
        this.selectedTrack = new MusicTrack();
    }

    /**
     * Check if the selected tracks is empty.
     *
     * @return true if it is not empty.
     */
    public boolean hasSelectedTracks() {
        return this.selectedTracks != null && !this.selectedTracks.isEmpty();
    }

    /**
     * Set the available table field for each selected track to false.
     *
     * @throws Exception
     */
    public void removeSelectedTracks() throws Exception {
        for (MusicTrack track : this.selectedTracks) {
            track.setAvailable(Boolean.FALSE);
            track.setRemovaldate(new Date());
            trackController.edit(track);
        }

        this.selectedTracks = null;
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "productSetUnavailable", null));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
    }

    /**
     * Set the selected track's available field to false.
     *
     * @throws Exception
     */
    public void removeTrack() throws Exception {
        this.selectedTrack.setAvailable(Boolean.FALSE);
        setRemovalDate();
        trackController.edit(this.selectedTrack);
        this.selectedTrack = null;
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "productSetUnavailable", null));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * Update the selected track and display a message for the user.
     *
     * @throws Exception
     */
    public void saveProduct() throws Exception {
        //If this is a new track
        if (this.selectedTrack.getInventoryid() == null) {
            //The default sale price will be 0 and can be changed in the set sales tab for tracks
            this.selectedTrack.setSaleprice(0.0f);
            //The new track was entered at the current date and time
            this.selectedTrack.setDateentered(new Date());
            trackController.create(this.selectedTrack);
            this.tracks.add(this.selectedTrack);
            FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "trackCreated", null));
            //A currently existing track that was edited.
        } else {
            setRemovalDate();
            trackController.edit(this.selectedTrack);
            FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "trackUpdated", null));
        }

        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * When the cancel button is clicked for the management form, all changes
     * made before the cancel button was clicked will not affect the datatable
     * or the database. Reset the datatable values.
     *
     * @throws NonexistentEntityException
     */
    public void cancelTrackForm() throws NonexistentEntityException {
        this.tracks = trackController.findMusicTrackEntities();
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * If the admin makes a song unavailable to clients, then set the removal
     * date to the current day
     */
    public void setRemovalDate() {
        if (this.selectedTrack.getAvailable()) {
            this.selectedTrack.setRemovaldate(null);
            return;
        }
        this.selectedTrack.setRemovaldate(new Date());
    }

}
