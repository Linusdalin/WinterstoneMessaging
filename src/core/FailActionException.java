package core;

/******************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-10-13
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */

public class FailActionException extends Exception{

    private int outcome;

    FailActionException( int outcome ){

        this.outcome = outcome;
    }


    public int getOutcome(){

        return outcome;
    }


}
