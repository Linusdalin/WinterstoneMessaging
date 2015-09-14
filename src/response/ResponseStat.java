package response;

/************************************************************************
 *
 *          Response is only Notifications for now
 */
public class ResponseStat {

    private final int exposures;
    private final int sessions;


    ResponseStat(int exposures, int sessions){

        this.exposures = exposures;
        this.sessions = sessions;
    }

    public String toString(){

        return  sessions +" / " + exposures + (exposures > 0 ? " (" + (100*sessions)/exposures + ")" : "");
    }

}
