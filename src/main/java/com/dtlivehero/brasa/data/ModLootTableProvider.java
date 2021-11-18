package dtlivehero.brasa.data;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dtlivehero.brasa.BrasaRegister;
import dtlivehero.brasa.common.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    public ModLootTableProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
        return ImmutableList.of(Pair.of(ModBlockLootTables::new, LootContextParamSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach(((resourceLocation, lootTable) -> LootTables.validate(validationtracker, resourceLocation, lootTable)));
    }

    public static class ModBlockLootTables extends BlockLoot {

        @Override
        protected void addTables() {
            dropSelf(ModBlocks.PEDRITA_BLOCK.get());
            dropSelf(ModBlocks.BRASA_FURNACE_BLOCK.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BrasaRegister.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        }
    }
}
