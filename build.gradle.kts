import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar

plugins {
    base
    id("com.gradleup.shadow") version "8.3.8" apply false
    id("com.vanniktech.maven.publish") version "0.37.0" apply false
}

group = "io.github.lijinhong11"
version = "2.4.0"

allprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/repository/public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
        maven("https://repo.codemc.org/repository/maven-public/")
        maven("https://repo.codemc.io/repository/bentoboxworld/")
        maven("https://repo.onarandombox.com/dumptruckman-releases/")
        maven("https://repo.nightexpressdev.com/releases")
        maven("https://repo.william278.net/releases")
        maven("https://jitpack.io")
        maven("https://dependency.download/releases")
        maven("https://maven.reposilite.com/snapshots")
        maven("https://repo.minebench.de/")
        maven("https://epicericee.github.io/ShopChest/maven/")
        maven("https://repo.glaremasters.me/repository/maven-public/")
        maven("https://repo.glaremasters.me/repository/towny/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://nexus.sirblobman.xyz/public/")
        maven("https://repo.extendedclip.com/releases/")
        maven("https://uskyblock.ovh/maven/uskyblock/")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    dependencies {
        "compileOnly"("org.jetbrains:annotations:26.0.2-1")
    }

    // Protection plugins are only compile-time APIs. Do not resolve their
    // optional/runtime dependency trees; add a dependency explicitly if this
    // project actually references one of its types.
    configurations.named("compileOnly") {
        dependencies.withType<ExternalModuleDependency>().configureEach {
            if (group != "org.spigotmc" && group != "io.papermc.paper") {
                isTransitive = false
            }
        }
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    // These optional integrations use APIs compiled for Java 21/25. Read them
    // with JDK 25 while still emitting Java 17 bytecode for our adapters.
    if (path in setOf(
            ":block-implementations:excellentclaims",
            ":block-implementations:factionsuuid",
            ":block-implementations:landclaimplugin"
        )) {
        val integrationCompiler = extensions.getByType<JavaToolchainService>().compilerFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        tasks.withType<JavaCompile>().configureEach {
            javaCompiler.set(integrationCompiler)
        }
    }

    tasks.withType<Javadoc>().configureEach {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<ProcessResources>().configureEach {
        filteringCharset = "UTF-8"
        filesMatching(listOf("plugin.yml", "config.yml")) {
            expand("project" to mapOf("version" to project.version.toString()))
        }
    }
}

project(":api") {
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "signing")

    extensions.configure<MavenPublishBaseExtension> {
        coordinates("io.github.lijinhong11", "protectorapi-api", project.version.toString())
        publishToMavenCentral(true, DeploymentValidation.PUBLISHED)
        signAllPublications()

        pom {
            name.set("ProtectorAPI")
            description.set("An API used to docking almost all protection plugins")
            inceptionYear.set("2024")
            url.set("https://github.com/LinsMinecraftStudio/ProtectorAPI")
            licenses {
                license {
                    name.set("GNU General Public License v3.0")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("lijinhong11")
                    name.set("lijinhong11")
                    email.set("tygfhk@outlook.com")
                    url.set("https://github.com/lijinhong11")
                }
            }
            scm {
                url.set("https://github.com/LinsMinecraftStudio/ProtectorAPI")
                connection.set("scm:git:https://github.com/LinsMinecraftStudio/ProtectorAPI.git")
                developerConnection.set("scm:git:ssh://git@github.com/LinsMinecraftStudio/ProtectorAPI.git")
            }
        }
    }

    extensions.configure<SigningExtension> {
        useGpgCmd()
    }
}

project(":plugin") {
    apply(plugin = "com.gradleup.shadow")
    tasks.named<ShadowJar>("shadowJar") {
        archiveBaseName.set("ProtectorAPI-Plugin")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
    }
    tasks.named("build") { dependsOn("shadowJar") }
    tasks.named<Jar>("jar") { archiveClassifier.set("plain") }
}
