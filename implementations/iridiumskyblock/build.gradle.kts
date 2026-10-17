dependencies {
    compileOnly(project(":api"))
    // Upstream no longer publishes Maven metadata. Download a Java 8-compatible
    // release JAR into lib/IridiumSkyblock.jar before compiling this module.
    compileOnly(files("lib/IridiumSkyblock.jar"))
}
