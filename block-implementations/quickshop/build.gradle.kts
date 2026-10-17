dependencies {
    compileOnly("org.maxgamer:QuickShop:5.1.2.5-SNAPSHOT") {
        exclude("org.spigotmc")
    }
    compileOnly("com.ghostchu:quickshop-api:6.1.0.0-SNAPSHOT") {
        exclude("org.spigotmc")
    }
    compileOnly(project(":api"))
}
