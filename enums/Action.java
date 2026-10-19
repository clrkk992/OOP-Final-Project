package enums;

/*
 * ===== CHANGES MADE IN THIS FILE =====
 *  - NEW: RETURN_EQUIPMENT (borrower files a return notice)
 * Search for "NEW:" and "CHANGED:" to find each change.
 */
public enum Action {
    SUBMIT_REQUEST,
    CANCEL_REQUEST,
    VIEW_HISTORY,
    APPROVE_REQUEST,
    REJECT_REQUEST,
    RELEASE_EQUIPMENT,
    RECEIVE_RETURN,
    ADD_EQUIPMENT,
    REMOVE_EQUIPMENT,
    RESOLVE_DAMAGE,
    VIEW_REQUESTS,
    UPDATE_EQUIPMENT,
    VIEW_EQUIPMENTS,
    RETURN_EQUIPMENT, // NEW: lets a borrower file a return notice
}