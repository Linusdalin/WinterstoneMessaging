package localData;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/*******************************************************************
 *
 *          Times a player has played a game
 *
 *
 */
public class GamePlay {


    public final String facebookId;
    public final String game;
    public final int occurrences;
    public Timestamp lastTime;

    public GamePlay(String facebookId, String game, int occurrences, Timestamp lastTime){


        this.facebookId = facebookId;
        this.game = game;
        this.occurrences = occurrences;
        this.lastTime = lastTime;

    }

    public String toString(){

        return "(" + facebookId + ", " +game + ", " +occurrences+ ", " +lastTime +  ")";

    }

    private String toSQLValues() {

        return "'" + facebookId + "', '" +game + "', " +occurrences+ ", '" +lastTime+ "'";


    }

    public void store(Connection connection) {

        String insert = "insert into game_play values (" + toSQLValues() + ")";

        //System.out.println("Insert with: " + insert);

        try{

            Statement statement = connection.createStatement();
            statement.execute(insert);

        }catch(SQLException e){

            System.out.println("Error accessing data in database. SQL:" + insert);
            e.printStackTrace();
        }

    }


}
