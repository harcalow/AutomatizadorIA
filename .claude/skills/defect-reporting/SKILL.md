---
name: defect-reporting
description: Clasificación de resultados de ejecución y redacción de informes de defectos según ISTQB (CTFL v4.0, sección 5.5). Usar al analizar fallos de pruebas automatizadas o al reportar un defecto.
---

# Gestión de defectos (ISTQB)

## Terminología
Error (humano) → Defecto (en el producto) → Fallo (comportamiento observado). Un escenario rojo es un
**fallo**; hay que determinar si su causa es un defecto del producto o no.

## Clasificación de cada escenario fallido
| Clasificación | Indicios | Acción |
|---|---|---|
| **Defecto del producto** | el SUT no cumple el resultado esperado del requerimiento; reproducible manualmente | Informe de defecto `DEF-NNN` |
| **Problema del script** | locator roto, timing, step mal implementado, dato de prueba erróneo | Devolver a implementación |
| **Problema de entorno** | SUT caído, timeouts de red, driver/navegador | Reintentar 1 vez; documentar |
| **Defecto del requerimiento / test basis** | el resultado esperado era incorrecto o ambiguo | Escalar al usuario (Test Manager) |
| **Falso positivo / flaky** | pasa y falla sin cambios | Marcar `@flaky`, investigar |

Confirmar defectos del producto reproduciéndolos con Playwright MCP antes de reportarlos.

## Plantilla
Usar `docs/templates/defecto.md`. Campos obligatorios:
ID, título (qué falla + dónde), requerimiento y caso relacionados, entorno (URL, navegador, versión),
precondiciones, pasos para reproducir numerados, **resultado esperado**, **resultado actual**,
severidad, prioridad, evidencia (screenshot/log), estado = `Nuevo`.

## Severidad (impacto técnico)
- **Crítica:** bloquea funcionalidad principal, pérdida de datos, seguridad.
- **Alta:** funcionalidad importante incorrecta sin alternativa.
- **Media:** funcionalidad incorrecta con alternativa.
- **Baja:** cosmético / texto.

Prioridad (urgencia de negocio) se propone y la confirma el usuario.

## Ciclo de vida
Nuevo → Abierto → Asignado → Resuelto → Re-test → Cerrado (o Rechazado / Diferido / Reabierto).
Tras una corrección: **prueba de confirmación** (re-test del caso) + **prueba de regresión**
(`@regresion` del módulo).
