package com.dtlivehero.brasa.data.client;

import com.dtlivehero.brasa.common.ModBlocks;
import com.dtlivehero.brasa.Brasa;
import com.google.gson.Gson;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModBlockModelProvider extends BlockStateProvider {
    public ModBlockModelProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Brasa.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.PEDRITA_BLOCK.get());
        horizontalBlock(ModBlocks.BRASA_FURNACE_BLOCK.get(), blockModelOrientable("brasa_furnace", true));
        horizontalBlock(ModBlocks.FUSION_CHAMBER_BLOCK.get(), blockModelOrientable("fusion_chamber_block", false));
    }

    private ModelFile blockModelOrientable(String name, boolean hasLit) {
        ModelFile modelFile = models().getExistingFile(mcLoc("block/orientable"));
        if(hasLit) {
            models().getBuilder(name + "_lit").parent(modelFile)
                    .texture("top", "block/" + name + "_top")
                    .texture("front", "block/" + name + "_front_lit")
                    .texture("side", "block/" + name + "_side")
                    .texture("bottom", "block/" + name + "_bottom");
        }
        return models().getBuilder(name).parent(modelFile)
                .texture("top", "block/" + name + "_top")
                .texture("front", "block/" + name + "_front")
                .texture("side", "block/" + name + "_side")
                .texture("bottom", "block/" + name + "_bottom");
    }
}
