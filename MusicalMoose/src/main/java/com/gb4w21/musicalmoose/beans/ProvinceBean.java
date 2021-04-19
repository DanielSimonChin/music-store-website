/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gb4w21.musicalmoose.beans;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.faces.model.SelectItem;
import javax.inject.Named;

/**
 *
 * @author victo
 */
@Named(value = "provinceBean")
@Dependent
public class ProvinceBean {

    private String selectedProvince;

//    public ProvinceBean(String selectedProvince) {
//        this.selectedProvince = selectedProvince;
//    }
    public String getSelectedItem() {
        if (selectedProvince == null) {
            selectedProvince = "valueAlberta"; // This will be the default selected item.
        }
        return selectedProvince;
    }

    public void setSelectedItem(String selectedProvince) {
        this.selectedProvince = selectedProvince;
    }

    public List getSelectItems() {
        List selectProvinces = new ArrayList();
        selectProvinces.add(new SelectItem("valueAlberta", "Alberta"));
        selectProvinces.add(new SelectItem("valueBritishColumbia", "British Columbia"));
        selectProvinces.add(new SelectItem("valueManitoba", "Manitoba"));
        selectProvinces.add(new SelectItem("valueNewBrunswick", "New Brunswick"));
        selectProvinces.add(new SelectItem("valueNewfoundlandandLabrador", "Newfoundland and Labrador"));
        selectProvinces.add(new SelectItem("valueNorthwestTerritories", "Northwest Territories"));
        selectProvinces.add(new SelectItem("valueNoveScotia", "Nove Scotia"));
        selectProvinces.add(new SelectItem("valueNunavut", "Nunavut"));
        selectProvinces.add(new SelectItem("valueOntario", "Ontario"));
        selectProvinces.add(new SelectItem("valuePrinceEdwardIsland", "Prince Edward Island"));
        selectProvinces.add(new SelectItem("valueQuebec", "Quebec"));
        selectProvinces.add(new SelectItem("valueSaskatchewan", "Saskatchewan"));
        selectProvinces.add(new SelectItem("valueYukon", "Yukon"));

        return selectProvinces;
    }
}
