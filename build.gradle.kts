import net.masterthought.cucumber.Configuration
import net.masterthought.cucumber.ReportBuilder
import net.masterthought.cucumber.presentation.PresentationMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

buildscript {
    repositories { mavenCentral() }
    dependencies { classpath("net.masterthought:cucumber-reporting:5.8.4") }
}

plugins {
    java
}

group = "com.qa"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("io.cucumber:cucumber-bom:7.20.1"))
    testImplementation(platform("org.junit:junit-bom:5.11.3"))

    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("io.cucumber:cucumber-picocontainer")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.27.0")
    testImplementation("org.assertj:assertj-core:3.26.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Propiedades que se reenvían desde la línea de comandos a la JVM de pruebas.
// Ej: ./gradlew test -Dcucumber.filter.tags="@REQ-001 and @smoke" -Dheadless=false -Dbrowser=edge
// -Devidence=all|failed|none controla las capturas de pantalla por paso.
val forwardedProps = listOf("browser", "headless", "baseUrl", "timeout", "evidence", "orangehrm.baseUrl", "orangehrm.timeout")

tasks.test {
    useJUnitPlatform()
    systemProperty("file.encoding", "UTF-8")
    System.getProperties().forEach { (k, v) ->
        val key = k.toString()
        if (key.startsWith("cucumber.") || key in forwardedProps) {
            systemProperty(key, v)
        }
    }
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    outputs.upToDateWhen { false }
    finalizedBy("cucumberReport")
}

// Reporte HTML enriquecido (gráficos, tags, capturas por paso) y archivo histórico por requerimiento.
// Si el filtro de tags apunta a un único @REQ-NNN, la ejecución se copia a
// qa-artifacts/REQ-NNN/reportes/<fecha-hora>/ para conservar la evidencia.
tasks.register("cucumberReport") {
    group = "verification"
    description = "Genera build/reports/cucumber-html y archiva la evidencia por requerimiento."
    doLast {
        val json = layout.buildDirectory.file("reports/cucumber/cucumber.json").get().asFile
        if (!json.exists() || json.length() == 0L) {
            logger.warn("No existe cucumber.json; no se genera el reporte enriquecido.")
            return@doLast
        }
        val tags = System.getProperty("cucumber.filter.tags", "not @manual and not @wip")
        val outDir = layout.buildDirectory.dir("reports/cucumber-html").get().asFile
        outDir.deleteRecursively()

        val config = Configuration(outDir, "Automatización IA - QA ISTQB")
        config.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS)
        config.addClassifications("Fecha", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        config.addClassifications("Filtro de tags", tags)
        config.addClassifications("Navegador", System.getProperty("browser", "chrome") +
                if (System.getProperty("headless", "true") == "true") " (headless)" else "")
        config.addClassifications("Evidencia", System.getProperty("evidence", "all"))
        config.addClassifications("Java", System.getProperty("java.version"))
        config.addClassifications("SO", System.getProperty("os.name"))
        ReportBuilder(listOf(json.absolutePath), config).generateReports()
        val overview = File(outDir, "cucumber-html-reports/overview-features.html")
        logger.lifecycle("Reporte enriquecido: ${overview.toURI()}")

        val reqs = Regex("""@REQ-\d{3}""").findAll(tags).map { it.value.removePrefix("@") }.toSet()
        if (reqs.size == 1) {
            val stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
            val archive = file("qa-artifacts/${reqs.first()}/reportes/$stamp")
            File(outDir, "cucumber-html-reports").copyRecursively(File(archive, "html"))
            json.copyTo(File(archive, "cucumber.json"))
            layout.buildDirectory.file("reports/cucumber/cucumber.html").get().asFile
                .copyTo(File(archive, "cucumber-nativo.html"))
            logger.lifecycle("Evidencia archivada: ${File(archive, "html/overview-features.html").toURI()}")
        }
    }
}
