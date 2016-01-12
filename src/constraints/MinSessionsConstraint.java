package constraints;

import core.PlayerInfo;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-12-23
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
public class MinSessionsConstraint implements ConstraintInterface{

    private int sessions;

    public MinSessionsConstraint(int sessions){


        this.sessions = sessions;
    }


    public String getMessage(PlayerInfo info){

        return "not implemented error mess";
    }

    @Override
    public boolean evaluate(PlayerInfo info, Timestamp executionTime) {
        return true;  //TODO: Not implemented
    }

}
