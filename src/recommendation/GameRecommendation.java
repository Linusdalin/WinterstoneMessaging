package recommendation;

import java.sql.Timestamp;

/************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-09-13
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public class GameRecommendation {

    private final String recommendation;
    private final String code;
    private final String launchDate;

    GameRecommendation(String text, String code, String launchDate){

        this.recommendation = text;
        this.code = code;
        this.launchDate = launchDate;
    }

    public Timestamp getLaunchDate() {

        Timestamp ts = Timestamp.valueOf(launchDate);
        return ts;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getCode() {
        return code;
    }
}
