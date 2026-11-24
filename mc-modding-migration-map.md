# Карта классов, пакетов и методов при миграции мода

**Версия 1.0.1 (errata)** — исправления от 23.11.2026; расширять только под конкретную задачу портирования.

**Источник:** Minecraft Forge **1.7.10**
**Цели:** **1.12.2, 1.16.5, 1.20.1, 1.21.1, 26.1.2, 26.3**
**Загрузчики:** Forge / NeoForge / Fabric

> Карта собрана по официальным праймерам NeoForge, документации Forge/Fabric и MDK соответствующих версий (состояние на 23.11.2026).
> Кратко — пять эпох: **FML 1.7.10**, **RegistryEvent 1.12.2**, **DeferredRegister + Mojang-имена 1.16.5**, **форк загрузчиков и data components с 1.20.5**, **Java 25 / Renderpearl / SDL в 26.x**.

## 1. Версии и поддержка загрузчиков

С 2026 г. Minecraft Java перешёл на «календарное» версионирование: `YY.Номер_дропа.Патч`
(год → номер релиза/game drop → номер патча или hotfix; [официальное объявление](https://minecraft.net/en-us/article/minecraft-new-version-numbering-system)).
`26.1` = первый дроп 2026 г., `26.2` = второй, `26.3` = третий. Это **не** «1.22».

| Версия | Кодовое имя | Дата | Java | Forge | NeoForge | Fabric |
|---|---|---|---|---|---|---|
| 1.7.10 | — | 2013 | 6/7 | ✅ (FML) | ❌ | ❌ (почти) |
| 1.12.2 | — | 2017 | 8 | ✅ | ❌ | ⚠️ только Legacy Fabric (неофициально); официально Fabric — с 1.14 |
| 1.16.5 | — | 2021 | 8/11 | ✅ | ❌ (не как таковой) | ✅ |
| 1.20.1 | — | 2023 | 17 | ✅ | ⚠️ 20.1.x — 1:1 клон Forge, поддержка свёрнута; с 20.2 начался форк | ✅ |
| 1.21.1 | Tricky Trials | 2024 | 21 | ✅ | ✅ | ✅ |
| 26.1.2 | — (hotfix для 26.1) | 09.04.2026 | 25 | ✅ | ✅ | ✅ |
| 26.3 | Wilderness Bound | 15.09.2026 | 25 | ✅ (по состоянию на ноябрь 2026) | ✅ | ✅ |

> Fabric официально поддерживает «snapshot 18w43b и выше, релизы 1.14 и выше» ([FAQ](https://wiki.fabricmc.net/faq:user)).
> Для версий **1.13 и ниже** (включая 1.12.2) существует только **Legacy Fabric** — неофициальный путь, несовместимый с обычным Fabric API. Не указывайте «Fabric ✅» для 1.12.2 в генераторе кода.

Источники: [Minecraft Versions in 2026](https://mc-node.net/blog/en/minecraft-versions-2026-explained),
[Fabric porting docs](https://docs.fabricmc.net/develop/porting/),
[Fabric FAQ](https://wiki.fabricmc.net/faq:user),
[NeoForge primers](https://docs.neoforged.net/primer/docs/).

## 2. Как читать таблицы

- Для 1.7.10–1.12.2 используются имена **MCP/SRG** (`func_xxxxx`, `field_xxxxx`) — официальных маппингов тогда не было.
- С 1.14+ (для Forge фактически с 1.16.5) имена методов/полей → **Mojang official mappings**; имена классов оставались MCP вплоть до 1.17.
- `Registries.*` / `BuiltInRegistries.*` — ванильные реестры; `ForgeRegistries.*` — обёртки Forge.

## 3. Матрица загрузчиков (API, не ваниль)

### 3.1. Forge

| Аспект | 1.7.10 | 1.12.2 | 1.16.5 | 1.20.1 | 1.21.1 | 26.1.2 / 26.3 |
|---|---|---|---|---|---|---|
| `@Mod` | `cpw.mods.fml.common.Mod` | `net.minecraftforge.fml.common.Mod` | тот же | тот же | тот же | тот же (`net.minecraftforge.fml.common.Mod`) |
| Entrypoint | `@Mod.EventHandler` → `FMLPre/Init/PostInitEvent` | `@EventHandler` в классе `@Mod`; `@Mod.EventBusSubscriber` | конструктор + `FMLJavaModLoadingContext.get().getModEventBus()` | конструктор `(FMLJavaModLoadingContext)` — только Forge **≥ 47.3.10**; в 47.2.x — `FMLJavaModLoadingContext.get()` | тот же | тот же; строгие проверки EventBus на 26.x |
| Метаданные | `mcmod.info` | `mcmod.info` | `META-INF/mods.toml`, `loaderVersion="[36,)"` | `mods.toml`, `loaderVersion="[47,)"` (MDK: `loader_version_range=[47,)`) | `mods.toml`, `loaderVersion="[52,)"` | `mods.toml`, `loaderVersion` из `@FORGE_SPEC_VERSION@` |
| Регистрация | `GameRegistry.registerBlock/registerItem` | `GameRegistry.register`, `RegistryEvent.Register<T>` | `DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)` → `RegistryObject<T>` | тот же | тот же + `RegisterEvent` с `ForgeRegistries.Keys.*` | тот же (`ForgeRegistries.Keys.*`) |
| Шины | FML-события + `MinecraftForge.EVENT_BUS` | `MinecraftForge.EVENT_BUS`, `@SubscribeEvent` | MOD bus + FORGE bus | тот же | тот же | тот же |
| Сеть | `SimpleNetworkWrapper`, `IMessage`, `IMessageHandler` | те же | `SimpleChannel` (`net.minecraftforge.fml.network.*`) | `SimpleChannel` (`net.minecraftforge.network.*`), `FriendlyByteBuf`, `PacketDistributor` | тот же | тот же (SimpleImpl) |
| Конфиг | `net.minecraftforge.common.config.Configuration` | `Configuration` + аннотации `@Config` | `ForgeConfigSpec` + `ModLoadingContext.registerConfig` | тот же | тот же | тот же |
| Java / Gradle | 6–8, ForgeGradle `1.2-SNAPSHOT` | 8, ForgeGradle `2.3`/`3` | 8, FG `[6.0,6.2)`, маппинг `official` | 17, FG `[7.0,8)` | 21, FG `[7.0.3,8)` | **25**, FG `[7.0.17,8)` |

### 3.2. NeoForge

| Аспект | 1.20.1 | 1.21.1 | 26.1.2 / 26.3 |
|---|---|---|---|
| Статус | **20.1.x существовал**, 1:1 совместим с Forge, пакеты `net.minecraftforge.*`; поддержка свёрнута командой | полный форк с 20.2 | активная ветка |
| `@Mod` | `net.minecraftforge.fml.common.Mod` | `net.neoforged.fml.common.Mod`; конструктор `(IEventBus modBus, ModContainer, …)` | тот же |
| Метаданные | `META-INF/mods.toml` | `META-INF/neoforge.mods.toml`; `type=required/optional/…` вместо `mandatory` | `neoforge.mods.toml` |
| Регистрация | `ForgeRegistries.*` | `DeferredRegister.create(BuiltInRegistries.BLOCK, MODID)` → `DeferredHolder`; `DeferredRegister.Blocks/Items` | тот же |
| События | `MinecraftForge.EVENT_BUS` | `NeoForge.EVENT_BUS` + mod bus из конструктора | тот же |
| Сеть | старый SimpleImpl | `RegisterPayloadHandlersEvent` → `PayloadRegistrar`, `CustomPacketPayload`, `StreamCodec`, `PacketDistributor` | тот же |
| Конфиг | ForgeConfigSpec | `net.neoforged.neoforge.common.ModConfigSpec` | тот же |
| Java / Gradle | 17 | 21, `net.neoforged.gradle.userdev 7.1.x` | **25** |

> Ключевой момент: NeoForge **20.2** (не 20.1) — точка расхождения: переименование всех пакетов в `net.neoforged.*`, `neoforge.mods.toml` и payload-сеть пришли в 20.2/20.5.

### 3.3. Fabric

| Аспект | 1.16.5 | 1.20.1 | 1.21.1 | 26.1.2 / 26.3 |
|---|---|---|---|---|
| Точка входа | `net.fabricmc.api.ModInitializer#onInitialize()` (+ Client/Server), без аннотации | тот же | тот же | тот же |
| Метаданные | `fabric.mod.json`: `schemaVersion`, `id`, `entrypoints`, `depends` | тот же | `java >=21` | `java >=25`, Fabric API `0.16x+26.x` |
| Реестры | ванильный `Registry.register(...)` + `Identifier` | `Registry.register(BuiltInRegistries.BLOCK, ...)` | `BuiltInRegistries`, `ResourceLocation` | тот же; удалены хелперы `CompostingChanceRegistry`, `FuelRegistry`, `StrippableBlockRegistry`, `TillableBlockRegistry`, `FlattenableBlockRegistry` |
| События | Fabric API callbacks (`ServerLifecycleEvents`, `UseBlockCallback`, …) + миксины | тот же | тот же | тот же |
| Сеть | `ServerPlayNetworking.send(..., Identifier, PacketByteBuf)` | тот же | `CustomPayload`, `CustomPayload.Id`, `PayloadTypeRegistry`, `PlayPayloadHandler` | `CustomPacketPayload.Type`, `StreamCodec<RegistryFriendlyByteBuf,T>`, `PayloadTypeRegistry.clientboundPlay()/serverboundPlay()` |
| Конфиг | встроенного нет — Cloth Config / YACL / owo-config / свой JSON | тот же | тот же | тот же |
| Java / Loom | 8, Loom 0.10.x, Yarn | 17, Loom 1.x, official mappings | 21, Loom 1.19 | 25, Loom 1.19, `splitEnvironmentSourceSets()` |

## 4. Пошаговые переходы

### 4.1. 1.7.10 → 1.12.2 → 1.16.5 (Forge)

| Область | 1.7.10 | 1.12.2 | 1.16.5 | Примечание |
|---|---|---|---|---|
| Метаданные | `mcmod.info` (JSON: `modid`, `name`, `version`, `mcversion`, …) | `mcmod.info`, часто с `useMetadata = true` | `META-INF/mods.toml` (`modLoader`, `loaderVersion`, `license`, `[[mods]]`, `[[dependencies.<modid>]]`) | Переход на `mods.toml` — в 1.13; `license` обязателен. |
| Главный класс | `@cpw.mods.fml.common.Mod(modid, name, version)` | `@net.minecraftforge.fml.common.Mod(...)` | `@net.minecraftforge.fml.common.Mod("<modid>")` — только ID; инициализация в конструкторе | Пакет `cpw.mods.fml.*` → `net.minecraftforge.fml.*` после 1.7.10. |
| Жизненный цикл | `@Mod.EventHandler` + `FMLPreInitializationEvent`, `FMLInitializationEvent`, `FMLPostInitializationEvent` (пакет `cpw.mods.fml.common.event`) | те же классы, пакет `net.minecraftforge.fml.common.event` | `FMLConstructModEvent` ⚠️ **INTERNAL FORGE USE ONLY — модам не использовать**, `FMLCommonSetupEvent`, `FMLClientSetupEvent`, `FMLDedicatedServerSetupEvent`, `FMLLoadCompleteEvent` (`net.minecraftforge.fml.event.lifecycle`) | `preInit/init/postInit` удалены; слушатели регистрируются на **mod bus**. Событие `FMLConstructModEvent` помечено праймером 1.16.5 как внутреннее. |
| Шина событий | `@cpw.mods.fml.common.eventhandler.SubscribeEvent`, `MinecraftForge.EVENT_BUS` | `@net.minecraftforge.fml.common.eventhandler.SubscribeEvent` | `@net.minecraftforge.eventbus.api.SubscribeEvent`, `@Mod.EventBusSubscriber(bus = Bus.MOD/FORGE)` | С 1.13 EventBus — отдельная библиотека, шины MOD/FORGE разделены. |
| Регистрация | `GameRegistry.registerBlock/registerItem/registerTileEntity` | `RegistryEvent.Register<T>`, `IForgeRegistryEntry`, `@ObjectHolder` | `DeferredRegister.create(ForgeRegistries.ITEMS, MODID)` + `RegistryObject<T>` | `GameRegistry` → событийная регистрация → `DeferredRegister`. |
| Имена объектов | `setUnlocalizedName`, `setTextureName`, имена без неймспейса | `setRegistryName("modid", "name")` | `DeferredRegister.register("path", …)` даёт `modid:path` | С 1.13 имя всегда с неймспейсом. |
| Блок/предмет | `net.minecraft.block.Block`, `net.minecraft.item.Item`, `ItemStack`, `EntityPlayer`, `World` | те же MCP-пакеты | **MCP (MDK 1.16.5):** `net.minecraft.block.Block`, `net.minecraft.item.Item`, `PlayerEntity`, `World`, `TileEntity` | Пакеты `net.minecraft.world.level.*`, `net.minecraft.world.item.*`, `net.minecraft.world.entity.*` и класс `Level` появились только в **1.17** — не используйте их для 1.16.5. |
| Состояние / TileEntity | `net.minecraft.block.state.IBlockState`, `net.minecraft.tileentity.TileEntity` | те же | Mojang: `BlockState`, `BlockEntity` (MCP в 1.16.5 — `TileEntity`) | Полное переименование `TileEntity → BlockEntity` — в 1.17. |
| `ResourceLocation`, творческий таб | `net.minecraft.util.ResourceLocation`, `CreativeTabs.tabMisc` | те же | **`net.minecraft.util.ResourceLocation`**, **`ItemGroup.TAB_MISC`**, `Item.Properties#tab(ItemGroup)` | Для Forge 1.16.5 MDK верны именно `net.minecraft.util` и `ItemGroup`. Пакет `net.minecraft.resources` и класс `CreativeModeTab` появились в **1.17**; `.tab(...)` удалён в **1.19.3** (содержимое табов задаётся самими вкладками). |
| Правый клик | `Block#onBlockActivated(World, int,int,int, EntityPlayer, …)`, `Item#onItemRightClick(ItemStack, World, EntityPlayer)` | `onBlockActivated(IBlockState, World, BlockPos, EntityPlayer, EnumHand, EnumFacing, …)`, `onItemRightClick(World, EntityPlayer, EnumHand) → ActionResult<ItemStack>` | **1.16.5:** `Block#use(BlockState, World, BlockPos, PlayerEntity, Hand, BlockRayTraceResult) → ActionResultType`; `Item#use(World, PlayerEntity, Hand) → ActionResult<ItemStack>` | Для 1.20.1+: `Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult) → InteractionResult`, `Item#use(Level, Player, InteractionHand) → InteractionResult`. Типы `Level`/`Player`/`InteractionResult` **не компилируются** в 1.16.5. |
| Модели / языки | `assets/<modid>/lang/en_US.lang`, `ModelLoader.setCustomModelResourceLocation` | тот же подход | `assets/<modid>/models/item/<path>.json`, `lang/en_us.json`; старый хук удалён | Модели по registry name. |
| Рецепты / данные | `GameRegistry.addRecipe`, `CraftingManager` | JSON в `assets/<modid>/recipes/` | JSON в `data/<modid>/recipes`, `advancements`, `loot_tables`, `tags/` | 1.13: `assets/` → `data/`. |
| OreDictionary | `OreDictionary.registerOre(...)` | всё ещё актуален | Теги `data/<modid>/tags/items/…`, `ItemTags`, `ITag` | Замена OreDictionary — теги. |
| Сайды | `Side`, `@SideOnly(Side.CLIENT)`, `@SidedProxy` | те же, пакет `net.minecraftforge.fml.relauncher` | `Dist`, `@OnlyIn(Dist.CLIENT)`, `DistExecutor`, `FMLEnvironment.dist` | `@EventBusSubscriber(dist = Dist.CLIENT, …)`. |
| Сеть | `SimpleNetworkWrapper`, `IMessage`, `IMessageHandler` (`cpw.mods.fml.common.network.simpleimpl`) | те же, пакет `net.minecraftforge.fml.common.network.simpleimpl` | `SimpleChannel`, `NetworkRegistry.newSimpleChannel(...)`, `messageBuilder(...)`, `enqueueWork(...)` | Работу с пакетами всегда возвращать в игровой поток. |
| Команды | `ICommand`, `CommandBase`, `FMLServerStartingEvent` | те же | Brigadier: `RegisterCommandsEvent`, `CommandDispatcher` | 1.13 заменил `ICommand` на Brigadier. |
| Рендер | GL11, `RenderHelper` | GL11, `RenderHelper` | `com.mojang.blaze3d.matrix.MatrixStack`, `ItemProperties.register(...)` в `FMLClientSetupEvent` | — |
| Сборка | ForgeGradle `1.2-SNAPSHOT`, Java 6/7 | ForgeGradle `2.3-SNAPSHOT`/`3`, Java 8, MCP `snapshot_20171003` | Java 8; актуальный MDK 1.16.x — ForgeGradle `[6.0,6.2)`, `mappings channel: official` (ранние проекты — FG 4.1) | Дальше — FG 7 / Gradle 8+. |

**Главные разрывы:** смена пакета FML; исчезновение `@Mod.EventHandler`; `GameRegistry` → `RegistryEvent` → `DeferredRegister`; `mcmod.info` → `mods.toml`; Mojang-маппинги; data-driven контент и теги; две шины событий; `SideOnly` → `OnlyIn`; `SimpleNetworkWrapper` → `SimpleChannel`; `ICommand` → Brigadier.

### 4.2. 1.16.5 → 1.20.1 → 1.21.1

Имена ниже — **Mojang official mappings**.

| Область | 1.16.5 | 1.20.1 | 1.21.1 | Где изменилось |
|---|---|---|---|---|
| Java | 8 | 17 | 21 | 1.17 → 16, 1.18 → 17, **1.20.5 → 21** |
| Мир | `net.minecraft.world.World` | `net.minecraft.world.level.Level` | `Level`, `ServerLevel`, `ClientLevel` | **1.17** |
| Блоки | `net.minecraft.block.*` | `net.minecraft.world.level.block.*` | тот же | **1.17** |
| Предметы | `net.minecraft.item.*` | `net.minecraft.world.item.*` | тот же | **1.17** |
| Сущности | `net.minecraft.entity.*` | `net.minecraft.world.entity.*` | тот же | **1.17** |
| Текст | `net.minecraft.util.text.*` (`TextComponent`) | `net.minecraft.network.chat.*` (`Component`, `Component.literal`) | тот же | **1.17** |
| `ResourceLocation` | `net.minecraft.util.ResourceLocation` | `net.minecraft.resources.ResourceLocation` | тот же, конструктор **private** → `fromNamespaceAndPath`, `parse`, `withDefaultNamespace` | пакет — **1.17**, конструктор — **1.21** |
| TileEntity | `net.minecraft.tileentity.TileEntity` | `net.minecraft.world.level.block.entity.BlockEntity` | тот же; `BlockEntity#isValidBlockState` | **1.17**, `TileEntityProvider` → `EntityBlock` |
| Контейнеры | `net.minecraft.inventory.container.Container` | `net.minecraft.world.inventory.AbstractContainerMenu` | `RecipeBookMenu<I extends RecipeInput, R extends Recipe<I>>` | **1.17**, дженерики — **1.21** |
| Реестры | `net.minecraft.util.registry.Registry.BLOCK` | `net.minecraft.core.registries.BuiltInRegistries.BLOCK` | тот же | **1.19.3**: ключи в `Registries.*`, экземпляры в `BuiltInRegistries.*`; динамические — только через `RegistryAccess` |
| Holder’ы | почти нет | `ResourceKey`, `Holder`, `HolderSet`, `Registries.BLOCK` | `ByteBufCodecs.holderRegistry(...)`, `holderSet(...)` | **1.19.3+** |
| Творческий таб | `ItemGroup` (`net.minecraft.item`), `Item.Properties#tab(ItemGroup)`, `ItemGroup.TAB_MISC` | `net.minecraft.world.item.CreativeModeTab` как реестр (`Registries.CREATIVE_MODE_TAB`), `CreativeModeTab.builder()`, `BuildCreativeModeTabContentsEvent` | `CreativeModeTab.ItemDisplayParameters`, `CreativeModeTab.Output` | **1.17** — переименование `ItemGroup → CreativeModeTab` (Mojang-имена классов); **1.19.3** — `.tab()` удалён, содержимое задаётся вкладкой; **1.19.4** — сигнатура `displayItems(ItemDisplayParameters, Output)` |
| Данные ItemStack | `getTag()/getOrCreateTag()/setTag()` (`CompoundTag`) | **всё ещё NBT** | `DataComponentMap`, `DataComponentType`, `DataComponents`; `stack.get/set/update/remove(...)`, `minecraft:custom_data` | **1.20.5**, не 1.20.1 |
| Рецепты | `Recipe<C extends Container>` | `Recipe<C>`, `assemble(C, RegistryAccess)` | `Recipe<T extends RecipeInput>`, `CraftingInput.of(...)`, `SingleRecipeInput` | **1.19.4**, **1.20.5** (`streamCodec`), **1.21** (`Container` → `RecipeInput`) |
| Сеть (ваниль) | `PacketBuffer` | `FriendlyByteBuf`, ручной IO | `RegistryFriendlyByteBuf`, `StreamCodec`, `ByteBufCodecs`, `Packet#codec`, `PacketType` | **1.20.5** |
| GUI | `GuiComponent` | `GuiGraphics` | `GuiGraphics#pose` | **1.20** |
| Материалы блока | `Material`, `MaterialColor` | `Material` удалён, `MapColor` | `BlockBehaviour.Properties#mapColor/#pushReaction/#ignitedByLava/…` | **1.20** |
| Папки датапака | `recipes`, `tags/items` | множественное число | **единственное**: `recipe`, `tags/item`, `tags/block` | **1.21** |
| Рендер | GL | `RenderSystem#setShaderTexture/…` | тот же | **1.17**: OpenGL 3.2 Core, `MatrixStack` |

**Промежуточные вехи:** 1.17 — пакетная перестройка + OpenGL 3.2 Core + Java 16; 1.18 — Java 17 и мирогенерация; 1.19.3 — реестры и табы; 1.19.4 — `Holder<DamageType>`; 1.20 — `GuiGraphics`, удалён `Material`; **1.20.5 — настоящая граница перед 1.21.1**: Java 21, data components, `StreamCodec`, `neoforge.mods.toml`.

**Загрузчик:** Forge остаётся в `net.minecraftforge.*` и сохраняет `SimpleChannel` даже в 1.21.1. NeoForge с 20.2 перешёл на `net.neoforged.*`, с 20.5 — на `neoforge.mods.toml` и payload-сеть (`RegisterPayloadHandlersEvent`, `CustomPacketPayload`, `StreamCodec`). Один исходный дерево для Forge и NeoForge 1.21.1 требует loader-специфичной сети и метаданных.

### 4.3. 1.21.1 → 26.1.2 → 26.3

Между 1.21.1 и 26.x лежат **1.21.2, 1.21.4, 1.21.5–1.21.11 и 26.2** — их тоже нужно пройти. 26.1.2 — патч-линия 26.1 (отдельного ванильного праймера нет).

#### 4.3.0. Версии и тулчейн

| | 1.21.1 | 26.1.2 | 26.3 |
|---|---|---|---|
| Java | 21 | **25** | 25 |
| Forge | `1.21.1-52.1.x` | `26.1.2-64.1.x` | `26.3-66.1.x` |
| NeoForge | `1.21.1.x` | `26.1.2.129` | `26.3.0.115` |
| Fabric API | `0.116.x+1.21.1` | `0.155.3+26.1.2` | `0.166.2+26.3` (на 23.11.2026: Loader **0.20.1**, Loom **1.19-SNAPSHOT**; стартовые значения релиза — Loader 0.19.5 / Loom 1.17, Gradle 9.6) |
| Маппинги | official + Parchment | официальные имена Mojang **вернулись** для value-типов; Parchment опционален | тот же |

#### 4.3.1. Ваниль: ядро, данные, реестры

| Область | 1.21.1 | 26.1.2 | 26.3 |
|---|---|---|---|
| Идентификатор | `net.minecraft.resources.ResourceLocation` | `net.minecraft.resources.Identifier` | тот же |
| Пакеты критериев | `net.minecraft.advancements.critereon` | `net.minecraft.advancements.criterion` | `net.minecraft.advancements.triggers`, `.predicates`, `.entity` |
| `ChunkPos` | класс, `new ChunkPos(BlockPos)`, `toLong()` | **record**: `ChunkPos.containing(...)`, `ChunkPos.unpack(long)`, `pack()` | тот же |
| Случайность / клик | `Level#random`, `Entity#interactAt`, `ClickType` | `getRandom()`, `Entity#interact(Vec3)`, `ContainerInput` | тот же |
| Имена блоков | `FarmBlock`, `WaterlilyBlock` | `FarmlandBlock`, `LilyPadBlock` | тот же |
| Типизированные holder’ы | `getItemHolder()`, `getBlockHolder()`, `FluidState#holder()` | `TypedInstance`, `typeHolder()`, `FluidState#typeHolder()` | тот же |
| Loot-типы | обёртки `LootPoolEntryType`, `LootItemFunctionType`, `LootItemConditionType`, `FloatProviderType`, `IntProviderType`, …; `getType()`, `CODEC` | реестры держат `MapCodec`; обёртки удалены; `getType()` → `codec()`, `CODEC` → `MAP_CODEC`; `FloatProvider`/`IntProvider` — интерфейсы + `FloatProviders`/`IntProviders` | + reloadable-слой, `LootDataType.SLOT_SOURCE/FLOAT_PROVIDER/INT_PROVIDER` |
| Валидация | ad-hoc | `Validatable`, `validate(ValidationContext)`, `ProblemReporter` | перенесена в construction reloadable lookup |
| Шаблоны предметов | конкретный `ItemStack` в билдерах | `ItemStackTemplate`, `ItemInstance`, `fromNonEmptyStack`, `typeHolder()`, `count()` | тот же |
| Компоненты | статические компоненты предмета | `DataComponentInitializers`, `Item.Properties#delayedComponent(...)`, `Holder#components(...)` | тот же |
| Рецепты | `Recipe#assemble(HolderLookup.Provider)`, `EitherHolder`, `RecipeBuilder#getResult()` | `HolderLookup.Provider` убран из `assemble`, `EitherHolder` и `getResult()` удалены; `defaultId`, `getDefaultRecipeId()` | рецепты — reloadable datapack-реестр |
| Теги | старые провайдеры | новые провайдеры | удалены `HolderTagProvider`, `IntrinsicHolderTagsProvider`, `KeyTagProvider`; `TagAppender` принимает `ResourceKey` |
| Игровые правила | `GameRules.RULE_*` | реестровые ключи `ENTITY_DROPS`, `SPAWN_MOBS`, `RAIDS`; `EnvironmentAttributes` | тот же |

#### 4.3.2. Сводные изменения рендера и ввода: 1.21.1 → 26.3

> Это сравнение **конечных состояний**, а не список изменений только 26.3: `CuboidModel`/`FluidRenderer` пришли в 26.1, submit-node API — в 26.2, Renderpearl и SDL — в следующем крупном этапе. См. §4.3.5.

| Было | Стало в 26.3 |
|---|---|
| `com.mojang.blaze3d.*` | `com.mojang.renderpearl.api.*` / `.backend.*` / `.frontend.*` |
| `GpuFormat` | `renderpearl.api.GpuFormat` |
| `GpuBuffer`, `GpuTexture`, `GpuTextureView` | `renderpearl.api.buffers.GpuBuffer`, `.textures.GpuTexture(View)` (теперь интерфейсы; реализации в `renderpearl.backend.common.Base*`) |
| `GpuDevice`, `CommandEncoder`, `RenderPass`, `GpuSurface` | `renderpearl.frontend.FrontendGpuDevice`, `FrontendCommandEncoder`, `FrontendRenderPass`, `FrontendGpuSurface` (интерфейсы — в `renderpearl.api.*`) |
| `RenderPipeline`, `VertexFormat`, `VertexFormatElement`, `BindGroupLayout`, `BlendFunction`, `DepthStencilState`, `PrimitiveTopology` | `renderpearl.api.pipeline.*` / `renderpearl.api.vertex.*` |
| `com.mojang.blaze3d.opengl.*`, `.vulkan.*` | `renderpearl.backend.opengl.*`, `renderpearl.backend.vulkan.*` |
| `MultiBufferSource` (ещё в 1.21.1) | `SubmitNodeStorage`, `SubmitNodeCollection`, `FeatureRenderDispatcher`, `OrderedSubmitNodeCollector` (пришли в 26.2) |
| `ItemBlockRenderTypes`, `BlockModel`, `BlockModelPart`, `BlockModelDefinition`, `BlockColor` | удалены/`CuboidModel`, `BlockStateModelPart`, `BlockStateModelDispatcher`, `BlockTintSource`; `LiquidBlockRenderer` → `FluidRenderer` |
| `SourceFactor`/`DestFactor`, `TextureFormat` | `BlendFactor`, `GpuFormat` (26.2) |
| `#moj_import` | `#include`; обязательные `layout(location = …) in/out`; шейдеры требуют `GL_ARB_separate_shader_objects`; OIT через `minecraft:oit.glsl`, `OitPipelineSet`, `RenderSetup#setOitPipelines` |
| `RenderSystem#outputColorTextureOverride` / `#outputDepthTextureOverride` | удалены — вывод задаётся через `createRenderPass(...)` |
| GLFW-сканкоды | SDL: `InputConstants.KEY_M` / `SDLScancode.SDL_SCANCODE_M`; `KeyEvent.scancode` → `keycode` (`SDLKeycode`); `MouseButtonInfo#button` → `SDLMouse.SDL_BUTTON_*`; модификаторы `SDL_KMOD_*` |
| Text input | `TextInputManager#startTextInput/stopTextInput/onTextInputFocusChange`; в GUI — `Minecraft.getInstance().onTextInputFocusChange(this, focused)` из `GuiEventListener#setFocused` |
| `PoseStack#mulPose` | `rotate` / `rotateDegrees` |
| Краbling-overlay в `submitModel` | `OrderedSubmitNodeCollector#submitCrumblingOverlay`; `ItemQuads.split(...)`, `layer.setQuads(...)` |

#### 4.3.3. Ваниль: мир, предметы, генерация

| 1.21.1 | 26.3 |
|---|---|
| `ContextAwarePredicate`, `NumberProvider` | `Holder<LootItemCondition>`, `ContextFloatProvider`, `ContextIntProvider` |
| `AxeItem`, `ShovelItem`, `HoeItem`, `AxeItem.STRIPPABLES` и др. | удалены → `DataComponents.BLOCK_TRANSFORMER`, `BlockTransformers.AXE/SHOVEL/HOE` |
| `PotionBrewing`, `ItemTags.BREWING_FUEL`, `FuelValues`, `Villager#FOOD_POINTS`, `ComposterBlock#COMPOSTABLES` | `BrewingRecipe`/`RecipeType.BREWING`, `COOKING_FUEL`, `VILLAGER_FOOD`, `COMPOSTABLE`, `MOB_VISIBILITY` |
| `LootTable`/`Advancement`/`Recipe` не все reloadable | reloadable datapack-реестры + `RegistryDataLoader.RELOADABLE_REGISTRIES` |
| `RegistrySetBuilder.RegistryBootstrap`, `RecipeProvider.Runner`, провайдеры | `SingleRegistryBootstrap`, `MultiRegistryBootstrap`, `RegistriesDatapackGenerator.forReloadableLayer` |
| `Feature`/`ConfiguredFeature`, `WorldCarver`/`ConfiguredWorldCarver` | объединены; `BuiltInRegistries.FEATURE` → `FEATURE_TYPE`, `Registries.CONFIGURED_FEATURE` → `FEATURE` (`minecraft:worldgen/feature`) |
| wrapper-типы worldgen (`BlockStateProviderType`, `PlacementModifierType`, …) | удалены; реестры держат `MapCodec`; `Block#codec` удалён |
| `SurfaceSystem`/`SurfaceRules` | `MaterialSystem`/`MaterialRules`; `MATERIAL_CONDITION(_TYPE)` |
| `PlacementModifier#getPositions` | `modify(..., Consumer<BlockPos>)` |
| resource key внутри объектов | `*Ids`-классы: `BlockItemIds`, `EntityTypes`, `BlockEntityTypes` |

#### 4.3.4. Специфика загрузчиков в 26.x

- **Forge**: `@Mod`, `DeferredRegister`, `RegistryObject`, `ModConfig` остаются, но MDK перешёл на новый стиль: `context.getModBusGroup()` вместо `getModEventBus()`, `FMLCommonSetupEvent.getBus(modBusGroup)`, `BLOCKS.register(modBusGroup)`, `BuildCreativeModeTabContentsEvent.BUS.addListener(...)`, `net.minecraftforge.eventbus.api.listener.SubscribeEvent`, `Item.Properties#setId(...)`.
- **NeoForge**: пространство `net.neoforged.*`, `DeferredRegister`/`DeferredHolder` на `BuiltInRegistries`, `neoforge.mods.toml`, payload-сеть; все ванильные сигнатуры — по праймерам 26.x.
- **Fabric**: удалены `CompostingChanceRegistry`, `FuelRegistry`, `StrippableBlockRegistry`, `TillableBlockRegistry`, `FlattenableBlockRegistry`, `FabricPotionBrewingBuilder` — вместо них ванильные компоненты и `DefaultItemComponentEvents` / `BlockTransformerEvents.MODIFY`; сеть — `CustomPacketPayload.Type` + `StreamCodec<RegistryFriendlyByteBuf,T>` + `PayloadTypeRegistry.clientboundPlay()/serverboundPlay()`.

#### 4.3.5. Пошаговый список переходов

- **1.21.1 → 1.21.2**: `TagKey` → `HolderSet` (`#direct(...)`); имя/модель предмета → компоненты `ITEM_NAME`/`ITEM_MODEL`; `InteractionResult` — sealed interface (`$Pass`, `$Fail`, `$Success`, …), `InteractionResultHolder` и `ItemInteractionResult` свёрнуты; consumables, новые render types.
- **1.21.2 → 1.21.4**: Client Items (`assets/<ns>/items/<path>.json`, `ItemModel$Unbaked`, `ItemModels`, `ItemModelResolver`, `ItemStackRenderState`); экипировка через `DataComponents.EQUIPPABLE`.
- **1.21.5 → 1.21.11**: 1.21.5 — реwork рендера/моделей, **разделение логики удаления block entity** (`BlockEntity#preRemoveSideEffects` — побочные действия перед удалением BlockEntity из уровня; `BlockBehaviour#affectNeighborsAfterRemoval` — обновление соседей после; класс `BlockEntity` не удалён), дедуп инструментов; 1.21.6 — GUI, waypoints, Blaze3d, tag appender; 1.21.9 — input consolidation; **1.21.11** — `ResourceLocation` → `Identifier`, `critereon` → `criterion`, environment attributes, переименование game rules.
- **1.21.11 → 26.1**: Java 25; unrolled loot-кодеки; валидация; `ItemStackTemplate`/`ItemInstance`; materials/sprites; `CuboidModel`; GuiExtractor; `FluidRenderer`.
- **26.1 → 26.2**: Vulkan; `BlendFactor`/`GpuFormat`; `IndexType`/`PrimitiveTopology`; dynamic vertex formats; `BindGroupLayout`; `Minecraft.getInstance().gui.screen()`; `Font$PreparedText`; submit-node APIs.
- **26.2 → 26.3**: SDL + text input; Renderpearl; OIT; item quads; reloadable реестры; block transformers; worldgen codec rewrite.

## 5. Примеры кода: регистрация предмета

**1.7.10 (Forge, MCP/SRG):**
```java
@Mod(modid = "examplemod", name = "Example Mod", version = "1.0")
public class ExampleMod {
    public static Item EXAMPLE_ITEM = new Item()
        .setUnlocalizedName("example_item")
        .setTextureName("examplemod:example_item")
        .setCreativeTab(CreativeTabs.tabMisc);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(EXAMPLE_ITEM, "example_item");
    }
}
```

**1.12.2 (Forge):**
```java
@Mod(modid = MODID, name = "Example Mod", version = "1.0")
public class ExampleMod {
    public static final Item EXAMPLE_ITEM = new Item()
        .setUnlocalizedName("example_item")
        .setCreativeTab(CreativeTabs.tabMisc)
        .setRegistryName(MODID, "example_item");

    public ExampleMod() {
        // @SubscribeEvent сам по себе обработчик не регистрирует — это делает docs 1.12.x
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(EXAMPLE_ITEM);
    }
}
```

**1.16.5 (Forge, MCP-имена, FG 4.1):**
```java
@Mod(MODID)
public class ExampleMod {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> EXAMPLE_ITEM =
        ITEMS.register("example_item",
            () -> new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public ExampleMod() { // MDK 1.16.x: static get(), не constructor injection
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
    }
}
```

**1.20.1 (Forge, official mappings, FG 7, Forge ≥ 47.3.10) — ".tab()" здесь уже нет:**
```java
@Mod(ExampleMod.MODID)
public class ExampleMod {
    public static final String MODID = "examplemod";
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Item> EXAMPLE_ITEM =
        ITEMS.register("example_item", () -> new Item(new Item.Properties()));
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB =
        TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group.examplemod.example_tab"))
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((params, out) -> out.accept(EXAMPLE_ITEM.get()))
            .build());

    public ExampleMod(FMLJavaModLoadingContext context) { // подтверждено MDK 1.20.1
        IEventBus modBus = context.getModEventBus();
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
```

> `.title(...)` требует записи в `assets/examplemod/lang/en_us.json`:
> `{"item_group.examplemod.example_tab": "Example Tab"}` (иначе в UI будет показан ключ локализации).
> Это **один** способ — предмет добавляется только в собственную вкладку через `displayItems`.
> Чтобы добавить его во **vanilla-вкладку**, вместо `displayItems` используется слушатель
> `BuildCreativeModeTabContentsEvent` на mod bus:
> ```java
> modBus.addListener((BuildCreativeModeTabContentsEvent e) -> {
>     if (e.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
>         e.accept(EXAMPLE_ITEM.get());
> });
> ```
> Если применить оба способа, предмет появится сразу в двух вкладках — это редко нужно;
> в примере оставлен один способ, чтобы не смешивать демонстрацию двух API.

**1.21.1 (NeoForge):**
```java
@Mod(MODID)
public class ExampleMod {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final Supplier<Item> EXAMPLE_ITEM =
        ITEMS.registerSimpleItem("example_item", new Item.Properties());

    public ExampleMod(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
```

**1.21.1 (Fabric, официальные маппинги) — `setId()` ещё нет:**
```java
public class ExampleModItems implements ModInitializer {
    public static final ResourceLocation ITEM_ID =
        ResourceLocation.fromNamespaceAndPath("examplemod", "example_item");

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, ITEM_ID,
            new Item(new Item.Properties()));
    }
}
```

**26.3 (Fabric, official mappings) — `Item.Properties#setId` только с 1.21.2:**
```java
public class ExampleModItems implements ModInitializer {
    public static final ResourceKey<Item> EXAMPLE_ITEM_ID =
        ResourceKey.create(Registries.ITEM, ExampleMod.id("example_item"));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, EXAMPLE_ITEM_ID,
            new Item(new Item.Properties().setId(EXAMPLE_ITEM_ID)));
    }
}
```

> 26.x: модель предмета задаётся отдельным клиентским JSON в `assets/<modid>/items/<name>.json`
> (`{"model":{"type":"minecraft:model","model":"<modid>:item/<name>"}}`) — старый хук
> `ModelLoader.setCustomModelResourceLocation` давно удалён. `appendHoverText` объявлен
> deprecated в пользу data components.

## 6. Быстрый выбор раздела по исходному коду

Агенту не нужно читать весь документ — по токенам в исходнике выбирается только нужная строка.

| Токен в исходнике | Читать | Что это значит |
|---|---|---|
| `cpw.mods.fml.*`, `FMLPreInitializationEvent`, `GameRegistry.registerItem`, `mcmod.info` | §4.1, колонки 1.7.10 / 1.12.2 | старый FML |
| `net.minecraftforge.fml.common.*`, `RegistryEvent.Register<T>`, `setRegistryName` | §4.1, колонка 1.12.2 | событийная регистрация |
| `DeferredRegister`, `RegistryObject`, `net.minecraft.util.ResourceLocation`, `ItemGroup`, `ActionResultType`, `World`, `PlayerEntity` | §4.1 (1.16.5) + §5 | Forge 1.16.5, MCP-имена |
| `Item.Properties#tab(`, `CreativeModeTab.TAB_MISC` | §4.1 + §5 | частый баг: метода нет уже с **1.19.3** (в 1.16.5 — `ItemGroup.TAB_MISC`) |
| `InteractionResult`, `Level`, `BuiltInRegistries`, `DataComponentType`, `StreamCodec`, `RecipeInput` | §4.2 | 1.17–1.21.1 |
| `SimpleChannel`, `net.minecraftforge.network.*`, `META-INF/mods.toml` | §3.1 | Forge |
| `net.neoforged.fml.common.Mod`, `neoforge.mods.toml`, `CustomPacketPayload`, `PayloadRegistrar` | §3.2 | NeoForge 20.2+ / 21.1+ |
| `ModInitializer`, `fabric.mod.json`, `ServerPlayNetworking`, `fabric.*Registry` | §3.3, §4.3.4 | Fabric |
| `com.mojang.blaze3d.*`, `GLFW*`, `#moj_import`, `AxeItem`, `FuelRegistry`, `TileEntity` | §4.3 | 26.x / переход 1.17+ |
| `net.minecraft.util.ResourceLocation` | §4.1 | **≤ 1.16.5** (в 1.16.5 — MCP-имя) |
| `net.minecraft.resources.ResourceLocation` | §4.2 | **1.17–1.21.10** |
| `net.minecraft.resources.Identifier` | §4.3.1 | **≥ 1.21.11** — `ResourceLocation` переименован |

## 7. Статус достоверности

| Статус | Значение | Где в документе |
|---|---|---|
| **Verified** | **сверено с MDK/документацией**, НЕ компилировалось и НЕ запускалось; исполнитель должен подтвердить сборкой. Для 26.x — только сверка с MDK/праймерами | §5: Forge 1.7.10, Forge 1.12.2, Forge 1.16.5 (MDK 1.16.x), Forge 1.20.1 (MDK, Forge **47.4.0+** для constructor injection), Forge 1.21.1, Forge 26.3 (**66.1.9**, последняя публикация на 18.11.2026), Fabric 26.3 (Loader 0.20.1 / Loom 1.19-SNAPSHOT); §3.1 |
| **Documented** | подтверждено официальной документацией или праймером | §4.1–§4.3, docs Forge / Fabric / NeoForge |
| **Inferred** | выведено из соседних версий — обязательно проверять сборкой | 26.1.2 (отдельного праймера нет), часть API 26.3 |

Праймеры NeoForge — **high-level, неисчерпывающие** руководства: они не заменяют промежуточные переходы 1.21.2 → 1.21.4 → 1.21.5–1.21.11 → 26.1 → 26.2 → 26.3.

### Исправления после ревью (23.11.2026)

| Замечание | Вердикт | Действие |
|---|---|---|
| Fabric 1.12.2 | ✅ верно | Строка таблицы заменена: официально с 1.14, для 1.12.2 — только Legacy Fabric |
| `ResourceLocation` в 1.16.5 | ✅ верно | `net.minecraft.util`, не `net.minecraft.resources` (переход в 1.17) |
| `CreativeModeTab.TAB_MISC` в 1.16.5 | ✅ верно | `ItemGroup.TAB_MISC` в §4.1 и §5 |
| `.tab(...)` в 1.20.1 | ✅ верно (критично) | Пример переписан на `BuildCreativeModeTabContentsEvent` / `CreativeModeTab.builder()` |
| Constructor injection в Forge 1.20.1 | ❌ неверно | MDK 1.20.1 подтверждает `new ExampleMod(FMLJavaModLoadingContext)`; уточнено: вариант работает с Forge ≥ 47.3.10 |
| Сигнатура `use` в 1.16.5 | ✅ верно | `World`, `PlayerEntity`, `Hand`, `BlockRayTraceResult` → `ActionResultType`; типы 1.17+ вынесены отдельно |
| `loaderVersion="[46,)"` в 1.20.1 | ✅ верно | Заменено на `[47,)` — MDK: `loader_version_range=[47,)` |
| `Item.Properties#setId` в 1.21.1 | ✅ верно | Разделены примеры Fabric: 1.21.1 — `ResourceLocation` без `setId`, 26.3 — `ResourceKey` + `setId` (метод с 1.21.2) |
| `FMLConstructModEvent` в 1.16.5 | ✅ верно | Добавлена пометка *INTERNAL FORGE USE ONLY* — модам не использовать |
| Loader/Loom для Fabric 26.3 | ✅ верно | Указаны текущие 0.20.1 / 1.19-SNAPSHOT и стартовые значения релиза |
| Точность `Verified` | ✅ верно | В статусе указаны точные версии MC/Forge/MDK/Loader |
| Дата 26.1.2 | ✅ верно | Было «Tiny Takeover, март 2026» (это 26.1 от 24.03); исправлено: hotfix 26.1.2 от 09.04.2026 |
| «Удаление block entity» в 1.21.5 | ✅ верно | Класс `BlockEntity` не удалён; указано разделение `preRemoveSideEffects` / `affectNeighborsAfterRemoval` |
| Двойное добавление предмета в 1.20.1 | ✅ верно | Оставлен один способ (собственный таб); добавление в ванильный таб через `BuildCreativeModeTabContentsEvent` вынесено в пояснение |
| Формат версии `YY.ПРИОРИТЕТ.ПАТЧ` | ✅ верно | Заменено на `YY.Номер_дропа.Патч` со ссылкой на объявление Mojang |
| Опечатка «до ударения» | ✅ верно | «Побочные действия перед удалением BlockEntity из уровня» |
| «Четыре эпохи» | ✅ верно | Исправлено на «пять эпох» — по числу перечисленных этапов |

### Errata 1.0.1 (23.11.2026)

| Замечание | Вердикт | Действие |
|---|---|---|
| Пример Forge 1.12.2 не регистрирует обработчик | ✅ верно (критично) | Добавлен `MinecraftForge.EVENT_BUS.register(this)` в конструктор — по [docs 1.12.x](https://docs.minecraftforge.net/en/1.12.x/events/intro) `@SubscribeEvent` сам по себе не регистрирует обработчик |
| История `CreativeModeTab` | ✅ верно | **1.17** — переименование `ItemGroup → CreativeModeTab` (Mojang-имена классов); **1.19.3** — удалён `.tab()` ([праймер 1.19.3](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.19.3/index.md)); **1.19.4** — `ItemDisplayParameters`/`Output` ([праймер 1.19.4](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.19.4/index.md)). Исправлено в §4.1, §4.2, §6 |
| Противоречие по `ResourceLocation` в §6 | ✅ верно | Правило заменено на три строки: `net.minecraft.util` ≤ 1.16.5 → `net.minecraft.resources` 1.17–1.21.10 → `Identifier` ≥ 1.21.11 ([праймер 1.21.11](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21.11/index.md)) |
| `.title(...)` в примере 1.20.1 | ✅ верно (улучшение) | Добавлен `.title(Component.translatable(...))` + запись в `en_us.json`. API есть в 1.20.1, но MDK 1.20.1 его не использует |
| Заголовок §4.3.2 | ✅ верно | Переименован в «Сводные изменения рендера и ввода: 1.21.1 → 26.3» с пометкой, что изменения пришли из 26.1/26.2/26.3 |
| Объём `Verified` | ✅ верно | Явно указано: сверка с MDK/документацией ≠ компиляция и запуск, особенно для 26.x |

## 8. Рекомендуемый маршрут переноса

Прямой прыжок 1.7.10 → 26.3 практически нереален: каждое из этих изменений ломает либо сигнатуры, либо саму модель загрузки. Практичная последовательность:

1. **1.7.10 → 1.12.2** — самый «дешёвый» шаг: смена `cpw.mods.fml` на `net.minecraftforge.fml`, `RegistryEvent`, JSON-рецепты. Сохранить совместимость со старым миром.
2. **1.12.2 → 1.16.5** — крупнейший сдвиг: сплющивание реестров (1.13), `data/`, теги, `DeferredRegister`, Mojang-имена методов, две шины событий. Здесь проще переписать мод, чем патчить.
3. **1.16.5 → 1.17/1.18** — чисто механический, но огромный по объёму rebrand пакетов; автоматизируется IDE + скриптами. Обязательна смена Java.
4. **1.18 → 1.20.1** — в основном ванильные изменения (1.19.3 реестры, 1.20 GUI/материалы). Для Forge это «последняя стабильная точка» с широкой экосистемой.
5. **1.20.1 → 1.21.1** — **выбор загрузчика**: Forge сохраняет `SimpleChannel` и `net.minecraftforge.*`; NeoForge переходит на `net.neoforged.*`, `neoforge.mods.toml` и payload-сеть. Решите заранее, целить ли в оба.
6. **1.21.1 → 26.1.2 → 26.3** — Java 25; в 26.3 — крупнейший клиентский разрыв: `com.mojang.blaze3d.*` → `com.mojang.renderpearl.*`, GLFW → SDL. Если мод клиентский с кастомным рендером, закладывайте отдельный этап.

Правило: переносить по одному «большому» разрыву за раз и собирать после каждого; проверять не только компиляцию, но и загрузку мира/ресурсов.

## 9. Источники

**Праймеры миграции (основной источник):** https://github.com/neoforged/.github/tree/main/primers
Отдельные праймеры: [1.12→1.13/1.14](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.14/index.md),
[1.15.2→1.16.5](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.16.5/index.md),
[1.16.5→1.17](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.17/index.md),
[1.19.3](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.19.3/index.md),
[1.19.4](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.19.4/index.md),
[1.20](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.20/index.md),
[1.20.5](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.20.5/index.md),
[1.21](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21/index.md),
[1.21.1](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21.1/index.md),
[1.21.2](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21.2/index.md),
[1.21.4](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21.4/index.md),
[1.21.11](https://raw.githubusercontent.com/neoforged/.github/main/primers/1.21.11/index.md),
[26.1](https://raw.githubusercontent.com/neoforged/.github/main/primers/26.1/index.md),
[26.2](https://raw.githubusercontent.com/neoforged/.github/main/primers/26.2/index.md),
[26.3](https://raw.githubusercontent.com/neoforged/.github/main/primers/26.3/index.md).

**Forge docs:** https://docs.minecraftforge.net/ (ветки `1.12.x`, `1.16.x`, `1.20.1`, `1.20.x`, `1.21.x`);
MDK: https://github.com/MinecraftForge/MinecraftForge (ветки `1.21.1`, `26.1`, `26.3`);
мавен-метаданные: https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml.

**NeoForge docs:** https://docs.neoforged.net/docs/1.21.1/ и https://docs.neoforged.net/docs/ ;
https://docs.neoforged.net/primer/docs/ ;
https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml ;
https://neoforged.net/news/20.2release , https://neoforged.net/news/20.5release , https://neoforged.net/news/26.1release.

**Fabric docs:** https://docs.fabricmc.net/develop/ , https://docs.fabricmc.net/develop/porting/ ,
https://docs.fabricmc.net/develop/items/first-item , https://docs.fabricmc.net/develop/loader/fabric-mod-json ,
https://fabricmc.net/2026/09/15/263.html , https://github.com/FabricMC/fabric-example-mod (ветки `1.16.5`, `1.20.1`, `1.21.1`, `26.3`).

**Легаси 1.7.10:** https://skmedix.github.io/ForgeJavaDocs/javadoc/forge/1.7.10-10.13.4.1614/ ;
https://github.com/GTNewHorizons/ExampleMod1.7.10/blob/master/docs/migration.md.

**Версии 2026:** https://mc-node.net/blog/en/minecraft-versions-2026-explained .

> Точность: названия классов и методов сверены с праймерами и MDK на дату 23.11.2026. Для 26.1.2 отдельного праймера нет — данные экстраполированы из 26.1 + патч-нот. Для части Fabric/Forge-API 26.x возможны расхождения между минорными сборками; помечайте такие места при переносе.
