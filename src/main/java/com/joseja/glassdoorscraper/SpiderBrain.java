/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.joseja.glassdoorscraper;

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

    public int findTotalPages() {
        return Integer.parseInt(driver
                .findElementByXPath("//*[@id=\"MainCol\"]/div[1]/header/div[1]/strong[3]")
                .getText()
                .replace(",", ""));
    }
}
