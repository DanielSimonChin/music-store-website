/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.converters;

import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Class with converters for selectOneMenu tags
 *
 * @author Daniel
 */
@FacesConverter(value = "albumConverter")
public class AlbumConverter implements Converter {

    /**
     * Returns the Album object given its albumId. Used in the selectOneMenu for
     * an admin to select a track's album.
     *
     * @param ctx
     * @param uiComponent
     * @param albumId
     * @return
     */
    @Override
    public Object getAsObject(FacesContext ctx, UIComponent uiComponent, String albumId) {
        ValueExpression vex
                = ctx.getApplication().getExpressionFactory()
                        .createValueExpression(ctx.getELContext(),
                                "#{albumJpaController}", AlbumJpaController.class);

        AlbumJpaController albums = (AlbumJpaController) vex.getValue(ctx.getELContext());
        try {
            return albums.findAlbumById(Integer.valueOf(albumId));
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(AlbumConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param album
     * @return the string value of the object's albumid
     */
    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object album) {
        return ((Album) album).getAlbumid().toString();
    }

}
