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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *
 * @author Joseja
 */
public class GlassdoorScraper {

    public static TreeMap<String, ArrayList<Float>> companies = new TreeMap<>();
    private static int NUM_PAGES;
    private static ExcelManager excel;

    public static void main(String[] args) throws InterruptedException, IOException {
        ChromeDriver driver = initializeDriver();
        try {
            excel = new ExcelManager("compañias");
            // Get the webpage (spanish companies ordered by rating - Page1).
            driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_SDOR.htm");

            updateNumberOfPages(driver);

            int page = (excel.getLastRowIndex()-1) / 10;
            
            System.out.println("PAGE: " + page);

            for (int i = page; i < (NUM_PAGES - 1); i++) {
                jumpToPage(driver, page);
                processPage(driver);
                advancePage(driver);
            }
        } catch (Exception exception) {
            System.out.println("Sistema de seguridad activado: Guardando estado...");
        } finally {
            boolean isSaved = excel.write(companies);
            if (isSaved) {
                System.out.println("Estado guardado correctamente");
            } else {
                System.out.println("Hubo fallos al guardar.");
            }
            // Wait 10 seconds before shuting down the driver.
            System.out.println("Terminando programa");
            TimeUnit.SECONDS.sleep(10);
            driver.quit();
        }
    }

    private static void updateNumberOfPages(ChromeDriver driver) throws NumberFormatException {
        final int NUM_COMPANIES = Integer.parseInt(
                driver.findElementByXPath("//*[@id=\"MainCol\"]/div[1]/header/div[1]/strong[3]")
                .getText()
                .replace(",", ""));
        final int COMPANIES_PER_PAGE = 10;
        calculateNumberOfPages(NUM_COMPANIES, COMPANIES_PER_PAGE);
    }

    private static void calculateNumberOfPages(final int NUM_COMPANIES,
                                               final int COMPANIES_PER_PAGE) {
        if (NUM_COMPANIES % COMPANIES_PER_PAGE == 0) {
            NUM_PAGES = NUM_COMPANIES / COMPANIES_PER_PAGE;
        } else {
            NUM_PAGES = (NUM_COMPANIES / COMPANIES_PER_PAGE) + 1;
        }
    }

    private static ChromeDriver initializeDriver() {
        // Set path to Chrome web driver.
        System.setProperty("webdriver.chrome.driver", "C:\\MyPrograms\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        // Set implicit wait.
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
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
    private static void processPage(ChromeDriver driver) throws IOException {
        // Get all companies links from the current page.
        ArrayList<WebElement> companiesLinks = new ArrayList<>();
        getCompaniesLinks(driver, companiesLinks);
        int linksNumber = companiesLinks.size();
        int currentCompany = (excel.getLastRowIndex()-1) % 10;
        for (int i = currentCompany; i < linksNumber; i++) {
            String companyName = companiesLinks.get(i).getText();
            companiesLinks.get(i).click();  // Enter selected company page.
            getReviews(driver, companyName);
            getDetailedRatings(driver, companyName);
            printDetailedRatings(companyName);
            driver.navigate().back();
            getCompaniesLinks(driver, companiesLinks);  // Update companies links.
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

    private static void jumpToPage(ChromeDriver driver, int page) {
        if (page != 0) {
            driver.get("https://www.glassdoor.com/Reviews/spain-reviews-SRCH_IL.0,5_IN219_SDOR_IP" + (page+1) + ".htm");
        }
    }
}
