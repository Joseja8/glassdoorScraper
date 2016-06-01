/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Joseja
 */
public class GlassdoorScraper {

    public static ExcelManager excel;
    public static ChromeDriver driver;
    public static TreeMap<String, ArrayList<Float>> companies;
    public static int pageIndex;
    public static int numPages;
    public static int companyIndex;

    static final int COMPANIES_PER_PAGE = 10;

    public static void main(String[] args) throws InterruptedException {
        initializeDriver();
        initializeExcelManager();
        Spider spider = new Spider(driver);
        companies = new TreeMap<>();

        // Go to the webpage (spanish companies ordered by popularity).
        spider.enterPage("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219.htm");

        try {
            numPages = spider.getTotalPages();

            pageIndex = (excel.getLastRowNum() / COMPANIES_PER_PAGE) + 1;
            companyIndex = excel.getLastRowNum() % COMPANIES_PER_PAGE;
        } catch (Exception ex) {
            Logger.getLogger(GlassdoorScraper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Webpage Security activated.");
            System.err.println("Please enter the CAPTCHA and restart");
            shutdownDriver(15);
            System.exit(0);
        }

        try {
            while (pageIndex < numPages) {
                spider.jumpToPage(driver, pageIndex);
                System.out.println("Processing  page...: " + pageIndex);
                spider.processPage(driver, companyIndex);
                pageIndex++;
            }
        } catch (Exception ex) {
            Logger.getLogger(GlassdoorScraper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error found (Webpage Security likely), saving and exiting");
            excel.save(companies);
            shutdownDriver(50);
            System.exit(0);
        }
    }

    private static void shutdownDriver(int seconds) throws InterruptedException {
        // Wait 10 seconds before shuting down the driver.
        System.out.println("Shutting down driver in " + seconds + " seconds");
        TimeUnit.SECONDS.sleep(seconds);
        driver.quit();
    }

    private static void initializeDriver() {
        // Set path to Chrome web driver.
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
        // Set implicit wait.
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        JavascriptExecutor js = ((JavascriptExecutor)driver);
        js.executeScript("window.open('','default','width=350,height=800,top=0,left‌=0')");        driver.close();
        driver.switchTo().window("default");
    }

    private static void initializeExcelManager() {
        ExcelManager newExcelManager = null;
        try {
            excel = new ExcelManager("compañias");
        } catch (IOException ex) {
            System.err.println("Excel Module error");
            Logger.getLogger(GlassdoorScraper.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
