ProtectorAPI
=
An API for collecting almost all protection plugins in one place.

## Supported Plugins
[Dominion](https://www.spigotmc.org/resources/dominion.119514/)
[RedProtect](https://www.spigotmc.org/resources/redprotect-anti-grief-server-protection-region-management-mod-mobs-flag-compat-1-7-1-21.15841/)
[Residence](https://www.spigotmc.org/resources/residence-1-7-10-up-to-1-21.11480/)
[WorldGuard](https://modrinth.com/plugin/worldguard)

## Usage
### For Server Owners
1. Download the latest release from [here]()
2. Put the jar into your plugins folder
3. Start your server or restart

### For Developers
Add the dependency to your project

**Maven**
```xml
<dependency>
  <groupId>io.github.lijinhong11</groupId>
  <artifactId>protector-api</artifactId>
  <version>VERSION</version>
  <scope>provided</scope>  
</dependency>
```
**Gradle**
Groovy DSL:
```groovy
dependencies {
  compileOnly 'io.github.lijinhong11:protector-api:VERSION'
}
```
Kotlin DSL:
```kotlin
dependencies {
  compileOnly("io.github.lijinhong11:protector-api:VERSION")
}
```
