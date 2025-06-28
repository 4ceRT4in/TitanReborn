package net.shirojr.titanfabric.util;

import java.util.Objects;

/**
 * Data structure which allows for first and second entry order ambiguity
 *
 * @param <T> datatype of entries
 */
public record UnorderedPair<T>(T first, T second) {
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnorderedPair<?> other)) return false;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second) ||
                Objects.equals(first, other.second) && Objects.equals(second, other.first);
    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return "UnorderedPair{" + first + ", " + second + '}';
    }
}
