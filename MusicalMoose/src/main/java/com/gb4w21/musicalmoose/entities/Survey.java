/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Survey.findBySurveytext", query = "SELECT s FROM Survey s WHERE s.surveytext = :surveytext"),
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
    @Column(name = "SURVEYTEXT")
    private String surveytext;
    @Column(name = "DATESURVEYRCREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datesurveyrcreated;
    @Column(name = "DATELASTUSED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datelastused;
    @Column(name = "SURVERYENDED")
    private Boolean surveryended;
    @OneToMany(mappedBy = "surveyid")
    private List<Surveyrow> surveyrowList;

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

    public String getSurveytext() {
        return surveytext;
    }

    public void setSurveytext(String surveytext) {
        this.surveytext = surveytext;
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

    public List<Surveyrow> getSurveyrowList() {
        return surveyrowList;
    }

    public void setSurveyrowList(List<Surveyrow> surveyrowList) {
        this.surveyrowList = surveyrowList;
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
        return "com.gb4w21.musicalmoose.entities.Survey[ surveyid=" + surveyid + " ]";
    }
    
}
