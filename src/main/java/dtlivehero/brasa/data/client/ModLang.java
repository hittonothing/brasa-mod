package dtlivehero.brasa.data.client;

import dtlivehero.brasa.Brasa;
import dtlivehero.brasa.common.ModBlocks;
import dtlivehero.brasa.common.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLang extends LanguageProvider {
    public ModLang(DataGenerator gen) {
        super(gen, Brasa.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(ModBlocks.PEDRITA_BLOCK.get(), "Pedrita Block");
        add(ModBlocks.BRASA_FURNACE_BLOCK.get(), "Brasa Furnace");
        add(ModItems.PEDRITA_DEBRIS.get(), "Pedrita Debris");
        add(ModItems.FERRILUME_INGOT.get(), "Ferrilume Ingot");
        add(ModItems.FERRILUME_NUGGET.get(), "Ferrilume Nugget");
        add("container.brasa.brasa_furnace_container", "Brasa Furnace");
        add("itemGroup.brasa_stuff", "Brasa Mod Tab");
    }
}
