package com.qa.automation.support;

import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestStepStarted;

/**
 * Plugin de Cucumber que registra el paso Gherkin en ejecución, para que Hooks
 * pueda nombrar cada evidencia con el texto del paso ("Paso 2 - Cuando ...").
 */
public class StepTracker implements ConcurrentEventListener {

    private static final ThreadLocal<String> CURRENT_STEP = ThreadLocal.withInitial(() -> "");
    private static final ThreadLocal<Integer> STEP_NUMBER = ThreadLocal.withInitial(() -> 0);

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, event -> STEP_NUMBER.set(0));
        publisher.registerHandlerFor(TestStepStarted.class, event -> {
            if (event.getTestStep() instanceof PickleStepTestStep step) {
                STEP_NUMBER.set(STEP_NUMBER.get() + 1);
                CURRENT_STEP.set(step.getStep().getKeyword() + step.getStep().getText());
            }
        });
    }

    /** "Paso N - <keyword> <texto>" del último paso Gherkin iniciado. */
    public static String currentStepLabel() {
        return "Paso " + STEP_NUMBER.get() + " - " + CURRENT_STEP.get().trim();
    }
}
