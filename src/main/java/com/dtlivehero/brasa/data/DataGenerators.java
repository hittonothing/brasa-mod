package com.dtlivehero.brasa.data;

import com.dtlivehero.brasa.Brasa;
import com.dtlivehero.brasa.data.client.ModBlockModelProvider;
import com.dtlivehero.brasa.data.client.ModItemModelProvider;
import com.dtlivehero.brasa.data.client.ModLang;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = Brasa.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        //add providers
        gen.addProvider(new ModBlockModelProvider(gen, existingFileHelper));
        gen.addProvider(new ModItemModelProvider(gen, existingFileHelper));
        gen.addProvider(new ModLootTableProvider(gen));
        gen.addProvider(new ModTagProvider(gen, existingFileHelper));
        gen.addProvider(new ModRecipeProvider(gen));
        gen.addProvider(new ModLang(gen));
    }
}
