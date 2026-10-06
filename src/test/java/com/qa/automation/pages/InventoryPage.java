package com.qa.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** REQ-001 — Catálogo de productos mostrado tras el login (ejemplo). */
public class InventoryPage extends BasePage {

    private static final By TITLE = By.cssSelector("[data-test='title']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public String title() {
        return text(TITLE);
    }
}
