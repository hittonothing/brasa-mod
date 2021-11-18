package dtlivehero.brasa.common.block.brasafurnace;

import dtlivehero.brasa.common.ModCommon;
import dtlivehero.brasa.common.ModRecipes;
import dtlivehero.brasa.common.recipe.DebrisSmelting;
import dtlivehero.brasa.common.util.MachineData;
import dtlivehero.brasa.common.util.MachineSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class BrasaFurnaceBE extends BlockEntity implements MenuProvider {

    public static final int FUEL_SLOTS_COUNT = 4;
    public static final int INPUT_SLOTS_COUNT = 2;
    public static final int OUTPUT_SLOTS_COUNT = 2;

    private final MachineSlot fuelContents;
    private final MachineSlot inputContents;
    private final MachineSlot outputContents;

    private final MachineData machineData = new MachineData(FUEL_SLOTS_COUNT);

    public BrasaFurnaceBE(BlockPos blockPos, BlockState blockState) {
        super(ModCommon.BRASA_FURNACE_BE.get(), blockPos, blockState);
        fuelContents = MachineSlot.createForBlockEntity(FUEL_SLOTS_COUNT, this::canPlayerAccessInventory, this::setChanged);
        inputContents = MachineSlot.createForBlockEntity(INPUT_SLOTS_COUNT, this::canPlayerAccessInventory, this::setChanged);
        outputContents = MachineSlot.createForBlockEntity(OUTPUT_SLOTS_COUNT, this::canPlayerAccessInventory, this::setChanged);
    }

    public boolean canPlayerAccessInventory(Player player) {
        if(this.level.getBlockEntity(this.worldPosition) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.distanceToSqr(worldPosition.getX() + X_CENTRE_OFFSET, worldPosition.getY() + Y_CENTRE_OFFSET, worldPosition.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    private boolean isLit() {
        boolean isBurning = false;
        for(int burning : machineData.burnTimeRemainings) {
            if (burning > 0) {
                isBurning = true;
                break;
            }
        }
        return isBurning;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BrasaFurnaceBE brasaFurnaceBE) {
        brasaFurnaceBE.machineWork(level, blockPos, blockState);
    }

    public void machineWork(Level level, BlockPos blockPos, BlockState blockState) {
        ItemStack currentlySmeltingItem = getCurrentlySmeltingInputItem(level);

        for(int i = 0; i < 2; i++) {
            if(isLit()) --machineData.burnTimeRemainings[i];
            if(machineData.itemTransferTime[i] > 0) --machineData.itemTransferTime[i];
            ItemStack secondFuelItemStack = fuelContents.getItem(i + 2);
            ItemStack transferStack = secondFuelItemStack.copy();
            transferStack.setCount(1);
            if(machineData.itemTransferTime[i] == 0) {
                if(!secondFuelItemStack.isEmpty() && fuelContents.doesItemStackFit(i + 2, transferStack) || !secondFuelItemStack.isEmpty() && fuelContents.getItem(i).isEmpty()) {
                    machineData.itemTransferTime[i] = 10;
                    fuelContents.removeItem(i + 2, 1);
                    fuelContents.increaseStackSize(i, transferStack);
                    setChanged();
                }
            }
        }

        if(!ItemStack.isSame(currentlySmeltingItem, currentlySmeltingItemLastTick)) {
            machineData.cookTimeElapsed = 0;
        }
        currentlySmeltingItemLastTick = currentlySmeltingItem.copy();

        if(!currentlySmeltingItem.isEmpty()) {
            int numberOfFuelBurning = burnFuel();
            if(numberOfFuelBurning > 0) {
                machineData.cookTimeElapsed += numberOfFuelBurning;
            } else machineData.cookTimeElapsed -= 2;

            if(machineData.cookTimeElapsed < 0) machineData.cookTimeElapsed = 0;
            int cookTimeForCurrentItem = getCookTime(level, currentlySmeltingItem);
            machineData.cookTimeForCompletion = cookTimeForCurrentItem;

            if(machineData.cookTimeElapsed >= cookTimeForCurrentItem) {
                smeltFirstSuitableInputItem(level);
                machineData.cookTimeElapsed = 0;
            }
        } else machineData.cookTimeElapsed = 0;

        if(isLit()) {
            level.setBlock(blockPos, blockState.setValue(BrasaFurnaceBlock.LIT, true), 3);
        } else {
            level.setBlock(blockPos, blockState.setValue(BrasaFurnaceBlock.LIT, false), 3);
        }

        BlockState currentBlockState = level.getBlockState(blockPos);
        BlockState newBlockState = blockState.setValue(BrasaFurnaceBlock.LIT, true);
        if(!newBlockState.equals(currentBlockState)) {
            setChanged(level, blockPos, blockState);
        }
    }

    private int burnFuel() {
        int burningCount = 0;
        boolean inventoryChanged = false;
        for(int fuelIndex = 0; fuelIndex < 2; fuelIndex++) {
            if(machineData.burnTimeRemainings[fuelIndex] > 0) {
                ++burningCount;
            }
            if(machineData.burnTimeRemainings[fuelIndex] == 0) {
                ItemStack fuelItemStack = fuelContents.getItem(fuelIndex);
                if(!fuelItemStack.isEmpty() && getItemBurnTime(fuelItemStack) > 0) {
                    int burnTimeForItem = getItemBurnTime(fuelItemStack);
                    machineData.burnTimeRemainings[fuelIndex] = burnTimeForItem;
                    machineData.burnTimeInitialValues[fuelIndex] = burnTimeForItem;
                    fuelContents.removeItem(fuelIndex, 1);
                    ++burningCount;
                    inventoryChanged = true;
                    if(fuelItemStack.isEmpty()) {
                        ItemStack containerItem = fuelItemStack.getContainerItem();
                        fuelContents.setItem(fuelIndex, containerItem);
                    }
                }
            }
        }
        if(inventoryChanged) setChanged();
        return burningCount;
    }

    private ItemStack getCurrentlySmeltingInputItem(Level level) {
        return smeltFirstSuitableInputItem(level, false);
    }

    private void smeltFirstSuitableInputItem(Level level) {
        smeltFirstSuitableInputItem(level, true);
    }

    private ItemStack smeltFirstSuitableInputItem(Level level, boolean performSmelt) {
        Integer firstSuitableInputSlot = null;
        Integer firstSuitableOutputSlot = null;
        ItemStack result = ItemStack.EMPTY;

        for(int inputIndex = 0; inputIndex < INPUT_SLOTS_COUNT; inputIndex++) {
            ItemStack itemStackToSmelt = inputContents.getItem(inputIndex);
            if(!itemStackToSmelt.isEmpty()) {
                result = getSmeltingResultForItem(level, itemStackToSmelt);
                machineData.experience = getExperience(level, itemStackToSmelt);
                if(!result.isEmpty()) {
                    for(int outputIndex = 0; outputIndex < OUTPUT_SLOTS_COUNT; outputIndex++) {
                        if(willItemStackFit(outputContents, outputIndex, result)) {
                            firstSuitableInputSlot = inputIndex;
                            firstSuitableOutputSlot = outputIndex;
                        }
                    }
                    if(firstSuitableInputSlot != null) break;
                }
            }
        }
        if(firstSuitableInputSlot == null) return ItemStack.EMPTY;
        ItemStack returnValue = inputContents.getItem(firstSuitableInputSlot).copy();
        if(!performSmelt) return returnValue;
        inputContents.removeItem(firstSuitableInputSlot, 1);
        outputContents.increaseStackSize(firstSuitableOutputSlot, result);
        setChanged();
        return returnValue;
    }

    public boolean willItemStackFit(MachineSlot machineSlot, int slotIndex, ItemStack itemStackOrigin) {
        ItemStack itemStackDestination = machineSlot.getItem(slotIndex);
        if(itemStackDestination.isEmpty() || itemStackOrigin.isEmpty()) return true;
        if(!itemStackOrigin.sameItem(itemStackDestination)) return false;
        int sizeAfterMerge = itemStackDestination.getCount() + itemStackOrigin.getCount();
        return sizeAfterMerge <= machineSlot.getMaxStackSize() && sizeAfterMerge <= itemStackDestination.getMaxStackSize();
    }

    public static int getItemBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, ModRecipes.Types.DEBRIS_SMELTING_RECIPE_TYPE);
    }

    public static ItemStack getSmeltingResultForItem(Level level, ItemStack stack) {
        Optional<DebrisSmelting> matchingRecipe = getMatchingRecipeForInput(level, stack);
        return matchingRecipe.map(debrisSmelting -> debrisSmelting.getResultItem().copy()).orElse(ItemStack.EMPTY);
    }

    public static float getExperience(Level level, ItemStack stack) {
        Optional<DebrisSmelting> matchingRecipe = getMatchingRecipeForInput(level, stack);
        return matchingRecipe.map(DebrisSmelting::getExperience).orElse(0.0f);
    }

    public static int getCookTime(Level level, ItemStack stack) {
        Optional<DebrisSmelting> matchingRecipe = getMatchingRecipeForInput(level, stack);
        return matchingRecipe.map(DebrisSmelting::getCookingTime).orElse(0);
    }

    public static Optional<DebrisSmelting> getMatchingRecipeForInput(Level level, ItemStack stack) {
        RecipeManager recipeManager = level.getRecipeManager();
        SimpleContainer singleItemInventory = new SimpleContainer(stack);
        return recipeManager.getRecipeFor(ModRecipes.Types.DEBRIS_SMELTING_RECIPE_TYPE, singleItemInventory, level);
    }

    public float getStoredExp() {
        return machineData.experience;
    }

    public void createExperience(Level level, BlockPos blockPos) {
        int i = Mth.floor(machineData.experience);
        ExperienceOrb.award((ServerLevel) level, new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()), i);
        machineData.experience = 0;
    }

    public static boolean isItemValidForFuelSlot(ItemStack stack) {
        return true;
    }

    public static boolean isItemValidForInputSlot(ItemStack stack) {
        return true;
    }

    public static boolean isItemValidForOutputSlot(ItemStack stack) {
        return true;
    }

    private final String FUEL_SLOTS_NBT = "fuelSlots";
    private final String INPUT_SLOTS_NBT = "inputSlots";
    private final String OUTPUT_SLOTS_NBT = "outputSlots";

    @Override
    public CompoundTag save(CompoundTag nbt) {
        super.save(nbt);

        machineData.toNBT(nbt);
        nbt.put(FUEL_SLOTS_NBT, fuelContents.serializeNBT());
        nbt.put(INPUT_SLOTS_NBT, inputContents.serializeNBT());
        nbt.put(OUTPUT_SLOTS_NBT, outputContents.serializeNBT());

        return nbt;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        machineData.readNBT(nbt);

        CompoundTag inventoryNBT = nbt.getCompound(FUEL_SLOTS_NBT);
        fuelContents.deserializeNBT(inventoryNBT);

        inventoryNBT = nbt.getCompound(INPUT_SLOTS_NBT);
        inputContents.deserializeNBT(inventoryNBT);

        inventoryNBT = nbt.getCompound(OUTPUT_SLOTS_NBT);
        outputContents.deserializeNBT(inventoryNBT);

        if(fuelContents.getContainerSize() != FUEL_SLOTS_COUNT || inputContents.getContainerSize() != INPUT_SLOTS_COUNT || outputContents.getContainerSize() != OUTPUT_SLOTS_COUNT) {
            throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match the expected.");
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag updateTagDescribingBlockEntityState = getUpdateTag();
        final int METADATA = 42;
        return new ClientboundBlockEntityDataPacket(this.worldPosition, METADATA, updateTagDescribingBlockEntityState);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag updateTagDescribingBlockEntityState = pkt.getTag();
        handleUpdateTag(updateTagDescribingBlockEntityState);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        save(compoundTag);
        return compoundTag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public void dropAllContents(Level level, BlockPos blockPos) {
        Containers.dropContents(level, blockPos, fuelContents);
        Containers.dropContents(level, blockPos, inputContents);
        Containers.dropContents(level, blockPos, outputContents);
        createExperience(level, blockPos);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.brasa.brasa_furnace_container");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return BrasaFurnaceContainer.createContainerServerSide(windowId, inventory, inputContents, outputContents, fuelContents, machineData);
    }

    private static ItemStack currentlySmeltingItemLastTick = ItemStack.EMPTY;
}
