/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller.management;

import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller methods and variables that allow an admin to set the sale prices
 * of an album
 *
 * @author Daniel
 */
@Named
@SessionScoped
public class AlbumSetSalesManagerController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumSetSalesManagerController.class);

    @Inject
    private AlbumJpaController albumController;

    //The global variables used for the track management page
    private List<Album> albums;
    private Album selectedAlbum;

    public AlbumSetSalesManagerController() {
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
        init();
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
     * Make changes to the db for this track
     *
     * @throws Exception
     */
    public void saveProduct() throws Exception {
        this.albumController.edit(this.selectedAlbum);
        FacesContext.getCurrentInstance().addMessage(null, com.gb4w21.musicalmoose.util.Messages.getMessage(
                "com.gb4w21.musicalmoose.bundles.messages", "albumUpdated", null));

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
     * Ensure that the sale price is less than the list price.
     *
     * @param context
     * @param component
     * @param value
     */
    public void validateSaleInput(FacesContext context, UIComponent component,
            Object value) {
        Double price = Double.valueOf(value.toString());

        if (price >= this.selectedAlbum.getListprice()) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "saleInputError", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }
}
