package com.dtlivehero.brasa.data.client;

import com.dtlivehero.brasa.common.ModBlocks;
import com.dtlivehero.brasa.common.ModItems;
import com.dtlivehero.brasa.Brasa;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLang extends LanguageProvider {
    public ModLang(DataGenerator gen) {
        super(gen, Brasa.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        //Blocks
        add(ModBlocks.PEDRITA_BLOCK.get(), "Pedrita Block");
        add(ModBlocks.BRASA_FURNACE_BLOCK.get(), "Brasa Furnace");
        add(ModBlocks.FUEL_CAN_BLOCK.get(), "Fuel Can");
        add(ModBlocks.FUSION_CHAMBER_BLOCK.get(), "Fusion Chamber");
        //Items
        add(ModItems.PEDRITA_DEBRIS.get(), "Pedrita Debris");
        add(ModItems.FERRILUME_INGOT.get(), "Ferrilume Ingot");
        add(ModItems.FERRILUME_NUGGET.get(), "Ferrilume Nugget");
        //Others
        add("container.brasa.brasa_furnace_container", "Brasa Furnace");
        add("itemGroup.brasa_stuff", "Brasa Mod Tab");
    }
}
