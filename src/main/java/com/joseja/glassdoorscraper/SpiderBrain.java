/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.util.ArrayList;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author joseja
 */
public class SpiderBrain {

    private final ChromeDriver driver;

    public SpiderBrain(ChromeDriver driver) {
        this.driver = driver;
    }

    public int scrapTotalPages() {
        return Integer.parseInt(driver
                .findElementByXPath("//*[@id=\"MainCol\"]/div[1]/header/div[1]/strong[3]")
                .getText()
                .replace(",", ""));
    }

    /**
     * Get companies links in the current page.
     *
     * @param driver
     * @param companiesLinks List of links.
     *
     * @return
     */
    public ArrayList<WebElement> scrapCompaniesLinks() {
        ArrayList<WebElement> filteredLinks = new ArrayList<>();
        ArrayList<WebElement> links = new ArrayList<>(driver.findElements(By.cssSelector("a[href*='Overview']")));
        String text;
        for (int i = 0; i < links.size(); i++) {
            text = links.get(i).getText();
            if (!text.equals("") && !text.equals("See all Reviews")) {
                filteredLinks.add(links.get(i));
            }
        }
        return filteredLinks;
    }

    public Float scrapTotalReviews(ChromeDriver driver) {
        String totalReviews = driver
                .findElementByXPath("//*[@id=\"EIHdrModule\"]/div[2]/div/a[2]/span[1]")
                .getText()
                .replace(".", "")
                .replace("k", "00");
        Float reviewNumber = Float.parseFloat(totalReviews);
        return reviewNumber;
    }

    /**
     * Enter Ratings and Trends of the selected company and save all the data.
     *
     * @param driver
     * @param company
     *
     * @return
     */
    public ArrayList<Float> scrapDetailedRatings(ChromeDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        jse.executeScript("scroll(0, 250);");
        WebElement ratingsDetailLink = driver.findElement(
                By.xpath("//*[@id=\"EmpStats\"]/div/div[1]/div[2]/span[5]"));
        ratingsDetailLink.click();
        // Scrap content.
        ArrayList<Float> ratings = new ArrayList<>();
        int ratingIndex = getFirstRatingIndex();
        for (int i = 0; i < 6; i++) {
            ratings.add(scrapRating(driver, ratingIndex));
            ratingIndex += 2;
        }
        return ratings;
    }

    private float scrapRating(ChromeDriver driver, int index) {
        float rating = Float.parseFloat(driver.findElement(By.xpath("//*[@id=\"ui-id-" + index + "\"]/div[1]/div[3]/span")).getText());
        return rating;
    }

    private int getFirstRatingIndex() {
        try {
            driver.findElement(By.xpath("//*[@id=\"ui-id-1\"]/div[1]/div[3]/span"));
        } catch (Exception ex) {
            return 3;
        }
        return 1;
    }
}
