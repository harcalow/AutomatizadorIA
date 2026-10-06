# Informe de cierre de pruebas — REQ-002

## 1. Resumen ejecutivo
Recomendación: **Apto con observaciones** — el inicio de sesión de OrangeHRM cumple todos los
resultados esperados automatizados (12/12) sin defectos; quedan 2 casos manuales de prioridad baja
pendientes y un riesgo de inestabilidad por la latencia de la demo pública.

## 2. Alcance probado
- Requerimiento: `requirements/REQ-002.md` — login de OrangeHRM OS 5.9 (demo pública).
- Criterios: CA-01 ingreso exitoso (Admin / admin123); CA-02 rechazo de credenciales inválidas o
  incompletas.
- Nivel: Sistema (enfoque de aceptación en el camino feliz). Tipos: funcional, seguridad básica
  (no enumeración de usuarios, acceso sin sesión), usabilidad básica.
- Técnicas: partición de equivalencia, tabla de decisión, transición de estados, predicción de
  errores, lista de comprobación.
- Fuera de alcance: recuperación de contraseña, bloqueo por intentos/captcha, logout/expiración,
  rendimiento, multi-navegador, otros roles.

## 3. Métricas
| Métrica | Valor |
|---|---|
| Condiciones identificadas / cubiertas | 14 / 14 (alta 4/4, media 7/7, baja 3/3) |
| Condiciones cubiertas por ejecución automatizada | 12 / 14 (CP-12, CP-14 manuales) |
| Casos diseñados / automatizados / manuales | 10 / 8 / 2 |
| Ejecuciones automatizadas (incluye filas de Ejemplos) | 12 |
| Pasados / fallidos / no ejecutados | 12 / 0 / 2 (manuales) |
| % éxito (automatizado) | 100 % |
| Duración de la ejecución | 2 min 16 s |
| Defectos por severidad | Ninguno |
| Intentos fallidos contra la cuenta compartida "Admin" | 4 por ejecución (según diseño) |

## 4. Defectos abiertos
| ID | Título | Severidad | Estado |
|---|---|---|---|
| — | Sin defectos | — | — |

## 5. Criterios de salida
| Criterio | Cumple |
|---|---|
| 100 % condiciones de prioridad alta ejecutadas | Sí (4/4) |
| 0 defectos críticos/altos abiertos | Sí |
| Trazabilidad REQ → CP → TC → escenario completa | Sí (`02-diseno.md`, etiquetas validadas por hook) |

## 6. Riesgos residuales y desviaciones
- **Casos manuales pendientes:** TC-011 (usuario " Admin " con espacios) y TC-012 (contraseña
  enmascarada), prioridad baja. RE-07 queda confirmado solo para la contraseña.
- **Inestabilidad del entorno:** en TC-009 el paso "debería ver el Dashboard" tardó 61,2 s pese a
  `orangehrm.timeout=25` (probablemente la carga de página de `driver.get`/redirección, no la espera
  explícita). Pasó, pero es candidato a flaky. Acción: revisar `pageLoadTimeout` y la espera del
  Dashboard en `OrangeHrmLoginSteps` / `OrangeHrmDashboardPage`.
- **Dependencia de demo pública compartida:** los datos y la cuenta Admin pueden ser modificados o
  reiniciados por terceros; un fallo de TC-003 debe verificarse manualmente antes de reportarse.
- Aviso de Selenium 4.27.0 sin soporte CDP para Chrome 154: sin impacto; actualizar Selenium.
- **Desviación del proceso:** la regla "asserts solo en `Entonces`" se exceptúa en el step
  `Dado falló un intento...` (verificación de precondición, decisión consciente).

## 7. Lecciones aprendidas / mejoras
- La configuración multi-aplicación (`orangehrm.baseUrl`, `openUrl`) permite agregar nuevos SUT sin
  afectar a REQ-001; formalizar el patrón `<app>.baseUrl` en la skill `selenium-cucumber-java`.
- Gherkin recorta espacios de las celdas de `Ejemplos`: los valores con espacios significativos deben
  ir como `Escenario` con el literal entre comillas.
- Limitar intentos fallidos contra cuentas compartidas desde el diseño funcionó (4 por ejecución,
  sin bloqueo).
