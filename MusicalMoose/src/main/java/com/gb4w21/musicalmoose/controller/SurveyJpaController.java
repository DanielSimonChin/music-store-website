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
    private final int vote1=1; 
    private final int vote2=2; 
    private final int vote3=3; 
    private final int vote4=4; 
    private boolean isSurveyUsed = false;

     private final static Logger LOG = LoggerFactory.getLogger(SurveyJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Survey survey) throws RollbackFailureException, javax.transaction.RollbackException {
       
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
    /**
     * Gets the survey that's currently available
     * @return Survey available survey
     */
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
    public void setIsSurveyUsed(boolean isSurveyUsed){
        this.isSurveyUsed=isSurveyUsed;
    }
    public boolean isSurveyUsed() {
        return this.isSurveyUsed;
    }
    /**
     * increases vote for answer1
     * @return String index page (reloads page)
     * @throws Exception 
     */
    public String incearseVote1() throws Exception {
        incearseVote(vote1);
        return "reloadindex";
    }
 /**
     * increases vote for answer2
     * @return String index page (reloads page)
     * @throws Exception 
     */
    public String incearseVote2() throws Exception {
        incearseVote(vote2);
        return "reloadindex";
    }
 /**
     * increases vote for answer3
     * @return String index page (reloads page)
     * @throws Exception 
     */
    public String incearseVote3() throws Exception {
        incearseVote(vote3);
        return "reloadindex";
    }
 /**
     * increases vote for answer4
     * @return String index page (reloads page)
     * @throws Exception 
     */
    public String incearseVote4() throws Exception {
        incearseVote(vote4);
        return "reloadindex";
    }
    /**
     * Increases the vote for selected survey and sets it to be used so the user can't vote again
     * @param voteNumber The answer that should be voted
     * @throws Exception 
     */
    private void incearseVote(int voteNumber) throws Exception {
        Survey survey = getRunningSurvey();
        increaseRow(voteNumber, survey);
        this.isSurveyUsed = true;

    }
    /**
     * Increases the the vote for selected answer on the currently running survey
     * @param rowNumber int
     * @param survey Survey
     * @throws Exception 
     */
    private void increaseRow(int rowNumber, Survey survey) throws Exception {
        LOG.info("Vote Number:"+rowNumber);
        LOG.info("Current Survey:"+survey.getSurveytitle());
        if (rowNumber == vote1) {
            survey.setAnserw1votes(survey.getAnserw1votes() + 1);
        } else if (rowNumber ==vote2) {
            survey.setAnserw2votes(survey.getAnserw2votes() + 1);
        } else if (rowNumber == vote3){
            survey.setAnserw3votes(survey.getAnserw3votes() + 1);
        } else if (rowNumber == vote4) {
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
