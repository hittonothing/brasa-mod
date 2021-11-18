package com.dtlivehero.brasa.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Predicate;

public class MachineSlot implements Container {

    public static MachineSlot createForBlockEntity(int size, Predicate<Player> canPlayerAccessInventory, Notify setChangedNotificationLambda) {
        return new MachineSlot(size, canPlayerAccessInventory, setChangedNotificationLambda);
    }

    public static MachineSlot createForClientSideContainer(int size) {
        return new MachineSlot(size);
    }

    public CompoundTag serializeNBT() {
        return machineComponentsContents.serializeNBT();
    }

    public void deserializeNBT(CompoundTag nbt) {
        machineComponentsContents.deserializeNBT(nbt);
    }

    public void setSetChangedNotificationLambda(Notify setChangedNotificationLambda) {
        this.setChangedNotificationLambda = setChangedNotificationLambda;
    }

    public void setCanPlayerAccessInventoryLambda(Predicate<Player> canPlayerAccessInventoryLambda) {
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
    }

    public void setStartOpenNotificationLambda(Notify startOpenNotificationLambda) {
        this.startOpenNotificationLambda = startOpenNotificationLambda;
    }

    public void setStopOpenNotificationLambda(Notify stopOpenNotificationLambda) {
        this.stopOpenNotificationLambda = stopOpenNotificationLambda;
    }

    @Override
    public boolean stillValid(Player player) {
        return canPlayerAccessInventoryLambda.test(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return machineComponentsContents.isItemValid(index, stack);
    }

    @FunctionalInterface
    public interface Notify {
        void invoke();
    }

    @Override
    public void setChanged() {
        setChangedNotificationLambda.invoke();
    }

    @Override
    public void startOpen(Player p_18955_) {
        startOpenNotificationLambda.invoke();
    }

    @Override
    public void stopOpen(Player p_18954_) {
        stopOpenNotificationLambda.invoke();
    }

    @Override
    public int getContainerSize() {
        return machineComponentsContents.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i< machineComponentsContents.getSlots(); ++i) {
            if(!machineComponentsContents.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        return machineComponentsContents.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if(count < 0) throw new IllegalArgumentException("count should be >= 0: " + count);
        return machineComponentsContents.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        int maxPossibleItemStackSize = machineComponentsContents.getSlotLimit(index);
        return machineComponentsContents.extractItem(index, maxPossibleItemStackSize, false);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        machineComponentsContents.setStackInSlot(index, stack);
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < machineComponentsContents.getSlots(); ++i) {
            machineComponentsContents.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public ItemStack increaseStackSize(int index, ItemStack itemStackToInsert) {
        return machineComponentsContents.insertItem(index, itemStackToInsert, false);
    }

    public boolean doesItemStackFit(int index, ItemStack itemStackToInsert) {
        ItemStack leftoverItemStack = machineComponentsContents.insertItem(index, itemStackToInsert, true);
        return leftoverItemStack.isEmpty();
    }

    private MachineSlot(int size) {
        this.machineComponentsContents = new ItemStackHandler(size);
    }

    private MachineSlot(int size, Predicate<Player> canPlayerAccessInventoryLambda, Notify setChangedNotificationLambda) {
        this.machineComponentsContents = new ItemStackHandler(size);
        this.canPlayerAccessInventoryLambda = canPlayerAccessInventoryLambda;
        this.setChangedNotificationLambda = setChangedNotificationLambda;
    }

    private Predicate<Player> canPlayerAccessInventoryLambda = player -> true;

    private Notify setChangedNotificationLambda = () -> {};
    private Notify startOpenNotificationLambda = () -> {};
    private Notify stopOpenNotificationLambda = () -> {};

    private final ItemStackHandler machineComponentsContents;
}
