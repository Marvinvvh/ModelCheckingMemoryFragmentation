package org.TraceVerifier.Trace.Weights;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * Map to hold different weights. Allows us to pick a random one.
 */
public class WeightedMap {
    @Expose
    private final TreeMap<Integer, WeightedElement> weightedElementsMap;
    @Expose
    private int totalWeight = 0;

    public WeightedMap() {
        this.weightedElementsMap = new TreeMap<>();
    }

    public List<WeightedElement> getWeightedElements() {
        return weightedElementsMap.values().stream().toList();
    }

    public void clear() {
        totalWeight = 0;
        weightedElementsMap.clear();
    }

    public void add(WeightedElement element) {
        totalWeight += element.weight();
        weightedElementsMap.put(totalWeight, element);
    }

    public int getRandomizedValue(Random rng) {
        int randomValue = rng.nextInt(totalWeight);
        WeightedElement weightedElement = weightedElementsMap.higherEntry(randomValue).getValue();
        return weightedElement.isRanged() ? weightedElement.getValueRanged(rng) : weightedElement.value();
    }

    public List<WeightedElement> toList() {
        return weightedElementsMap.values().stream().toList();
    }
}
