/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.controller.exceptions.NonexistentEntityException;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Survey;
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
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class SurveyJpaController implements Serializable {

    private boolean isSurveyUsed = false;

     private final static Logger LOG = LoggerFactory.getLogger(SurveyJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Survey survey) throws RollbackFailureException, javax.transaction.RollbackException {
        LOG.debug("id"+survey.getSurveyid());
        LOG.debug("id"+survey.getSurveytitle());
        LOG.debug("id"+survey.getQuestion());
        LOG.debug("id"+survey.getSurveryinuse());
        LOG.debug("id"+survey.getAnserw1());
        LOG.debug("id"+survey.getAnserw2());
        LOG.debug("id"+survey.getAnserw3());
        LOG.debug("id"+survey.getAnserw4());
        LOG.debug("id"+survey.getAnserw1votes());
        LOG.debug("id"+survey.getAnserw2votes());
        LOG.debug("id"+survey.getAnserw3votes());
        LOG.debug("id"+survey.getAnserw4votes());
        LOG.debug("id"+survey.getDatesurveyrcreated());
        LOG.debug("id"+survey.getDatelastused());
        try {
            utx.begin();

            em.persist(survey);

            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            try {
                utx.rollback();
                LOG.error("Rollback" +ex.getLocalizedMessage());
            } catch (IllegalStateException | SecurityException | SystemException re) {
                LOG.error("Rollback2");

                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
        }

    }

    public void edit(Survey survey) throws NonexistentEntityException, Exception {
        try {
            utx.begin();
            survey = em.merge(survey);

            utx.commit();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            try {
                utx.rollback();
            } catch (IllegalStateException | SecurityException | SystemException re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = survey.getSurveyid();
                if (findSurvey(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }

    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Survey survey;
            try {
                survey = em.getReference(Survey.class, id);
                survey.getSurveyid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The survey with id " + id + " no longer exists.", enfe);
            }
            em.remove(survey);
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

    public List<Survey> findSurveyEntities() {
        return findSurveyEntities(true, -1, -1);
    }

    public List<Survey> findSurveyEntities(int maxResults, int firstResult) {
        return findSurveyEntities(false, maxResults, firstResult);
    }

    private List<Survey> findSurveyEntities(boolean all, int maxResults, int firstResult) {
        
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Survey.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
      
    }

    public Survey findSurvey(Integer id) {
       
            return em.find(Survey.class, id);
      
    }

    public Survey getRunningSurvey() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Survey> survey = cq.from(Survey.class);
        cq.select(survey);
        cq.where(cb.equal(survey.get("surveryinuse"), 1));
        TypedQuery<Survey> query = em.createQuery(cq);
        try {
            return query.getSingleResult();
        } catch (javax.persistence.NoResultException NoResultException) {
            return null;
        }

    }

    public boolean isSurveyUsed() {
        return this.isSurveyUsed;
    }

    public String incearseVote1() throws Exception {
        incearseVote(1);
        return "reloadindex";
    }

    public String incearseVote2() throws Exception {
        incearseVote(2);
        return "reloadindex";
    }

    public String incearseVote3() throws Exception {
        incearseVote(3);
        return "reloadindex";
    }

    public String incearseVote4() throws Exception {
        incearseVote(4);
        return "reloadindex";
    }

    private void incearseVote(int voteNumber) throws Exception {
        Survey survey = getRunningSurvey();
        increaseRow(voteNumber, survey);
        this.isSurveyUsed = true;

    }

    private void increaseRow(int rowNumber, Survey survey) throws Exception {
        if (rowNumber == 1) {
            survey.setAnserw1votes(survey.getAnserw1votes() + 1);
        } else if (rowNumber == 2) {
            survey.setAnserw2votes(survey.getAnserw2votes() + 1);
        } else if (rowNumber == 3) {
            survey.setAnserw3votes(survey.getAnserw3votes() + 1);
        } else if (rowNumber == 4) {
            survey.setAnserw4votes(survey.getAnserw4votes() + 1);
        }
        edit(survey);
    }

    public int getSurveyCount() {
     
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Survey> rt = cq.from(Survey.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
    
    }

}
