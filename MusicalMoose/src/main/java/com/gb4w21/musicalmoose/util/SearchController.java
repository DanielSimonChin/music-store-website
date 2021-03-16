/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.SearchResult;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.BanneradJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

enum SearchCategory {
    Date,
    TrackName,
    AlbumTitle,
    Artist
}

/**
 *
 * @author owner
 */
@Named
@SessionScoped
public class SearchController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(BanneradJpaController.class);
    private String searchText;
    private List<SearchResult> searchResultsTrack = new ArrayList<SearchResult>();
    private List<SearchResult> searchResultsAlbum = new ArrayList<SearchResult>();
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    private String category;
    private String errorMessage;
    @PersistenceContext
    private EntityManager entityManager;

    public SearchController() {

    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public List<SearchResult> getSearchResultsTrack() {

        return searchResultsTrack;
    }

    public void setSearchResultsTrack(List<SearchResult> searchResultsTrack) {
        this.searchResultsTrack = searchResultsTrack;

    }

    public List<SearchResult> getSearchResultsAlbum() {

        return searchResultsAlbum;
    }

    public void setSearchResultsAlbum(List<SearchResult> searchResultsAlbum) {
        this.searchResultsAlbum = searchResultsAlbum;

    }

    public String searchForPage() throws Exception {
        searchResultsTrack = new ArrayList<SearchResult>();
        searchResultsAlbum = new ArrayList<SearchResult>();
        if (category.equals(SearchCategory.AlbumTitle.toString())) {
            searchResultsAlbums();
        } else if (category.equals(SearchCategory.Artist.toString())) {
            searchResultsArtist();
        } else if (category.equals(SearchCategory.Date.toString())) {
            if (valdiate()) {
                searchResultsDate();
            }
            else{
                return "index";
            }
        } else if (category.equals(SearchCategory.TrackName.toString())) {
            searchResultsMusicTrack();
        }
        searchText = "";
        
        if (searchResultsAlbum.size() + searchResultsTrack.size() == 0) {
            errorMessage = "your search had no results";
            return "index";
        }
        errorMessage ="";
        if (searchResultsTrack.size() == 0 && searchResultsAlbum.size() == 1) {
            setSingleAlbum();
            return "albumpage";
        } else if (searchResultsTrack.size() == 1 && searchResultsAlbum.size() == 0) {
            setSingleTrack();

            return "trackpage";
        }
        return "searchPage";

    }

    private boolean valdiate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date = formatter.parse(searchText);
            return true;
        } catch (ParseException ex) {
            errorMessage = "worng fromat date format must be in:\"dd-MM-yyyy\" ";
            return false;
        }
    }

    private void setSingleTrack() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        // Use String to refernce a field
        cq.where(cb.equal(musicTrack.get("tracktitle"), searchResultsTrack.get(0).getTracktitle()),
                cb.equal(musicTrack.get("artist"), searchResultsTrack.get(0).getArtist()),
                cb.equal(musicTrack.get("musiccategory"), searchResultsTrack.get(0).getMusiccategory()));

        TypedQuery<MusicTrack> query = entityManager.createQuery(cq);

        musicTrackJpaController.setMusicTrack(query.getSingleResult());

    }

    private void setSingleAlbum() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);
        // Use String to refernce a field
        cq.where(cb.equal(album.get("albumtitle"), searchResultsAlbum.get(0).getAlbumtitle()),
                cb.equal(album.get("artist"), searchResultsAlbum.get(0).getArtist()),
                cb.equal(album.get("releasedate"), searchResultsAlbum.get(0).getReleasedate()));

        TypedQuery<Album> query = entityManager.createQuery(cq);
        albumJpaController.setAlbum(query.getSingleResult());

    }

    private void searchResultsMusicTrack() {
        searchText = "%" + searchText + "%";

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("tracktitle"), searchText));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsTrack = query.getResultList();

    }

    private void searchResultsAlbums() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("albumtitle"), searchText));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

    }

    private void searchResultsDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = formatter.parse(searchText);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.greaterThan(album.get("dateentered"), date));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.greaterThan(musicTrack.get("dateentered"), date));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchResultsTrack.addAll(query.getResultList());

    }

    private void searchResultsArtist() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("artist"), searchText));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("artist"), searchText));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchResultsTrack.addAll(query.getResultList());

    }
}
