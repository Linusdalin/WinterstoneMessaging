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
    public final int sessions;
    public final int totalWager;
    public final int balance;
    public final int nextNumberOfPicks;
    public final int userCategory;
    public int level;
    public String group;
    public String sex;
    public Timestamp lastActivity;

    public User(String facebookId, String name, String email, String promoCode, String lastgamePlayed, Timestamp created,
                int payments, int amount, int sessions, int totalWager, int balance, int nextNumberOfPicks, int userCategory, int level, String group, String sex, Timestamp lastActivity){


        this.facebookId = facebookId;

        this.name = name;
        this.email = email;
        this.promoCode = promoCode;
        this.lastgamePlayed = lastgamePlayed;
        this.userCategory = userCategory;
        this.level = level;
        this.group = group;
        this.sex = sex;
        this.lastActivity = lastActivity;

        if(created != null)
            this.created = created;
        else
            this.created = new Timestamp(2015,1,1,0,0,0,0);

        this.payments = payments;
        this.amount = amount;
        this.sessions = sessions;
        this.totalWager = totalWager;
        this.balance = balance;
        this.nextNumberOfPicks = nextNumberOfPicks;
    }


    public String toString(){

        return "(" + facebookId + ", " +name + ", " +promoCode + ", "+level + ", " +created.toString() +  ")";

    }

    public boolean isMobileFirst() {

        return promoCode.startsWith("Mobile");
    }
}
