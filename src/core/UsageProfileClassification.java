package core;

/************************************************************************
 *
 *              Clasification of the player profile to decide how to address the player
 */


public enum UsageProfileClassification {

    UNKNOWN,                 // No knowledge of the player
    CANVAS,                  // An exclusive (or almost exclusive) canvas player
    ANONYMOUS,               // Not registered by facebook
    MOBILE_AQU,              // A player that comes from a mobile channel and playing predominantly mobile
    CONVERTED,               // Converted from canvas play now playing predominately mobile
    HALF_HALF,               // Plays both
    MOBILE_TRY,              // Tried mobile but went back to
    CANVAS_PAY               // Plays on mobile but pays on facebook
    ;


    //TODO: handle HALF_HALF separately

    public boolean isMobilePlayer(){

        return this == ANONYMOUS || this == CONVERTED ||this == CANVAS_PAY ||this == MOBILE_AQU||this == HALF_HALF;
    }

    public boolean isMobileExclusive(){

        return this == ANONYMOUS || this == MOBILE_AQU;
    }


    public boolean isAnonymousMobile() {
        return this == ANONYMOUS;
    }

    public boolean hasTriedMobile(){

        return  this == MOBILE_TRY || isMobilePlayer();
    }


}
