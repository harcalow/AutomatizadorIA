# language: es
# Trazabilidad: requirements/REQ-002.md -> qa-artifacts/REQ-002/02-diseno.md
# Intentos fallidos contra la cuenta compartida "Admin" por ejecución (escenarios automatizados): 4
#   TC-005 (filas "usuario válido y contraseña incorrecta" y "contraseña en mayúsculas"), TC-006, TC-009.
#   El resto de negativos usa usuarios inexistentes o se detiene en la validación de campos obligatorios.
@REQ-002 @login @orangehrm
Característica: Inicio de sesión en OrangeHRM
  Como administrador de OrangeHRM
  Quiero iniciar sesión con mis credenciales
  Para acceder al Dashboard de la aplicación

  @TC-003 @smoke @prioridad-alta @tecnica-particion-equivalencia @tecnica-tabla-decision
  Escenario: Inicio de sesión exitoso con credenciales válidas del administrador
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "Admin" y la contraseña "admin123"
    Entonces debería ver el Dashboard de OrangeHRM

  @TC-004 @regresion @prioridad-media @tecnica-prediccion-errores
  Escenario: Inicio de sesión exitoso con el usuario escrito en minúsculas
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "admin" y la contraseña "admin123"
    Entonces debería ver el Dashboard de OrangeHRM

  @TC-005 @regresion @negativo @prioridad-alta @tecnica-tabla-decision @tecnica-particion-equivalencia @tecnica-prediccion-errores
  Esquema del escenario: Inicio de sesión rechazado por credenciales inválidas - <condicion>
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "<usuario>" y la contraseña "<clave>"
    Entonces OrangeHRM debería mostrar el mensaje de error "Invalid credentials"
    Y debería estar en la página de inicio de sesión de OrangeHRM

    Ejemplos:
      | condicion                                 | usuario                | clave        |
      | usuario válido y contraseña incorrecta    | Admin                  | wrongPass123 |
      | usuario inexistente con contraseña válida | usuario_inexistente_qa | admin123     |
      | contraseña en mayúsculas                  | Admin                  | ADMIN123     |

  @TC-006 @regresion @negativo @prioridad-media @tecnica-prediccion-errores
  Escenario: Inicio de sesión rechazado con la contraseña rodeada de espacios
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "Admin" y la contraseña " admin123 "
    Entonces OrangeHRM debería mostrar el mensaje de error "Invalid credentials"
    Y debería estar en la página de inicio de sesión de OrangeHRM

  @TC-007 @regresion @negativo @prioridad-media @tecnica-tabla-decision
  Esquema del escenario: Inicio de sesión impedido por campos obligatorios vacíos - <condicion>
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "<usuario>" y la contraseña "<clave>"
    Entonces OrangeHRM debería marcar como "Required" los campos "<campos_vacios>"
    Y debería estar en la página de inicio de sesión de OrangeHRM

    Ejemplos:
      | condicion                            | usuario | clave    | campos_vacios      |
      | usuario vacío y contraseña informada |         | admin123 | Username           |
      | usuario informado y contraseña vacía | Admin   |          | Password           |
      | usuario y contraseña vacíos          |         |          | Username, Password |

  @TC-008 @regresion @negativo @prioridad-baja @tecnica-prediccion-errores
  Escenario: Inicio de sesión impedido cuando el usuario contiene solo espacios
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario "   " y la contraseña "admin123"
    Entonces OrangeHRM debería marcar como "Required" los campos "Username"
    Y debería estar en la página de inicio de sesión de OrangeHRM

  @TC-009 @regresion @prioridad-media @tecnica-transicion-estados
  Escenario: Inicio de sesión exitoso después de un intento fallido
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Y falló un intento de inicio de sesión en OrangeHRM con el usuario "Admin" y la contraseña "wrongPass456"
    Cuando inicia sesión en OrangeHRM con el usuario "Admin" y la contraseña "admin123"
    Entonces debería ver el Dashboard de OrangeHRM

  @TC-010 @regresion @negativo @prioridad-media @tecnica-transicion-estados
  Escenario: Acceso directo al Dashboard sin sesión redirige al inicio de sesión
    Dado que el usuario no ha iniciado sesión en OrangeHRM
    Cuando accede directamente a la URL del Dashboard de OrangeHRM
    Entonces debería estar en la página de inicio de sesión de OrangeHRM

  @TC-011 @manual @regresion @negativo @prioridad-baja @tecnica-prediccion-errores
  Escenario: Inicio de sesión rechazado con el usuario rodeado de espacios
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando inicia sesión en OrangeHRM con el usuario " Admin " y la contraseña "admin123"
    Entonces OrangeHRM debería mostrar el mensaje de error "Invalid credentials"
    Y debería estar en la página de inicio de sesión de OrangeHRM

  @TC-012 @manual @regresion @prioridad-baja @tecnica-lista-comprobacion
  Escenario: La contraseña escrita se muestra enmascarada
    Dado que el usuario está en la página de inicio de sesión de OrangeHRM
    Cuando escribe la contraseña "admin123" en el formulario de OrangeHRM
    Entonces el campo contraseña de OrangeHRM debería ocultar los caracteres escritos
