package com.qa.automation.pages.orangehrm;

import com.qa.automation.pages.BasePage;
import com.qa.automation.support.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * REQ-002 — Dashboard de OrangeHRM (/web/index.php/dashboard/index) mostrado tras el login.
 */
public class OrangeHrmDashboardPage extends BasePage {

    public static final String DASHBOARD_PATH = "/web/index.php/dashboard/index";

    private static final By HEADER = By.cssSelector(".oxd-topbar-header-breadcrumb h6");

    private final WebDriverWait slowWait;

    public OrangeHrmDashboardPage(WebDriver driver) {
        super(driver);
        this.slowWait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("orangehrm.timeout", 25)));
        this.slowWait.ignoring(StaleElementReferenceException.class);
    }

    /** Acceso directo por URL al Dashboard (sin pasar por el formulario de login). */
    public void openDirectly() {
        openUrl(ConfigReader.get("orangehrm.baseUrl") + DASHBOARD_PATH);
    }

    /** Elimina las cookies del navegador para garantizar que no hay sesión activa. */
    public void clearSession() {
        driver.manage().deleteAllCookies();
    }

    /** Espera a que la URL sea la del Dashboard; devuelve la URL actual (con o sin éxito). */
    public String waitForDashboardUrl() {
        try {
            slowWait.until(ExpectedConditions.urlContains(DASHBOARD_PATH));
        } catch (TimeoutException ignored) {
            // la aserción del step informa la URL real
        }
        return driver.getCurrentUrl();
    }

    /** Texto del encabezado de la barra superior; "" si no aparece. */
    public String headerTitle() {
        try {
            return slowWait.until(d -> {
                var headers = d.findElements(HEADER);
                if (headers.isEmpty() || !headers.get(0).isDisplayed()) {
                    return null;
                }
                String text = headers.get(0).getText().trim();
                return text.isEmpty() ? null : text;
            });
        } catch (TimeoutException e) {
            return "";
        }
    }
}
