/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.util.Date;
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
 * Controller class with methods that allow us to search, view and modify
 * albums. For the admin album management.
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class AlbumManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumManagerController.class);

    @Inject
    private AlbumJpaController albumController;
    @Inject
    private MusicTrackJpaController trackController;

    private List<Album> albums;
    private Album selectedAlbum;
    private List<Album> selectedAlbums;

    public AlbumManagerController() {

    }

    /**
     * The data table should be filled with all the album entity objects from
     * the database.
     */
    @PostConstruct
    public void init() {
        this.albums = albumController.findAlbumEntities();
    }

    /**
     * @return the list of all tracks displayed in the data table
     */
    public List<Album> getAlbums() {
        return this.albums;
    }

    /**
     * @return the selected album that the user chose.
     */
    public Album getSelectedAlbum() {
        return this.selectedAlbum;
    }

    /**
     * When a album is clicked, it becomes the selected album
     *
     * @param musicTrack
     */
    public void setSelectedAlbum(Album album) {
        this.selectedAlbum = album;
    }

    /**
     * @return the list of all selected albums
     */
    public List<Album> getSelectedAlbums() {
        return this.selectedAlbums;
    }

    /**
     * Set the list of selected albums
     *
     * @param selectedTracks
     */
    public void setSelectedAlbums(List<Album> selectedAlbums) {
        this.selectedAlbums = selectedAlbums;
    }

    /**
     * The new button creates a new album object
     */
    public void openNew() {
        this.selectedAlbum = new Album();
    }

    /**
     * Check if the selected albums is empty
     *
     * @return true if it is not empty
     */
    public boolean hasSelectedAlbums() {
        return this.selectedAlbums != null && !this.selectedAlbums.isEmpty();
    }

    /**
     * Set the available table field for each selected album to false.
     *
     * @throws Exception
     */
    public void removeSelectedAlbums() throws Exception {
        for (Album album : this.selectedAlbums) {
            album.setAvailable(Boolean.FALSE);
            album.setRemovaldate(new Date());
            this.albumController.edit(album);
        }

        this.selectedAlbums = null;
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "productSetUnavailable", null));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
    }

    /**
     * Set the selected album's available field to false.
     *
     * @throws Exception
     */
    public void removeAlbum() throws Exception {
        this.selectedAlbum.setAvailable(Boolean.FALSE);
        setRemovalDate();
        this.albumController.edit(this.selectedAlbum);
        this.selectedAlbum = null;
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "productSetUnavailable", null));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * Update the selected album and display a message for the user.
     *
     * @throws Exception
     */
    public void saveProduct() throws Exception {
        
        //If this is a new track
        if (this.selectedAlbum.getAlbumid() == null) {
            LOG.info("CREATING A NEW ALBUM ENTITY");
            //this.selectedAlbum.setAlbumid(this.albumController.getAlbumCount() + 1);
            //The new track was entered at the current date and time
            this.selectedAlbum.setDateentered(new Date());
            this.selectedAlbum.setReleasedate(new Date());
            this.albumController.create(this.selectedAlbum);
            this.albums.add(this.selectedAlbum);
            FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "albumCreated", null));
            //A currently existing track that was edited.
        } else {
            LOG.info("EDITING AN ALBUM");
            setRemovalDate();
            this.albumController.edit(this.selectedAlbum);
            FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "albumUpdated", null));
        }
        
        for (MusicTrack track : this.trackController.findMusicTrackEntities()) {
            if (track.getAlbumid() == null) {
                track.setPartofalbum(false);
                this.trackController.edit(track);
            } else {
                track.setPartofalbum(true);
                this.trackController.edit(track);
            }
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
    public void cancelAlbumForm() throws NonexistentEntityException {
        this.albums = this.albumController.findAlbumEntities();
        PrimeFaces.current().executeScript("PF('manageProductDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
    }

    /**
     * If the admin makes an album unavailable to clients, then set the removal
     * date to the current day
     */
    public void setRemovalDate() {
        if (this.selectedAlbum.getAvailable()) {
            this.selectedAlbum.setRemovaldate(null);
            return;
        }
        this.selectedAlbum.setRemovaldate(new Date());

    }

}
