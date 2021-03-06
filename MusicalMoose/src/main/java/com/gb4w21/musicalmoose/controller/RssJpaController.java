/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.entities.Rss;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
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
public class RssJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(RssJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Rss rss) throws RollbackFailureException {
        try {
            utx.begin();

            em.persist(rss);

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

    public void edit(Rss rss) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            rss = em.merge(rss);
            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = rss.getId();
                if (findRss(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Rss rss;
            try {
                rss = em.getReference(Rss.class, id);
                rss.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The rss with id " + id + " no longer exists.", enfe);
            }
            em.remove(rss);
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

    public List<Rss> findRssEntities() {
        return findRssEntities(true, -1, -1);
    }

    public List<Rss> findRssEntities(int maxResults, int firstResult) {
        return findRssEntities(false, maxResults, firstResult);
    }

    private List<Rss> findRssEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Rss.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Rss findRss(Integer id) {

        return em.find(Rss.class, id);

    }

    public int getRssCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Rss> rt = cq.from(Rss.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

}
