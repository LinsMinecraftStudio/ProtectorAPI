dependencies {
    compileOnly(project(":api"))

    // Add your block protection plugin API here. Keep it compileOnly so it is
    // not bundled into ProtectorAPI and its transitive runtime dependencies are ignored.
    // compileOnly("com.example:block-protection-api:1.0.0")
}
