/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.util.ArrayList;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author joseja
 */
public class Spider {

    private final ChromeDriver driver;
    private final SpiderBrain brain;

    public Spider(ChromeDriver driver) {
        this.driver = driver;
        this.brain = new SpiderBrain(driver);
    }

    public void enterPage(String url) {
        driver.get(url);
    }

    public void jumpToPage(ChromeDriver driver, int page) {
        if (page > 1) {
            driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_IP" + page + ".htm");
        } else {
            // Already in the first page.
        }
    }

    public int getTotalPages() {
        int numCompanies = brain.scrapTotalPages();
        if (numCompanies % GlassdoorScraper.COMPANIES_PER_PAGE == 0) {
            return numCompanies / GlassdoorScraper.COMPANIES_PER_PAGE;
        } else {
            return (numCompanies / GlassdoorScraper.COMPANIES_PER_PAGE) + 1;  // Extra page.
        }
    }

    /**
     * Extract data from every company in the current page (10 by default).
     *
     * @param driver
     * @param companyIndex
     */
    public void processPage(ChromeDriver driver, int companyIndex) {
        // Get all companies links from the current page.
        ArrayList<WebElement> links = brain.scrapCompaniesLinks();
        for (int i = companyIndex; i < links.size(); i++) {
            getCompanyInfo(driver, links.get(i));
            driver.navigate().back();
            links = brain.scrapCompaniesLinks(); // Update companies links.
        }
    }

    private void getCompanyInfo(ChromeDriver driver, WebElement companyLink) {
        String companyName = companyLink.getText();
        companyLink.click(); // Enter selected company page.
        System.out.println("Scraping Company: " + companyName);
        Float totalReviews = brain.scrapTotalReviews(driver);
        saveTotalReviews(companyName, totalReviews);
        ArrayList<Float> detailedRatings = brain.scrapDetailedRatings(driver);
        saveDetailedRatings(companyName, detailedRatings);
    }

    private void saveDetailedRatings(String companyName, ArrayList<Float> ratings) {
        ArrayList<Float> info = GlassdoorScraper.companies.get(companyName);
        info.addAll(ratings);
        GlassdoorScraper.companies.put(companyName, info); // Save company ratings.
    }

    private void saveTotalReviews(String companyName, float totalReviews) {
        ArrayList<Float> info = new ArrayList<>();
        info.add(totalReviews);
        GlassdoorScraper.companies.put(companyName, info);
    }

}
