/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.business.PreRenderViewBean;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
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
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class MusicTrackJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MusicTrackJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    private MusicTrack searchedTrack;

    //The global variables used for the track management page
    private List<MusicTrack> tracks;
    private MusicTrack selectedTrack;
    private List<MusicTrack> selectedTracks;
    //When the cancel button is clicked, we want the selectedTrack to be the same as the trackCopy
    private MusicTrack trackCopy;
    private PreRenderViewBean preRenderViewBean = new PreRenderViewBean();

    public void create(MusicTrack musicTrack) throws RollbackFailureException {
        try {
            utx.begin();

            Album albumid = musicTrack.getAlbumid();
            if (albumid != null) {
                albumid = em.getReference(albumid.getClass(), albumid.getAlbumid());
                musicTrack.setAlbumid(albumid);
            }
            em.persist(musicTrack);
            if (albumid != null) {
                albumid.getMusicTrackList().add(musicTrack);
                albumid = em.merge(albumid);
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

    public void edit(MusicTrack musicTrack) throws NonexistentEntityException, Exception {
        try {
            utx.begin();

            MusicTrack persistentMusicTrack = em.find(MusicTrack.class, musicTrack.getInventoryid());
            Album albumidOld = persistentMusicTrack.getAlbumid();
            Album albumidNew = musicTrack.getAlbumid();
            if (albumidNew != null) {
                albumidNew = em.getReference(albumidNew.getClass(), albumidNew.getAlbumid());
                musicTrack.setAlbumid(albumidNew);
            }
            musicTrack = em.merge(musicTrack);
            if (albumidOld != null && !albumidOld.equals(albumidNew)) {
                albumidOld.getMusicTrackList().remove(musicTrack);
                albumidOld = em.merge(albumidOld);
            }
            if (albumidNew != null && !albumidNew.equals(albumidOld)) {
                albumidNew.getMusicTrackList().add(musicTrack);
                albumidNew = em.merge(albumidNew);
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
                Integer id = musicTrack.getInventoryid();
                if (findMusicTrack(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            MusicTrack musicTrack;
            try {
                musicTrack = em.getReference(MusicTrack.class, id);
                musicTrack.getInventoryid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The musicTrack with id " + id + " no longer exists.", enfe);
            }
            Album albumid = musicTrack.getAlbumid();
            if (albumid != null) {
                albumid.getMusicTrackList().remove(musicTrack);
                albumid = em.merge(albumid);
            }
            em.remove(musicTrack);
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

    public List<MusicTrack> findMusicTrackEntities() {
        return findMusicTrackEntities(true, -1, -1);
    }

    public List<MusicTrack> findMusicTrackEntities(int maxResults, int firstResult) {
        return findMusicTrackEntities(false, maxResults, firstResult);
    }

    private List<MusicTrack> findMusicTrackEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(MusicTrack.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public MusicTrack findMusicTrack(Integer id) {
        MusicTrack mt = em.find(MusicTrack.class, id);
        LOG.info(mt.getArtist());
        return em.find(MusicTrack.class, id);
    }

    public void searchForTracks(FacesContext context, UIComponent component,
            Object value) {

    }

    /**
     * Returns a list of the three most recently added MusicTrack objects
     *
     * @return List of MusicTrack objects
     */
    public List<MusicTrack> findMostRecentTracks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(MusicTrack.class);
        cq.orderBy(cb.asc(e.get("dateentered")));
        Query q = em.createQuery(cq);

        List<MusicTrack> originalList = q.getResultList();

        List<MusicTrack> threeRecentTracks = new ArrayList<>();
        if (originalList.size() > 0) {
            for (int i = 0; i < 3; i++) {
                threeRecentTracks.add(originalList.get(i));
            }
        }
        return threeRecentTracks;
    }

    public int getMusicTrackCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<MusicTrack> rt = cq.from(MusicTrack.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Finds all tracks that belong to a track's album
     *
     * @param track
     * @return tracks from same album
     */
    public List<MusicTrack> findAllRelatedTracks(MusicTrack track) {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

            Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);

            //Joining the MusicTrack table to the Album table
            Join albumsTracks = musicTrack.join("albumid");

            //We want all the tracks from this album that isn't the selected track
            cq.where(cb.equal(albumsTracks.get("albumid"), track.getAlbumid().getAlbumid()), cb.notEqual(musicTrack.get("tracktitle"), track.getTracktitle()));

            Query q = em.createQuery(cq);

            return q.getResultList();
        } catch (NullPointerException ex) {
            return new ArrayList<MusicTrack>();
        }
    }
    
    /**
     * Set the selected track and display the trackpage.xhtml
     *
     * @param track
     * @return display the trackpage.xhtml
     */
    public String searchTrack(MusicTrack track) {
        this.searchedTrack = track;
        LOG.info("" + track.getTracktitle());

        
  

        LOG.info("" + track.getTracktitle());

        return "detailTrack";
    }

    public String showTrackFromAlbum(MusicTrack track) {
        this.searchedTrack = track;
        return "detailTrackFromAlbum";
    }

    public String searchSingleTrack(int id) {
        this.searchedTrack = findMusicTrack(id);
        if (searchedTrack != null) {
            this.preRenderViewBean.writeGenreTrackingCookie(searchedTrack.getMusiccategory());
        }
        return "searchTrack";
    }

    public String selectSingleTrack(int id) {
        try {
            this.searchedTrack = findTrackById(id);
        } catch (NonexistentEntityException e) {
            return null;
        }
        if (searchedTrack != null) {
            this.preRenderViewBean.writeGenreTrackingCookie(searchedTrack.getMusiccategory());
        }
        return "detailTrack";
    }

    public MusicTrack findTrackById(int id) throws NonexistentEntityException {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);

        cq.select(musicTrack);

        cq.where(cb.equal(musicTrack.get("inventoryid"), id));

        Query q = em.createQuery(cq);
        if (q.getResultList().size() != 1) {
            throw new NonexistentEntityException("Cannot find MusicTrack with id");
        }

        return (MusicTrack) q.getResultList().get(0);
    }

    public String backToTrack(MusicTrack musicTrack) {
        this.searchedTrack = musicTrack;
        return "reviewTrack";
    }

    public List<MusicTrack> getSpecialTracks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);

        cq.where(cb.lessThan(musicTrack.get("saleprice"), musicTrack.get("listprice")),cb.equal(musicTrack.get("available"), 1));
        cq.orderBy(cb.desc(musicTrack.get("saleprice")));
        TypedQuery<MusicTrack> query = em.createQuery(cq);
        List<MusicTrack> tracks = query.getResultList();
        final int specialsLimt = 3;
        List<MusicTrack> specialList = new ArrayList<>();
        if (tracks.size() > specialsLimt) {
            for (int i = 0; i < specialsLimt; i++) {
                specialList.add(tracks.get(i));
            }

            return specialList;
        } else {
            return tracks;
        }
    }

    /**
     * Simple getter so the track page can access the selected track
     *
     * @return a track
     */
    public MusicTrack getMusicTrack() {
        return this.searchedTrack;
    }

    public void setMusicTrack(MusicTrack musicTrack) {
        this.searchedTrack = musicTrack;
    }

    /**
     * When a user clicks on a related track, set the selected track and show
     * the track page once again.
     *
     * @param track
     * @return display the trackpage.xhtml
     */
    public String searchRelatedTrack(MusicTrack track) {
        this.searchedTrack = track;
        return "relatedTrack";
    }

    /**
     * The data table should be filled with all the track entity objects from
     * the database.
     */
    @PostConstruct
    public void init() {
        this.tracks = findMusicTrackEntities();
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
    public void openNew() throws NonexistentEntityException {
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
            edit(track);
        }

        this.selectedTracks = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Products set to unavailable"));
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

        edit(this.selectedTrack);
        this.selectedTrack = null;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product set to unavailable"));
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
            //The new track was entered at the current date and time
            this.selectedTrack.setDateentered(new Date());
            create(this.selectedTrack);
            this.tracks.add(this.selectedTrack);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Created"));
            //A currently existing track that was edited.
        } else {
            edit(this.selectedTrack);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product Updated"));
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
        this.tracks = findMusicTrackEntities();
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
