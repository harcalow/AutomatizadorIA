# Registro de ejecución — REQ-002

| Campo | Valor |
|---|---|
| Fecha | 2026-10-05 21:20 – 21:22 (inicio 21:20:38, fin 21:22:55) |
| Entorno (URL) | https://opensource-demo.orangehrmlive.com/web/index.php/auth/login (OrangeHRM OS 5.9, demo pública compartida, interfaz en inglés) |
| Navegador | chrome 154.0.8037.93 (headless), Selenium 4.27.0 |
| Comando | `.\gradlew.bat test "-Dcucumber.filter.tags=@REQ-002 and not @manual and not @wip" --console=plain` |
| Reporte | build/reports/cucumber/cucumber.html · build/reports/cucumber/cucumber.json |
| Resultado del build | BUILD SUCCESSFUL in 2m 16s (exit code 0) |
| Ejecuciones de la suite | 1 (sin re-ejecuciones: no hubo fallos) |
| Intentos fallidos contra "Admin" | 4 (TC-005 filas 1 y 3, TC-006, TC-009), según lo previsto en el diseño |

## Métricas
| Total | Pasados | Fallidos | Omitidos | % éxito | Duración |
|---|---|---|---|---|---|
| 14 (12 automatizados + 2 manuales) | 12 | 0 | 2 (manuales, no ejecutados) | 100 % de los ejecutados (12/12); 85,7 % del total planificado (12/14) | 2 min 14 s en escenarios (suma de cucumber.json) · 2 min 16 s build |

Los 5 escenarios de REQ-001 (Swag Labs) que Gradle lista como SKIPPED quedaron fuera del filtro de tags y no forman parte de esta ejecución.

## Resultados por caso
| Caso | Escenario | Resultado | Clasificación del fallo | Defecto | Observación |
|---|---|---|---|---|---|
| TC-003 | Inicio de sesión exitoso con credenciales válidas del administrador (`Admin`/`admin123`) — feature:13 | Pasado | – | – | 6,5 s. Dashboard visible. |
| TC-004 | Inicio de sesión exitoso con el usuario escrito en minúsculas (`admin`/`admin123`) — feature:19 | Pasado | – | – | 6,1 s. Confirma RE-05. |
| TC-005 fila 1 | Rechazado: usuario válido y contraseña incorrecta (`Admin`/`wrongPass123`) — feature:33 | Pasado | – | – | 5,8 s. "Invalid credentials". |
| TC-005 fila 2 | Rechazado: usuario inexistente con contraseña válida (`usuario_inexistente_qa`/`admin123`) — feature:34 | Pasado | – | – | 5,8 s. Mismo mensaje que fila 1. |
| TC-005 fila 3 | Rechazado: contraseña en mayúsculas (`Admin`/`ADMIN123`) — feature:35 | Pasado | – | – | 9,5 s (7,5 s en cargar la página de login). |
| TC-006 | Rechazado con la contraseña rodeada de espacios (`Admin`/`" admin123 "`) — feature:38 | Pasado | – | – | 5,8 s. La contraseña no se recorta. |
| TC-007 fila 1 | Impedido: usuario vacío y contraseña informada — feature:53 | Pasado | – | – | 5,3 s. "Required" bajo Username. |
| TC-007 fila 2 | Impedido: usuario informado y contraseña vacía — feature:54 | Pasado | – | – | 5,0 s. "Required" bajo Password. |
| TC-007 fila 3 | Impedido: usuario y contraseña vacíos — feature:55 | Pasado | – | – | 6,2 s. "Required" bajo ambos. |
| TC-008 | Impedido cuando el usuario contiene solo espacios (`"   "`/`admin123`) — feature:58 | Pasado | – | – | 4,4 s. Confirma RE-08 para Username. |
| TC-009 | Inicio de sesión exitoso después de un intento fallido — feature:65 | Pasado | – | – | **68,5 s**: el paso "debería ver el Dashboard de OrangeHRM" tardó 61,2 s (timeout configurado `orangehrm.timeout=25`). Pasó, pero indica latencia alta de la demo en ese momento; riesgo de inestabilidad (ver Observaciones). |
| TC-010 | Acceso directo al Dashboard sin sesión redirige al inicio de sesión — feature:72 | Pasado | – | – | 5,3 s. Redirige a `/auth/login`. |
| TC-011 | Inicio de sesión rechazado con el usuario rodeado de espacios (`" Admin "`/`admin123`) — feature:78 | No ejecutado (manual) | – | – | `@manual`, excluido por filtro. Pendiente de sesión exploratoria; RE-07 (parte usuario) sigue sin confirmar. |
| TC-012 | La contraseña escrita se muestra enmascarada — feature:85 | No ejecutado (manual) | – | – | `@manual`, excluido por filtro. Verificación visual pendiente (CP-14). |

## Supuestos del análisis confirmados por la ejecución
| Supuesto | Contenido | Estado | Evidencia |
|---|---|---|---|
| RE-01 | Ingreso correcto = redirección a `/dashboard/index` y encabezado "Dashboard" | Confirmado | TC-003, TC-004, TC-009 |
| RE-02 | Credenciales inválidas = alerta "Invalid credentials" y permanencia en `/auth/login` | Confirmado | TC-005 (3 filas), TC-006, paso previo de TC-009 |
| RE-03 | Campo obligatorio vacío = "Required" bajo el campo, sin envío ni cambio de URL | Confirmado | TC-007 (3 filas) |
| RE-04 | Usuario inexistente y contraseña errónea producen el mismo mensaje genérico (sin enumeración de usuarios) | Confirmado | TC-005 filas 1 y 2 |
| RE-05 | El usuario **no** es sensible a mayúsculas (`admin` ingresa) | Confirmado | TC-004 |
| RE-06 | La contraseña **sí** es sensible a mayúsculas (`ADMIN123` rechazada) | Confirmado | TC-005 fila 3 |
| RE-07 | Sin trim: contraseña `" admin123 "` rechazada / usuario `" Admin "` rechazado | Parcialmente confirmado: la parte de la contraseña sí; la del usuario sigue pendiente (TC-011 manual) | TC-006 |
| RE-08 | Campo con solo espacios = vacío ("Required") | Confirmado para Username (Password con solo espacios no se ejercitó) | TC-008 |
| RE-09 | Las credenciales `Admin`/`admin123` de la demo están vigentes | Confirmado en la fecha de ejecución | TC-003, TC-004, TC-009 |
| RE-10 | Bloqueo por intentos fallidos fuera de alcance | No evaluado. Dato observado: tras 4 intentos fallidos en la ejecución, el ingreso válido de TC-009 (último escenario con login) funcionó, así que no hubo bloqueo con ese volumen. | TC-009 |
| RE-11 | Interfaz en inglés | Confirmado (los textos "Invalid credentials", "Required" y "Dashboard" coincidieron) | Todos |
| CP-11 (sin RE) | Acceso directo a una ruta protegida sin sesión redirige al login | Confirmado | TC-010 |

## Observaciones
- **Sin fallos**: no se requirió clasificar fallos, reproducir con Playwright MCP ni crear informes de defecto. No se crearon archivos `DEF-NNN`; el próximo defecto que se registre en el proyecto seguirá siendo DEF-001.
- **Latencia de TC-009 (riesgo de entorno/flaky)**: la aserción del Dashboard tardó 61,2 s aunque el timeout explícito es de 25 s, lo que sugiere esperas encadenadas o un timeout de carga de página mayor en ese paso. Pasó, así que no es un fallo, pero si la demo responde aún más lento el caso podría fallar de forma intermitente. Se recomienda que Implementación revise las esperas del paso `debería ver el Dashboard de OrangeHRM` (no se modificó código de prueba).
- **Aviso de CDP**: Selenium 4.27.0 no tiene implementación de CDP para Chrome 154 ("Unable to find CDP implementation matching 154"). No afectó a los resultados; solo hay que tenerlo en cuenta si en el futuro se usan funciones de DevTools.
- **Pendiente**: ejecutar manualmente TC-011 y TC-012 para cerrar RE-07 (usuario) y CP-14.
