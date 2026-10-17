repositories {
    maven("https://repo.codemc.io/repository/bentoboxworld/")
}

dependencies {
    compileOnly("world.bentobox:bentobox:3.0.1-SNAPSHOT")
    compileOnly(project(":api"))
}
