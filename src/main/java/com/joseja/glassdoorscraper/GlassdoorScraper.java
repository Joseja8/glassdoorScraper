/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Joseja
 */
public class GlassdoorScraper {

    public static Map<String, ArrayList<Float>> companies = new ConcurrentHashMap<>();
    
    public static void main(String[] args) throws InterruptedException {
        // Set path to Chrome web driver.
        System.setProperty("webdriver.chrome.driver", "C:\\MyPrograms\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        // Set implicit wait.
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        // Get the webpage (spanish companies ordered by rating).
        driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_SDOR.htm");
        
        // Get all companies links from the current page.
        ArrayList<WebElement> companiesLinks = new ArrayList<>();
        getCompaniesLinks(driver, companiesLinks);
        
        // Save companies names.
        ArrayList<String> companiesNames = new ArrayList<>();
        companiesLinks.stream().forEach((companyLink) -> {
            companiesNames.add(companyLink.getText());
        });
        
        // Get first company's page.
        companiesLinks.get(0).click();
        
        getDetailedRatings(driver, companiesNames.get(0));
        printDetailedRatings(companiesNames.get(0));
        
        //driver.navigate().back();
        TimeUnit.SECONDS.sleep(10);
        
        //Close the browser
        driver.quit();
    }

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
        companies.put(company, ratings);
    }
    
    private static void printDetailedRatings(String company) {
        ArrayList<Float> detailedRatings = new ArrayList<>(companies.get(company));
        if (detailedRatings != null) {
            System.out.println("");
            System.out.println("Detailed ratings of -" + company + "- ");
            System.out.println("    - Overall: "              + detailedRatings.get(0));
            System.out.println("    - Culture & Values: "     + detailedRatings.get(1));
            System.out.println("    - Work/Life Balance: "    + detailedRatings.get(2));
            System.out.println("    - Senior Management: "    + detailedRatings.get(3));
            System.out.println("    - Comp & Benefits: "      + detailedRatings.get(4));
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

    private static void getCompaniesLinks(ChromeDriver driver, ArrayList companies) {
        ArrayList<WebElement> links = new ArrayList<>(driver.findElements(By.cssSelector("a[href*='Overview']")));
        String text;
        for (int i = 0; i < links.size(); i++) {
            text = links.get(i).getText();
            if (!text.equals("") && !text.equals("See all Reviews")) {
                companies.add(links.get(i));
            }
        }
    }
}
