package dtlivehero.brasa.data;

import dtlivehero.brasa.Brasa;
import dtlivehero.brasa.common.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ModTagProvider extends BlockTagsProvider {

    public ModTagProvider(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper) {
        super(gen, Brasa.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Brasa Mod Tags";
    }

    @Override
    protected void addTags() {
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.PEDRITA_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.PEDRITA_BLOCK.get());
    }
}
