package com.qa.automation.hooks;

import com.qa.automation.support.ConfigReader;
import com.qa.automation.support.StepTracker;
import com.qa.automation.support.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriverException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Evidencias de ejecución adjuntas al reporte de Cucumber.
 * -Devidence=all (defecto): captura tras cada paso · failed: solo en fallos · none: sin capturas.
 */
public class Hooks {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TestContext context;
    private final String evidenceMode = ConfigReader.get("evidence", "all").toLowerCase();

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        context.setScenario(scenario);
        scenario.log("Inicio: " + LocalDateTime.now().format(TIMESTAMP)
                + " | Navegador: " + ConfigReader.get("browser", "chrome")
                + (ConfigReader.getBoolean("headless", true) ? " (headless)" : "")
                + " | Etiquetas: " + String.join(" ", scenario.getSourceTagNames()));
    }

    @AfterStep
    public void stepEvidence(Scenario scenario) {
        boolean capture = evidenceMode.equals("all") || (evidenceMode.equals("failed") && scenario.isFailed());
        if (capture) {
            context.attachScreenshot(StepTracker.currentStepLabel() + (scenario.isFailed() ? " [FALLO]" : ""));
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (context.hasDriver()) {
            try {
                scenario.log("URL final: " + context.getDriver().getCurrentUrl());
            } catch (WebDriverException ignored) {
                // el navegador pudo haberse cerrado tras un fallo
            }
            context.quitDriver();
        }
    }
}
