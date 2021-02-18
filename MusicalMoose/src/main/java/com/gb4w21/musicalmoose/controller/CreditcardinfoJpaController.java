/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Client;
import com.gb4w21.musicalmoose.entities.Creditcardinfo;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
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
public class CreditcardinfoJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(CreditcardinfoJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Creditcardinfo creditcardinfo) throws RollbackFailureException {
        try {
            utx.begin();
            em.getTransaction().begin();
            Client clientid = creditcardinfo.getClientid();
            if (clientid != null) {
                clientid = em.getReference(clientid.getClass(), clientid.getClientid());
                creditcardinfo.setClientid(clientid);
            }
            em.persist(creditcardinfo);
            if (clientid != null) {
                clientid.getCreditcardinfoList().add(creditcardinfo);
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

    public void edit(Creditcardinfo creditcardinfo) throws NonexistentEntityException, Exception {

        try {
            utx.begin();
            Creditcardinfo persistentCreditcardinfo = em.find(Creditcardinfo.class, creditcardinfo.getCreditcardid());
            Client clientidOld = persistentCreditcardinfo.getClientid();
            Client clientidNew = creditcardinfo.getClientid();
            if (clientidNew != null) {
                clientidNew = em.getReference(clientidNew.getClass(), clientidNew.getClientid());
                creditcardinfo.setClientid(clientidNew);
            }
            creditcardinfo = em.merge(creditcardinfo);
            if (clientidOld != null && !clientidOld.equals(clientidNew)) {
                clientidOld.getCreditcardinfoList().remove(creditcardinfo);
                clientidOld = em.merge(clientidOld);
            }
            if (clientidNew != null && !clientidNew.equals(clientidOld)) {
                clientidNew.getCreditcardinfoList().add(creditcardinfo);
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
                Integer id = creditcardinfo.getCreditcardid();
                if (findCreditcardinfo(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Creditcardinfo creditcardinfo;
            try {
                creditcardinfo = em.getReference(Creditcardinfo.class, id);
                creditcardinfo.getCreditcardid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The creditcardinfo with id " + id + " no longer exists.", enfe);
            }
            Client clientid = creditcardinfo.getClientid();
            if (clientid != null) {
                clientid.getCreditcardinfoList().remove(creditcardinfo);
                clientid = em.merge(clientid);
            }
            em.remove(creditcardinfo);
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

    public List<Creditcardinfo> findCreditcardinfoEntities() {
        return findCreditcardinfoEntities(true, -1, -1);
    }

    public List<Creditcardinfo> findCreditcardinfoEntities(int maxResults, int firstResult) {
        return findCreditcardinfoEntities(false, maxResults, firstResult);
    }

    private List<Creditcardinfo> findCreditcardinfoEntities(boolean all, int maxResults, int firstResult) {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        cq.select(cq.from(Creditcardinfo.class));
        Query q = em.createQuery(cq);
        if (!all) {
            q.setMaxResults(maxResults);
            q.setFirstResult(firstResult);
        }
        return q.getResultList();

    }

    public Creditcardinfo findCreditcardinfo(Integer id) {

        return em.find(Creditcardinfo.class, id);

    }

    public int getCreditcardinfoCount() {

        CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
        Root<Creditcardinfo> rt = cq.from(Creditcardinfo.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

}
