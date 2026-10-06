# Análisis de prueba — REQ-002

## 1. Base de prueba
- Documento: `requirements/REQ-002.md` (CA-01, CA-02 derivados; sin criterios detallados del usuario).
- Fuente complementaria: comportamiento observable del SUT (oráculo por observación), vistazo con Playwright MCP el 2026-10-05:
  - URL: `https://opensource-demo.orangehrmlive.com/web/index.php/auth/login` — versión **OrangeHRM OS 5.9**.
  - Elementos: encabezado "Login", texto de ayuda con credenciales demo ("Username : Admin", "Password : admin123"), campo de texto `Username`, campo `Password`, botón `Login`, enlace "Forgot your password?", enlaces a redes sociales y pie de página.
- Nivel de prueba: **Sistema** (caja negra a través de la UI web), con enfoque de **Aceptación** para el camino feliz (CA-01).
- Tipos de prueba:
  - **Funcional:** autenticación válida e inválida, validación de campos obligatorios.
  - **Seguridad básica:** sensibilidad a mayúsculas de la contraseña, no enumeración de usuarios (mensaje genérico), campo contraseña enmascarado, acceso a ruta protegida sin sesión.
  - **Usabilidad básica (lista de comprobación):** mensajes de error visibles y comprensibles, reintento tras error.
  - No funcional de rendimiento/carga: no aplica (demo pública compartida).

## 2. Revisión estática
| ID | Tipo (ambigüedad, omisión, inconsistencia, no verificable) | Hallazgo | Severidad (Bloqueante/Menor) | Pregunta | Supuesto propuesto |
|---|---|---|---|---|---|
| RE-01 | No verificable | CA-01 dice "inicia sesión correctamente" sin resultado observable (página destino, elementos visibles). | Menor | ¿Cuál es la evidencia de ingreso exitoso? | Se redirige a `/web/index.php/dashboard/index`, se muestra el encabezado "Dashboard" y el menú de usuario en la barra superior. |
| RE-02 | No verificable / omisión | CA-02 dice "informa al usuario" sin texto ni ubicación del mensaje. | Menor | ¿Qué mensaje debe mostrarse ante credenciales inválidas? | Se muestra la alerta "Invalid credentials" sobre el formulario y se permanece en `/auth/login`. |
| RE-03 | Omisión | No se define la validación de campos vacíos. | Menor | ¿Cómo se informa un campo obligatorio vacío? | Mensaje "Required" bajo cada campo vacío; no se envía la autenticación ni se cambia de URL. |
| RE-04 | Ambigüedad | "Credenciales inválidas o incompletas" agrupa varias clases (usuario inexistente, contraseña errónea, campos vacíos) que pueden tener respuestas distintas. | Menor | ¿Todas las clases deben producir el mismo mensaje? | Usuario inexistente y contraseña errónea producen el **mismo** mensaje genérico ("Invalid credentials") para no permitir enumeración de usuarios; campos vacíos producen "Required". |
| RE-05 | Omisión | No se define si el **usuario** es sensible a mayúsculas. | Menor | ¿"admin" / "ADMIN" deben aceptarse como "Admin"? | El usuario **no** es sensible a mayúsculas (comportamiento conocido de OrangeHRM sobre base de datos con collation insensible): `admin` / `admin123` inicia sesión. Si en la primera ejecución el SUT lo rechaza, se registra como desviación del supuesto y se confirma con el usuario (no como defecto). |
| RE-06 | Omisión | No se define si la **contraseña** es sensible a mayúsculas. | Menor | ¿La contraseña distingue mayúsculas? | Sí, la contraseña es sensible a mayúsculas: `Admin` / `ADMIN123` es rechazado con "Invalid credentials" (práctica estándar de seguridad). |
| RE-07 | Omisión | No se define el tratamiento de espacios al inicio/fin de los valores. | Menor | ¿Se recortan (trim) los espacios del usuario y/o contraseña? | La contraseña **no** se recorta: `" admin123 "` es rechazada. El usuario con espacios alrededor (`" Admin "`) se rechaza con "Invalid credentials"; resultado a confirmar en primera ejecución (riesgo bajo). |
| RE-08 | Omisión | No se define el comportamiento si los campos contienen solo espacios. | Menor | ¿Un campo con solo espacios cuenta como vacío? | Se trata como vacío y muestra "Required" (validador de requerido con trim en el cliente). A confirmar en primera ejecución. |
| RE-09 | Supuesto implícito | Ambiente demo público y compartido: otros usuarios podrían cambiar la contraseña de Admin o el proveedor reiniciar datos. | Menor | ¿Se dispone de un ambiente propio? | Se usa la demo pública; las credenciales `Admin` / `admin123` están vigentes (publicadas en la propia página). Un fallo del camino feliz se verifica primero manualmente antes de reportarse como defecto. |
| RE-10 | Omisión | No se especifica política de bloqueo por intentos fallidos ni captcha. | Menor | ¿Existe bloqueo tras N intentos? | Fuera de alcance (ver sección 5); los negativos se limitan a pocos intentos para no afectar la cuenta compartida. |
| RE-11 | Supuesto implícito | Idioma de la interfaz no especificado. | Menor | ¿Se valida en otro idioma? | Interfaz en inglés (idioma por defecto de la demo); los textos esperados se toman en inglés. |

No se identifican preguntas bloqueantes: todos los resultados esperados pueden definirse a partir de los supuestos anteriores y del comportamiento observable del SUT.

## 3. Condiciones de prueba
Partición de equivalencia identificada:
- Username: válido existente (`Admin`) · variante de mayúsculas (`admin`) · inexistente · vacío · solo espacios · con espacios alrededor.
- Password: correcta (`admin123`) · incorrecta · variante de mayúsculas (`ADMIN123`) · vacía · con espacios alrededor.

Tabla de decisión base (colapsada): C1 Username informado (S/N) · C2 Password informado (S/N) · C3 Credenciales coinciden (S/N) → A1 ingreso a Dashboard · A2 "Invalid credentials" · A3 "Required" en campo(s) vacío(s). Columnas factibles: (N,N)→A3 ambos · (N,S)→A3 usuario · (S,N)→A3 password · (S,S,coincide)→A1 · (S,S,no coincide)→A2. Total 5 columnas.

| ID | Criterio | Condición | Positiva/Negativa | Técnica candidata | Prob. | Impacto | Riesgo | Prioridad |
|---|---|---|---|---|---|---|---|---|
| CP-01 | CA-01 | Con `Admin` / `admin123` el usuario ingresa: redirección a `/dashboard/index`, encabezado "Dashboard" visible (camino feliz crítico, candidato `@smoke`). | P | EP + Tabla de decisión (col. S,S,coincide) | 2 | 3 | 6 | Alta |
| CP-02 | CA-02 | Usuario válido (`Admin`) con contraseña incorrecta: alerta "Invalid credentials", permanece en `/auth/login`. | N | EP + Tabla de decisión (col. S,S,no coincide) | 2 | 3 | 6 | Alta |
| CP-03 | CA-02 | Usuario inexistente con cualquier contraseña: alerta "Invalid credentials" (mismo mensaje genérico que CP-02, sin enumeración de usuarios). | N | EP | 2 | 3 | 6 | Alta |
| CP-04 | CA-02 | Contraseña con variación de mayúsculas (`Admin` / `ADMIN123`): rechazada con "Invalid credentials". | N | Predicción de errores | 2 | 3 | 6 | Alta |
| CP-05 | CA-02 | Username vacío y password informado: "Required" bajo Username, no hay ingreso ni cambio de URL. | N | Tabla de decisión (col. N,S) | 2 | 2 | 4 | Media |
| CP-06 | CA-02 | Password vacío y username informado: "Required" bajo Password, no hay ingreso. | N | Tabla de decisión (col. S,N) | 2 | 2 | 4 | Media |
| CP-07 | CA-02 | Ambos campos vacíos: "Required" bajo ambos campos, no hay ingreso. | N | Tabla de decisión (col. N,N) | 2 | 2 | 4 | Media |
| CP-08 | CA-02 | Contraseña correcta con espacios alrededor (`Admin` / `" admin123 "`): rechazada con "Invalid credentials" (no se recorta). | N | Predicción de errores | 2 | 2 | 4 | Media |
| CP-09 | CA-01 | Usuario con variación de mayúsculas (`admin` / `admin123`): ingresa al Dashboard (supuesto RE-05). | P | Predicción de errores / EP | 2 | 2 | 4 | Media |
| CP-10 | CA-01, CA-02 | Tras un intento fallido (CP-02), un nuevo intento con credenciales válidas ingresa al Dashboard (transición Login-con-error → Autenticado). | P | Transición de estados | 2 | 2 | 4 | Media |
| CP-11 | CA-01 (implícito) | Acceso directo a `/web/index.php/dashboard/index` sin sesión redirige a `/auth/login`. | N | Transición de estados (transición inválida) | 1 | 3 | 3 | Media |
| CP-12 | CA-02 | Username con espacios alrededor (`" Admin "` / `admin123`): rechazado con "Invalid credentials" (supuesto RE-07). | N | Predicción de errores | 1 | 2 | 2 | Baja |
| CP-13 | CA-02 | Campos con solo espacios: tratados como vacíos, "Required" (supuesto RE-08). | N | Predicción de errores / EP | 1 | 2 | 2 | Baja |
| CP-14 | CA-01 (implícito) | El campo Password enmascara el valor ingresado (input tipo password). | P | Lista de comprobación (seguridad básica) | 1 | 2 | 2 | Baja |

Cobertura prevista:
- Tabla de decisión: 5/5 columnas factibles (CP-01, CP-02, CP-05, CP-06, CP-07).
- EP: Username 6/6 particiones; Password 5/5 particiones (cada una ejercitada al menos una vez combinada con un valor válido en el otro campo).
- Transición de estados: Sin sesión → Autenticado (CP-01), Login con error → Autenticado (CP-10), transición inválida Sin sesión → Dashboard (CP-11).
- BVA: no aplica (no hay rangos ordenados ni longitudes definidas en el requerimiento).

## 4. Datos de prueba y entorno
- **Precondiciones del entorno:**
  - SUT accesible en `https://opensource-demo.orangehrmlive.com/web/index.php/auth/login` (OrangeHRM OS 5.9), interfaz en inglés.
  - Cada escenario inicia sin sesión activa (contexto de navegador limpio: sin cookies) para garantizar independencia.
  - Conexión a internet estable; considerar latencia variable de la demo pública (esperas explícitas, no fijas).
- **Datos de prueba:**
  | Dato | Valor | Uso |
  |---|---|---|
  | Usuario válido | `Admin` | CP-01, CP-02, CP-04, CP-06, CP-08, CP-10 |
  | Contraseña válida | `admin123` | CP-01, CP-03, CP-05, CP-09, CP-10, CP-12 |
  | Contraseña incorrecta | valor no coincidente, p. ej. `wrongPass123` (variar entre ejecuciones) | CP-02, CP-10 |
  | Usuario inexistente | valor aleatorio no registrado, p. ej. `usuario_inexistente_<timestamp>` | CP-03 |
  | Contraseña con mayúsculas | `ADMIN123` | CP-04 |
  | Usuario con mayúsculas | `admin` | CP-09 |
  | Valores con espacios | `" admin123 "`, `" Admin "` | CP-08, CP-12 |
  | Solo espacios | `"   "` | CP-13 |
  | Vacío | `""` | CP-05, CP-06, CP-07 |
- Las credenciales válidas deben externalizarse en configuración/variables de entorno, no codificarse en los escenarios.
- Limitar el número total de intentos fallidos contra `Admin` por ejecución (aprox. 4: CP-02, CP-04, CP-08, CP-10) para no afectar la cuenta compartida.

## 5. Fuera de alcance
- **"Forgot your password?"**: flujo de recuperación distinto al login; no solicitado y podría generar solicitudes de restablecimiento sobre la cuenta compartida.
- **Bloqueo por intentos fallidos / fuerza bruta / captcha**: la demo es compartida; provocar un bloqueo afectaría a la cuenta Admin de todos los usuarios. Política no definida en el requerimiento.
- **Cierre de sesión, expiración de sesión y "recordarme"**: no forman parte del requerimiento (el SUT no ofrece "recordarme").
- **Pruebas de seguridad profundas** (inyección SQL/XSS, CSRF, fijación de sesión): requieren autorización y ambiente propio; no se ejecutan contra una demo pública de terceros.
- **Rendimiento y carga**: ambiente compartido de terceros; resultados no representativos y riesgo de afectar el servicio.
- **Enlaces de redes sociales y pie de página**: no relacionados con la autenticación.
- **Login con otros roles (ESS) o usuarios creados**: no hay datos provistos y la demo puede reiniciarse.
- **Longitudes máximas de campos (BVA)**: no definidas en el requerimiento ni observables en la UI; candidato a sesión exploratoria si se solicita.
- **Accesibilidad completa (WCAG) y compatibilidad multinavegador**: no solicitadas; se ejecuta en un navegador (Chromium) por defecto.
