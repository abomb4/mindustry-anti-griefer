package antigriefer;

import mindustry.net.Administration;

/**
 * Algorithms to detect griefer
 *
 * @author abomb4 2023-09-05
 */
public interface GrieferDetector {

    /**
     * Detect
     *
     * @param action player action
     * @return detect result
     */
    EnumDetectResult detect(Administration.PlayerAction action);

    /** Griefer detect result */
    enum EnumDetectResult {

        /** Very good */
        SAFE,
        /** Good */
        MAYBE_SAFE,
        /** ? */
        NOPE,
        /** May be */
        DANGER,
        /** Yes it is */
        CONFIRMED,
    }
}
