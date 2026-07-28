dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-20180712.012057-156")
    compileOnly(project(":api"))
    // Upstream no longer publishes Maven metadata. Download a Java 8-compatible
    // release JAR into lib/IridiumSkyblock.jar before compiling this module.
    compileOnly(files("lib/IridiumSkyblock.jar"))
}
