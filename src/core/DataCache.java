package core;

import remoteData.dataObjects.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/***********************************************************************
 *
 *              Precached data that then will be used to filter data per user
 *
 */

public class DataCache {

    private List<GameSession> allSessions = new ArrayList<GameSession>();
    private List<Payment> allPayments;
    private Connection connection;

    /********************************************************************************
     *
     *
     * @param connection
     * @param startDate
     * @param limit                        - how many users
     */


    public DataCache(Connection connection, String startDate, int limit){

        System.out.println(" **********************************************************\n*  Loading cached data...");

        this.connection = connection;
        //GameSessionTable sessionTable = new GameSessionTable();
        //sessionTable.load(connection,"timestamp > '"+ startDate+"'", limit );
        //allSessions = sessionTable.getAll();

        //System.out.println(" -- Got " + allSessions.size() + " sessions after " + startDate);

        PaymentTable paymentTable = new PaymentTable();
        paymentTable.load(connection,"timestamp > '"+ startDate+"'", "ASC", limit );
        allPayments = paymentTable.getAll();

        //System.out.println(" -- Got " + allPayments.size() + " payments after " + startDate);

    }

    public List<GameSession> getSessionsForUser(User user) {

        GameSessionTable sessionTable = new GameSessionTable();
        sessionTable.load(connection,"facebookId='" + user.facebookId + "'","ASC", -1 );

        List<GameSession> sessionsForUser = sessionTable.getAll();

        System.out.println(" -- Got " + sessionsForUser.size() + " sessions for user " + user.name);

        return sessionsForUser;

    }

    public List<Payment> getPaymentsForUser(User user) {

        List<Payment> paymentsForUser = new ArrayList<Payment>();

        for (Payment payment : allPayments) {
            if(payment.facebookId.equals(user.facebookId))
                paymentsForUser.add(payment);
        }

        return paymentsForUser;


    }
}