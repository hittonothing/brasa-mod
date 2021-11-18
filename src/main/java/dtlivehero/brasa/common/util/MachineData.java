package dtlivehero.brasa.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;

import java.util.Arrays;

public class MachineData implements ContainerData {

    private final int FUEL_SLOTS_COUNT;

    public int cookTimeElapsed;
    public int cookTimeForCompletion;
    public float experience;

    public int[] burnTimeInitialValues;
    public int[] burnTimeRemainings;
    public int[] itemTransferTime;

    public void toNBT(CompoundTag nbt) {
        nbt.putInt("CookTimeElapsed", cookTimeElapsed);
        nbt.putInt("CookTimeForCompletion", cookTimeForCompletion);
        nbt.putIntArray("burnTimeRemainings", burnTimeRemainings);
        nbt.putIntArray("burnTimeInitialValues", burnTimeInitialValues);
        nbt.putFloat("Experience", experience);
    }

    public void readNBT(CompoundTag nbt) {
        cookTimeElapsed = nbt.getInt("CookTimeElapsed");
        cookTimeForCompletion = nbt.getInt("CookTimeForCompletion");
        burnTimeRemainings = Arrays.copyOf(nbt.getIntArray("burnTimeRemainings"), FUEL_SLOTS_COUNT);
        burnTimeInitialValues = Arrays.copyOf(nbt.getIntArray("burnTimeInitialValues"), FUEL_SLOTS_COUNT);
        experience = nbt.getFloat("Experience");
    }

    private final int COOKTIME_INDEX = 0;
    private final int COOK_TIME_FOR_COMPLETION_INDEX = 1;
    private final int BURNTIME_INITIAL_VALUE_INDEX = 2;
    private final int BURNTIME_REMAINING_INDEX;
    private final int END_OF_DATA_INDEX_PLUS_ONE;

    public MachineData(final int count) {
        FUEL_SLOTS_COUNT = count;
        this.burnTimeInitialValues = new int[FUEL_SLOTS_COUNT];
        this.burnTimeRemainings = new int[FUEL_SLOTS_COUNT];
        this.itemTransferTime = new int[FUEL_SLOTS_COUNT];
        this.BURNTIME_REMAINING_INDEX = BURNTIME_INITIAL_VALUE_INDEX + FUEL_SLOTS_COUNT;
        this.END_OF_DATA_INDEX_PLUS_ONE = BURNTIME_REMAINING_INDEX + FUEL_SLOTS_COUNT;
    }

    @Override
    public int get(int index) {
        validateIndex(index);
        if(index == COOKTIME_INDEX) {
            return cookTimeElapsed;
        } else if(index == COOK_TIME_FOR_COMPLETION_INDEX) {
            return cookTimeForCompletion;
        } else if(index >= BURNTIME_INITIAL_VALUE_INDEX && index < BURNTIME_REMAINING_INDEX) {
            return burnTimeInitialValues[index - BURNTIME_INITIAL_VALUE_INDEX];
        } else {
            return burnTimeRemainings[index - BURNTIME_REMAINING_INDEX];
        }
    }

    @Override
    public void set(int index, int value) {
        validateIndex(index);
        if(index == COOKTIME_INDEX) {
            cookTimeElapsed = value;
        } else if(index == COOK_TIME_FOR_COMPLETION_INDEX) {
            cookTimeForCompletion = value;
        } else if(index >= BURNTIME_INITIAL_VALUE_INDEX && index < BURNTIME_REMAINING_INDEX) {
            burnTimeInitialValues[index - BURNTIME_INITIAL_VALUE_INDEX] = value;
        } else {
            burnTimeRemainings[index - BURNTIME_REMAINING_INDEX] = value;
        }
    }

    @Override
    public int getCount() {
        return END_OF_DATA_INDEX_PLUS_ONE;
    }

    private void validateIndex(int index) throws IndexOutOfBoundsException {
        if(index < 0 || index >= getCount()) {
            throw new IndexOutOfBoundsException("Index out of bounds:" + index);
        }
    }
}
