# Async support audit

This document records the thread-safety evidence and unresolved questions for
ProtectorAPI adapters. Decisions apply to the operations used by an adapter, not
to every API offered by its plugin.

## Scheduling contract

Adapters declare `supportsAsync(ProtectionCheck)` separately for:

- `LOOKUP`: location-based protection lookup, including region information retrieval.
- `FLAGS`: region flag reads or block break/place/interact permission checks.
- `GLOBAL_FLAGS`: global flag support and reads; unused by block adapters.

Unverified operations default to the server thread. An asynchronous database API,
a concurrent outer map, or Folia support alone does not establish that the whole
call path is safe on Bukkit's asynchronous scheduler.

## Confirmed asynchronous operations

| Adapter / audited dependency | Enabled operations | Evidence                                                                                                                                                                                                                                                                |
|------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| WorldGuard 7.0.9             | `LOOKUP`, `FLAGS`  | [WorldGuard documents its region API as thread-safe][worldguard-regions]. This adapter's region flag reads do not access player state. Global configuration reads stay on the server thread.                                                                            |
| Towny 0.100.0.0              | `LOOKUP`           | `getTownBlock(Location)` converts coordinates and reads `TownyUniverse.townBlocks`, a `ConcurrentHashMap`. Permissions use live block types and `PlayerCacheUtil`. [Source][towny-lookup]                                                                               |
| LandsAPI 7.15.20             | `LOOKUP`           | Uses `getLandByUnloadedChunk(World, int, int)`, explicitly documented as async-safe. Computes chunk coordinates without `Location.getChunk()`. Role checks have no equivalent guarantee; interaction reads a live block. [Source][lands-lookup]                         |
| LandClaimPlugin 3.0.0        | `LOOKUP`           | Uses `isChunkClaimed(String, int, int)` to read the concurrent spatial index. The Location overload loads chunks; permission resolution reads mutable `ClaimProfile`/`Role` maps and sets. [API][landclaim-api], [index][landclaim-index], [profile][landclaim-profile] |

Lands lookup covers claimed chunks even when they are unloaded, without loading
them as a side effect. LandClaimPlugin likewise avoids loading chunks during lookup.
Coordinate conversion uses `getBlockX() >> 4` / `getBlockZ() >> 4`, including negative
coordinates.

## Block adapters kept on the server thread

Both `LOOKUP` and `FLAGS` remain synchronous for the adapters below. The reasons
distinguish known unsafe access from paths that have not been fully verified.

| Adapter / audited dependency        | Evidence / unresolved restriction                                                                                                                                                                                                                                                       |
|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| BlockLocker 1.13                    | Protection finding uses a live Bukkit block and sign-backed protection; permission checks also create a player profile.                                                                                                                                                                 |
| Bolt 1.0.580                        | Adapter obtains live Bukkit blocks for both `isProtected` and `canAccess`.                                                                                                                                                                                                              |
| ChestProtectAPI 5.19.16             | Both paths read the live block material before looking up protection/trust.                                                                                                                                                                                                             |
| ChestShop 3.12.2                    | Reads container and connected sign state; permission checks also use Bukkit players.                                                                                                                                                                                                    |
| ExcellentClaims API 2.0.1           | Source JAR: `ClaimRegistry` iterates a mutable repository list; `ClaimRepository` and `ClaimPermissionAPI` provide no thread-safety contract. Repository implementations are not verified.                                                                                              |
| FactionsUUID 0.7.0                  | Dependency bytecode: `MemoryBoard.worldTrackers` uses `Object2ObjectOpenHashMap`; `WorldTracker.chunkToFaction` uses `Long2IntOpenHashMap`, without synchronized access.                                                                                                                |
| FunnyGuilds 4.14.1-SNAPSHOT         | Dependency bytecode: `RegionManager` has a concurrent outer map but mutable inner `HashSet` values. Build checks also create Bukkit events/read block state.                                                                                                                            |
| GriefPrevention 16.18.2             | `DataStore.getClaimAt` is synchronized, but shares its monitor with claim mutations/storage operations; moving it off-thread does not remove main-thread lock contention. No broad async opt-in; player permissions read mutable claim permissions. [Source][gp-datastore]              |
| HuskTowns 3.1.4                     | Claim caches are concurrent, but lookup also resolves towns/admin-town settings across separately updated structures; complete lookup consistency is not verified. Operation checks access player permissions and may send messages. [Cache][husk-cache], [operations][husk-operations] |
| LockettePro (local JAR)             | Both paths pass live blocks to sign-based protection checks.                                                                                                                                                                                                                            |
| LWCX 2.2.9-dev                      | `isProtectable`, protection-cache lookup and access checks receive live Bukkit blocks.                                                                                                                                                                                                  |
| NoBuildPlus 1.5.16                  | `Settings.getEnableWorldList()` exposes a mutable `ArrayList`; flag checks read mutable YAML configuration. [Source][nbp-settings]                                                                                                                                                      |
| QuickShop-Hikari 6.1.0.0-SNAPSHOT   | 6.1.0.0 source: `getShop(Location)` can read block material depending on configuration. Forcing `skipShopableChecking` would change the check's semantics. [Source][hikari-shop]                                                                                                        |
| QuickShop-Reremake 5.1.2.5-SNAPSHOT | Dependency bytecode: `getShop(Location, boolean)` calls `Location.getChunk()` even when material checking is skipped. Build checks also use player state and other protection integrations.                                                                                             |
| ShopChest 1.11.1                    | Dependency bytecode: `ShopUtils.isShop` reads an ordinary `HashMap`, also modified by shop add/remove operations.                                                                                                                                                                       |

## Verification scope

These are source/bytecode audit results for the listed versions, not live-server
concurrency tests or guarantees for arbitrary forks and future versions. Scheduling
tests cover asynchronous block lookup followed by server-thread permissions for
break, place and interact, along with ordering, errors and cancellation.

Before opting in another operation, verify the complete call path, including
Bukkit access, collection mutations and third-party permission providers. Supporting
asynchronous lookup does not imply asynchronous permission checks or thread-safe
range mutations.

[worldguard-regions]: https://worldguard.enginehub.org/en/latest/developer/regions/
[towny-lookup]: https://github.com/TownyAdvanced/Towny/blob/0.100.0.0/Towny/src/main/java/com/palmergames/bukkit/towny/TownyUniverse.java
[lands-lookup]: https://github.com/angeschossen/LandsAPI/blob/7.15.20/src/main/java/me/angeschossen/lands/api/LandsIntegration.java
[landclaim-api]: https://github.com/synkfr/LandClaimPlugin/blob/v3.0.0/src/main/java/org/ayosynk/landClaimPlugin/api/LandClaimAPIImpl.java
[landclaim-index]: https://github.com/synkfr/LandClaimPlugin/blob/v3.0.0/src/main/java/org/ayosynk/landClaimPlugin/managers/ClaimManager.java
[landclaim-profile]: https://github.com/synkfr/LandClaimPlugin/blob/v3.0.0/src/main/java/org/ayosynk/landClaimPlugin/models/ClaimProfile.java
[gp-datastore]: https://github.com/GriefPrevention/GriefPrevention/blob/16.18.2/src/main/java/me/ryanhamshire/GriefPrevention/DataStore.java
[husk-cache]: https://github.com/WiIIiam278/HuskTowns/blob/3.1.4/common/src/main/java/net/william278/husktowns/claim/ClaimWorld.java
[husk-operations]: https://github.com/WiIIiam278/HuskTowns/blob/3.1.4/common/src/main/java/net/william278/husktowns/listener/OperationHandler.java
[nbp-settings]: https://github.com/Ez4p1xEL/NoBuildPlus/blob/1.5.16/src/main/java/p1xel/nobuildplus/Storage/Settings.java
[hikari-shop]: https://github.com/QuickShop-Community/QuickShop-Hikari/blob/6.1.0.0/quickshop-bukkit/src/main/java/com/ghostchu/quickshop/shop/SimpleShopManager.java
