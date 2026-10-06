package com.qa.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** REQ-001 — Página de inicio de sesión de Swag Labs (ejemplo). */
public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("[data-test='username']");
    private static final By PASSWORD = By.cssSelector("[data-test='password']");
    private static final By LOGIN_BUTTON = By.cssSelector("[data-test='login-button']");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        open("/");
        visible(USERNAME);
    }

    public void enterCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
    }

    public void submit() {
        click(LOGIN_BUTTON);
    }

    public String errorMessage() {
        return text(ERROR_MESSAGE);
    }
}
