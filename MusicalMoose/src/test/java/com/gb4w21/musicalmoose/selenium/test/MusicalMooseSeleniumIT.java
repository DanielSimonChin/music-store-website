package com.gb4w21.musicalmoose.selenium.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.Random;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Testing the expected input and outputs for the functionally of the client
 * site ranging from login, registration, adding to cart, downloads, checkout,
 * etc.
 *
 * @author Daniel and Victor
 */
public class MusicalMooseSeleniumIT {

    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        // Normally an executable that matches the browser you are using must
        // be in the classpath. The webdrivermanager library by Boni Garcia
        // downloads the required driver and makes it available
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new ChromeDriver();
    }

    /**
     * The most basic Selenium test method that tests to see if the page name
     * matches a specific name.
     *
     * @throws Exception
     */
    @Test
    public void testIndexHomePage() throws Exception {
        // And now use this to visit this project
        driver.get("http://localhost:8080/MusicalMoose/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));
    }

    /**
     * Test the login input and login button, and ensure it returns us to where
     * we previously were.
     *
     * @throws Exception
     */
    @Test
    public void loginToIndex() throws Exception {
        // And now use this to visit this project
        driver.get("http://localhost:8080/MusicalMoose/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));

        // Click the login/register button
        driver.findElement(By.id("loginButton")).click();

        WebElement inputElement = driver.findElement(By.id("loginForm:cname"));
        inputElement.clear();
        // Enter text into the input field
        inputElement.sendKeys("DawsonConsumer");

        // Find password input field
        inputElement = driver.findElement(By.id("loginForm:password"));
        // Clear out anything currently in the field
        inputElement.clear();
        // Enter text into the input field
        inputElement.sendKeys("dawsoncollege");

        // Click the submit button
        driver.findElement(By.id("loginForm:formLogin")).click();

        //After loggin in, we should see the main section of the index page. So it brought us back to where we were.
        WebElement resultElement = driver.findElement(By.xpath("//*[@id=\"indexSectionContainer\"]"));
        wait.until(ExpectedConditions.visibilityOf(resultElement));
    }

    /**
     * Test the registration form works and returns to the index.
     *
     * @throws Exception
     */
    @Test
    public void testRegisterForm() throws Exception {
        // And now use this to visit this project
        driver.get("http://localhost:8080/MusicalMoose/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));
        driver.findElement(By.id("loginButton")).click();
        driver.findElement(By.id("noAccountForm")).click();
        WebElement inputElement = driver.findElement(By.id("registerForm:cname"));
        inputElement.clear();
        //Since clients cannot have the same username, we append two random ints to ensure that they will never have the same username
        inputElement.sendKeys(generateRandomInt() + "SampleTestUser" + generateRandomInt());

        inputElement = driver.findElement(By.xpath("//*[@id=\"registerForm:title:0\"]"));
        inputElement.click();

        inputElement = driver.findElement(By.id("registerForm:password1"));
        inputElement.clear();
        inputElement.sendKeys("Simplepassword123");

        inputElement = driver.findElement(By.id("registerForm:password2"));
        inputElement.clear();
        inputElement.sendKeys("Simplepassword123");

        inputElement = driver.findElement(By.id("registerForm:fname"));
        inputElement.clear();
        inputElement.sendKeys("Michael");

        inputElement = driver.findElement(By.id("registerForm:lname"));
        inputElement.clear();
        inputElement.sendKeys("Wazowski");

        inputElement = driver.findElement(By.id("registerForm:compname"));
        inputElement.clear();
        inputElement.sendKeys("Dawson College");

        inputElement = driver.findElement(By.id("registerForm:addr1"));
        inputElement.clear();
        inputElement.sendKeys("Address avenue 123");

        inputElement = driver.findElement(By.id("registerForm:addr2"));
        inputElement.clear();
        inputElement.sendKeys("Address road 123");

        inputElement = driver.findElement(By.id("registerForm:city"));
        inputElement.clear();
        inputElement.sendKeys("Montreal");

        inputElement = driver.findElement(By.id("registerForm:province"));
        inputElement.clear();
        inputElement.sendKeys("Quebec");

        inputElement = driver.findElement(By.id("registerForm:crountry"));
        inputElement.clear();
        inputElement.sendKeys("Canada");

        inputElement = driver.findElement(By.id("registerForm:pcode"));
        inputElement.clear();
        inputElement.sendKeys("A1A 1A1");

        inputElement = driver.findElement(By.id("registerForm:hphone"));
        inputElement.clear();
        inputElement.sendKeys("514-143-1524");

        inputElement = driver.findElement(By.id("registerForm:cphone"));
        inputElement.clear();
        inputElement.sendKeys("514-143-1523");

        inputElement = driver.findElement(By.id("registerForm:email"));
        inputElement.clear();
        inputElement.sendKeys("genericemail@hotmail.com");

        driver.findElement(By.id("registerForm:registerUserButton")).click();

        //Once we register, it should bring us back to the index page where we previously were.
        WebElement indexSectionElement = driver.findElement(By.xpath("//*[@id=\"indexSectionContainer\"]"));
        wait.until(ExpectedConditions.visibilityOf(indexSectionElement));
    }

    /**
     * @return a random number to be appended to the username.
     */
    private static int generateRandomInt() {
        Random rand = new Random();
        int randomNum = rand.nextInt((1000 - 1) + 1) + 1;
        return randomNum;
    }

    /**
     * Test that the vote output of the survey is displayed once a vote button
     * is clicked.
     *
     * @throws Exception
     */
    @Test
    public void testSurveyClick() throws Exception {
        driver.get("http://localhost:8080/MusicalMoose/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));

        driver.findElement(By.xpath("//*[@id=\"surveyForm:answerbtn\"]")).click();
        WebElement inputElement = driver.findElement(By.xpath("//*[@id=\"surveyForm:voteOutput1\"]"));
        wait.until(ExpectedConditions.visibilityOf(inputElement));
    }

    /**
     * Test that when clicking on a genre, it brings us to the search page
     *
     * @throws Exception
     */
    @Test
    public void testClickingOnGenre() throws Exception {
        driver.get("http://localhost:8080/MusicalMoose/");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));

        driver.findElement(By.xpath("//*[@id=\"j_idt80\"]/a")).click();
        WebElement inputElement = driver.findElement(By.xpath("//*[@id=\"searchSectionContainer\"]"));
        wait.until(ExpectedConditions.visibilityOf(inputElement));

    }

    /**
     * Test that when clicking on the Add to Cart music track and album page
     * that it will add to the shopping cart
     *
     * @throws Exception
     */
    @Test
    public void testClickAddCart() throws Exception {
        driver.get("http://localhost:8080/MusicalMoose/");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));

        driver.findElement(By.className("specialTrackButton")).click();
        driver.findElement(By.id("addTrackCartForm:addCartTrack")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.className("form")).click();
        driver.findElement(By.id("addAlbumCartForm:addAlbumCartButton")).click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.id("logo")).click();
        driver.findElement(By.id("shoppingCartForm")).click();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement resultElement = driver.findElement(By.xpath("//*[@id=\"cartSectionContainer\"]"));
        wait.until(ExpectedConditions.visibilityOf(resultElement));

        // Checks if items are inside the shopping cart
        Assert.assertTrue(!driver.findElements(By.className("itemsInCart")).isEmpty());
    }

    /**
     * Test that the items are in the cart are able to checkout
     *
     * @throws Exception
     */
    @Test
    public void testCheckOut() throws Exception {
        driver.get("http://localhost:8080/MusicalMoose/");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));

        driver.findElement(By.className("specialTrackButton")).click();
        driver.findElement(By.id("addTrackCartForm:addCartTrack")).click();
        driver.findElement(By.className("form")).click();
        driver.findElement(By.id("addAlbumCartForm:addAlbumCartButton")).click();

        driver.findElement(By.id("logo")).click();
        driver.findElement(By.id("shoppingCartForm")).click();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(By.id("checkoutForm:checkoutButton")).click();
        WebElement clickElement = driver.findElement(By.id("loginForm:cname"));
        clickElement.clear();
        // Enter text into the input field
        clickElement.sendKeys("DawsonConsumer");

        // Find password input field
        clickElement = driver.findElement(By.id("loginForm:password"));
        // Clear out anything currently in the field
        clickElement.clear();
        // Enter text into the input field
        clickElement.sendKeys("dawsoncollege");

        driver.findElement(By.id("loginForm:formLogin")).click();

        WebElement inputElement = driver.findElement(By.id("checkingOutForm:cname"));
        inputElement.clear();
        inputElement.sendKeys("DawsonConsumer");

        inputElement = driver.findElement(By.xpath("//*[@id=\"checkingOutForm:cardSelect:0\"]"));
        inputElement.click();

        inputElement = driver.findElement(By.id("checkingOutForm:ccnum"));
        inputElement.clear();
        inputElement.sendKeys("4024007111674510");

        inputElement = driver.findElement(By.id("checkingOutForm:expmonth"));
        inputElement.clear();
        inputElement.sendKeys("11/2021");

        // Click the submit button
        driver.findElement(By.id("checkingOutForm:confirmPurchase")).click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement resultElement = driver.findElement(By.xpath("//*[@id=\"invoiceSectionContainer\"]"));
        wait.until(ExpectedConditions.visibilityOf(resultElement));
    }

    @After
    public void shutdownTest() {
        driver.quit();
    }
}
