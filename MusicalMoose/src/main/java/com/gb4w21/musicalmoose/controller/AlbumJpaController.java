/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.servlet.http.Cookie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class AlbumJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(AlbumJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    private Album selectedAlbum;
//    private String recentGenre;

    public void create(Album album) throws RollbackFailureException {
        if (album.getMusicTrackList() == null) {
            album.setMusicTrackList(new ArrayList<MusicTrack>());
        }
        try {
            utx.begin();

            em.getTransaction().begin();
            List<MusicTrack> attachedMusicTrackList = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListMusicTrackToAttach : album.getMusicTrackList()) {
                musicTrackListMusicTrackToAttach = em.getReference(musicTrackListMusicTrackToAttach.getClass(), musicTrackListMusicTrackToAttach.getInventoryid());
                attachedMusicTrackList.add(musicTrackListMusicTrackToAttach);
            }
            album.setMusicTrackList(attachedMusicTrackList);
            em.persist(album);
            for (MusicTrack musicTrackListMusicTrack : album.getMusicTrackList()) {
                Album oldAlbumidOfMusicTrackListMusicTrack = musicTrackListMusicTrack.getAlbumid();
                musicTrackListMusicTrack.setAlbumid(album);
                musicTrackListMusicTrack = em.merge(musicTrackListMusicTrack);
                if (oldAlbumidOfMusicTrackListMusicTrack != null) {
                    oldAlbumidOfMusicTrackListMusicTrack.getMusicTrackList().remove(musicTrackListMusicTrack);
                    oldAlbumidOfMusicTrackListMusicTrack = em.merge(oldAlbumidOfMusicTrackListMusicTrack);
                }
            }
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
                LOG.error("Rollback");
            } catch (IllegalStateException | SecurityException | SystemException re) {
                LOG.error("Rollback2");

                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
        }
    }

    public void edit(Album album) throws NonexistentEntityException, Exception {
        try {

            utx.begin();
            Album persistentAlbum = em.find(Album.class, album.getAlbumid());
            List<MusicTrack> musicTrackListOld = persistentAlbum.getMusicTrackList();
            List<MusicTrack> musicTrackListNew = album.getMusicTrackList();
            List<MusicTrack> attachedMusicTrackListNew = new ArrayList<MusicTrack>();
            for (MusicTrack musicTrackListNewMusicTrackToAttach : musicTrackListNew) {
                musicTrackListNewMusicTrackToAttach = em.getReference(musicTrackListNewMusicTrackToAttach.getClass(), musicTrackListNewMusicTrackToAttach.getInventoryid());
                attachedMusicTrackListNew.add(musicTrackListNewMusicTrackToAttach);
            }
            musicTrackListNew = attachedMusicTrackListNew;
            album.setMusicTrackList(musicTrackListNew);
            album = em.merge(album);
            for (MusicTrack musicTrackListOldMusicTrack : musicTrackListOld) {
                if (!musicTrackListNew.contains(musicTrackListOldMusicTrack)) {
                    musicTrackListOldMusicTrack.setAlbumid(null);
                    musicTrackListOldMusicTrack = em.merge(musicTrackListOldMusicTrack);
                }
            }
            for (MusicTrack musicTrackListNewMusicTrack : musicTrackListNew) {
                if (!musicTrackListOld.contains(musicTrackListNewMusicTrack)) {
                    Album oldAlbumidOfMusicTrackListNewMusicTrack = musicTrackListNewMusicTrack.getAlbumid();
                    musicTrackListNewMusicTrack.setAlbumid(album);
                    musicTrackListNewMusicTrack = em.merge(musicTrackListNewMusicTrack);
                    if (oldAlbumidOfMusicTrackListNewMusicTrack != null && !oldAlbumidOfMusicTrackListNewMusicTrack.equals(album)) {
                        oldAlbumidOfMusicTrackListNewMusicTrack.getMusicTrackList().remove(musicTrackListNewMusicTrack);
                        oldAlbumidOfMusicTrackListNewMusicTrack = em.merge(oldAlbumidOfMusicTrackListNewMusicTrack);
                    }
                }
            }
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = album.getAlbumid();
                if (findAlbum(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Album album;
            try {
                album = em.getReference(Album.class, id);
                album.getAlbumid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The album with id " + id + " no longer exists.", enfe);
            }
            List<MusicTrack> musicTrackList = album.getMusicTrackList();
            for (MusicTrack musicTrackListMusicTrack : musicTrackList) {
                musicTrackListMusicTrack.setAlbumid(null);
                musicTrackListMusicTrack = em.merge(musicTrackListMusicTrack);
            }
            em.remove(album);
            utx.commit();
        } catch (NonexistentEntityException | IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        }
    }

    public List<Album> findAlbumEntities() {
        return findAlbumEntities(true, -1, -1);
    }

    public List<Album> findAlbumEntities(int maxResults, int firstResult) {
        return findAlbumEntities(false, maxResults, firstResult);
    }

    private List<Album> findAlbumEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Album.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Album findAlbum(Integer id) {

        return em.find(Album.class, id);

    }

    public int getAlbumCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Album> rt = cq.from(Album.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    public String searchSingleAlbum(int id) {
        this.selectedAlbum = findAlbum(id);
        writeCookie();
  
        return "searchAlbum";
    }
    
    public String selectSingleTrack(int id) {
        try {
            this.selectedAlbum = findAlbumById(id);
        }
        catch (NonexistentEntityException e) {
            return null;
        }
        writeCookie();
        return "detailAlbum";
    }
    
    private Album findAlbumById(int id) throws NonexistentEntityException {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> album = cq.from(Album.class);

        cq.select(album);
        
        cq.where(cb.equal(album.get("albumid"), id));

        Query q = em.createQuery(cq);
        if (q.getResultList().size() != 1) {
            throw new NonexistentEntityException("Cannot find Album with id");
        }

        return (Album) q.getResultList().get(0);
    }

    public String backToAlbum(Album album) {
        this.selectedAlbum = album;
        return "reviewAlbum";
    }

    /**
     * The track page will show 3 albums from other artists that are part of the
     * same category
     *
     * @param track
     * @return A list of Album objects
     */
    public List<Album> findRelatedAlbums(MusicTrack track) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> album = cq.from(Album.class);

        Join albumsTracks = album.join("musicTrackList");

        cq.where(cb.equal(albumsTracks.get("musiccategory"), track.getMusiccategory()), cb.notEqual(album.get("albumid"), track.getAlbumid().getAlbumid())).distinct(true);

        Query q = em.createQuery(cq);

        return q.getResultList().subList(0, 3);
    }

    /**
     * Overloaded method that finds all related albums given an input album
     *
     * @param album
     * @return all related albums in that music category
     */
    public List<Album> findRelatedAlbums(Album album) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> albums = cq.from(Album.class);

        Join albumsTracks = albums.join("musicTrackList");

        cq.where(cb.equal(albumsTracks.get("musiccategory"), album.getMusicTrackList().get(0).getMusiccategory()), cb.notEqual(albums.get("albumid"), album.getAlbumid()),
                cb.notEqual(albums.get("artist"), album.getArtist())).distinct(true);

        Query q = em.createQuery(cq);

        return q.getResultList().subList(0, 3);
    }

    public List<Album> getRecentGenreAlbums() {
        String recentGenre = findRecentGenreCookie();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Album> cq = cb.createQuery(Album.class);

        Root<Album> albums = cq.from(Album.class);

        Join albumsTracks = albums.join("musicTrackList");

        cq.where(cb.equal(albumsTracks.get("musiccategory"), recentGenre)).distinct(true);

        Query q = em.createQuery(cq);

        return q.getResultList();
    }

    private String findRecentGenreCookie() {
        FacesContext context = FacesContext.getCurrentInstance();

        // Retrieve a GenreTracking cookie
        Object genreTrackingCookie = context.getExternalContext().getRequestCookieMap().get("GenreTracking");
        if (genreTrackingCookie == null || ((Cookie) genreTrackingCookie).getValue().isEmpty()) {
            return null;
        }
        return ((Cookie) genreTrackingCookie).getValue();
    }

    /**
     * Set the selected album into the private variable and return the
     * navigation rule to display the album page.
     *
     * @param album
     * @return the view for the album page.
     */
    public String selectAlbum(Album album) {
        this.selectedAlbum = album;
        LOG.info("" + album.getAlbumtitle());
        
        writeCookie();
        return "detailAlbum";
    }

    public String toAlbum(Album album) {
        this.selectedAlbum = album;
        LOG.info("" + album.getAlbumtitle());

        writeCookie();
        return "albumpage";
    }

    private void writeCookie() {

        if (selectedAlbum != null) {
            List<MusicTrack> musicTracks = selectedAlbum.getMusicTrackList();
            if (musicTracks.size() > 0 && musicTracks.get(0).getMusiccategory() != null) {

//            recentGenre = musicTracks.get(0).getMusiccategory();
                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().addResponseCookie("GenreTracking", musicTracks.get(0).getMusiccategory(), null);
            }
        }
    }

    /**
     * @return The selected album to be displayed in the album page.
     */
    public Album getSelectedAlbum() {
        return this.selectedAlbum;
    }

    /**
     * @param album The selected album to be displayed in the album page.
     */
    public void setSelectedAlbum(Album album) {
        this.selectedAlbum = album;
    }

    public String showRelatedAlbum(Album album) {
        this.selectedAlbum = album;
        return "relatedAlbumFromAlbum";
    }

    public List<Album> getSpecialAlbums() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Album> cq = cb.createQuery(Album.class);
        Root<Album> album = cq.from(Album.class);
        cq.select(album);

        cq.where(cb.lessThan(album.get("saleprice"), album.get("listprice")));
        cq.orderBy(cb.desc(album.get("saleprice")));
        TypedQuery<Album> query = em.createQuery(cq);
        List<Album> albums = query.getResultList();
        final int specialsLimt = 3;
        List<Album> specialList = new ArrayList<>();
        if (albums.size()>specialsLimt) {
            for (int i = 0; i < specialsLimt; i++) {
                specialList.add(albums.get(i));
            }
            return specialList;
        }
        else{
            return albums;
        }
    }

    /**
     * Retrieves an album's tracks in a list of MusicTrack objects
     *
     * @param albumId
     * @return all the tracks in the album
     */
    public List<MusicTrack> getAlbumTracks(int albumId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> track = cq.from(MusicTrack.class);

        //Joining the Album table to the MusicTrack table
        Join albumTracks = track.join("albumid");

        //We want all the tracks from this album that isn't the selected track
        cq.where(cb.equal(albumTracks.get("albumid"), albumId));

        Query q = em.createQuery(cq);

        return q.getResultList();
    }

    public boolean hasGenreCookie() {
        FacesContext context = FacesContext.getCurrentInstance();
        Object genreCookie = context.getExternalContext().getRequestCookieMap().get("GenreTracking");

        if (genreCookie == null) {
            return false;
        } else {
            return true;
        }
    }
}
