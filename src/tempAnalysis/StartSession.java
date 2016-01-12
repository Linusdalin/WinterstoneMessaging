package tempAnalysis;

import dbManager.ConnectionHandler;
import dbManager.DatabaseException;
import firstGameExperience.GameExperience;
import remoteData.dataObjects.GameSession;
import remoteData.dataObjects.GameSessionTable;
import remoteData.dataObjects.User;
import remoteData.dataObjects.UserTable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/*********************************************************************
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-12-09
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */


public class StartSession {

    private static final String START = "2015-05-01";
    private static final int SESSIONS = 10000;


    public static void main(String[] args){

        StartSession analyser = new StartSession();
        analyser.analyseAll(START, SESSIONS);
    }

    private void analyseAll(String startDate, int sessions) {

        Connection connection = ConnectionHandler.getConnection(ConnectionHandler.Location.local);
        Connection remoteConnection = ConnectionHandler.getConnection(ConnectionHandler.Location.remote);


        UserTable allPlayers = new UserTable();
        try {
            allPlayers.load(remoteConnection, " and users.created >= '" + startDate + "' and users.uninstall=0", "ASC", sessions, -1);      // Restriction for testing
        } catch (DatabaseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        User user = allPlayers.getNext();
        int userCount = 1;

        if(user == null)
            return;

        List<GameExperience> startSessions = new ArrayList<>();

        while(user != null){

            System.out.println(" ----------------------------------------------------------\n  " + userCount++ + "- Evaluating User "+ user.toString());
            GameSessionTable allSessions = new GameSessionTable();

            // Get all sessions for player

            allSessions.loadAndRetry(connection, "and facebookid = '"+ user.facebookId+"' and timestamp > '" + startDate + "'", "ASC", sessions);

            GameSession session = allSessions.getNext();

            if(session != null){


                System.out.println("  - first session " + session.toString());
                registerFirstExperience(session, startSessions, user.payments, user.sessions);

            }

            user = allPlayers.getNext();
        }

        System.out.println("Start Sessions:");

        for (GameExperience experience : startSessions) {

            System.out.print(experience.startSession.timeStamp.toString().substring(0, 10) + ", ");
            System.out.print(experience.startSession.game + ", ");

            if(experience.startSession.spins > 30)
                System.out.print((experience.startSession.totalWin - experience.startSession.totalWager) / experience.startSession.spins + ", ");
            else
                System.out.print("0 , ");

            System.out.print(experience.totalGamePayments+ ", ");
            System.out.print(experience.totalGameSessions);

            System.out.println("");

        }

    }

    private void registerFirstExperience(GameSession session, List<GameExperience> startSessions, int payments, int sessions) {

        GameExperience experience = new GameExperience(session, payments, sessions);
        startSessions.add(experience);
    }


    private boolean isFirstGameExperience(GameSession session, List<GameSession> sessions) {

        for (GameSession experience : sessions) {

            if(session.game.equals(experience.game) &&
                    session.facebookId.equals(experience.facebookId))
                return false;
        }

        return true;


    }


}
