/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

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
import java.util.regex.Matcher;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
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
    private Date toDate;
    private Date fromDate;
    private List<SearchResult> searchResultsTrack = new ArrayList<SearchResult>();
    private List<SearchResult> searchResultsAlbum = new ArrayList<SearchResult>();
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    private String category = "TrackName";
    private boolean searchError;
    @PersistenceContext
    private EntityManager entityManager;
    // @Inject
    // private FacesContext facesContext1;

    public SearchController() {

    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public boolean getSearchError() {
        return searchError;
    }

    public void setSearchError(boolean searchError) {
        this.searchError = searchError;
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

    private void clearSearchResult() {
        searchResultsTrack = new ArrayList<SearchResult>();
        searchResultsAlbum = new ArrayList<SearchResult>();
    }

    public String searchForPage() throws Exception {
        clearSearchResult();
        LOG.info("category" + category);
        LOG.info("category" + category);
        LOG.info("category" + category);
        LOG.info("category" + category);

        if (category.equals(SearchCategory.AlbumTitle.toString())) {
            searchResultsAlbums();
        } else if (category.equals(SearchCategory.Artist.toString())) {
            searchResultsArtist();
        } else if (category.equals(SearchCategory.Date.toString())) {

            searchResultsDate();

        } else if (category.equals(SearchCategory.TrackName.toString())) {
            searchResultsMusicTrack();
        }
        searchText = "";

        fromDate = null;
        toDate = null;
        if (searchResultsAlbum.size() + searchResultsTrack.size() == 0) {
            searchText = "";
            searchError = true;
            return "index";
        }
        searchError = false;
        if (searchResultsTrack.size() == 0 && searchResultsAlbum.size() == 1) {
            setSingleAlbum();
            return "albumpage";
        } else if (searchResultsTrack.size() == 1 && searchResultsAlbum.size() == 0) {
            setSingleTrack();

            return "trackpage";
        }
        return "searchPage";

    }

    private void setSingleTrack() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);
        // Use String to refernce a field
        cq.where(cb.equal(musicTrack.get("tracktitle"), searchResultsTrack.get(0).getTracktitle()),
                cb.equal(musicTrack.get("artist"), searchResultsTrack.get(0).getArtist()),
                cb.equal(musicTrack.get("musiccategory"), searchResultsTrack.get(0).getMusiccategory()), 
                cb.equal(musicTrack.get("available"), 1));

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
                cb.equal(album.get("releasedate"), searchResultsAlbum.get(0).getReleasedate()),
                cb.equal(album.get("available"), 1));

        TypedQuery<Album> query = entityManager.createQuery(cq);
        albumJpaController.setSelectedAlbum(query.getSingleResult());

    }

    private void searchResultsMusicTrack() {
        searchText = "%" + searchText + "%";

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("tracktitle"), searchText), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsTrack = query.getResultList();

    }

    public void validateDateFrom(FacesContext context, UIComponent component,Object value) {
        if (value != null && compareDate((Date) value)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "dateErrorFrom", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    public void validateDateTo(FacesContext context, UIComponent component, Object value) {
        if (value != null && compareDate((Date) value)) {
            FacesMessage message = com.gb4w21.musicalmoose.util.Messages.getMessage(
                    "com.gb4w21.musicalmoose.bundles.messages", "dateErrorTo", null);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);

            throw new ValidatorException(message);
        }
    }

    private boolean compareDate(Date chosenDate) {
        Date currentDate = new Date();
        LOG.info("Date:" + chosenDate.toString());
        return chosenDate.compareTo(currentDate) > 0;
    }

  
    private void searchResultsAlbums() {
        searchText = "%" + searchText + "%";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);
        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.like(album.get("albumtitle"), searchText),cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

    }

    private void searchResultsDate() throws ParseException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SearchResult> cq = cb.createQuery(SearchResult.class);

        Root<Album> album = cq.from(Album.class);
        Join musicTrack = album.join("musicTrackList");
        cq.where(cb.between(album.get("dateentered"), fromDate, toDate), cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.between(album.get("dateentered"), fromDate, toDate), cb.equal(musicTrack.get("available"), 1));
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
        cq.where(cb.like(album.get("artist"), searchText),cb.equal(album.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, album.get("albumtitle"), album.get("releasedate"), album.get("artist"), musicTrack.get("musiccategory"), album.get("albumimagefilenamesmall"), album.get("albumid"))).distinct(true);
        TypedQuery<SearchResult> query = entityManager.createQuery(cq);
        searchResultsAlbum = query.getResultList();

        album = cq.from(Album.class);
        musicTrack = album.join("musicTrackList");
        cq.where(cb.like(musicTrack.get("artist"), searchText), cb.equal(musicTrack.get("available"), 1));
        cq.select(cb.construct(SearchResult.class, musicTrack.get("tracktitle"), musicTrack.get("musiccategory"), musicTrack.get("artist"), album.get("releasedate"), album.get("albumimagefilenamesmall"), musicTrack.get("inventoryid"))).distinct(true);
        query = entityManager.createQuery(cq);
        searchResultsTrack.addAll(query.getResultList());

    }

}
