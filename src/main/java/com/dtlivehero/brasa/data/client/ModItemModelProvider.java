package com.dtlivehero.brasa.data.client;

import com.dtlivehero.brasa.common.ModItems;
import com.dtlivehero.brasa.Brasa;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Brasa.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("pedrita_block", modLoc("block/pedrita_block"));
        withExistingParent("brasa_furnace_block", modLoc("block/brasa_furnace"));

        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));

        builder(ModItems.PEDRITA_DEBRIS.get(), itemGenerated);
        builder(ModItems.FERRILUME_INGOT.get(), itemGenerated);
        builder(ModItems.FERRILUME_NUGGET.get(), itemGenerated);
    }

    private void builder(ItemLike name, ModelFile modelFile) {
        getBuilder(name.toString()).parent(modelFile).texture("layer0", "item/" + name);
    }
}
