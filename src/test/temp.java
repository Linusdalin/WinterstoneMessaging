package test;

import output.DeliveryException;
import output.GiveAwayHandler;
import output.NotificationHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-11-30
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class temp {

    private static String testUser = null;          //"627716024"

    private Reimbursement[] reimbursements = {

            new Reimbursement("10204680883602031", 10,  25000),
            new Reimbursement("1494520887",	    2,    25000),
            new Reimbursement("990425367680401",	2,    25000),
            new Reimbursement("825907837504184", 	25, 100000),
            new Reimbursement("749446095176182", 	2,    25000),
            new Reimbursement("10203734410229962",	5, 10000),
            new Reimbursement("10204084891702333",	2,    25000),
            new Reimbursement("804234392989566",	5, 10000),
            new Reimbursement("867678773345710",	6,    15000),
            new Reimbursement("100004355851573",	6,    15000),
            new Reimbursement("100004355851573",	5 , 10000),
            new Reimbursement("969939579685422",	6   , 15000),
            new Reimbursement("969939579685422",	5  , 10000),
            new Reimbursement("100000355330825",	2,    25000),
            new Reimbursement("10205394282802637",	2,    25000),
            new Reimbursement("10204084891702333",	3    , 5000),
            new Reimbursement("10204322960428327",	2,    25000),
            new Reimbursement("10200825773483117",	6   , 15000),
            new Reimbursement("100005385882341",	2,    25000),
            new Reimbursement("751395251631837",	3   , 5000),

    };




    public static void main(String[] args){


        temp t = new temp();
        t.reimburse(3.0);



    }

    private void reimburse(double factor) {

        for (Reimbursement reimbursement : reimbursements) {

            int coins = (int)(reimbursement.coins * factor);

            System.out.println("Reimbursing player " + reimbursement.user + " " + coins + " coins");

            try {

                GiveAwayHandler giveAwayHandler = new GiveAwayHandler(testUser)
                    .toRecipient(reimbursement.user)
                    .withAmount(coins);

                giveAwayHandler.send();

                NotificationHandler notificationHandler = new NotificationHandler(testUser)
                        .withRecipient(reimbursement.user)
                        .withMessage("You have got " + coins + " coins back for your purchase on the No Risk weekend. Click here to play again :-)");

                notificationHandler.send();


            } catch (DeliveryException e) {

                e.printStackTrace();
            }
        }

    }


    private class Reimbursement{

        public final String user;
        public final int payment;
        public final int coins;

        Reimbursement(String user, int payment, int coins){

            this.user = user;
            this.payment = payment;
            this.coins = coins;
        }
    }
}
