package net.shirojr.titanfabric;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks entities that need the client-side soul-fire rendering.
 *
 * <p>This class must remain environment-neutral because it is accessed by
 * common mixins that are also applied to a dedicated server.</p>
 */
public final class SoulFireEntityTracker {

    public static final Set<UUID> SOUL_FIRE_ENTITIES = new HashSet<>();

    private SoulFireEntityTracker() {
    }
}
