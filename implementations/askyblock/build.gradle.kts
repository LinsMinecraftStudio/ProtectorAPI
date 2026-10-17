dependencies {
    compileOnly(project(":api"))
    compileOnly("com.wasteofplastic:askyblock:3.0.9.4")

    // Add your protection plugin API here. Keep it compileOnly so it is not
    // bundled into ProtectorAPI and its transitive runtime dependencies are ignored.
    // compileOnly("com.example:protection-api:1.0.0")
}
