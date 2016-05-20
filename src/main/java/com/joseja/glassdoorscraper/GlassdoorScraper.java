/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author Joseja
 */
public class GlassdoorScraper {

    public static Map<String, ArrayList<Float>> companies = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        ChromeDriver driver = initializeDriver();
        // Get the webpage (spanish companies ordered by rating - Page1).
        driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_SDOR.htm");

        final int NUM_PAGES = getNumberOfPages(driver);

        for (int i = 0; i < (NUM_PAGES - 1); i++) {
            processPage(driver);
            advancePage(driver);
        }

        // Wait 10 seconds before shuting down the driver.
        TimeUnit.SECONDS.sleep(10);
        driver.quit();
    }

    private static int getNumberOfPages(ChromeDriver driver) throws NumberFormatException {
        final int NUM_COMPANIES = Integer.parseInt(
                driver.findElementByXPath("//*[@id=\"MainCol\"]/div[1]/header/div[1]/strong[3]")
                .getText()
                .replace(",", ""));
        final int COMPANIES_PER_PAGE = 10;
        int NUM_PAGES = calculateNumberOfPages(NUM_COMPANIES, COMPANIES_PER_PAGE);
        return NUM_PAGES;
    }

    private static int calculateNumberOfPages(final int NUM_COMPANIES,
                                              final int COMPANIES_PER_PAGE) {
        final int NUM_PAGES;
        if (NUM_COMPANIES % COMPANIES_PER_PAGE == 0) {
            NUM_PAGES = NUM_COMPANIES / COMPANIES_PER_PAGE;
        } else {
            NUM_PAGES = (NUM_COMPANIES / COMPANIES_PER_PAGE) + 1;
        }
        return NUM_PAGES;
    }

    private static ChromeDriver initializeDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--proxy-server=http://127.0.0.1:8118"));
        // Set path to Chrome web driver.
        System.setProperty("webdriver.chrome.driver", "C:\\MyPrograms\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver(capabilities);
        // Set implicit wait.
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    /**
     * Go to next page.
     *
     * @param driver
     */
    private static void advancePage(ChromeDriver driver) {
        WebElement nextPage = driver.findElement(By.className("next"));
        nextPage.click();  // Go to next page.
    }

    /**
     * Extract data from every company in the current page (10 by default).
     *
     * @param driver
     */
    private static void processPage(ChromeDriver driver) {
        // Get all companies links from the current page.
        ArrayList<WebElement> companiesLinks = new ArrayList<>();
        getCompaniesLinks(driver, companiesLinks);
        int linksNumber = companiesLinks.size();
        for (int i = 0; i < linksNumber; i++) {
            String companyName = companiesLinks.get(i).getText();
            beHuman(driver);
            companiesLinks.get(i).click();  // Enter selected company page.
            getDetailedRatings(driver, companyName);
            printDetailedRatings(companyName);
            beHuman(driver);
            driver.navigate().back();
            getCompaniesLinks(driver, companiesLinks);  // Update companies links.
        }
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
        ArrayList<Float> ratings = new ArrayList<>();
        ratings.add(getRating1(driver));
        ratings.add(getRating2(driver));
        ratings.add(getRating3(driver));
        ratings.add(getRating4(driver));
        ratings.add(getRating5(driver));
        ratings.add(getRating6(driver));
        companies.put(company, ratings);  // Save company ratings.
    }

    private static void printDetailedRatings(String company) {
        ArrayList<Float> detailedRatings = new ArrayList<>(companies.get(company));
        if (detailedRatings != null) {
            System.out.println("");
            System.out.println("Detailed ratings of -" + company + "- ");
            System.out.println("    - Overall: " + detailedRatings.get(0));
            System.out.println("    - Culture & Values: " + detailedRatings.get(1));
            System.out.println("    - Work/Life Balance: " + detailedRatings.get(2));
            System.out.println("    - Senior Management: " + detailedRatings.get(3));
            System.out.println("    - Comp & Benefits: " + detailedRatings.get(4));
            System.out.println("    - Career Opportunities: " + detailedRatings.get(5));
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
    private static void getCompaniesLinks(ChromeDriver driver,
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

    /**
     * Simulate human behavior to evade the anti-bot system.
     */
    private static void beHuman(ChromeDriver driver) {
        Random rand = new Random();
        try {
            TimeUnit.SECONDS.sleep(Math.abs(rand.nextInt() % 5));  // Wait for random seconds.
        } catch (InterruptedException ex) {
            Logger.getLogger(GlassdoorScraper.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
