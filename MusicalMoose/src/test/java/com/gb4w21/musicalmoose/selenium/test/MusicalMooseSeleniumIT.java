package com.gb4w21.musicalmoose.selenium.test;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
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
 *
 * @author Daniel
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


    @Test
    public void loginToIndex() throws Exception{
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
        
        wait.until(ExpectedConditions.titleIs("Musical Moose"));
    }
    
    @Test
    public void testSurveyClick() throws Exception{
        driver.get("http://localhost:8080/MusicalMoose/");

        // Wait for the page to load, timeout after 10 seconds
        WebDriverWait wait = new WebDriverWait(driver, 10);
        // Wait for the page to load, timeout after 10 seconds
        wait.until(ExpectedConditions.titleIs("Musical Moose"));
        
        // Click the login/register button
        driver.findElement(By.id("anserbtn")).click();
//        WebElement inputElement = driver.findElement(By.id("surveyForm:surveyDiv:surveyGrid:surveyPanel1:voteOutput1"));
//        wait.until(ExpectedConditions.visibilityOf(inputElement));
        
    }

    @After
    public void shutdownTest() {
        driver.quit();
    }
}
