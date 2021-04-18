package com.gb4w21.musicalmoose.controller;

import com.gb4w21.musicalmoose.beans.ProvinceBean;
import com.gb4w21.musicalmoose.beans.MusicItem;
import com.gb4w21.musicalmoose.controller.AlbumJpaController;
import com.gb4w21.musicalmoose.controller.ClientJpaController;
import com.gb4w21.musicalmoose.controller.InvoicedetailJpaController;
import com.gb4w21.musicalmoose.controller.MusicTrackJpaController;
import com.gb4w21.musicalmoose.controller.SaleJpaController;
import com.gb4w21.musicalmoose.controller.exceptions.RollbackFailureException;
import com.gb4w21.musicalmoose.entities.Album;
import com.gb4w21.musicalmoose.entities.Invoicedetail;
import com.gb4w21.musicalmoose.entities.MusicTrack;
import com.gb4w21.musicalmoose.entities.Sale;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jodd.mail.Email;
import jodd.mail.MailServer;
import jodd.mail.RFC2822AddressParser;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;

/**
 *
 * @author victor
 */
@Named
@SessionScoped
public class CheckoutController implements Serializable {
    
    private final static Logger LOG = LoggerFactory.getLogger(CheckoutController.class);
    
    @Inject
    private AlbumJpaController albumJpaController;
    @Inject
    private MusicTrackJpaController musicTrackJpaController;
    @Inject
    private ShoppingCartController shoppingCartController;
    @Inject
    private LoginController loginController;
    @Inject
    private SaleJpaController saleJpaController;
    @Inject
    private InvoicedetailJpaController invoicedetailJpaController;
    @Inject
    private ClientJpaController clientJpaController;
    
    private Sale saleBean;
    private ProvinceBean provinceBean = new ProvinceBean();
    private BigDecimal GST;
    private BigDecimal HST;
    private BigDecimal PST;
    private Float totalProfit;
    
    public CheckoutController() {
    }
    
    public void setSaleBean(Sale saleBean){
        this.saleBean = saleBean;
    }
    
    public Sale getSaleBean() {
        return this.saleBean;
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
    
    public void setGST(BigDecimal GST) {
        this.GST = GST;
    }
                
    public BigDecimal getHST() {
        return this.HST;
    }
    
    public void setHST(BigDecimal HST) {
        this.HST = HST;
    }
    
    public BigDecimal getPST() {
        return this.PST;
    }
    
    public void setPST(BigDecimal PST) {
        this.PST = PST;
    }
    
    public Float getTotalProfit() {
        return this.totalProfit;
    }
    
    public void setTotalProfit(Float totalProfit) {
        this.totalProfit = totalProfit;
    }
    
    public String toCheckout() {
        if (loginController.getLoginBean().isLoggedIn()) {
            this.saleBean = new Sale();
            return "checkout";
        }
        else {
            return loginController.toLoginPage();
        }
    }
    
    public BigDecimal getShippingHandlingPrice() {
        return this.shoppingCartController.calculateTotal().multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
    }
    
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
                .add(getShippingHandlingPrice())
                .add(GST)
                .add(HST)
                .add(PST)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calculateTotalNetValue() {
        return new BigDecimal(Float.toString(totalProfit))
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    public String toInvoice() throws RollbackFailureException {
        createSale();
        createInvoiceDetails();
        this.shoppingCartController.clearCart();
//        addMessage("Processing purchase & email", "This may take a few seconds.");
          sendInvoiceEmail();
        return "invoice";
    }
    
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
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
    
    private void createSale() throws RollbackFailureException {
        this.saleBean.setClientid(clientJpaController.findClient(this.loginController.getLoginBean().getId()));
        this.saleBean.setSaledate(new Date());
        this.saleBean.setSaleremoved(false);
        this.saleJpaController.create(saleBean);
    }
    
    private void createInvoiceDetails() throws RollbackFailureException {
        List<MusicItem> shoppingCartList = this.shoppingCartController.getShoppingCartList();
        this.totalProfit = (float)0;
        for (int i = 0; i < shoppingCartList.size(); i++) {
            Invoicedetail invoiceDetailBean = new Invoicedetail();
            invoiceDetailBean.setSaleid(saleBean);
            invoiceDetailBean.setSaledate(new Date());
            invoiceDetailBean.setInvoicedetailremoved(false);
            if (shoppingCartList.get(i).getIsAlbum()) {
                Album album = this.albumJpaController.findAlbum(shoppingCartList.get(i).getId());
                invoiceDetailBean.setAlbumid(album);
                
                if (album.getSaleprice() == 0) {
                    invoiceDetailBean.setCurrentcost(album.getListprice());
                    invoiceDetailBean.setProfit(album.getListprice() - album.getCostprice());
                    totalProfit += album.getListprice() - album.getCostprice();
                }
                else {
                    invoiceDetailBean.setCurrentcost(album.getSaleprice());
                    invoiceDetailBean.setProfit(album.getSaleprice() - album.getCostprice());
                    totalProfit += album.getSaleprice() - album.getCostprice();
                }
            }
            else {
                MusicTrack musicTrack = this.musicTrackJpaController.findMusicTrack(shoppingCartList.get(i).getId());
                invoiceDetailBean.setInventoryid(musicTrack);
                
                if (musicTrack.getSaleprice() == 0) {
                    invoiceDetailBean.setCurrentcost(musicTrack.getListprice());
                    invoiceDetailBean.setProfit(musicTrack.getListprice() - musicTrack.getCostprice());
                    totalProfit += musicTrack.getListprice() - musicTrack.getCostprice();
                }
                else {
                    invoiceDetailBean.setCurrentcost(musicTrack.getSaleprice());
                    invoiceDetailBean.setProfit(musicTrack.getSaleprice() - musicTrack.getCostprice());
                    totalProfit += musicTrack.getSaleprice() - musicTrack.getCostprice();
                }
            }
            invoicedetailJpaController.create(invoiceDetailBean);
        }
    }
    
    /**
     * Standard send routine for Jodd using SmtpServer
     * 
     * @return the Email object if all checks and validations are correct, 
     *         null if SendMailSessionsession session error
     */
    public boolean sendInvoiceEmail() {
        String toEmailAddress = this.loginController.getLoginBean().getEmailAddress();
        SmtpServer smtpServer = createSmtpServer();

        Email email = Email.create().from("project.username01@gmail.com");
        if (checkEmailAddress(toEmailAddress)) {
            String emailSubject = "Invoice for your MusicalMoose purchase";
            createEmailSubMsg(emailSubject, createHTMLInvoiceMsg(), email);
            email.to(toEmailAddress);
        
            try (SendMailSession session = smtpServer.createSession()) {
                session.open();
                // Set date/time right before sending to get most accurate time
                email.currentSentDate();
                session.sendMail(email);
                LOG.info("Email sent");
            }
            catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    private String createHTMLInvoiceMsg() {
        return "<p>Thank you for your purchase " + loginController.getLoginBean().getUsername() + ". Looking forward to next time! See invoice below.</p><br/><br/>"
                + "<div>"
                + "<h1><u>INVOICE</u></h1>"
                + "<p><b>Sale #: " + this.saleBean.getSaleid() + "</b></p>"
                + "<p><b>Date: " + this.saleBean.getSaledate()+ "</b></p>"
                + "<p>---</p>"
                + "<p><b>Total Gross Value Of Sale: " + this.shoppingCartController.getTotalCost() + "</b></p>"
                + "<p><b>GST: " + this.GST + "</b></p>"
                + "<p><b>HST: " + this.HST + "</b></p>"
                + "<p><b>PST: " + this.PST + "</b></p>"
                + "<p><b>Total Net Value Of Sale: " + this.calculateTotalNetValue() + "</b></p>"
                + "</div>";
    }
        
    /**
     * Creates and returns StmpServer
     * Use this email and user as a base admin email
     * 
     * @return the SmtpServer
     */
    private SmtpServer createSmtpServer() {
        return MailServer.create()
                .ssl(true)
                .host("smtp.gmail.com")
                .auth("project.username01@gmail.com", "simplepass01")
                .buildSmtpMailServer();
    }
    
    /**
     * Assign subject and calls method to assign messages to null
     *
     * @param email
     * @param subject
     * @param textMsg
     */
    private void createEmailSubMsg(String subject, String htmlMsg, Email email) {
        if (subject != null) {
            email.subject(subject);
        }
        if (htmlMsg != null) {
            email.htmlMessage(htmlMsg);
        }
    }
        
    /**
     * Checks if emailAddress is not null and valid
     * 
     * @param emailAddress
     * @return true if emailAddress not null and 
     *         RFC2822AddressParser is not null, false if so
     */
    private boolean checkEmailAddress(String emailAddress) {
        return emailAddress != null && RFC2822AddressParser.STRICT.parseToEmailAddress(emailAddress) != null;
    }
    
}
