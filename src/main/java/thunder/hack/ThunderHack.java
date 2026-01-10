package thunder.hack;

import com.mojang.logging.LogUtils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import thunder.hack.core.Core;
import thunder.hack.core.Managers;
import thunder.hack.core.hooks.ManagerShutdownHook;
import thunder.hack.core.hooks.ModuleShutdownHook;
import thunder.hack.utility.ThunderUtility;
import thunder.hack.utility.render.Render2DEngine;

import java.awt.*;
import java.lang.invoke.MethodHandles;
import java.util.Optional;

public final class ThunderHack implements ClientModInitializer {

    /* ================= MOD INFO ================= */

    public static final String MOD_ID = "thunderhack";
    public static final String VERSION = "1.0";

    public static String GITHUB_HASH = "dev";
    public static String BUILD_DATE = "unknown";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Runtime RUNTIME = Runtime.getRuntime();

    /** ⚠️ FIX CRASH: KHÔNG orElseThrow */
    public static final ModMetadata MOD_META =
            FabricLoader.getInstance()
                    .getModContainer(MOD_ID)
                    .map(ModMetadata::getMetadata)
                    .orElse(null);

    /* ================= GLOBAL ================= */

    public static MinecraftClient mc;
    public static long initTime;

    public static boolean isOutdated = false;
    public static String[] contributors = new String[32];
    public static Color copy_color = new Color(255, 255, 255);
    public static BlockPos gps_position;
    public static float TICK_TIMER = 1f;

    /* ================= EVENT BUS ================= */

    public static final IEventBus EVENT_BUS = new EventBus();
    public static final Core core = new Core();

    /* ================= GUI ================= */

    public static KeyListening currentKeyListener = null;

    public enum KeyListening {
        ThunderGui,
        ClickGui,
        Search,
        Sliders,
        Strings
    }

    /* ================= MOD CHECK ================= */

    public static final boolean baritone =
            FabricLoader.getInstance().isModLoaded("baritone")
                    || FabricLoader.getInstance().isModLoaded("baritone-meteor");

    /* ================= INIT ================= */

    @Override
    public void onInitializeClient() {
        initTime = System.currentTimeMillis();

        /* ⚠️ CLIENT ONLY – SAFE */
        mc = MinecraftClient.getInstance();

        /* ===== ORBIT FIX – KHÔNG CRASH ===== */
        EVENT_BUS.registerLambdaFactory(
                "thunder.hack",
                (lookupInMethod, klass) -> {
                    try {
                        return (MethodHandles.Lookup) lookupInMethod.invoke(
                                null,
                                klass,
                                MethodHandles.lookup()
                        );
                    } catch (Throwable t) {
                        throw new RuntimeException("Orbit lambda factory failed", t);
                    }
                }
        );

        /* ===== MANIFEST SAFE READ ===== */
        try {
            BUILD_DATE = Optional
                    .ofNullable(ThunderUtility.readManifestField("Build-Timestamp"))
                    .orElse("dev");

            GITHUB_HASH = Optional
                    .ofNullable(ThunderUtility.readManifestField("Git-Commit"))
                    .orElse("dev");
        } catch (Throwable ignored) {}

        /* ===== CORE ===== */
        EVENT_BUS.subscribe(core);

        /* ===== MANAGERS ===== */
        try {
            Managers.init();
            Managers.subscribe();
        } catch (Throwable t) {
            LOGGER.error("[ThunderHack] Managers init failed", t);
        }

        /* ===== RENDER ===== */
        try {
            Render2DEngine.initShaders();
        } catch (Throwable t) {
            LOGGER.warn("[ThunderHack] Shader init skipped");
        }

        /* ===== SHUTDOWN ===== */
        RUNTIME.addShutdownHook(new ManagerShutdownHook());
        RUNTIME.addShutdownHook(new ModuleShutdownHook());

        LOGGER.info("[ThunderHack] Loaded successfully in {} ms",
                System.currentTimeMillis() - initTime);
    }

    /* ================= UTILS ================= */

    public static boolean isFuturePresent() {
        return FabricLoader.getInstance().getModContainer("future").isPresent();
    }
}
