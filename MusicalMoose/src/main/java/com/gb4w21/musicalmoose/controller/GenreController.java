/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.SearchResult;
import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class GenreController implements Serializable {

    private final static String Pop = "Pop";
    private final static String Rock = "Rock";
    private final static String Anime = "Anime";
    private final static String Hip_hop = "Hip hop";
    private final static String RB = "R&B";
    private final static Logger LOG = LoggerFactory.getLogger(GenreController.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Inject
    SearchController searchController;

    public GenreController() {

    }

    public String searchPop() throws Exception {
      
        searchGenre(Pop);
   
        return "searchPage";
    }

    public String searchRB() throws Exception {
    
        searchGenre(RB);
      
        return "searchPage";
    }

    public String searchRock() throws Exception {
      
        searchGenre(Rock);
   
        return "searchPage";
    }

    public String searchHipHop() throws Exception {
  
        searchGenre(Hip_hop);
       
        return "searchPage";
    }

    public String searchAnime() throws Exception {
        
        searchGenre(Anime);
     
        return "searchPage";
    }

    private void searchGenre(String genre) throws Exception {
        searchController.setSearchResultsAlbum(new ArrayList<SearchResult>());
        searchController.setSearchResultsTrack(new ArrayList<SearchResult>());
        searchResultsGenre(genre);
        searchController.setSearchText("");
        searchController.setSearchError(false);

    }

    private void searchResultsGenre(String musicGenre) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("musiccategory"), musicGenre),cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"),  album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchController.setSearchResultsAlbum(query.getResultList());

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("musiccategory"), musicGenre), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchController.setSearchResultsTrack(query.getResultList());

    }
}
