package balanceAnalysis;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-01-05
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class ActiveUser {


    private final String facebookId;
    private Game first = null;
    private Game last = null;

    ActiveUser(String facebookId, Game last){


        this.facebookId = facebookId;
        this.last = last;
    }

    public Game getLast() {
        return last;
    }

    public Game getFirst() {
        return first;
    }

    public void setLast(Game last) {
        this.last = last;
    }

    public void setFirst(Game first) {

        if(this.first == null)
            this.first = first;
    }


    public String getFacebookId() {
        return facebookId;
    }
}
