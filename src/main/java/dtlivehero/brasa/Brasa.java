package dtlivehero.brasa;

import dtlivehero.brasa.common.ModCommon;
import dtlivehero.brasa.common.block.brasafurnace.BrasaFurnaceScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Brasa.MOD_ID)
public class Brasa {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "brasa";

    public Brasa() {
        LOGGER.info("BRASA MOD INITIALIZATION");
        MinecraftForge.EVENT_BUS.register(this);

        BrasaRegister.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setupClient(FMLClientSetupEvent event) {
        MenuScreens.register(ModCommon.BRASA_FURNACE_CONTAINER.get(), BrasaFurnaceScreen::new);
        LOGGER.info("BRASA CLIENT SETUP");
    }
}
