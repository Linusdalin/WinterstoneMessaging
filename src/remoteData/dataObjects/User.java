package remoteData.dataObjects;


import java.sql.Timestamp;

/**
 *
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-04-16
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class User {


    public final String facebookId;
    public final String name;
    public final String email;
    public final String promoCode;
    public final String lastgamePlayed;
    public final Timestamp created;
    public final int payments;
    public final int amount;
    public final int totalWager;
    public final int balance;
    public final int nextNumberOfPicks;

    public User(String facebookId, String name, String email, String promoCode, String lastgamePlayed, Timestamp created, int payments, int amount, int totalWager, int balance, int nextNumberOfPicks){


        this.facebookId = facebookId;

        this.name = name;
        this.email = email;
        this.promoCode = promoCode;
        this.lastgamePlayed = lastgamePlayed;
        this.created = created;
        this.payments = payments;
        this.amount = amount;
        this.totalWager = totalWager;
        this.balance = balance;
        this.nextNumberOfPicks = nextNumberOfPicks;
    }

    public String toString(){

        return "(" + facebookId + ", " +name + ", " +promoCode + ", " +created.toString() +  ")";

    }

}
