/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "surveyrow", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Surveyrow.findAll", query = "SELECT s FROM Surveyrow s"),
    @NamedQuery(name = "Surveyrow.findByRowid", query = "SELECT s FROM Surveyrow s WHERE s.rowid = :rowid"),
    @NamedQuery(name = "Surveyrow.findByRowname", query = "SELECT s FROM Surveyrow s WHERE s.rowname = :rowname"),
    @NamedQuery(name = "Surveyrow.findByNumberofvotes", query = "SELECT s FROM Surveyrow s WHERE s.numberofvotes = :numberofvotes")})
public class Surveyrow implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ROWID")
    private Integer rowid;
    @Size(max = 255)
    @Column(name = "ROWNAME")
    private String rowname;
    @Column(name = "NUMBEROFVOTES")
    private Integer numberofvotes;
    @JoinColumn(name = "SURVEYID", referencedColumnName = "SURVEYID")
    @ManyToOne
    private Survey surveyid;

    public Surveyrow() {
    }

    public Surveyrow(Integer rowid) {
        this.rowid = rowid;
    }

    public Integer getRowid() {
        return rowid;
    }

    public void setRowid(Integer rowid) {
        this.rowid = rowid;
    }

    public String getRowname() {
        return rowname;
    }

    public void setRowname(String rowname) {
        this.rowname = rowname;
    }

    public Integer getNumberofvotes() {
        return numberofvotes;
    }

    public void setNumberofvotes(Integer numberofvotes) {
        this.numberofvotes = numberofvotes;
    }

    public Survey getSurveyid() {
        return surveyid;
    }

    public void setSurveyid(Survey surveyid) {
        this.surveyid = surveyid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rowid != null ? rowid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Surveyrow)) {
            return false;
        }
        Surveyrow other = (Surveyrow) object;
        if ((this.rowid == null && other.rowid != null) || (this.rowid != null && !this.rowid.equals(other.rowid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Surveyrow[ rowid=" + rowid + " ]";
    }
    
}
