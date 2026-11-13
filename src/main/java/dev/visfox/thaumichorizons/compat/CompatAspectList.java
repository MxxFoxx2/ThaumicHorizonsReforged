package dev.visfox.thaumichorizons.compat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;

public final class CompatAspectList {
    private final LinkedHashMap<String, Integer> aspects = new LinkedHashMap<>();

    public CompatAspectList add(String tag, int amount) {
        if (amount <= 0) {
            return this;
        }
        aspects.merge(tag, amount, Integer::sum);
        return this;
    }

    public CompatAspectList add(CompatAspectList other) {
        for (Map.Entry<String, Integer> entry : other.aspects.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public CompatAspectList remove(String tag, int amount) {
        int remaining = getAmount(tag) - amount;
        if (remaining <= 0) {
            aspects.remove(tag);
        } else {
            aspects.put(tag, remaining);
        }
        return this;
    }

    public int getAmount(String tag) {
        Integer amount = aspects.get(tag);
        return amount == null ? 0 : amount;
    }

    public List<String> getTags() {
        return List.copyOf(aspects.keySet());
    }

    public int size() {
        return aspects.size();
    }

    public int totalAmount() {
        int total = 0;
        for (int amount : aspects.values()) {
            total += amount;
        }
        return total;
    }

    public boolean isEmpty() {
        return aspects.isEmpty();
    }

    public CompatAspectList copy() {
        CompatAspectList copy = new CompatAspectList();
        copy.aspects.putAll(aspects);
        return copy;
    }

    public Map<String, Integer> asMap() {
        return Map.copyOf(aspects);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CompatAspectList list && list.aspects.equals(aspects);
    }

    @Override
    public int hashCode() {
        return aspects.hashCode();
    }

    @Override
    public String toString() {
        return aspects.toString();
    }
}
