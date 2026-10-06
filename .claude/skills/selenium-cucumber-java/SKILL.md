---
name: selenium-cucumber-java
description: Convenciones del framework de automatización de este proyecto (Java 21, Cucumber 7, Selenium 4, JUnit 5, PicoContainer, Gradle) - Page Objects, step definitions en español, locators y ejecución. Usar al implementar o depurar steps y páginas.
---

# Framework Java + Cucumber + Selenium

## Estructura
```
src/test/java/com/qa/automation/
├── runners/CucumberTestSuite.java   # suite JUnit Platform (no tocar salvo plugins)
├── hooks/Hooks.java                  # cierre de driver + screenshot en fallo
├── support/                          # ConfigReader, DriverFactory, TestContext (DI)
├── pages/                            # BasePage + un Page Object por pantalla
└── steps/                            # una clase de steps por dominio/funcionalidad
src/test/resources/
├── features/                         # REQ-NNN_<nombre>.feature
├── config/config.properties          # baseUrl, browser, headless, timeout
└── junit-platform.properties
```

## Page Objects
- Extienden `BasePage`. Locators como `private static final By`.
- Prioridad de locators: `data-test`/`data-testid` > `id` > `name` > CSS estable > texto/rol >
  XPath relativo. **Nunca** XPath absoluto ni clases generadas (`.css-1x2y3`).
- Métodos con intención de negocio (`login(user, pass)`, `addToCart(product)`), sin asserts.
- Usar los helpers de `BasePage` (`visible`, `click`, `type`, `text`): esperas explícitas.
  **Prohibido `Thread.sleep`.**

## Steps
- Anotaciones en español: `io.cucumber.java.es.{Dado, Cuando, Entonces, Y, Pero}`.
- Constructor recibe `TestContext` (PicoContainer) y crea los Page Objects con `context.getDriver()`.
- Los asserts van **solo** en los `Entonces`, con AssertJ (`assertThat(...)`) y mensaje descriptivo
  cuando el valor no sea obvio (`.as("...")`).
- Usar Cucumber Expressions (`{string}`, `{int}`), no regex, salvo necesidad.
- Antes de crear un step, buscar si ya existe uno equivalente (Grep en `steps/`). Un step duplicado
  causa `DuplicateStepDefinitionException`.
- Compartir datos entre clases de steps con `context.put/get`.

## Descubrir locators con Playwright MCP
1. `browser_navigate` a la URL del SUT.
2. `browser_snapshot` para obtener el árbol de accesibilidad y atributos.
3. Ejecutar el flujo manualmente (`browser_type`, `browser_click`) para confirmar el comportamiento
   y los textos reales de mensajes.
4. Si hace falta, `browser_evaluate` con `document.querySelector(...)` para verificar unicidad de un
   selector CSS. Traducir el selector a `By.cssSelector` en el Page Object.
5. `browser_close` al terminar.

## Comandos
```bash
./gradlew compileTestJava                                      # compilar
./gradlew test                                                 # todo
./gradlew test -Dcucumber.filter.tags="@REQ-001"               # por requerimiento
./gradlew test -Dcucumber.filter.tags="@smoke and not @manual" # expresión de tags
./gradlew test -Dheadless=false -Dbrowser=edge                 # navegador visible / otro
```
En Windows PowerShell usar `.\gradlew.bat`.

## Reportes y evidencias
- **Enriquecido (principal):** `build/reports/cucumber-html/cucumber-html-reports/overview-features.html`
  (masterthought: gráficos, tags, capturas por paso). Lo genera la tarea `cucumberReport` al terminar `test`.
- Nativo: `build/reports/cucumber/cucumber.html` · JSON: `build/reports/cucumber/cucumber.json`
  (las capturas de `@AfterStep` están en `steps[].after[].embeddings`).
- **Historial:** si el filtro de tags contiene un único `@REQ-NNN`, la ejecución se archiva en
  `qa-artifacts/REQ-NNN/reportes/<yyyyMMdd-HHmmss>/` (html/, cucumber.json, cucumber-nativo.html).
- Capturas: `-Devidence=all` (defecto, una por paso, nombradas "Paso N - <texto>"), `failed` o `none`.
- En steps de acción que cambian de página, adjuntar la evidencia previa al envío con
  `context.evidence("Formulario diligenciado ...")` antes del clic.
- Headless usa ventana fija 1920x1080 (no `maximize()`, que la reduce).
