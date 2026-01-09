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

import java.awt.*;

public class ThunderHack implements ModInitializer {

    /* ================= CONST ================= */

    public static final String MOD_ID = "thunderbounce";
    public static final String VERSION = "0.1";

    /* ================= META ================= */

    public static ModMetadata MOD_META;

    /* ================= GLOBAL ================= */

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Runtime RUNTIME = Runtime.getRuntime();

    public static MinecraftClient mc;
    public static long initTime;

    /* ================= EVENT ================= */

    public static final IEventBus EVENT_BUS = new EventBus();

    /* ================= CORE ================= */

    public static Core core = new Core();

    /* ================= STATE ================= */

    public static float TICK_TIMER = 1f;
    public static BlockPos gps_position;

    public static String GITHUB_HASH = "dev";
    public static String BUILD_DATE = "unknown";

    public static final boolean baritone =
            FabricLoader.getInstance().isModLoaded("baritone")
                    || FabricLoader.getInstance().isModLoaded("baritone-meteor");

    /* ================= INIT ================= */

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();

        mc = MinecraftClient.getInstance();

        FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .ifPresent(container -> MOD_META = container.getMetadata());

        try {
            Managers.init();
            Managers.subscribe();
        } catch (Throwable t) {
            LOGGER.error("[ThunderBounce] Manager init failed", t);
        }

        LOGGER.info("[ThunderBounce] Loaded ({} ms)",
                System.currentTimeMillis() - initTime);
    }

    /* ================= UTIL ================= */

    public static boolean isFuturePresent() {
        return FabricLoader.getInstance().isModLoaded("future");
    }
}
