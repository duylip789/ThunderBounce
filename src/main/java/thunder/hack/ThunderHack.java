package thunder.hack;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import thunder.hack.core.Managers;

public class ThunderHack implements ModInitializer {

    public static final String MOD_ID = "thunderbounce";
    public static final String VERSION = "0.1";

    public static final Logger LOGGER = LogUtils.getLogger();
    public static MinecraftClient mc;
    public static long initTime;

    @Override
    public void onInitialize() {
        initTime = System.currentTimeMillis();

        mc = MinecraftClient.getInstance();

        try {
            Managers.init();
            Managers.subscribe();
        } catch (Throwable t) {
            LOGGER.error("[ThunderBounce] Manager init failed", t);
        }

        LOGGER.info("[ThunderBounce] Loaded successfully ({} ms)",
                System.currentTimeMillis() - initTime);
    }

    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }
}
