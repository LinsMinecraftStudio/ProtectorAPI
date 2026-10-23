dependencies {
    compileOnly("com.ghostchu:quickshop-api:6.2.0.10") {
        exclude("org.spigotmc")
    }
    compileOnly(project(":api"))
}
