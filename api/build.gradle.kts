dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-20180712.012057-156")
    compileOnly("org.jetbrains:annotations:26.0.2-1")
    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-20180712.012057-156")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation("org.mockito:mockito-core:5.15.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
