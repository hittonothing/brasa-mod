package com.dtlivehero.brasa.common.block.brasa_furnace;

import com.dtlivehero.brasa.common.ModCommon;
import com.dtlivehero.brasa.common.util.MachineData;
import com.dtlivehero.brasa.common.util.MachineSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BrasaFurnaceContainer extends AbstractContainerMenu {

    public static BrasaFurnaceContainer createContainerServerSide(int windowId, Inventory inventory, MachineSlot inputContents, MachineSlot outputContents, MachineSlot fuelContents, MachineData machineData) {
        return new BrasaFurnaceContainer(windowId, inventory, inputContents, outputContents, fuelContents, machineData);
    }

    public static BrasaFurnaceContainer createContainerClientSide(int windowId, Inventory inventory, FriendlyByteBuf extraData) {
        MachineSlot inputContents = MachineSlot.createForClientSideContainer(INPUT_SLOTS_COUNT);
        MachineSlot outputContents = MachineSlot.createForClientSideContainer(OUTPUT_SLOTS_COUNT);
        MachineSlot fuelContents = MachineSlot.createForClientSideContainer(FUEL_SLOTS_COUNT);
        MachineData machineData = new MachineData(BrasaFurnaceBE.FUEL_SLOTS_COUNT);
        return new BrasaFurnaceContainer(windowId, inventory, inputContents, outputContents, fuelContents, machineData);
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;

    public static final int INPUT_SLOTS_COUNT = BrasaFurnaceBE.INPUT_SLOTS_COUNT;
    public static final int OUTPUT_SLOTS_COUNT = BrasaFurnaceBE.OUTPUT_SLOTS_COUNT;
    public static final int FUEL_SLOTS_COUNT = BrasaFurnaceBE.FUEL_SLOTS_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX;
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;
    private static final int FIRST_FUEL_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int FIRST_INPUT_SLOT_INDEX = FIRST_FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT;
    private static final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 125;

    public BrasaFurnaceContainer(int windowId, Inventory inventory, MachineSlot inputContents, MachineSlot outputContents, MachineSlot fuelContents, MachineData machineData) {
        super(ModCommon.BRASA_FURNACE_CONTAINER.get(), windowId);
        this.inputContents = inputContents;
        this.outputContents = outputContents;
        this.fuelContents = fuelContents;
        this.machineData = machineData;
        this.level = inventory.player.level;

        addDataSlots(machineData);

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 183;

        for(int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            addSlot(new Slot(inventory, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        for(int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for(int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xPos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int yPos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(inventory, slotNumber, xPos, yPos));
            }
        }

        final int FUEL_SLOTS_XPOS = 71;
        final int FUEL_SLOTS_YPOS = 78;

        for(int x = 0; x < 2; x++) {
            addSlot(new SlotFuel(fuelContents, x, FUEL_SLOTS_XPOS + SLOT_X_SPACING * x, FUEL_SLOTS_YPOS));
            addSlot(new SlotFuel(fuelContents, x + 2, FUEL_SLOTS_XPOS + SLOT_X_SPACING * x, FUEL_SLOTS_YPOS + SLOT_Y_SPACING));
        }

        final int INPUT_SLOTS_XPOS = 26;
        final int INPUT_SLOTS_YPOS = 24;

        for(int y = 0; y < INPUT_SLOTS_COUNT; y++) {
            addSlot(new SlotSmeltableInput(inputContents, y, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }

        final int OUTPUT_SLOTS_XPOS = 134;
        final int OUTPUT_SLOTS_YPOS = 24;

        for(int y = 0; y < OUTPUT_SLOTS_COUNT; y++) {
            addSlot(new SlotOutput(outputContents, y, OUTPUT_SLOTS_XPOS, OUTPUT_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return fuelContents.stillValid(player) && inputContents.stillValid(player) && outputContents.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int sourceSlotIndex) {
        Slot sourceSlot = slots.get(sourceSlotIndex);
        if(sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceItemStack = sourceSlot.getItem();
        ItemStack sourceStackBeforeMerge = sourceItemStack.copy();
        boolean successfulTransfer = false;

        SlotZone sourceZone = SlotZone.getZoneFromIndex(sourceSlotIndex);
        switch (sourceZone) {
            case OUTPUT_ZONE:
                successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, true);
                if(!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, true);
                if(successfulTransfer) sourceSlot.onQuickCraft(sourceItemStack, sourceStackBeforeMerge);
                break;
            case INPUT_ZONE:
            case FUEL_ZONE:
                successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, false);
                if(!successfulTransfer) successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, false);
                break;
            case PLAYER_HOTBAR:
            case PLAYER_MAIN_INVENTORY:
                if(!BrasaFurnaceBE.getSmeltingResultForItem(level, sourceItemStack).isEmpty()) successfulTransfer = mergeInto(SlotZone.INPUT_ZONE, sourceItemStack, false);
                if(!successfulTransfer && BrasaFurnaceBE.getItemBurnTime(sourceItemStack) > 0) successfulTransfer = mergeInto(SlotZone.FUEL_ZONE, sourceItemStack, true);
                if(!successfulTransfer) {
                    if(sourceZone == SlotZone.PLAYER_HOTBAR) {
                        successfulTransfer = mergeInto(SlotZone.PLAYER_MAIN_INVENTORY, sourceItemStack, false);
                    } else successfulTransfer = mergeInto(SlotZone.PLAYER_HOTBAR, sourceItemStack, false);

                }
                break;
            default:
                throw new IllegalArgumentException("unexpected sourceZone: " + sourceZone);
        }
        if(!successfulTransfer) return ItemStack.EMPTY;
        if(sourceItemStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else sourceSlot.setChanged();

        if(sourceItemStack.getCount() == sourceStackBeforeMerge.getCount()) return ItemStack.EMPTY;
        sourceSlot.onTake(player, sourceItemStack);

        return sourceStackBeforeMerge;
    }

    private boolean mergeInto(SlotZone destinationSlot, ItemStack sourceItemStack, boolean fillFromEnd) {
        return moveItemStackTo(sourceItemStack, destinationSlot.firstIndex, destinationSlot.lastIndexPlus1, fillFromEnd);
    }

    public double fractionOfFuelRemaining(int fuelSlot) {
        if(machineData.burnTimeInitialValues[fuelSlot] <= 0) return 0;
        double fraction = machineData.burnTimeRemainings[fuelSlot] / (double)machineData.burnTimeInitialValues[fuelSlot];
        return Mth.clamp(fraction, 0.0, 1.0);
    }

    public int secondsOfFuelRemaining(int fuelSlot) {
        if(machineData.burnTimeRemainings[fuelSlot] <= 0) return 0;
        return machineData.burnTimeRemainings[fuelSlot] / 20;
    }

    public double fractionOfCookTimeComplete() {
        if(machineData.cookTimeForCompletion == 0) return 0;
        double fraction = machineData.cookTimeElapsed / (double)machineData.cookTimeForCompletion;
        return Mth.clamp(fraction, 0.0, 1.0);
    }

    public static class SlotFuel extends Slot {

        public SlotFuel(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BrasaFurnaceBE.isItemValidForFuelSlot(stack);
        }
    }

    public static class SlotSmeltableInput extends Slot {

        public SlotSmeltableInput(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BrasaFurnaceBE.isItemValidForInputSlot(stack);
        }
    }

    public static class SlotOutput extends Slot {

        public SlotOutput(Container inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BrasaFurnaceBE.isItemValidForOutputSlot(stack);
        }
    }

    private final MachineSlot inputContents;
    private final MachineSlot outputContents;
    private final MachineSlot fuelContents;
    private final MachineData machineData;

    private final Level level;

    private enum SlotZone {
        FUEL_ZONE(FIRST_FUEL_SLOT_INDEX, FUEL_SLOTS_COUNT),
        INPUT_ZONE(FIRST_INPUT_SLOT_INDEX, INPUT_SLOTS_COUNT),
        OUTPUT_ZONE(FIRST_OUTPUT_SLOT_INDEX, OUTPUT_SLOTS_COUNT),
        PLAYER_MAIN_INVENTORY(PLAYER_INVENTORY_FIRST_SLOT_INDEX, PLAYER_INVENTORY_SLOT_COUNT),
        PLAYER_HOTBAR(HOTBAR_FIRST_SLOT_INDEX, HOTBAR_SLOT_COUNT);

        SlotZone(int firstIndex, int numberOfSlots) {
            this.firstIndex = firstIndex;
            this.slotCount = numberOfSlots;
            this.lastIndexPlus1 = firstIndex + numberOfSlots;
        }

        public final int firstIndex;
        public final int slotCount;
        public final int lastIndexPlus1;

        public static SlotZone getZoneFromIndex(int slotIndex) {
            for(SlotZone slotZone : SlotZone.values()) {
                if(slotIndex >= slotZone.firstIndex && slotIndex < slotZone.lastIndexPlus1) return slotZone;
            }
            throw new IndexOutOfBoundsException("Unexpected slotIndex");
        }
    }
}
