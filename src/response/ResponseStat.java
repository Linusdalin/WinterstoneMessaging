package response;

/************************************************************************
 *
 *          Response is only Notifications for now
 */
public class ResponseStat {

    private final int exposures;
    private final int sessions;

    private static final int STRIKE_OUT_LEVEL = 9;     //number of exposures to still get messages (Should be 10)


    ResponseStat(int exposures, int sessions){

        this.exposures = exposures;
        this.sessions = sessions;
    }

    public String toString(){

        return  sessions +" / " + exposures + (exposures > 0 ? " (" + (100*sessions)/exposures + ")" : "");
    }

    public int getExposures() {
        return exposures;
    }

    public boolean isStrikeout() {
        return sessions == 0 && exposures > STRIKE_OUT_LEVEL;
    }
}
