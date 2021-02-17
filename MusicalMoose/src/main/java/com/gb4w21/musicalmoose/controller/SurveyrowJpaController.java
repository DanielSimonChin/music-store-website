/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Survey;
import com.gb4w21.musicalmoose.entities.Surveyrow;
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
public class SurveyrowJpaController implements Serializable {

        private final static Logger LOG = LoggerFactory.getLogger(SurveyrowJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;
    public void create(Surveyrow surveyrow) throws RollbackFailureException {
    
        try{
      
            utx.begin();
            Survey surveyid = surveyrow.getSurveyid();
            if (surveyid != null) {
                surveyid = em.getReference(surveyid.getClass(), surveyid.getSurveyid());
                surveyrow.setSurveyid(surveyid);
            }
            em.persist(surveyrow);
            if (surveyid != null) {
                surveyid.getSurveyrowList().add(surveyrow);
                surveyid = em.merge(surveyid);
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

    public void edit(Surveyrow surveyrow) throws NonexistentEntityException, Exception {
     
        try {
      
            utx.begin();
            Surveyrow persistentSurveyrow = em.find(Surveyrow.class, surveyrow.getRowid());
            Survey surveyidOld = persistentSurveyrow.getSurveyid();
            Survey surveyidNew = surveyrow.getSurveyid();
            if (surveyidNew != null) {
                surveyidNew = em.getReference(surveyidNew.getClass(), surveyidNew.getSurveyid());
                surveyrow.setSurveyid(surveyidNew);
            }
            surveyrow = em.merge(surveyrow);
            if (surveyidOld != null && !surveyidOld.equals(surveyidNew)) {
                surveyidOld.getSurveyrowList().remove(surveyrow);
                surveyidOld = em.merge(surveyidOld);
            }
            if (surveyidNew != null && !surveyidNew.equals(surveyidOld)) {
                surveyidNew.getSurveyrowList().add(surveyrow);
                surveyidNew = em.merge(surveyidNew);
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
                Integer id = surveyrow.getRowid();
                if (findSurveyrow(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
            utx.begin();
            Surveyrow surveyrow;
            try {
                surveyrow = em.getReference(Surveyrow.class, id);
                surveyrow.getRowid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The surveyrow with id " + id + " no longer exists.", enfe);
            }
            Survey surveyid = surveyrow.getSurveyid();
            if (surveyid != null) {
                surveyid.getSurveyrowList().remove(surveyrow);
                surveyid = em.merge(surveyid);
            }
            em.remove(surveyrow);
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

    public List<Surveyrow> findSurveyrowEntities() {
        return findSurveyrowEntities(true, -1, -1);
    }

    public List<Surveyrow> findSurveyrowEntities(int maxResults, int firstResult) {
        return findSurveyrowEntities(false, maxResults, firstResult);
    }

    private List<Surveyrow> findSurveyrowEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Surveyrow.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        
    }

    public Surveyrow findSurveyrow(Integer id) {
       
            return em.find(Surveyrow.class, id);
       
    }

    public int getSurveyrowCount() {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Surveyrow> rt = cq.from(Surveyrow.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
    
}
