package thunder.hack;

import com.mojang.logging.LogUtils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import net.fabricmc.api.ModInitializer;
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

public class ThunderHack implements ModInitializer {

    /* ===== MOD INFO ===== */

    public static final String MOD_ID = "thunderhack";
    public static final String VERSION = "1.0";
    public static String GITHUB_HASH = "dev";
    public static String BUILD_DATE = "unknown";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Runtime RUNTIME = Runtime.getRuntime();

    public static final ModMetadata MOD_META =
            FabricLoader.getInstance()
                    .getModContainer(MOD_ID)
                    .orElseThrow()
                    .getMetadata();

    /* ===== GLOBAL ===== */

    public static MinecraftClient mc;
    public static long initTime;

    public static boolean isOutdated = false;
    public static String[] contributors = new String[32];
    public static Color copy_color = new Color(-1);
    public static BlockPos gps_position;
    public static float TICK_TIMER = 1f;

    /* ===== BARITONE CHECK (CỰC QUAN TRỌNG) ===== */

    public static final boolean baritone =
            FabricLoader.getInstance().isModLoaded("baritone")
                    || FabricLoader.getInstance().isModLoaded("baritone-meteor");

    /* ===== EVENT BUS ===== */

    public static final IEventBus EVENT_BUS = new EventBus();
    public static final Core core = new Core();

    /* ===== GUI KEY LISTEN ===== */

    public static KeyListening currentKeyListener = null;

    public enum KeyListening {
        ThunderGui,
        ClickGui,
        Search,
        Sliders,
        Strings
    }

    /* ===== INIT ===== */

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        mc = MinecraftClient.getInstance();

        // ORBIT LAMBDA FIX
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
                        throw new RuntimeException(t);
                    }
                }
        );

        try {
            BUILD_DATE = ThunderUtility.readManifestField("Build-Timestamp");
            GITHUB_HASH = ThunderUtility.readManifestField("Git-Commit");
        } catch (Exception ignored) {}

        EVENT_BUS.subscribe(core);

        Managers.init();
        Managers.subscribe();

        Render2DEngine.initShaders();

        RUNTIME.addShutdownHook(new ManagerShutdownHook());
        RUNTIME.addShutdownHook(new ModuleShutdownHook());

        LOGGER.info("[ThunderHack] Loaded in {} ms",
                System.currentTimeMillis() - initTime);
    }

    /* ===== UTILS ===== */

    public static boolean isFuturePresent() {
        return FabricLoader.getInstance().getModContainer("future").isPresent();
    }
}
