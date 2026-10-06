package com.qa.automation.pages.orangehrm;

import com.qa.automation.pages.BasePage;
import com.qa.automation.support.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * REQ-002 — Página de inicio de sesión de OrangeHRM (/web/index.php/auth/login).
 * SPA Vue: el envío hace POST a /auth/validate y recarga /auth/login, por eso todas las
 * lecturas se hacen dentro de esperas explícitas que toleran elementos obsoletos.
 */
public class OrangeHrmLoginPage extends BasePage {

    public static final String LOGIN_PATH = "/web/index.php/auth/login";

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("button[type='submit'].orangehrm-login-button");
    private static final By ERROR_ALERT_TEXT = By.cssSelector("[role='alert'] .oxd-alert-content-text");

    /** Mensaje de validación ("Required") ubicado en el mismo grupo que el input indicado. */
    private static final String FIELD_MESSAGE_XPATH =
            "//input[@name='%s']/ancestor::div[contains(@class,'oxd-input-group')][1]"
                    + "//span[contains(@class,'oxd-input-field-error-message')]";

    private final WebDriverWait slowWait;

    public OrangeHrmLoginPage(WebDriver driver) {
        super(driver);
        this.slowWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("orangehrm.timeout", 25)));
        this.slowWait.ignoring(StaleElementReferenceException.class);
    }

    public static String loginUrl() {
        return ConfigReader.get("orangehrm.baseUrl") + LOGIN_PATH;
    }

    public void open() {
        openUrl(loginUrl());
        waitForForm();
    }

    public void login(String username, String password) {
        enterCredentials(username, password);
        submit();
    }

    public void enterCredentials(String username, String password) {
        fill(USERNAME, username);
        fill(PASSWORD, password);
    }

    public void enterPassword(String password) {
        fill(PASSWORD, password);
    }

    public void submit() {
        slowWait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
    }

    /** Espera a que aparezca la alerta de error y devuelve su texto; "" si no aparece. */
    public String errorMessage() {
        try {
            return slowWait.until(d -> {
                List<WebElement> alerts = d.findElements(ERROR_ALERT_TEXT);
                if (alerts.isEmpty() || !alerts.get(0).isDisplayed()) {
                    return null;
                }
                String text = alerts.get(0).getText().trim();
                return text.isEmpty() ? null : text;
            });
        } catch (TimeoutException e) {
            return "";
        }
    }

    /**
     * Texto del mensaje de validación bajo el campo ("Username" o "Password").
     * Espera hasta que aparezca; "" si no aparece.
     */
    public String fieldMessage(String fieldLabel) {
        By message = fieldMessageLocator(fieldLabel);
        try {
            return slowWait.until(d -> {
                List<WebElement> found = d.findElements(message);
                if (found.isEmpty() || !found.get(0).isDisplayed()) {
                    return null;
                }
                return found.get(0).getText().trim();
            });
        } catch (TimeoutException e) {
            return "";
        }
    }

    /** Lectura inmediata (sin espera) para comprobar que un campo NO tiene mensaje de validación. */
    public boolean hasFieldMessageNow(String fieldLabel) {
        return isDisplayed(fieldMessageLocator(fieldLabel));
    }

    /** true si la URL es la de login y el formulario está visible (espera explícita). */
    public boolean isOnLoginPage() {
        try {
            slowWait.until(ExpectedConditions.urlContains(LOGIN_PATH));
            waitForForm();
            return driver.getCurrentUrl().contains(LOGIN_PATH);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String passwordInputType() {
        return slowWait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD)).getDomAttribute("type");
    }

    public String passwordValue() {
        return slowWait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD)).getDomProperty("value");
    }

    private void waitForForm() {
        slowWait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        slowWait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
    }

    private void fill(By locator, String value) {
        WebElement input = slowWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        input.clear();
        if (value != null && !value.isEmpty()) {
            input.sendKeys(value);
        }
    }

    private static By fieldMessageLocator(String fieldLabel) {
        String name = switch (fieldLabel.trim().toLowerCase()) {
            case "username" -> "username";
            case "password" -> "password";
            default -> throw new IllegalArgumentException("Campo de login desconocido: " + fieldLabel);
        };
        return By.xpath(String.format(FIELD_MESSAGE_XPATH, name));
    }
}
