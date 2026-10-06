---
name: gherkin-writing
description: Reglas para escribir archivos .feature en Gherkin en español (Cucumber, "# language: es") con etiquetas de trazabilidad ISTQB y steps reutilizables. Usar al crear o revisar escenarios BDD.
---

# Escritura de Gherkin (español)

## Palabras clave (`# language: es` en la primera línea)
`Característica` · `Antecedentes` · `Escenario` · `Esquema del escenario` · `Ejemplos` ·
`Dado` / `Dada` / `Dados` · `Cuando` · `Entonces` · `Y` · `Pero` · `Regla`

## Estructura del archivo
```gherkin
# language: es
# Trazabilidad: requirements/REQ-NNN.md -> qa-artifacts/REQ-NNN/02-diseno.md
@REQ-NNN @<modulo>
Característica: <capacidad de negocio>
  Como <rol>
  Quiero <acción>
  Para <beneficio>

  Antecedentes:
    Dado <precondición común a TODOS los escenarios>

  @TC-001 @smoke @prioridad-alta @tecnica-particion-equivalencia
  Escenario: <comportamiento esperado en una frase>
    Cuando ...
    Entonces ...
```
Nombre del archivo: `src/test/resources/features/REQ-NNN_<nombre_snake_case>.feature`.

## Reglas
1. **Declarativo, no imperativo:** describir el *qué* del negocio, no clics ni selectores.
   ✗ `Cuando hace clic en el botón con id "btn-42"` ✓ `Cuando confirma el pedido`
2. Un escenario = un comportamiento. Un solo bloque `Cuando` → `Entonces`.
3. `Dado` = estado, `Cuando` = acción, `Entonces` = resultado observable y verificable.
4. Escenarios independientes: no depender del orden de ejecución ni de datos de otro escenario.
5. Usar `Esquema del escenario` para EP/BVA/tablas de decisión; añadir columna `condicion` que
   documente la partición/límite/regla de cada fila y usarla en el título: `... - <condicion>`.
6. Parámetros entre comillas dobles (`"valor"`) para mapear a `{string}`; números sin comillas → `{int}`.
7. **Reutilizar steps existentes**: antes de redactar, buscar con Grep en
   `src/test/java/**/steps/*.java` las anotaciones `@Dado|@Cuando|@Entonces|@Y` y copiar su texto
   exacto si el significado coincide.
8. Máximo ~7 pasos por escenario. Si hay más, falta abstracción.
9. Sin datos sensibles reales; usar datos de prueba.
10. Lenguaje del dominio del requerimiento (mismo vocabulario que el negocio).

## Etiquetas
| Etiqueta | Dónde | Uso |
|---|---|---|
| `@REQ-NNN` | Característica | trazabilidad al requerimiento |
| `@TC-NNN` | Escenario | ID del caso en `02-diseno.md` |
| `@prioridad-alta/media/baja` | Escenario | según riesgo |
| `@tecnica-...` | Escenario | técnica ISTQB aplicada |
| `@smoke` / `@regresion` | Escenario | suite |
| `@negativo` | Escenario | caso de error |
| `@manual` | Escenario | no automatizable (se excluye de la ejecución) |
| `@wip` | Escenario | en construcción |
