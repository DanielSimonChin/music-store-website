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
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.gb4w21.musicalmoose.entities.Surveyrow;
import java.util.ArrayList;
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
public class SurveyJpaController implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(SurveyJpaController.class);

    @Resource
    private UserTransaction utx;

    @PersistenceContext(unitName = "musicPU")
    private EntityManager em;

    public void create(Survey survey) throws RollbackFailureException {
        if (survey.getSurveyrowList() == null) {
            survey.setSurveyrowList(new ArrayList<Surveyrow>());
        }
      
        try{
          
            utx.begin();
            List<Surveyrow> attachedSurveyrowList = new ArrayList<Surveyrow>();
            for (Surveyrow surveyrowListSurveyrowToAttach : survey.getSurveyrowList()) {
                surveyrowListSurveyrowToAttach = em.getReference(surveyrowListSurveyrowToAttach.getClass(), surveyrowListSurveyrowToAttach.getRowid());
                attachedSurveyrowList.add(surveyrowListSurveyrowToAttach);
            }
            survey.setSurveyrowList(attachedSurveyrowList);
            em.persist(survey);
            for (Surveyrow surveyrowListSurveyrow : survey.getSurveyrowList()) {
                Survey oldSurveyidOfSurveyrowListSurveyrow = surveyrowListSurveyrow.getSurveyid();
                surveyrowListSurveyrow.setSurveyid(survey);
                surveyrowListSurveyrow = em.merge(surveyrowListSurveyrow);
                if (oldSurveyidOfSurveyrowListSurveyrow != null) {
                    oldSurveyidOfSurveyrowListSurveyrow.getSurveyrowList().remove(surveyrowListSurveyrow);
                    oldSurveyidOfSurveyrowListSurveyrow = em.merge(oldSurveyidOfSurveyrowListSurveyrow);
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

    public void edit(Survey survey) throws NonexistentEntityException, Exception {
       
        try {
           
            
            utx.begin();
            Survey persistentSurvey = em.find(Survey.class, survey.getSurveyid());
            List<Surveyrow> surveyrowListOld = persistentSurvey.getSurveyrowList();
            List<Surveyrow> surveyrowListNew = survey.getSurveyrowList();
            List<Surveyrow> attachedSurveyrowListNew = new ArrayList<Surveyrow>();
            for (Surveyrow surveyrowListNewSurveyrowToAttach : surveyrowListNew) {
                surveyrowListNewSurveyrowToAttach = em.getReference(surveyrowListNewSurveyrowToAttach.getClass(), surveyrowListNewSurveyrowToAttach.getRowid());
                attachedSurveyrowListNew.add(surveyrowListNewSurveyrowToAttach);
            }
            surveyrowListNew = attachedSurveyrowListNew;
            survey.setSurveyrowList(surveyrowListNew);
            survey = em.merge(survey);
            for (Surveyrow surveyrowListOldSurveyrow : surveyrowListOld) {
                if (!surveyrowListNew.contains(surveyrowListOldSurveyrow)) {
                    surveyrowListOldSurveyrow.setSurveyid(null);
                    surveyrowListOldSurveyrow = em.merge(surveyrowListOldSurveyrow);
                }
            }
            for (Surveyrow surveyrowListNewSurveyrow : surveyrowListNew) {
                if (!surveyrowListOld.contains(surveyrowListNewSurveyrow)) {
                    Survey oldSurveyidOfSurveyrowListNewSurveyrow = surveyrowListNewSurveyrow.getSurveyid();
                    surveyrowListNewSurveyrow.setSurveyid(survey);
                    surveyrowListNewSurveyrow = em.merge(surveyrowListNewSurveyrow);
                    if (oldSurveyidOfSurveyrowListNewSurveyrow != null && !oldSurveyidOfSurveyrowListNewSurveyrow.equals(survey)) {
                        oldSurveyidOfSurveyrowListNewSurveyrow.getSurveyrowList().remove(surveyrowListNewSurveyrow);
                        oldSurveyidOfSurveyrowListNewSurveyrow = em.merge(oldSurveyidOfSurveyrowListNewSurveyrow);
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
                Integer id = survey.getSurveyid();
                if (findSurvey(id) == null) {
                    throw new NonexistentEntityException("The fish with id " + id + " no longer exists.");
                }
            }
            throw ex;
        }
    }

    public void destroy(Integer id) throws  NonexistentEntityException, RollbackFailureException, Exception {
        try{
            utx.begin();
            Survey survey;
            try {
                survey = em.getReference(Survey.class, id);
                survey.getSurveyid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The survey with id " + id + " no longer exists.", enfe);
            }
            List<Surveyrow> surveyrowList = survey.getSurveyrowList();
            for (Surveyrow surveyrowListSurveyrow : surveyrowList) {
                surveyrowListSurveyrow.setSurveyid(null);
                surveyrowListSurveyrow = em.merge(surveyrowListSurveyrow);
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

    public int getSurveyCount() {
       
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Survey> rt = cq.from(Survey.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
       
    }
    
}
