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
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
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
public class MusicTrackJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MusicTrackJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    private MusicTrack searchedTrack;

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
     * @author Daniel
     *
     * @return List of MusicTrack objects
     */
    public List<MusicTrack> findMostRecentTracks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery cq = cb.createQuery();
        Root e = cq.from(MusicTrack.class);
        cq.orderBy(cb.desc(e.get("dateentered"))).where(cb.equal(e.get("available"), 1));

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
     * @author Daniel
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
            cq.where(cb.equal(albumsTracks.get("albumid"), track.getAlbumid().getAlbumid()), cb.notEqual(musicTrack.get("tracktitle"), track.getTracktitle()), cb.equal(musicTrack.get("available"), 1));

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

        if (searchedTrack != null) {
            this.preRenderViewBean.writeGenreTrackingCookie(searchedTrack.getMusiccategory());
        }

        return "detailTrack";
    }

    public String showTrackFromAlbum(MusicTrack track) {
        this.searchedTrack = track;
        return "detailTrackFromAlbum";
    }

    /**
     * Store given track by id to selectedAlbum
     *
     * @author Victor
     *
     * @param id
     * @return string to navigate to track page
     *
     */
    public String searchSingleTrack(int id) {
        this.searchedTrack = findMusicTrack(id);
        if (searchedTrack != null) {
            this.preRenderViewBean.writeGenreTrackingCookie(searchedTrack.getMusiccategory());
        }
        return "searchTrack";
    }

    /**
     * Takes the id of a track a takes the user to that track page from the
     * index page
     *
     * @param id int
     * @return track page
     */
    public String selectSingleTrack(int id) {
        this.searchedTrack = findMusicTrack(id);

        if (searchedTrack != null) {
            this.preRenderViewBean.writeGenreTrackingCookie(searchedTrack.getMusiccategory());
        }
        return "detailTrack";
    }

    /**
     * Finds an track given the id
     *
     * @author Victor
     *
     * @param id
     * @return track
     * @throws NonexistentEntityException
     */
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

    /**
     * takes a user back to the track page after writing a review
     *
     * @param musicTrack MusicTrack
     * @return track page
     */
    public String backToTrack(MusicTrack musicTrack) {
        this.searchedTrack = musicTrack;
        return "reviewTrack";
    }

    /**
     * get a list of tracks that have the biggest sale
     *
     * @author Alessandro Dare
     * @return List<MusicTrack> a list of special tracks
     */
    public List<MusicTrack> getSpecialTracks() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);
        Root<MusicTrack> musicTrack = cq.from(MusicTrack.class);
        cq.select(musicTrack);

        cq.where(cb.notEqual(musicTrack.get("saleprice"), 0), cb.lessThan(musicTrack.get("saleprice"), musicTrack.get("listprice")), cb.equal(musicTrack.get("available"), 1));
        cq.orderBy(cb.asc(musicTrack.get("saleprice")));
        TypedQuery<MusicTrack> query = em.createQuery(cq);
        List<MusicTrack> tracks = query.getResultList();
        final int specialsLimt = 3;
        List<MusicTrack> specialList = new ArrayList<>();
        //Adds to three tracks to special list
        if (tracks.size() > specialsLimt) {
            for (int i = 0; i < specialsLimt; i++) {
                LOG.info("Special ablum:" + tracks.get(i).getTracktitle());
                LOG.info("Special cost" + tracks.get(i).getSaleprice());
                specialList.add(tracks.get(i));
            }

            return specialList;
        } else {
            return tracks;
        }
    }

    /**
     * Gets the list of tracks of the most recently searched genre
     *
     * @author Victor
     *
     * @return list of tracks of specific genre
     */
    public List<MusicTrack> getRecentGenreTracks() {
        String recentGenre = this.preRenderViewBean.findRecentGenreCookie();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MusicTrack> cq = cb.createQuery(MusicTrack.class);

        Root<MusicTrack> musicTracks = cq.from(MusicTrack.class);

        cq.where(cb.equal(musicTracks.get("musiccategory"), recentGenre));

        Query q = em.createQuery(cq);

        List<MusicTrack> results = q.getResultList();
        List<MusicTrack> genreRelatedResults = new ArrayList<MusicTrack>();

        for (int i = 0; i < results.size(); i++) {
            if (i == 0) {
                genreRelatedResults.add(results.get(i));
            } else if (!results.get(i).getArtist().equals(genreRelatedResults.get(0).getArtist())) {
                genreRelatedResults.add(results.get(i));
                break;
            }
        }
        return genreRelatedResults;
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
     * @author Alessandro Dare
     * @param track
     * @return display the trackpage.xhtml
     */
    public String searchRelatedTrack(MusicTrack track) {
        this.searchedTrack = track;
        return "relatedTrack";
    }
}
