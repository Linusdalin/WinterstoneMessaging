package email;

import remoteData.dataObjects.User;

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

    private User user;

    public ContentBoxManager(User user) {

        this.user = user;
    }

    //TODO: Not implemented adding any content here

    public List<String> getBoxes(int max) {

        List<String> boxes = new ArrayList<>();

        boxes.add("<li> Remember you can play both mobile and on Facebook </li>");

        return boxes;

    }
}
