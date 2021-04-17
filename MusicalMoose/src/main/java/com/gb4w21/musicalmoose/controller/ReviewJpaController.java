/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Album;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Review;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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
public class ReviewJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(ReviewJpaController.class);
    private Review review;
    private boolean fromAlbum;
    private boolean trackCreated = false;
    private String starRating;
    @Resource
    private UserTransaction utx;
    @Inject
    ClientJpaController clientJpaController;
   // @Inject
   // LoginController loginController;
    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Review review) throws RollbackFailureException {
        try {
            utx.begin();

            Client clientid = review.getClientid();
            if (clientid != null) {
                clientid = em.getReference(clientid.getClass(), clientid.getClientid());
                review.setClientid(clientid);
            }
            em.persist(review);
            if (clientid != null) {
                clientid.getReviewList().add(review);
                clientid = em.merge(clientid);
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

    public void edit(Review review) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Review persistentReview = em.find(Review.class, review.getReviewid());
            Client clientidOld = persistentReview.getClientid();
            Client clientidNew = review.getClientid();
            if (clientidNew != null) {
                clientidNew = em.getReference(clientidNew.getClass(), clientidNew.getClientid());
                review.setClientid(clientidNew);
            }
            review = em.merge(review);
            if (clientidOld != null && !clientidOld.equals(clientidNew)) {
                clientidOld.getReviewList().remove(review);
                clientidOld = em.merge(clientidOld);
            }
            if (clientidNew != null && !clientidNew.equals(clientidOld)) {
                clientidNew.getReviewList().add(review);
                clientidNew = em.merge(clientidNew);
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
                Integer id = review.getReviewid();
                if (findReview(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Review review;
            try {
                review = em.getReference(Review.class, id);
                review.getReviewid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The review with id " + id + " no longer exists.", enfe);
            }
            Client clientid = review.getClientid();
            if (clientid != null) {
                clientid.getReviewList().remove(review);
                clientid = em.merge(clientid);
            }
            em.remove(review);
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

    public List<Review> findReviewEntities() {
        return findReviewEntities(true, -1, -1);
    }

    public List<Review> findReviewEntities(int maxResults, int firstResult) {
        return findReviewEntities(false, maxResults, firstResult);
    }

    private List<Review> findReviewEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Review.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Review findReview(Integer id) {

        return em.find(Review.class, id);

    }

    public String getStarRating() {
        return starRating;
    }

    public void setStarRating(String starRating) {
        this.starRating = starRating;
    }

    public int getReviewCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Review> rt = cq.from(Review.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    /**
     * Returns all reviews related to a track
     *
     * @param track
     * @return List of review objects
     */
    public List<Review> getTrackReviews(MusicTrack track) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Review> cq = cb.createQuery(Review.class);

        Root<Review> review = cq.from(Review.class);

        Join trackReview = review.join("inventoryid");

        //We want all review related to this track that has been approved by an admin. All approved reviews have the field set with "1"
        cq.where(cb.equal(trackReview.get("inventoryid"), track.getInventoryid()), cb.equal(review.get("aprovalstatus"), 1));

        Query q = em.createQuery(cq);

        return q.getResultList();
    }

    /**
     * Returns all the review objects for an album's tracks
     *
     * @param album
     * @return A list if Review objects
     */
    public List<Review> getAlbumTrackReviews(Album album) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Review> cq = cb.createQuery(Review.class);

        Root<Review> review = cq.from(Review.class);

        Join reviewTrack = review.join("inventoryid");

        //We only want the reviews whose tracks are part of the given album. Only want reviews approved by admin
        cq.where(cb.equal(reviewTrack.get("albumid").get("albumid"), album.getAlbumid()), cb.equal(review.get("aprovalstatus"), 1));

        Query q = em.createQuery(cq);

        return q.getResultList();

    }
    /**
     * takes the user to the review page so they could write the review for the specific track
     * @param track  MusicTrack
     * @param fromAlbum boolean
     * @return String review page
     */
    public String writeReview(MusicTrack track, boolean fromAlbum) {
        review = new Review();
        review.setInventoryid(track);
        trackCreated = false;
        this.fromAlbum = fromAlbum;
        return "reviewpage";
    }
    /**
     * Creates specified review and stores it to the database
     * @param id int
     * @return String search page
     * @throws RollbackFailureException 
     */
    public String postReview(int id) throws RollbackFailureException {
        review.setReviewdate(new Date());
        review.setRating(Integer.parseInt(this.starRating));
        LOG.info("Rating:" + review.getRating());
        LOG.info("Review:" + review.getReviewtext());
        review.setAprovalstatus(false);
        Client client = clientJpaController.findClient(id);
        LOG.info("Client id:"+client.getClientid());
        LOG.info("Client name:"+client.getUsername());
        review.setClientid(client);
        review.setClientname(client.getUsername());
        create(review);
        trackCreated = true;
        return "reviewpage";
    }
    /**
     * returns you to the track/album page where to created the review
     * @return track/album page
     */
    public String backToPage() {
        review = null;
        if (fromAlbum) {
            return "reviewAlbum";

        } else {
            return "reviewTrack";
        }
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public boolean getTrackCreated() {
        return trackCreated;
    }

 

}
