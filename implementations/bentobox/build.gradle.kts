repositories {
    maven("https://repo.codemc.io/repository/bentoboxworld/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("world.bentobox:bentobox:3.0.1-SNAPSHOT")
    compileOnly(project(":api"))
}
