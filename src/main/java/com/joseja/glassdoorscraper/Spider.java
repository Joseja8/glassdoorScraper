/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author joseja
 */
public class Spider {
    
    private final ChromeDriver driver;
    private SpiderBrain brain;
    
    public Spider(ChromeDriver driver) {
        this.driver = driver;
        this.brain = new SpiderBrain(driver);
    }
    
    public void enterPage(String url) {
        driver.get(url);
    }

    /**
     * Go to next page.
     *
     * @param driver
     */
    static void advancePage(ChromeDriver driver) {  // TODO: Useless?¿?¿ Use jump instead
        WebElement nextPage = driver.findElement(By.className("next"));
        nextPage.click(); // Go to next page.
    }

    static void jumpToPage(ChromeDriver driver, int page) {
        System.out.println("Jumping to page: " + (page + 1));
        if (page > 1) {
            driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_IP" + page + ".htm");
        }
    }

    public int getTotalPages() {
        int numCompanies = brain.findTotalPages();
        if (numCompanies % GlassdoorScraper.COMPANIES_PER_PAGE == 0) {
                return numCompanies / GlassdoorScraper.COMPANIES_PER_PAGE;
        } else {
            return (numCompanies / GlassdoorScraper.COMPANIES_PER_PAGE) + 1;  // Extra page.
        }
    }
    
    
}
