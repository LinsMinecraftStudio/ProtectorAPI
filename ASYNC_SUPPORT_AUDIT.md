# Async support audit

[简体中文](ASYNC_SUPPORT_AUDIT_CN.md)

This document records the thread-safety evidence and unresolved questions for
ProtectorAPI adapters. Decisions apply to the operations used by an adapter, not
to every API offered by its plugin.

## Scheduling contract

Adapters declare `supportsAsync(ProtectionCheck)` separately for:

- `LOOKUP`: location-based protection lookup, including region information retrieval.
- `OWNED_RANGES`: enumeration of ranges owned by an offline player, independent of location lookup.
- `CHUNK_LOOKUP`: chunk-coordinate protection lookup. Live block scans and chunk loading require the server thread.
- `FLAGS`: region flag reads or block break/place/interact permission checks.
- `GLOBAL_FLAGS`: global flag support and reads; unused by block adapters.

Unverified operations default to the server thread. An asynchronous database API,
a concurrent outer map, or Folia support alone does not establish that the whole
call path is safe on Bukkit's asynchronous scheduler.

Chunk-coordinate queries are part of both `IProtectionModule` and
`IBlockProtectionModule`; normal registration includes them automatically. Adapters
must implement `isProtected(World, int, int)` for both block and region modules.
Block modules use native protection data or a complete block scan; region modules
use a protection index or region intersection query without loading chunks.
The facade invokes module async methods,
allowing adapters to split a lookup into server-thread capture and asynchronous data access.

## Confirmed asynchronous operations

| Adapter / audited dependency | Enabled operations | Evidence                                                                                                                                                                                                                                                                |
|------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| WorldGuard 7.0.9             | `LOOKUP`, `FLAGS`, `CHUNK_LOOKUP` | [WorldGuard documents its region API as thread-safe][worldguard-regions]. This adapter's region flag reads do not access player state. Global configuration reads stay on the server thread. The chunk query checks a cuboid covering the chunk. |
| Towny 0.100.0.0              | `LOOKUP`, `CHUNK_LOOKUP` | `getTownBlock(Location)` converts coordinates and reads `TownyUniverse.townBlocks`, a `ConcurrentHashMap`. The chunk adapter enumerates every TownBlock coordinate covered by the chunk; permissions use live block types and `PlayerCacheUtil`. [Source][towny-lookup] |
| LandsAPI 7.15.20             | `LOOKUP`, `CHUNK_LOOKUP` | Uses `getLandByUnloadedChunk(World, int, int)`, explicitly documented as async-safe. Computes chunk coordinates without `Location.getChunk()`. Role checks have no equivalent guarantee; interaction reads a live block. [Source][lands-lookup]                         |
| LandClaimPlugin 3.0.0        | `LOOKUP`, `CHUNK_LOOKUP` | Uses `isChunkClaimed(String, int, int)` to read the concurrent spatial index. The Location overload loads chunks; permission resolution reads mutable `ClaimProfile`/`Role` maps and sets. [API][landclaim-api], [index][landclaim-index], [profile][landclaim-profile] |
| QuickShop-Hikari 6.1.0.0-SNAPSHOT | `LOOKUP`, `CHUNK_LOOKUP` | The project user relayed confirmation from the QuickShop-Hikari maintainer that `getShop(Location)` supports asynchronous calls. For chunk lookup, 6.1.0.0 `getShops(String, int, int)` reads concurrent maps at every level of the shop index. Player authorization (`playerAuthorize`) has no equivalent confirmation, so `FLAGS` stays on the server thread. [Source][hikari-shop] |
| GriefPrevention 16.18.2 | Staged `LOOKUP`, `CHUNK_LOOKUP` | Async overrides capture world configuration and the datastore on the server thread. Location lookup then uses synchronized `DataStore.getClaimAt` on a worker. Chunk lookup consumes the mutable `getClaims(int, int)` view and claim boundaries while holding the datastore monitor used by claim mutations. `supportsAsync` stays false because the complete synchronous methods include server-thread configuration access. [Source][gp-datastore] |

The following adapters implement chunk lookup but keep it on the server thread:

| Adapter | Chunk lookup path |
|---------|-------------------|
| FactionsUUID 0.7.0 | Reads the faction board at the chunk coordinate. Its mutable primitive maps are not verified for concurrent access. |
| PlotSquared 7.3.0 | Queries plot areas intersecting a cuboid covering the chunk. |
| BentoBox 3.0.1 | Compares every island protection bounding box in the world with the chunk bounding box. |
| GriefDefender 2.1.1 | Reads the claim manager's chunk-to-claims index using the same key as `GDClaim.getChunkHashes(boolean)` / `BlockUtil.asLong(x, z)`: X in the low 32 bits and Z in the high 32 bits. |
| RedProtect 8.1.2 | Checks region bounds against the requested chunk. |
| Residence 6.0.3.3 | Enumerates all Residence cuboid areas in the world and checks horizontal intersection with the chunk. |
| Dominion API 4.5 | Filters all dominions by world UUID and intersects their cuboids with the chunk's full build height, respecting exclusive upper bounds. No chunk load. |
| HuskClaims 1.5.10 | Checks world claimability and uses `ClaimWorld.isRegionClaimed` with a region covering the chunk's entire X/Z footprint. No chunk load; claim index concurrency is not verified. |
| IridiumSkyblock (local JAR) | Checks the island world and intersects each island's `getPos1` / `getPos2` bounds with the chunk. No chunk load; island data concurrency is not verified. |
| ShopChest 1.11.1 | Enumerates shop locations and compares world and block coordinates; never calls `Location.getChunk()`. Its shop map remains mutable. |
| BlockLocker 1.13 | Loads the chunk and checks every block across its full build height with the sign-backed protection finder, stopping at the first match. |
| LockettePro (local JAR) | Loads the chunk and checks every block with `LocketteProAPI.isProtected`, including non-container blocks such as doors. |
| ChestShop 3.12.2 | Loads the chunk and checks every block for a shop container with a connected sign. |
| Bolt 1.0.580 | Uses `findProtections(World, BoundingBox)` over the chunk's full build height. Its complete call path has not been verified for asynchronous access. |
| ChestProtectAPI 5.19.16 | Loads the Bukkit chunk, then checks `ProtectionWorld.getBlockProtectionsByChunk`. The API only exposes loaded protection chunks. |
| ExcellentClaims API 2.0.1 | Gets a Bukkit chunk and checks `ClaimRegistry.getInChunk(Chunk)`. Repository implementations have no verified concurrency contract. |
| FunnyGuilds 4.14.1-SNAPSHOT | Checks every region in the matching world for horizontal intersection with the entire chunk. |
| HuskTowns 3.1.4 | Uses its native claim `Chunk.at(x, z)` and world lookup; no Bukkit chunk load. The same consistency restrictions as location lookup apply. |
| LWCX 2.2.9-dev | Queries `PhysDB.loadProtections` for the world's inclusive X/Y/Z bounds of the chunk, without loading it. Database/JSON parser access has no verified concurrency contract. |
| NoBuildPlus 1.5.16 | Checks whether protection is enabled for the chunk's entire world. Configuration remains on the server thread. |

Lands lookup covers claimed chunks even when they are unloaded, without loading
them as a side effect. LandClaimPlugin likewise avoids loading chunks during lookup.

## Block adapters kept on the server thread

The complete synchronous `LOOKUP` and `FLAGS` methods cannot run on a worker for the adapters below.
GriefPrevention's async lookup override splits out its safe data access as described above. The reasons
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
| GriefPrevention 16.18.2             | World configuration capture and player permissions stay on the server thread. Async overrides offload synchronized data lookup only; lock contention with claim mutations/storage is still possible. [Source][gp-datastore] |
| HuskTowns 3.1.4                     | Claim caches are concurrent, but lookup also resolves towns/admin-town settings across separately updated structures; complete lookup consistency is not verified. Operation checks access player permissions and may send messages. [Cache][husk-cache], [operations][husk-operations] |
| LockettePro (local JAR)             | Both paths pass live blocks to sign-based protection checks.                                                                                                                                                                                                                            |
| LWCX 2.2.9-dev                      | `isProtectable`, protection-cache lookup and access checks receive live Bukkit blocks.                                                                                                                                                                                                  |
| NoBuildPlus 1.5.16                  | `Settings.getEnableWorldList()` exposes a mutable `ArrayList`; flag checks read mutable YAML configuration. [Source][nbp-settings]                                                                                                                                                      |
| QuickShop-Reremake 5.1.2.5-SNAPSHOT | Dependency bytecode: `getShop(Location, boolean)` calls `Location.getChunk()` even when material checking is skipped. Build checks also use player state and other protection integrations.                                                                                             |
| ShopChest 1.11.1                    | Dependency bytecode: `ShopUtils.isShop` reads an ordinary `HashMap`, also modified by shop add/remove operations.                                                                                                                                                                       |

## Verification scope

These are source/bytecode audit results for the listed versions, not live-server
concurrency tests or guarantees for arbitrary forks and future versions. Scheduling
tests cover asynchronous block lookup followed by server-thread permissions for
break, place and interact, along with ordering and errors. Chunk lookup tests cover
coordinate-based queries, registration order, the dedicated `CHUNK_LOOKUP`
capability, and no-module behavior. Module tests also cover player/block/chunk capture,
independent owned-range/global-flag capabilities, and adapter async overrides.

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
