/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Bannerad;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
public class BanneradJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(BanneradJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Bannerad bannerad) throws RollbackFailureException {
        try {
            utx.begin();

            em.persist(bannerad);

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

    public void edit(Bannerad bannerad) throws NonexistentEntityException, Exception {
        try {
            utx.begin();
            bannerad = em.merge(bannerad);
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = bannerad.getBanneraddid();
                if (findBannerad(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Bannerad bannerad;
            try {
                bannerad = em.getReference(Bannerad.class, id);
                bannerad.getBanneraddid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bannerad with id " + id + " no longer exists.", enfe);
            }
            em.remove(bannerad);
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

    public List<Bannerad> findBanneradEntities() {
        return findBanneradEntities(true, -1, -1);
    }

    public List<Bannerad> findBanneradEntities(int maxResults, int firstResult) {
        return findBanneradEntities(false, maxResults, firstResult);
    }

    private List<Bannerad> findBanneradEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Bannerad.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Bannerad findBannerad(Integer id) {

        return em.find(Bannerad.class, id);

    }

    public int getBanneradCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Bannerad> rt = cq.from(Bannerad.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    /**
     * Retrieves the Bannerad object given a position on the front page
     *
     * @author Daniel
     *
     * @param pagePosition
     * @return the banner ad at this position
     */
    public Bannerad getRunningAd(int pagePosition) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Bannerad> banneradRoot = cq.from(Bannerad.class);

        //we want the ad that was selected to be displayed and also has the first position
        cq.where(cb.equal(banneradRoot.get("displayed"), 1), cb.equal(banneradRoot.get("pageposition"), pagePosition));
        Query q = em.createQuery(cq);

        try {
            return (Bannerad) q.getSingleResult();
        } catch (javax.persistence.NoResultException NoResultException) {
            return null;
        }
    }

    /**
     * Redirect the user to a new window representing the ad's url field
     *
     * @Daniel
     *
     * @param ad
     * @throws IOException
     */
    public void redirectToWebsite(Bannerad ad) throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(ad.getUrl());
    }

}
