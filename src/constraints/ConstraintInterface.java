package constraints;

import core.PlayerInfo;

import java.sql.Timestamp;

/*************************************************************************
 *
 *          A constraint is a generic restriction on campaign.
 *
 *          It does replace code replicated over the campaigns and is a preparation for
 *          a generalization, where campaigns can be built and configured.
 */

public interface ConstraintInterface {

    String getMessage(PlayerInfo info);
    boolean evaluate(PlayerInfo info, Timestamp executionTime);

}
