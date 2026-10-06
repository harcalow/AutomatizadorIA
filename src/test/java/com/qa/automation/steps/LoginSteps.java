package com.qa.automation.steps;

import com.qa.automation.pages.InventoryPage;
import com.qa.automation.pages.LoginPage;
import com.qa.automation.support.TestContext;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;

import static org.assertj.core.api.Assertions.assertThat;

/** REQ-001 — Steps de inicio de sesión (ejemplo). */
public class LoginSteps {

    private final LoginPage loginPage;
    private final InventoryPage inventoryPage;

    public LoginSteps(TestContext context) {
        this.loginPage = new LoginPage(context.getDriver());
        this.inventoryPage = new InventoryPage(context.getDriver());
    }

    @Dado("que el usuario está en la página de inicio de sesión")
    public void queElUsuarioEstaEnLaPaginaDeInicioDeSesion() {
        loginPage.open();
    }

    @Cuando("ingresa el usuario {string} y la contraseña {string}")
    public void ingresaElUsuarioYLaContrasena(String usuario, String clave) {
        loginPage.enterCredentials(usuario, clave);
    }

    @Y("presiona el botón de iniciar sesión")
    public void presionaElBotonDeIniciarSesion() {
        loginPage.submit();
    }

    @Entonces("debería ver la página de {string}")
    public void deberiaVerLaPaginaDe(String titulo) {
        assertThat(inventoryPage.title()).isEqualTo(titulo);
    }

    @Entonces("debería ver el mensaje de error {string}")
    public void deberiaVerElMensajeDeError(String mensaje) {
        assertThat(loginPage.errorMessage()).isEqualTo(mensaje);
    }
}
