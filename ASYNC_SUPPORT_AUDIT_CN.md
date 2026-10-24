# 异步支持审计

[English](ASYNC_SUPPORT_AUDIT.md)

本文记录 ProtectorAPI 适配器的线程安全依据和仍未解决的问题。
审计结论仅适用于适配器实际使用的操作，不代表对应插件的全部 API 都具有相同的线程安全性。

## 调度约定

适配器通过 `supportsAsync(ProtectionCheck)` 分别声明以下操作的异步支持：

- `LOOKUP`：根据位置查询保护，包括获取区域信息。
- `OWNED_RANGES`：枚举离线玩家拥有的领地，与位置查询分别声明。
- `CHUNK_LOOKUP`：基于区块坐标查询保护。实时方块扫描和区块加载必须在服务器主线程执行。
- `FLAGS`：读取区域 flag，或检查方块破坏、放置和交互权限。
- `GLOBAL_FLAGS`：查询全局 flag 支持情况并读取其值；方块适配器不使用此项。

尚未验证的操作默认在服务器主线程执行。
仅有异步数据库 API、线程安全的外层 Map，或 Folia 支持，
不足以证明整个调用链可以安全地在 Bukkit 异步调度器上执行。

区块坐标查询已并入 `IProtectionModule` 和 `IBlockProtectionModule`，正常注册模块即可参与。
方块和区域适配器都必须实现 `isProtected(World, int, int)`。
方块模块通过原生保护数据或完整逐方块扫描查询，区域模块通过保护索引或领地边界相交查询，且不加载区块。
聚合查询调用模块的 async 方法，因此适配器可以将查询拆成
主线程状态捕获与异步数据访问两个阶段。

## 已确认可异步执行的操作

| 适配器 / 已审计依赖版本 | 已启用的异步操作 | 依据 |
| --- | --- | --- |
| WorldGuard 7.0.9 | `LOOKUP`、`FLAGS`、`CHUNK_LOOKUP` | [WorldGuard 文档明确说明区域 API 是线程安全的][worldguard-regions]。此适配器读取区域 flag 时不访问玩家状态。全局配置读取仍在服务器主线程执行。chunk 查询使用覆盖整个区块的 cuboid。 |
| Towny 0.100.0.0 | `LOOKUP`、`CHUNK_LOOKUP` | `getTownBlock(Location)` 转换坐标后读取 `TownyUniverse.townBlocks`，其类型为 `ConcurrentHashMap`。chunk 适配器枚举该区块覆盖的全部 TownBlock 坐标；权限检查会访问实时方块类型和 `PlayerCacheUtil`。[源码][towny-lookup] |
| LandsAPI 7.15.20 | `LOOKUP`、`CHUNK_LOOKUP` | 使用文档明确标注可安全异步调用的 `getLandByUnloadedChunk(World, int, int)`。计算区块坐标时不调用 `Location.getChunk()`。角色检查没有同等的线程安全保证，交互检查会读取实时方块。[源码][lands-lookup] |
| LandClaimPlugin 3.0.0 | `LOOKUP`、`CHUNK_LOOKUP` | 使用 `isChunkClaimed(String, int, int)` 读取并发空间索引。接收 `Location` 的重载会加载区块；权限解析会读取可变的 `ClaimProfile` / `Role` Map 和集合。[API][landclaim-api]、[索引][landclaim-index]、[权限配置][landclaim-profile] |
| QuickShop-Hikari 6.1.0.0-SNAPSHOT | `LOOKUP`、`CHUNK_LOOKUP` | 用户转述 QuickShop-Hikari 维护者确认：`getShop(Location)` 可以异步调用。区块查询使用的 6.1.0.0 `getShops(String, int, int)` 所读取的商店索引各层均为并发 Map。玩家授权检查（`playerAuthorize`）没有同等确认，因此 `FLAGS` 仍在主线程执行。[源码][hikari-shop] |
| GriefPrevention 16.18.2 | 分阶段 `LOOKUP`、`CHUNK_LOOKUP` | async 重载在主线程捕获世界配置与 datastore，再异步执行带同步锁的 `DataStore.getClaimAt`。区块查询在 datastore 监视器内消费 `getClaims(int, int)` 返回的可变视图及领地边界，与领地修改使用同一把锁。完整同步方法仍包含主线程配置访问，因此 `supportsAsync` 返回 false。[源码][gp-datastore] |

以下适配器实现了 chunk 查询，但保持在服务器主线程执行：

| 适配器 | Chunk 查询路径 |
| --- | --- |
| FactionsUUID 0.7.0 | 在区块坐标上读取 faction board。其可变 primitive Map 尚未验证可并发访问。 |
| PlotSquared 7.3.0 | 查询与区块 cuboid 相交的 plot area。 |
| BentoBox 3.0.1 | 将该世界每个岛屿的保护 BoundingBox 与目标区块 BoundingBox 比较。 |
| GriefDefender 2.1.1 | 读取 claim manager 的区块到领地索引，key 与 `GDClaim.getChunkHashes(boolean)` / `BlockUtil.asLong(x, z)` 一致：X 占低 32 位，Z 占高 32 位。 |
| RedProtect 8.1.2 | 将区域边界与目标区块进行比较。 |
| Residence 6.0.3.3 | 枚举该世界所有 Residence 的 cuboid 范围，并检查其水平范围是否与目标区块相交。 |
| Dominion API 4.5 | 按世界 UUID 筛选全部领地，用覆盖区块完整建筑高度的 cuboid 检查相交，遵循上界不包含的边界规则。不加载区块。 |
| HuskClaims 1.5.10 | 检查世界是否允许领地，使用 `ClaimWorld.isRegionClaimed` 查询覆盖整个区块 X/Z 范围的区域。不加载区块；领地索引尚未验证可并发访问。 |
| IridiumSkyblock（本地 JAR） | 检查岛屿世界，用每个岛屿的 `getPos1` / `getPos2` 边界检查与区块的相交。不加载区块；岛屿数据尚未验证可并发访问。 |
| ShopChest 1.11.1 | 枚举商店位置并比较世界和方块坐标，不调用 `Location.getChunk()`；商店 Map 仍是可变的。 |
| BlockLocker 1.13 | 加载区块，遍历完整建筑高度内的每个方块，使用告示牌保护查找器查询，命中即结束。 |
| LockettePro（本地 JAR） | 加载区块，对每个方块调用 `LocketteProAPI.isProtected`，覆盖门等非容器方块。 |
| ChestShop 3.12.2 | 加载区块，逐方块查找有相连告示牌的商店容器。 |
| Bolt 1.0.580 | 使用覆盖整个区块建筑高度的 `findProtections(World, BoundingBox)`；完整调用链尚未验证可异步执行。 |
| ChestProtectAPI 5.19.16 | 先加载 Bukkit 区块，再查询 `ProtectionWorld.getBlockProtectionsByChunk`；API 只暴露已加载的保护区块。 |
| ExcellentClaims API 2.0.1 | 取得 Bukkit 区块后查询 `ClaimRegistry.getInChunk(Chunk)`；仓库实现尚无已验证的并发约定。 |
| FunnyGuilds 4.14.1-SNAPSHOT | 遍历匹配世界的区域，检查水平范围是否与整个区块相交。 |
| HuskTowns 3.1.4 | 使用原生领地 `Chunk.at(x, z)` 和世界查询，不加载 Bukkit 区块；沿用位置查询的一致性限制。 |
| LWCX 2.2.9-dev | 使用 `PhysDB.loadProtections` 查询该世界区块的 X/Y/Z 闭区间，不加载区块；数据库及 JSON 解析器访问尚未验证可并发执行。 |
| NoBuildPlus 1.5.16 | 检查区块所在世界是否启用保护；配置访问保留在主线程。 |

Lands 查询可以覆盖已认领但未加载的区块，并且不会因查询而加载区块。
LandClaimPlugin 查询同样避免加载区块。

## 保留在服务器主线程执行的方块适配器

以下适配器的完整同步 `LOOKUP` 和 `FLAGS` 方法不能直接移到工作线程。
GriefPrevention 的 async 查询按上文所述分阶段执行，将安全的数据访问放到工作线程。
依据中区分了已知不安全的访问和尚未完整验证的调用链。

| 适配器 / 已审计依赖版本 | 依据 / 尚未解决的限制 |
| --- | --- |
| BlockLocker 1.13 | 查找保护时访问实时 Bukkit 方块和基于告示牌的保护；权限检查还会创建玩家档案。 |
| Bolt 1.0.580 | 适配器在 `isProtected` 和 `canAccess` 两条调用链中都会获取实时 Bukkit 方块。 |
| ChestProtectAPI 5.19.16 | 两条调用链都会先读取实时方块材质，再查询保护或信任关系。 |
| ChestShop 3.12.2 | 读取容器及相连告示牌的状态；权限检查还会访问 Bukkit 玩家对象。 |
| ExcellentClaims API 2.0.1 | 源码 JAR：`ClaimRegistry` 遍历可变的仓库列表；`ClaimRepository` 和 `ClaimPermissionAPI` 未提供线程安全约定。仓库实现尚未验证。 |
| FactionsUUID 0.7.0 | 依赖字节码：`MemoryBoard.worldTrackers` 使用 `Object2ObjectOpenHashMap`，`WorldTracker.chunkToFaction` 使用 `Long2IntOpenHashMap`，访问时没有同步保护。 |
| FunnyGuilds 4.14.1-SNAPSHOT | 依赖字节码：`RegionManager` 的外层 Map 支持并发访问，但内部值为可变的 `HashSet`。建造权限检查还会创建 Bukkit 事件或读取方块状态。 |
| GriefPrevention 16.18.2 | 世界配置捕获与玩家权限检查保留在主线程。async 重载仅异步执行受同步锁保护的数据查询；仍可能与领地修改及存储操作发生锁竞争。[源码][gp-datastore] |
| HuskTowns 3.1.4 | 领地缓存支持并发访问，但查询还会跨多个独立更新的数据结构解析城镇及管理员城镇设置；完整查询的一致性尚未验证。操作权限检查会访问玩家权限，并可能发送消息。[缓存][husk-cache]、[操作检查][husk-operations] |
| LockettePro（本地 JAR） | 两条调用链都会把实时方块传给基于告示牌的保护检查。 |
| LWCX 2.2.9-dev | `isProtectable`、保护缓存查询和访问权限检查都接收实时 Bukkit 方块。 |
| NoBuildPlus 1.5.16 | `Settings.getEnableWorldList()` 暴露可变的 `ArrayList`；flag 检查会读取可变的 YAML 配置。[源码][nbp-settings] |
| QuickShop-Reremake 5.1.2.5-SNAPSHOT | 依赖字节码：即使跳过材质检查，`getShop(Location, boolean)` 仍会调用 `Location.getChunk()`。建造权限检查还会访问玩家状态和其他保护插件集成。 |
| ShopChest 1.11.1 | 依赖字节码：`ShopUtils.isShop` 读取普通的 `HashMap`，添加或删除商店时也会修改该 Map。 |

## 验证范围

以上结论来自对所列版本的源码或字节码审计，并非真实服务器上的并发测试结果，
也不构成对任意分支版本或未来版本的保证。

调度测试覆盖方块保护的异步查询，以及查询后切回服务器主线程进行破坏、放置和交互权限检查，
同时验证检查顺序和异常处理。
Chunk 查询测试覆盖基于坐标的查询、注册顺序、独立的 `CHUNK_LOOKUP` 能力声明，
以及没有 chunk 模块时的行为。
模块测试还覆盖玩家、方块、区块状态捕获，独立的所属领地/全局 flag 能力声明，以及适配器 async 重载。

为其他操作启用异步支持前，应验证完整调用链，包括 Bukkit 访问、集合修改和第三方权限提供者。
支持异步查询不代表支持异步权限检查，也不代表区域修改是线程安全的。

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
