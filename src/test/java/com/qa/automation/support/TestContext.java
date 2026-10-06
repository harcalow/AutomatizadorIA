package com.qa.automation.support;

import io.cucumber.java.Scenario;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Estado compartido por escenario. PicoContainer inyecta una instancia nueva
 * en Hooks y en cada clase de steps del mismo escenario.
 */
public class TestContext {

    /** Espera máxima para que la página quede estable antes de una captura. */
    private static final Duration SETTLE_TIMEOUT = Duration.ofSeconds(5);

    private WebDriver driver;
    private Scenario scenario;
    private final Map<String, Object> data = new HashMap<>();

    public WebDriver getDriver() {
        if (driver == null) {
            driver = DriverFactory.create();
        }
        return driver;
    }

    public boolean hasDriver() {
        return driver != null;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    /**
     * Evidencia intermedia desde un step (p. ej. formulario diligenciado antes de enviarlo).
     * Solo se adjunta con -Devidence=all (valor por defecto).
     */
    public void evidence(String name) {
        if ("all".equalsIgnoreCase(ConfigReader.get("evidence", "all"))) {
            attachScreenshot(name);
        }
    }

    /** Adjunta una captura de pantalla al reporte, sin importar el modo de evidencia. */
    public void attachScreenshot(String name) {
        if (scenario == null || driver == null) {
            return;
        }
        waitForStablePage();
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", name);
        } catch (WebDriverException e) {
            scenario.log("No se pudo capturar la evidencia '" + name + "': " + e.getMessage());
        }
    }

    /** Espera a que el documento termine de cargar y tenga contenido visible (SPA en transición). */
    private void waitForStablePage() {
        try {
            new WebDriverWait(driver, SETTLE_TIMEOUT).until(d -> (Boolean) ((JavascriptExecutor) d).executeScript(
                    "return document.readyState === 'complete' && !!document.body"
                            + " && document.body.innerText.trim().length > 0;"));
        } catch (WebDriverException ignored) { // incluye TimeoutException
            // se captura el estado actual igualmente
        }
    }

    public void put(String key, Object value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }
}
