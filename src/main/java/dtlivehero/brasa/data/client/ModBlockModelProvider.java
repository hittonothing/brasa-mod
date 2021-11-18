package dtlivehero.brasa.data.client;

import dtlivehero.brasa.Brasa;
import dtlivehero.brasa.common.ModBlocks;
import net.minecraft.data.DataGenerator;
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
