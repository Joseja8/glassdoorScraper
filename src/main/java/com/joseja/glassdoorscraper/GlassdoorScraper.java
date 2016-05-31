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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Joseja
 */
public class GlassdoorScraper {
    
    private static ExcelManager excel;
    private static ChromeDriver driver;
    private static TreeMap<String, ArrayList<Float>> companies = new TreeMap<>();
    
    static final int COMPANIES_PER_PAGE = 10;

    public static void main(String[] args) throws InterruptedException {
        driver = initializeDriver();
        Spider spider = new Spider(driver);
        excel  = initializeExcelManager();
        
        // Go to the webpage (spanish companies ordered by popularity).
        spider.enterPage("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219.htm");


        int numPages = spider.getTotalPages();

        int currentPage = excel.getLastRowNum() / COMPANIES_PER_PAGE;

        for (int i = currentPage; i < numPages; i++) {
            Spider.jumpToPage(driver, currentPage);
            processPage(driver);
            Spider.advancePage(driver);
        }

        boolean isSaved = excel.save(companies);
        if (isSaved) {
            System.out.println("Estado del fichero de datos guardado correctamente");
        } else {
            System.out.println("Hubo fallos al guardar.");
        }
        // Wait 10 seconds before shuting down the driver.
        System.out.println("Programa terminado");
        TimeUnit.SECONDS.sleep(10);
        driver.quit();

    }

    private static ExcelManager initializeExcelManager() {
        ExcelManager newExcelManager = null;
        try {
            newExcelManager = new ExcelManager("compañias");
            return newExcelManager;
        } catch (IOException ex) {
            System.err.println("Error en el modulo ExcelManager: ");
            Logger.getLogger(GlassdoorScraper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Fin del log.");
        }
        return newExcelManager;
    }

    private static ChromeDriver initializeDriver() {
        // Set path to Chrome web driver.
        System.setProperty("webdriver.chrome.driver",
                           "/home/joseja/Documents/NetBeansProjects/glassdoorScraper/chromedriver");
        ChromeDriver driver = new ChromeDriver();
        // Set implicit wait.
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        return driver;
    }


    /**
     * Extract data from every company in the current page (10 by default).
     *
     * @param driver
     */
    private static void processPage(ChromeDriver driver) {
        // Get all companies links from the current page.
        ArrayList<WebElement> companiesLinks = new ArrayList<>();
        findCompaniesLinks(driver, companiesLinks);
        int linksNumber = companiesLinks.size();
        int currentCompany = (excel.getLastRowNum() - 1) % 10;
        for (int i = currentCompany; i < linksNumber; i++) {
            String companyName = companiesLinks.get(i).getText();
            companiesLinks.get(i).click();  // Enter selected company page.
            getReviews(driver, companyName);
            getDetailedRatings(driver, companyName);
            printDetailedRatings(companyName);
            driver.navigate().back();
            findCompaniesLinks(driver, companiesLinks);  // Update companies links.
        }
    }

    private static void getReviews(ChromeDriver driver, String company) {
        String numberOfReviews = driver.findElementByXPath("//*[@id=\"EIHdrModule\"]/div[2]/div/a[2]/span[1]")
                .getText()
                .replace(".", "")
                .replace("k", "00");
        ArrayList<Float> info = new ArrayList<>();
        info.add(Float.parseFloat(numberOfReviews));
        companies.put(company, info);
    }

    /**
     * Enter Ratings and Trends of the selected comapany and save all the data.
     *
     * @param driver
     * @param company
     */
    private static void getDetailedRatings(ChromeDriver driver, String company) {
        // Open detailed ratings window.
        driver.findElement(By.cssSelector("span[href*='ratingsDetails']")).click();
        ArrayList<Float> info = companies.get(company);
        info.add(getRating1(driver));
        info.add(getRating2(driver));
        info.add(getRating3(driver));
        info.add(getRating4(driver));
        info.add(getRating5(driver));
        info.add(getRating6(driver));
        companies.put(company, info);  // Save company ratings.
    }

    private static void printDetailedRatings(String company) {
        ArrayList<Float> detailedRatings = new ArrayList<>(companies.get(company));
        if (detailedRatings != null) {
            System.out.println("");
            System.out.println("Detailed ratings of -" + company + "- ");
            System.out.println("    - Overall: " + detailedRatings.get(1));
            System.out.println("    - Culture & Values: " + detailedRatings.get(2));
            System.out.println("    - Work/Life Balance: " + detailedRatings.get(3));
            System.out.println("    - Senior Management: " + detailedRatings.get(4));
            System.out.println("    - Comp & Benefits: " + detailedRatings.get(5));
            System.out.println("    - Career Opportunities: " + detailedRatings.get(6));
            System.out.println("");
        }
    }

    private static float getRating1(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-3"));
        float rating = Float.parseFloat(element.findElement(By.cssSelector(".ratingNum.h1")).getText());
        return rating;
    }

    private static float getRating2(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-5"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private static float getRating3(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-7"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private static float getRating4(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-9"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private static float getRating5(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-11"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private static float getRating6(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-13"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    /**
     * Get companies links in the current page.
     *
     * @param driver
     * @param companiesLinks List of links.
     */
    private static void findCompaniesLinks(ChromeDriver driver,
                                          ArrayList companiesLinks) {
        companiesLinks.clear();
        ArrayList<WebElement> links = new ArrayList<>(driver.findElements(By.cssSelector("a[href*='Overview']")));
        String text;
        for (int i = 0; i < links.size(); i++) {
            text = links.get(i).getText();
            if (!text.equals("") && !text.equals("See all Reviews")) {
                companiesLinks.add(links.get(i));
            }
        }
    }

}
