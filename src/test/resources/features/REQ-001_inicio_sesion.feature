# language: es
# Trazabilidad: requirements/REQ-001.md -> qa-artifacts/REQ-001/02-diseno.md
@REQ-001 @login
Característica: Inicio de sesión en Swag Labs
  Como usuario registrado de la tienda
  Quiero iniciar sesión con mis credenciales
  Para acceder al catálogo de productos

  Antecedentes:
    Dado que el usuario está en la página de inicio de sesión

  @TC-001 @smoke @prioridad-alta @tecnica-particion-equivalencia
  Escenario: Inicio de sesión exitoso con credenciales válidas
    Cuando ingresa el usuario "standard_user" y la contraseña "secret_sauce"
    Y presiona el botón de iniciar sesión
    Entonces debería ver la página de "Products"

  @TC-002 @regresion @prioridad-alta @tecnica-tabla-decision
  Esquema del escenario: Inicio de sesión rechazado - <condicion>
    Cuando ingresa el usuario "<usuario>" y la contraseña "<clave>"
    Y presiona el botón de iniciar sesión
    Entonces debería ver el mensaje de error "<mensaje>"

    Ejemplos:
      | condicion             | usuario         | clave        | mensaje                                                                    |
      | contraseña incorrecta | standard_user   | clave_mala   | Epic sadface: Username and password do not match any user in this service |
      | usuario vacío         |                 | secret_sauce | Epic sadface: Username is required                                         |
      | contraseña vacía      | standard_user   |              | Epic sadface: Password is required                                         |
      | usuario bloqueado     | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                        |
