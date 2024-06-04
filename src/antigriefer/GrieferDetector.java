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
     * @param action  player action
     * @param context behavior context
     * @return detect result
     */
    DetectResult detect(Administration.PlayerAction action, PlayerBehaviorContext context);

    /** Griefer detect result */
    enum EnumDetectResult {
        /** ? */
        NOPE,
        /** May be */
        DANGER,
        /** Yes it is */
        CONFIRMED,
    }

    class DetectResult {
        String reason;
        EnumDetectResult level;

        @Override
        public String toString() {
            return "DetectResult{" +
                "reason='" + reason + '\'' +
                ", level=" + level +
                '}';
        }
    }
}
