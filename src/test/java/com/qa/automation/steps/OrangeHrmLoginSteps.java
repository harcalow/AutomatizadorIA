package com.qa.automation.steps;

import com.qa.automation.pages.orangehrm.OrangeHrmDashboardPage;
import com.qa.automation.pages.orangehrm.OrangeHrmLoginPage;
import com.qa.automation.support.TestContext;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import org.assertj.core.api.SoftAssertions;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * REQ-002 — Steps de inicio de sesión en OrangeHRM.
 */
public class OrangeHrmLoginSteps {

    private static final String INVALID_CREDENTIALS = "Invalid credentials";
    private static final List<String> LOGIN_FIELDS = List.of("Username", "Password");

    private final TestContext context;
    private final OrangeHrmLoginPage loginPage;
    private final OrangeHrmDashboardPage dashboardPage;

    public OrangeHrmLoginSteps(TestContext context) {
        this.context = context;
        this.loginPage = new OrangeHrmLoginPage(context.getDriver());
        this.dashboardPage = new OrangeHrmDashboardPage(context.getDriver());
    }

    @Dado("que el usuario está en la página de inicio de sesión de OrangeHRM")
    public void queElUsuarioEstaEnLaPaginaDeInicioDeSesionDeOrangeHrm() {
        loginPage.open();
    }

    @Dado("que el usuario no ha iniciado sesión en OrangeHRM")
    public void queElUsuarioNoHaIniciadoSesionEnOrangeHrm() {
        dashboardPage.clearSession();
    }

    @Dado("falló un intento de inicio de sesión en OrangeHRM con el usuario {string} y la contraseña {string}")
    public void fallaUnIntentoDeInicioDeSesion(String usuario, String clave) {
        submitCredentials(usuario, clave);
        // Guarda de precondición: el estado "Login con error" debe alcanzarse antes del reintento (TC-009).
        assertThat(loginPage.errorMessage())
                .as("Precondición: el intento fallido debe mostrar '%s' antes de reintentar", INVALID_CREDENTIALS)
                .isEqualTo(INVALID_CREDENTIALS);
    }

    @Cuando("inicia sesión en OrangeHRM con el usuario {string} y la contraseña {string}")
    public void iniciaSesionEnOrangeHrm(String usuario, String clave) {
        submitCredentials(usuario, clave);
    }

    @Cuando("accede directamente a la URL del Dashboard de OrangeHRM")
    public void accedeDirectamenteALaUrlDelDashboard() {
        dashboardPage.openDirectly();
    }

    @Cuando("escribe la contraseña {string} en el formulario de OrangeHRM")
    public void escribeLaContrasenaEnElFormulario(String clave) {
        loginPage.enterPassword(clave);
    }

    @Entonces("debería ver el Dashboard de OrangeHRM")
    public void deberiaVerElDashboardDeOrangeHrm() {
        String url = dashboardPage.waitForDashboardUrl();
        assertThat(url).as("URL tras iniciar sesión").contains(OrangeHrmDashboardPage.DASHBOARD_PATH);
        assertThat(dashboardPage.headerTitle()).as("Encabezado de la página tras iniciar sesión").isEqualTo("Dashboard");
    }

    @Entonces("OrangeHRM debería mostrar el mensaje de error {string}")
    public void orangeHrmDeberiaMostrarElMensajeDeError(String mensaje) {
        assertThat(loginPage.errorMessage())
                .as("Mensaje de error en la página de login (URL actual: %s)", loginPage.currentUrl())
                .isEqualTo(mensaje);
    }

    @Entonces("OrangeHRM debería marcar como {string} los campos {string}")
    public void orangeHrmDeberiaMarcarLosCampos(String mensaje, String campos) {
        List<String> esperados = Arrays.stream(campos.split(","))
                .map(String::trim)
                .filter(c -> !c.isEmpty())
                .toList();
        SoftAssertions soft = new SoftAssertions();
        for (String campo : esperados) {
            soft.assertThat(loginPage.fieldMessage(campo))
                    .as("Mensaje de validación bajo el campo %s", campo)
                    .isEqualTo(mensaje);
        }
        // Los campos informados no deben marcarse (los mensajes ya se renderizaron en la espera anterior).
        for (String campo : LOGIN_FIELDS) {
            if (esperados.stream().noneMatch(campo::equalsIgnoreCase)) {
                soft.assertThat(loginPage.hasFieldMessageNow(campo))
                        .as("El campo %s está informado y no debería mostrar mensaje de validación", campo)
                        .isFalse();
            }
        }
        soft.assertAll();
    }

    @Entonces("debería estar en la página de inicio de sesión de OrangeHRM")
    public void deberiaEstarEnLaPaginaDeInicioDeSesionDeOrangeHrm() {
        assertThat(loginPage.isOnLoginPage())
                .as("Se esperaba la página de login con el formulario visible, URL actual: %s", loginPage.currentUrl())
                .isTrue();
    }

    @Entonces("el campo contraseña de OrangeHRM debería ocultar los caracteres escritos")
    public void elCampoContrasenaDeberiaOcultarLosCaracteres() {
        assertThat(loginPage.passwordValue()).as("La contraseña debe haberse escrito").isNotEmpty();
        assertThat(loginPage.passwordInputType()).as("Tipo del input de contraseña").isEqualTo("password");
    }

    /** Diligencia el formulario, adjunta la evidencia previa al envío y pulsa Login. */
    private void submitCredentials(String usuario, String clave) {
        loginPage.enterCredentials(usuario, clave);
        context.evidence("Formulario diligenciado antes de enviar (usuario: \"" + usuario + "\")");
        loginPage.submit();
    }
}
