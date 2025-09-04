package net.shirojr.titanfabric.util;

import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

public class BiDirectionalLookup<T> {
    private final HashMap<T, T> data;
    private final HashMap<T, T> invertedData;

    public BiDirectionalLookup(@Nullable Set<Pair<T, T>> input) {
        this.data = new HashMap<>();
        this.invertedData = new HashMap<>();
        if (input == null) return;
        for (var entry : input) {
            data.put(entry.getLeft(), entry.getRight());
            invertedData.put(entry.getRight(), entry.getLeft());
        }
    }

    @Nullable
    public T getOpposite(T entry) {
        T result = data.get(entry);
        if (result != null) return result;
        else return invertedData.get(entry);
    }

    public void put(T entry, T oppositeEntry) {
        this.data.put(entry, oppositeEntry);
        this.invertedData.put(oppositeEntry, entry);
    }

    public HashMap<T, T> getDataMap() {
        return this.data;
    }

    public HashMap<T, T> getInvertedDataMap() {
        return this.data;
    }
}
