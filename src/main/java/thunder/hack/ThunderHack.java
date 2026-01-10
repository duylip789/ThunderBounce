package thunder.hack;

import com.mojang.logging.LogUtils;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.IEventBus;
import meteordevelopment.orbit.listeners.LambdaListener;
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

    /* ================= META ================= */
    public static final String MOD_ID = "thunderhack";
    public static final String VERSION = "1.7b2407";
    public static String GITHUB_HASH = "0";
    public static String BUILD_DATE = "unknown";

    public static final ModMetadata MOD_META =
            FabricLoader.getInstance()
                    .getModContainer(MOD_ID)
                    .orElseThrow()
                    .getMetadata();

    /* ================= CORE ================= */
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Runtime RUNTIME = Runtime.getRuntime();
    public static final IEventBus EVENT_BUS = new EventBus();

    public static MinecraftClient mc;
    public static long initTime;

    public static final Core core = new Core();

    /* ================= STATE ================= */
    public static boolean isOutdated = false;
    public static boolean baritone =
            FabricLoader.getInstance().isModLoaded("baritone")
                    || FabricLoader.getInstance().isModLoaded("baritone-meteor");

    public static String[] contributors = new String[32];
    public static Color copy_color = new Color(-1);
    public static BlockPos gps_position;
    public static float TICK_TIMER = 1f;
    public static KeyListening currentKeyListener = null;

    /* ================= INIT ================= */
    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();
        mc = MinecraftClient.getInstance();

        // Đăng ký lambda factory cho Orbit (FIX CRASH)
        EVENT_BUS.registerLambdaFactory(
                "thunder.hack",
                (lookupInMethod, klass) ->
                        LambdaListener.Factory.create(klass)
        );

        // Đọc thông tin build
        BUILD_DATE = ThunderUtility.readManifestField("Build-Timestamp");
        GITHUB_HASH = ThunderUtility.readManifestField("Git-Commit");

        ThunderUtility.syncVersion();

        EVENT_BUS.subscribe(core);

        // Init managers
        Managers.init();
        Managers.subscribe();

        // Init render shader
        Render2DEngine.initShaders();

        LOGGER.info("[ThunderHack] Loaded in {} ms",
                System.currentTimeMillis() - initTime);

        // Shutdown hooks
        RUNTIME.addShutdownHook(new ManagerShutdownHook());
        RUNTIME.addShutdownHook(new ModuleShutdownHook());
    }

    /* ================= UTILS ================= */
    public static boolean isFuturePresent() {
        return FabricLoader.getInstance()
                .getModContainer("future")
                .isPresent();
    }

    /* ================= ENUM ================= */
    public enum KeyListening {
        ThunderGui,
        ClickGui,
        Search,
        Sliders,
        Strings
    }
}
