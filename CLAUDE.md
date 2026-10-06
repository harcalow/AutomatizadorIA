# Agente QA — ISTQB + BDD (Java · Cucumber · Selenium)

Proyecto para que Claude actúe como equipo de pruebas siguiendo ISTQB CTFL v4.0:
**requerimiento (chat) → análisis → feature Gherkin → automatización → ejecución → defectos → cierre.**

## Uso
- `/qa-flow <requerimiento> [url]` ejecuta el proceso completo (skill orquestadora).
- Subagentes en `.claude/agents/`: `qa-test-analyst` → `qa-test-designer` →
  `qa-automation-engineer` → `qa-test-executor`.
- Skills de conocimiento en `.claude/skills/`: `istqb-test-design`, `gherkin-writing`,
  `selenium-cucumber-java`, `defect-reporting`.

- Skills externas (instaladas con `npx skills add`, referencia complementaria): `qa-manual-istqb`,
  `cucumber-best-practices`, `selenium-cucumber-expert`, `webapp-selenium-testing`.
  **Ante conflicto mandan las skills propias y este archivo**: Gradle (no Maven), Gherkin en
  español, plantillas de `docs/templates/`, sin ExtentReports. No ejecutar sus scripts de
  scaffolding (`setup-maven-project.ps1`, `scaffold-selenium-bdd.mjs`) sobre este proyecto.

## Estructura
```
requirements/REQ-NNN.md            # requerimiento literal + aclaraciones
qa-artifacts/REQ-NNN/              # 01-analisis, 02-diseno, 03-ejecucion, 04-informe-cierre, defectos/
docs/templates/                    # plantillas ISTQB de cada artefacto
src/test/resources/features/       # REQ-NNN_<nombre>.feature (Gherkin en español)
src/test/java/com/qa/automation/   # runners, hooks, support, pages, steps
```

## Convenciones obligatorias
- Gherkin en español (`# language: es`), declarativo; etiquetas `@REQ-NNN`, `@TC-NNN`,
  `@prioridad-*`, `@tecnica-*`. El hook `.claude/hooks/validate_feature.py` las verifica.
- IDs globales y secuenciales: REQ, TC y DEF nunca se reutilizan.
- Un resultado esperado sólo cambia si el usuario corrige el requerimiento; un fallo del producto
  se reporta como defecto, nunca se "arregla" en el test.
- Page Object Model, esperas explícitas, sin `Thread.sleep`, asserts sólo en steps `Entonces`.

## Comandos
```bash
./gradlew compileTestJava
./gradlew test -Dcucumber.filter.tags="@REQ-001"
./gradlew test -Dheadless=false -Dbrowser=edge -DbaseUrl=https://otra-url
```
(PowerShell: `.\gradlew.bat`). Reporte: `build/reports/cucumber/cucumber.html`.
`REQ-001` (login de saucedemo.com) es el ejemplo de referencia.
