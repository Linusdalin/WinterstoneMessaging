package email;

import remoteData.dataObjects.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**********************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-02-17
 * Time: 18:06
 * To change this template use File | Settings | File Templates.
 */

public class ContentBoxManager {

    protected static final String canvasImageURL = "d24xsy76095nfe.cloudfront.net/email/canvas";


    private User user;
    ReleasedGame game = new ReleasedGame("Cocktail Cherries", "cocktail_cherries", Timestamp.valueOf("2016-04-07 00:00:00"),
            "More than a century ago, one American engineer created an innovation that fundamentally changed the entertainment industry!");


    public ContentBoxManager(User user) {

        this.user = user;
    }


    public List<String> getBoxes(int max) {

        List<String> boxes = new ArrayList<>();
        int count = max;

        addNewGameBox(boxes, count);

        //boxes.add("<li> Remember you can play both mobile and on Facebook </li>");

        return boxes;

    }

    private void addNewGameBox(List<String> boxes, int count) {

        if(count == 0)
            return;

        //TODO: Add time check

        String box = "<table width=\"100%\"><tr>" +
                "<td width=\"20%\"><img src=\"https://"+canvasImageURL+"/" +game.gameCode +".jpg\" width=80px></a></td>" +
                "<td width=\"60%\"><p><b>Don't miss <i>"+ game.name+"</i> on Facebook</b><br/>"+ game.promotionText +"</p></td>" +
                "<td width=\"20%\"><a href=\"https://apps.facebook.com/slotamerica/?game="+game.gameCode+"\">Play Now!</a></td>" +
                "</tr></table>";

        boxes.add(box);


    }

    private class ReleasedGame {

        private final String name;
        private final String gameCode;
        private final Timestamp release;
        private final String promotionText;

        public ReleasedGame(String name, String gameCode, Timestamp release, String promotionText) {

            this.name = name;
            this.gameCode = gameCode;
            this.release = release;
            this.promotionText = promotionText;
        }
    }
}
