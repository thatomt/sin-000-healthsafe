package co.wethinkcode.healthsafe;

import java.util.List;

/** The on-call plan for one ward at one Emergency Status level. */
public record Schedule(String wardId, String department, int alertLevel, List<ShiftCover> shifts) {

    /** How many doctors are on call for one shift. */
    public record ShiftCover(String shift, int doctors) {
    }
}