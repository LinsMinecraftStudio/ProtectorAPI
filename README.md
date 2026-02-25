<div align="center">
  <img alt="icon" src="icon.png">
  <h1>ProtectorAPI</h1>

  <a href="https://modrinth.com/plugin/protectorapi">
    <img alt="Modrinth" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg">
  </a>

  <a href="https://www.spigotmc.org/resources/protectorapi.126828/">
    <img alt="Spigot" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg">
  </a>

  <a href="https://hangar.papermc.io/lijinhong11/ProtectorAPI/">
    <img alt="hangar" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg">
  </a>

  <a href="https://lijinhong11.gitbook.io/protectotapi/">
    <img alt="Documentation" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/gitbook_vector.svg">
  </a>

  <p>
    An API used to docking almost all protection plugins.<br>
    Intended to provide a more standardized universal API.
  </p>
</div>

## Usage & Develop
See wiki: https://lijinhong11.gitbook.io/protectotapi/

## Building
Make sure you have installed:
1. JDK 21
2. Maven

Then run 
`mvn clean package`

## Roadmap
1. Protection range event handlers
2. More plugin supports

## Develop Examples
### Check whether a player can place
```java
Player player = ...;

boolean allow = ProtectorAPI.allowPlace(player);
```
If you need to check wehether a player can place a block at a location (safer than previous method):
```java
Player player = ...;
Block block = ...;

boolean allow = ProtectorAPI.allowPlace(player, block);
```
### Check whether a player can break
```java
Player player = ...;

boolean allow = ProtectorAPI.allowBreak(player);
```
If you need to check whether a player can break a block at a location (safer than previous method):
```java
Player player = ...;
Block block = ...;

boolean allow = ProtectorAPI.allowBreak(player, block);
```
### Check whether a player can interact
**NOTE: RedProtect didn't have a more general interaction flag, so we uses "redstone" flag to check instead.**
```java
Player player = ...;

boolean allow = ProtectorAPI.allowInteract(player);
```
If you need to whether a player can interact block at a location (safer than previous method):
```java
Player player = ...;
Block block = ...;

boolean allow = ProtectorAPI.allowInteract(player, block);
```