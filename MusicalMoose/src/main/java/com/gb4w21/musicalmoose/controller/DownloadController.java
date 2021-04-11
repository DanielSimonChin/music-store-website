/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.MusicItem;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.Sale;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
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
public class DownloadController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(DownloadController.class);
    
    @Inject
    private InvoicedetailJpaController invoiceDetailJpaController;
    @Inject
    private LoginController loginController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private ShoppingCartController shoppingCartController;
    @Inject
    private SaleJpaController saleJpaController;
    
    private List<MusicItem> downloadMusicItems;
    
    public List<MusicItem> findDownloads() throws NonexistentEntityException {
        try {
            this.downloadMusicItems = new ArrayList<MusicItem>();

            List<Sale> saleList = saleJpaController.findSaleByClientId(loginController.getLoginBean().getId());


            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().addResponseCookie("A1", saleList.size() + "", null);
            
            List<Invoicedetail> invoiceDetails = new ArrayList<Invoicedetail>();
            for (int i = 0; i < saleList.size(); i++) {
                invoiceDetails.addAll(invoiceDetailJpaController.findInvoiceDetailsBySaleId(saleList.get(i).getSaleid()));
            }

            for (int i = 0; i < invoiceDetails.size(); i++) {
                if (invoiceDetails.get(i).getInventoryid() != null) {
                    downloadMusicItems.add(shoppingCartController.convertMusicTrackToMusicItem(musicTrackJpaController.findMusicTrack(invoiceDetails.get(i).getInventoryid().getInventoryid())));
                }
                else if (invoiceDetails.get(i).getAlbumid() != null) {
                    downloadMusicItems.add(shoppingCartController.convertAlbumToMusicItem(albumJpaController.findAlbum(invoiceDetails.get(i).getAlbumid().getAlbumid())));
                }
            }
            return downloadMusicItems;
////            List<Sale> saleList = saleJpaController.findSaleByClientId(clientId);
        
            
            
        }
        catch (NonexistentEntityException e) {
            // return nothing if Client does not have any downloadable tracks/albums
            return null;
        }
    }
}