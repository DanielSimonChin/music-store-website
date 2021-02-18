/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "survey", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Survey.findAll", query = "SELECT s FROM Survey s"),
    @NamedQuery(name = "Survey.findBySurveyid", query = "SELECT s FROM Survey s WHERE s.surveyid = :surveyid"),
    @NamedQuery(name = "Survey.findBySurveytitle", query = "SELECT s FROM Survey s WHERE s.surveytitle = :surveytitle"),
    @NamedQuery(name = "Survey.findByQuestion", query = "SELECT s FROM Survey s WHERE s.question = :question"),
    @NamedQuery(name = "Survey.findByAnserw1", query = "SELECT s FROM Survey s WHERE s.anserw1 = :anserw1"),
    @NamedQuery(name = "Survey.findByAnserw1votes", query = "SELECT s FROM Survey s WHERE s.anserw1votes = :anserw1votes"),
    @NamedQuery(name = "Survey.findByAnserw2", query = "SELECT s FROM Survey s WHERE s.anserw2 = :anserw2"),
    @NamedQuery(name = "Survey.findByAnserw2votes", query = "SELECT s FROM Survey s WHERE s.anserw2votes = :anserw2votes"),
    @NamedQuery(name = "Survey.findByAnserw3", query = "SELECT s FROM Survey s WHERE s.anserw3 = :anserw3"),
    @NamedQuery(name = "Survey.findByAnserw3votes", query = "SELECT s FROM Survey s WHERE s.anserw3votes = :anserw3votes"),
    @NamedQuery(name = "Survey.findByAnserw4", query = "SELECT s FROM Survey s WHERE s.anserw4 = :anserw4"),
    @NamedQuery(name = "Survey.findByAnserw4votes", query = "SELECT s FROM Survey s WHERE s.anserw4votes = :anserw4votes"),
    @NamedQuery(name = "Survey.findByDatesurveyrcreated", query = "SELECT s FROM Survey s WHERE s.datesurveyrcreated = :datesurveyrcreated"),
    @NamedQuery(name = "Survey.findByDatelastused", query = "SELECT s FROM Survey s WHERE s.datelastused = :datelastused"),
    @NamedQuery(name = "Survey.findBySurveryended", query = "SELECT s FROM Survey s WHERE s.surveryended = :surveryended")})
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SURVEYID")
    private Integer surveyid;
    @Size(max = 255)
    @Column(name = "SURVEYTITLE")
    private String surveytitle;
    @Size(max = 255)
    @Column(name = "QUESTION")
    private String question;
    @Size(max = 255)
    @Column(name = "ANSERW1")
    private String anserw1;
    @Column(name = "ANSERW1VOTES")
    private Integer anserw1votes;
    @Size(max = 255)
    @Column(name = "ANSERW2")
    private String anserw2;
    @Column(name = "ANSERW2VOTES")
    private Integer anserw2votes;
    @Size(max = 255)
    @Column(name = "ANSERW3")
    private String anserw3;
    @Column(name = "ANSERW3VOTES")
    private Integer anserw3votes;
    @Size(max = 255)
    @Column(name = "ANSERW4")
    private String anserw4;
    @Column(name = "ANSERW4VOTES")
    private Integer anserw4votes;
    @Column(name = "DATESURVEYRCREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datesurveyrcreated;
    @Column(name = "DATELASTUSED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastused;
    @Column(name = "SURVERYENDED")
    private Boolean surveryended;

    public Survey() {
    }

    public Survey(Integer surveyid) {
        this.surveyid = surveyid;
    }

    public Integer getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(Integer surveyid) {
        this.surveyid = surveyid;
    }

    public String getSurveytitle() {
        return surveytitle;
    }

    public void setSurveytitle(String surveytitle) {
        this.surveytitle = surveytitle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnserw1() {
        return anserw1;
    }

    public void setAnserw1(String anserw1) {
        this.anserw1 = anserw1;
    }

    public Integer getAnserw1votes() {
        return anserw1votes;
    }

    public void setAnserw1votes(Integer anserw1votes) {
        this.anserw1votes = anserw1votes;
    }

    public String getAnserw2() {
        return anserw2;
    }

    public void setAnserw2(String anserw2) {
        this.anserw2 = anserw2;
    }

    public Integer getAnserw2votes() {
        return anserw2votes;
    }

    public void setAnserw2votes(Integer anserw2votes) {
        this.anserw2votes = anserw2votes;
    }

    public String getAnserw3() {
        return anserw3;
    }

    public void setAnserw3(String anserw3) {
        this.anserw3 = anserw3;
    }

    public Integer getAnserw3votes() {
        return anserw3votes;
    }

    public void setAnserw3votes(Integer anserw3votes) {
        this.anserw3votes = anserw3votes;
    }

    public String getAnserw4() {
        return anserw4;
    }

    public void setAnserw4(String anserw4) {
        this.anserw4 = anserw4;
    }

    public Integer getAnserw4votes() {
        return anserw4votes;
    }

    public void setAnserw4votes(Integer anserw4votes) {
        this.anserw4votes = anserw4votes;
    }

    public Date getDatesurveyrcreated() {
        return datesurveyrcreated;
    }

    public void setDatesurveyrcreated(Date datesurveyrcreated) {
        this.datesurveyrcreated = datesurveyrcreated;
    }

    public Date getDatelastused() {
        return datelastused;
    }

    public void setDatelastused(Date datelastused) {
        this.datelastused = datelastused;
    }

    public Boolean getSurveryended() {
        return surveryended;
    }

    public void setSurveryended(Boolean surveryended) {
        this.surveryended = surveryended;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (surveyid != null ? surveyid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Survey)) {
            return false;
        }
        Survey other = (Survey) object;
        if ((this.surveyid == null && other.surveyid != null) || (this.surveyid != null && !this.surveyid.equals(other.surveyid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.Survey[ surveyid=" + surveyid + " ]";
    }
    
}
