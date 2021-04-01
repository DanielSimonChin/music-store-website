package com.gb4w21.musicalmoose.util;

import com.gb4w21.musicalmoose.beans.LoginBean;
import com.gb4w21.musicalmoose.beans.ProvinceBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author victo
 */
@Named
@SessionScoped
public class CheckoutController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(CheckoutController.class);
    
    @Inject
    private ShoppingCartController shoppingCartController;
    @Inject
    private LoginController loginController;
    private ProvinceBean provinceBean = new ProvinceBean();
    private BigDecimal GST;
    private BigDecimal HST;
    private BigDecimal PST;
    
    public CheckoutController() {
    }
    
    public void setProvinceBean(ProvinceBean provinceBean){
        this.provinceBean = provinceBean;
    }
    
    public ProvinceBean getProvinceBean() {
        return this.provinceBean;
    }
    
    public BigDecimal getGST() {
        return this.GST;
    }
                
    public BigDecimal getHST() {
        return this.HST;
    }
                
    public BigDecimal getPST() {
        return this.PST;
    }
    
    public String toCheckout() {
        if (loginController.getLoginBean().isLoggedIn()) {
            return "checkout";
        }
        else {
            return loginController.toLoginPage();
        }
    }
    
    public BigDecimal getShippingHandlingPrice() {
        return this.shoppingCartController.calculateTotal().multiply(new BigDecimal("0.14")).setScale(2, RoundingMode.HALF_UP);
    }
    
//    private BigDecimal getValueTwoDecimal(double value) {
//        BigDecimal bd = BigDecimal.valueOf(value);
//        bd = bd.setScale(2, RoundingMode.HALF_UP);
//        return bd.doubleValue();
//    }
    
    public BigDecimal getGSTHSTPrice() {
        if (this.provinceBean.getSelectedItem().equals("valueOntario")) {
            return calculateGSTHST(new BigDecimal("0"), new BigDecimal("0.13"));
        }
        else if (checkProvinceFivePercentTax()) {
            return calculateGSTHST(new BigDecimal("0.05"), new BigDecimal("0"));
        }
        else if (checkProvinceFifteenPercentTax()) {
            return calculateGSTHST(new BigDecimal("0"), new BigDecimal("0.15"));
        }
        else {
            return calculateGSTHST(new BigDecimal("0"), new BigDecimal("0"));
        }
    }
    
    private boolean checkProvinceFivePercentTax() {
        return this.provinceBean.getSelectedItem().equals("valueAlberta") || 
                this.provinceBean.getSelectedItem().equals("valueBritishColumbia") ||
                this.provinceBean.getSelectedItem().equals("valueManitoba") ||
                this.provinceBean.getSelectedItem().equals("valueNorthwestTerritories") || 
                this.provinceBean.getSelectedItem().equals("valueNunavut") ||
                this.provinceBean.getSelectedItem().equals("valueQuebec") ||
                this.provinceBean.getSelectedItem().equals("valueSaskatchewan") ||
                this.provinceBean.getSelectedItem().equals("valueYukon");
    }
    
    private boolean checkProvinceFifteenPercentTax() {
        return this.provinceBean.getSelectedItem().equals("valueNewBrunswick") ||
                this.provinceBean.getSelectedItem().equals("valueNewfoundlandandLabrador") ||
                this.provinceBean.getSelectedItem().equals("valueNoveScotia") ||
                this.provinceBean.getSelectedItem().equals("valuePrinceEdwardIsland");
    }
    
    private BigDecimal calculateGSTHST(BigDecimal percentGST, BigDecimal percentHST) {
        this.GST = calculateTax(percentGST);
        this.HST = calculateTax(percentHST);
        return GST.add(HST);
    }
    
    public BigDecimal getPSTPrice() {
        if (this.provinceBean.getSelectedItem().equals("valueBritishColumbia") ||
                this.provinceBean.getSelectedItem().equals("valueManitoba")) {
            this.PST = calculateTax(new BigDecimal("0.07"));
        }
        else if (this.provinceBean.getSelectedItem().equals("valueQuebec")) {
            this.PST = calculateTax(new BigDecimal("0.09975"));
        }
        else if (this.provinceBean.getSelectedItem().equals("valueSaskatchewan")) {
            this.PST = calculateTax(new BigDecimal("0.06"));
        }
        else {
            this.PST = new BigDecimal("0");
        }
        return this.PST;
    }
    
    private BigDecimal calculateTax(BigDecimal taxPercentage) {
        return this.shoppingCartController.calculateTotal()
                .multiply(taxPercentage)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getTotalPrice() {
        return this.shoppingCartController.calculateTotal()
                .add(this.getShippingHandlingPrice())
                .add(this.getGSTHSTPrice())
                .add(this.getPSTPrice())
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Method called to validate the input. The validation rule is that there
     * is only 16 characters
     *
     * @param fc
     * @param c
     * @param value
     */
    public void validateCreditCard(FacesContext fc, UIComponent c, Object value) {
        if (!((String)value).matches("[0-9]+")) {
            throw new ValidatorException(new FacesMessage("Must only contain numbers"));
        }
        if (!luhnCheck((String)value)) {
            throw new ValidatorException(new FacesMessage("Invalid credit card number"));
        }
    }
    
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;

        for (int i = cardNumber.length() - 1; i >= 0; i -= 2) {
            sum += Integer.parseInt(cardNumber.substring(i, i + 1));
            if (i > 0) {
                int d = 2 * Integer.parseInt(cardNumber.substring(i - 1, i));
                if (d > 9) {
                    d -= 9;
                }
                sum += d;
            }
        }

        return sum % 10 == 0;
    }
    
    public String toInvoice() {
        this.shoppingCartController.clearCart();
        return "invoice";
    }
    
    
    
    
    
    
    
    
    // DELETE
    public void validateExpireDate(FacesContext fc, UIComponent c, Object value) {
        
        String date = (String) value;
        if (date.length() != 7 || checkDate(date)) {
            throw new ValidatorException(new FacesMessage("Expiry date format is invalid (MM/YYYY)"));
        }
    }
    private boolean checkDate(String date) {
        for (int i = 0; i < date.length(); i++) {
            if (i == 2) {
                if (date.charAt(i) != '/') {
                    return false;
                }
            }
            else {
                if (!Character.isDigit(date.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }
}
