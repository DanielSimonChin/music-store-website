/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.entities;

import java.io.Serializable;
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
import javax.validation.constraints.Size;

/**
 *
 * @author owner
 */
@Entity
@Table(name = "client", catalog = "MUSICSTORAGE", schema = "")
@NamedQueries({
    @NamedQuery(name = "Client.findAll", query = "SELECT c FROM Client c"),
    @NamedQuery(name = "Client.findByClientid", query = "SELECT c FROM Client c WHERE c.clientid = :clientid"),
    @NamedQuery(name = "Client.findByUsername", query = "SELECT c FROM Client c WHERE c.username = :username"),
    @NamedQuery(name = "Client.findByPassword", query = "SELECT c FROM Client c WHERE c.password = :password"),
    @NamedQuery(name = "Client.findByTitle", query = "SELECT c FROM Client c WHERE c.title = :title"),
    @NamedQuery(name = "Client.findByLastname", query = "SELECT c FROM Client c WHERE c.lastname = :lastname"),
    @NamedQuery(name = "Client.findByFirstname", query = "SELECT c FROM Client c WHERE c.firstname = :firstname"),
    @NamedQuery(name = "Client.findByCompanyname", query = "SELECT c FROM Client c WHERE c.companyname = :companyname"),
    @NamedQuery(name = "Client.findByAddress1", query = "SELECT c FROM Client c WHERE c.address1 = :address1"),
    @NamedQuery(name = "Client.findByAddress2", query = "SELECT c FROM Client c WHERE c.address2 = :address2"),
    @NamedQuery(name = "Client.findByCity", query = "SELECT c FROM Client c WHERE c.city = :city"),
    @NamedQuery(name = "Client.findByProvince", query = "SELECT c FROM Client c WHERE c.province = :province"),
    @NamedQuery(name = "Client.findByCountry", query = "SELECT c FROM Client c WHERE c.country = :country"),
    @NamedQuery(name = "Client.findByPostalcode", query = "SELECT c FROM Client c WHERE c.postalcode = :postalcode"),
    @NamedQuery(name = "Client.findByHometelephone", query = "SELECT c FROM Client c WHERE c.hometelephone = :hometelephone"),
    @NamedQuery(name = "Client.findByCelltelephone", query = "SELECT c FROM Client c WHERE c.celltelephone = :celltelephone"),
    @NamedQuery(name = "Client.findByEmail", query = "SELECT c FROM Client c WHERE c.email = :email"),
    @NamedQuery(name = "Client.findByGenreoflastsearch", query = "SELECT c FROM Client c WHERE c.genreoflastsearch = :genreoflastsearch"),
    @NamedQuery(name = "Client.findByIsmanager", query = "SELECT c FROM Client c WHERE c.ismanager = :ismanager"),
    @NamedQuery(name = "Client.findByClientremoved", query = "SELECT c FROM Client c WHERE c.clientactive= :clientactive")})
public class Client implements Serializable {

    @Size(max = 50)
    @Column(name = "USERNAME")
    private String username;
    @Size(max = 50)
    @Column(name = "PASSWORD")
    private String password;
    @Size(max = 255)
    @Column(name = "TITLE")
    private String title;
    @Size(max = 255)
    @Column(name = "LASTNAME")
    private String lastname;
    @Size(max = 255)
    @Column(name = "FIRSTNAME")
    private String firstname;
    @Size(max = 255)
    @Column(name = "COMPANYNAME")
    private String companyname;
    @Size(max = 255)
    @Column(name = "ADDRESS1")
    private String address1;
    @Size(max = 255)
    @Column(name = "ADDRESS2")
    private String address2;
    @Size(max = 255)
    @Column(name = "CITY")
    private String city;
    @Size(max = 255)
    @Column(name = "PROVINCE")
    private String province;
    @Size(max = 255)
    @Column(name = "COUNTRY")
    private String country;
    @Size(max = 255)
    @Column(name = "POSTALCODE")
    private String postalcode;
    @Size(max = 25)
    @Column(name = "HOMETELEPHONE")
    private String hometelephone;
    @Size(max = 25)
    @Column(name = "CELLTELEPHONE")
    private String celltelephone;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 255)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 255)
    @Column(name = "GENREOFLASTSEARCH")
    private String genreoflastsearch;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "CLIENTID")
    private Integer clientid;
    @Column(name = "ISMANAGER")
    private Boolean ismanager;
    @Column(name = "CLIENTACTIVE")
    private Boolean clientactive;
    @OneToMany(mappedBy = "clientid")
    private List<Sale> saleList;
    @OneToMany(mappedBy = "clientid")
    private List<Review> reviewList;

    public Client() {
    }

    public Client(Integer clientid) {
        this.clientid = clientid;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }
    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }
    public List<Review> getReviewList() {
        return reviewList;
    }
    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }


    public Integer getClientid() {
        return clientid;
    }

    public void setClientid(Integer clientid) {
        this.clientid = clientid;
    }
    public Boolean getIsmanager() {
        return ismanager;
    }
    public void setIsmanager(Boolean ismanager) {
        this.ismanager = ismanager;
    }
    public Boolean getClientactive() {
        return clientactive;
    }
    public void setClientactive(Boolean clientactive) {
        this.clientactive = clientactive;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (clientid != null ? clientid.hashCode() : 0);
        return hash;
    }
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Client)) {
            return false;
        }
        Client other = (Client) object;
        if ((this.clientid == null && other.clientid != null) || (this.clientid != null && !this.clientid.equals(other.clientid))) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "com.gb4w21.musicalmoose.entities.Client[ clientid=" + clientid + " ]";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getHometelephone() {
        return hometelephone;
    }

    public void setHometelephone(String hometelephone) {
        this.hometelephone = hometelephone;
    }

    public String getCelltelephone() {
        return celltelephone;
    }

    public void setCelltelephone(String celltelephone) {
        this.celltelephone = celltelephone;
    }

    public String getEmail() {
        return email;
    }

   


    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenreoflastsearch() {
        return genreoflastsearch;
    }

    public void setGenreoflastsearch(String genreoflastsearch) {
        this.genreoflastsearch = genreoflastsearch;
    }
    
}
