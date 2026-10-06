---
name: istqb-test-design
description: Conocimiento ISTQB CTFL v4.0 para análisis y diseño de pruebas - revisión estática de requerimientos, condiciones de prueba, pruebas basadas en riesgo y técnicas de caja negra (partición de equivalencia, valores límite, tabla de decisión, transición de estados), basadas en experiencia y en colaboración. Usar al analizar un requerimiento o diseñar casos de prueba.
---

# Diseño de pruebas según ISTQB

## Los 7 principios (aplicarlos siempre)
1. Las pruebas muestran la presencia de defectos, no su ausencia.
2. Las pruebas exhaustivas son imposibles → priorizar por riesgo y técnicas.
3. Probar temprano ahorra tiempo y dinero → revisar el requerimiento antes de diseñar.
4. Los defectos se agrupan → reforzar las zonas con más reglas de negocio.
5. Las pruebas se desgastan (paradoja del pesticida) → variar datos y escenarios.
6. Las pruebas dependen del contexto.
7. Falacia de ausencia de defectos → validar también que cumple la necesidad del usuario.

## Análisis: revisión estática del requerimiento
Buscar en la base de prueba (test basis) y registrar cada hallazgo:
| Tipo | Ejemplo |
|---|---|
| Ambigüedad | "rápido", "fácil", "varios", "etc." |
| Incompletitud | falta el comportamiento ante error, campos sin reglas de formato, límites sin definir |
| Inconsistencia | dos criterios que se contradicen |
| No verificable | criterio sin resultado esperado observable |
| Supuesto implícito | rol, permisos, estado previo de los datos |

Severidad de la pregunta: **Bloqueante** (no se puede definir el resultado esperado) o **Menor**
(se asume y se documenta el supuesto).

## Condiciones de prueba
Una condición = un aspecto verificable del requerimiento (ID `CP-NN`). Cada criterio de aceptación
genera ≥1 condición; incluir condiciones negativas y de borde.

## Pruebas basadas en riesgo
Nivel de riesgo = Probabilidad (1-3) × Impacto (1-3).
- 6-9 → prioridad **alta** (`@prioridad-alta`, entra en `@smoke` si es camino feliz crítico)
- 3-4 → **media**
- 1-2 → **baja**

## Técnicas de caja negra
Ver detalle y ejemplos en [references/tecnicas.md](references/tecnicas.md).
- **Partición de equivalencia (EP):** dividir entradas en clases válidas/inválidas; 1 valor por clase.
  Cobertura = particiones ejercitadas / particiones identificadas.
- **Análisis de valores límite (BVA):** para particiones ordenadas. 2 valores (límite y vecino fuera)
  o 3 valores (límite, vecino a cada lado). Indicar cuál se usa.
- **Tabla de decisión:** combinaciones de condiciones → acciones. Colapsar columnas irrelevantes.
  Cobertura = columnas factibles ejercitadas.
- **Transición de estados:** estados, eventos, transiciones válidas e inválidas. Cobertura mínima:
  todas las transiciones válidas; añadir las inválidas de riesgo alto.

## Basadas en experiencia
- **Predicción de errores (error guessing):** campos vacíos, espacios, mayúsculas, caracteres
  especiales, doble clic, retroceder en el navegador, sesión expirada.
- **Pruebas exploratorias:** sesiones con charter para lo no cubierto por técnicas formales.
- **Basadas en listas de comprobación:** usabilidad/accesibilidad básica cuando aplique.

## Enfoque colaborativo (ISTQB v4.0)
Criterios de aceptación escritos en formato escenario (Given/When/Then) = ATDD/BDD.
Cada escenario debe ser: independiente, determinista, con un único comportamiento, con resultado
esperado observable.

## Etiquetas obligatorias de trazabilidad
`@REQ-NNN` (feature) · `@TC-NNN` · `@prioridad-alta|media|baja` · `@tecnica-<nombre>` ·
`@smoke` / `@regresion` · `@negativo` cuando corresponde.
