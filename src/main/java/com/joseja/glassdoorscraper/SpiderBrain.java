/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.util.ArrayList;
import org.openqa.selenium.By;
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
        // Open detailed ratings window.
        driver.findElement(By.cssSelector("span[href*='ratingsDetails']")).click();
        // Scrap content.
        ArrayList<Float> ratings = new ArrayList<>();
        ratings.add(scrapRating1(driver));
        ratings.add(scrapRating2(driver));
        ratings.add(scrapRating3(driver));
        ratings.add(scrapRating4(driver));
        ratings.add(scrapRating5(driver));
        ratings.add(scrapRating6(driver));
        return ratings;
    }

    private float scrapRating1(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-3"));
        float rating = Float.parseFloat(element.findElement(By.cssSelector(".ratingNum.h1")).getText());
        return rating;
    }

    private float scrapRating2(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-5"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private float scrapRating3(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-7"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private float scrapRating4(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-9"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private float scrapRating5(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-11"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }

    private float scrapRating6(ChromeDriver driver) {
        WebElement element = driver.findElement(By.id("ui-id-13"));
        float rating = Float.parseFloat(element.findElement(By.className("ratingNum")).getText());
        return rating;
    }
}
