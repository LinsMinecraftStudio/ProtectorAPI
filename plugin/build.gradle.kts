import java.nio.charset.StandardCharsets
import io.github.lijinhong11.nexusmcpublisher.VersionTag

plugins {
    id("io.github.lijinhong11.nexusmcpublisher") version "1.0.6"
}

dependencies {
    implementation(project(":api"))

    implementation(project(":implementations:worldguard"))
    implementation(project(":implementations:dominion"))
    implementation(project(":implementations:griefdefender"))
    implementation(project(":implementations:huskclaims"))
    implementation(project(":implementations:iridiumskyblock"))
    implementation(project(":implementations:redprotect"))
    implementation(project(":implementations:residence"))
    implementation(project(":implementations:bentobox"))
    implementation(project(":implementations:plotsquared"))
    implementation(project(":block-implementations:blocklocker"))
    implementation(project(":block-implementations:towny"))
    implementation(project(":block-implementations:chestprotection"))
    implementation(project(":block-implementations:bolt"))
    implementation(project(":block-implementations:chestshop"))
    implementation(project(":block-implementations:factionsuuid"))
    implementation(project(":block-implementations:funnyguilds"))
    implementation(project(":block-implementations:landclaimplugin"))
    implementation(project(":block-implementations:lwcx"))
    implementation(project(":block-implementations:lands"))
    implementation(project(":block-implementations:quickshop"))
    implementation(project(":block-implementations:shopchest"))
    implementation(project(":block-implementations:lockettepro"))
    implementation(project(":block-implementations:griefprevention"))
    implementation(project(":block-implementations:husktowns"))
    implementation(project(":block-implementations:nobuildplus"))
    implementation(project(":block-implementations:excellentclaims"))
}

nexusMCPublisher {
    resourceId.set("cf71eb0b-ae23-4e7f-987d-4d947468ef98")
    versionTag.set(VersionTag.RELEASE)
    changelog.set(rootProject.file("changelog.txt").readLines(StandardCharsets.UTF_8).joinToString("\n"))
    versionTitle.set(project.version.toString())
    mcVersions.set(listOf("1.17", "1.17.1", "1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2", "1.19.3", "1.19.4", "1.20", "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6", "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11", "26.1", "26.1.1", "26.1.2", "26.2", "26.3"))
    subcategoryIds.set(listOf("paper"))

    artifact("build/libs/ProtectorAPI-Plugin-${project.version}.jar")
}

tasks.withType<Javadoc>().configureEach {
    title = "ProtectorAPI"
}