package test;


import org.junit.Test;
import receptivity.ReceptivityProfile;
import remoteData.dataObjects.GameSession;

import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  test Giving coins to a player
 *
 *
 */

public class ReceptivityTest {

    private static final int SUNDAY     = 0;
    private static final int MONDAY     = 1;
    private static final int TUESDAY    = 2;
    private static final int WEDNESDAY  = 3;
    private static final int THURSDAY   = 4;
    private static final int FRIDAY     = 5;
    private static final int SATURDAY   = 6;

    private static final int DAYTIME   = 0;
    private static final int EVENING   = 1;
    private static final int NIGHTTIME   = 2;


    private static final int NO_DAY     = -1;

    @Test
    public void dayTest(){

        try{                                           //17, 16, 12, 14, 15, 24, 24,

            ReceptivityProfile profile;
            int day;

            profile = new ReceptivityProfile("user", new int[] {0,0,0,0,0,0,0,});
            day = profile.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("No significance", day, is( NO_DAY ));

            profile = new ReceptivityProfile("user", new int[] {6, 5, 3, 6, 5, 10, 11 });
                    day = profile.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("Expecting Sunday", day, is( SATURDAY ));

            profile = new ReceptivityProfile("user", new int[] {20, 19, 20, 18, 21, 19, 0});
            day = profile.getFavouriteDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("No significance", day, is( NO_DAY ));



        }catch(Exception e){

            assertTrue(false);
        }

    }

    @Test
    public void timeOfDayTest(){

        try{                                           //17, 16, 12, 14, 15, 24, 24,

            ReceptivityProfile profile;
            int day;

            profile = new ReceptivityProfile("user", new int[][] {
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
            });
            day = profile.getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("No significance", day, is( NO_DAY ));

            profile = new ReceptivityProfile("user", new int[][] {
                    {0,1,0},
                    {1,0,0},
                    {0,2,0},
                    {0,1,1},
                    {2,1,3},
                    {0,3,0},
                    {0,1,1},
            });
            day = profile.getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("Expecting Evening", day, is( EVENING ));



        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }

    }


    /**************************************************************************************
     *
     *                          US              GMT
     *          0: day          06:00 - 17:00     11:00 - 23:00
     *          1: evening      17:00 - 23:00     23:00 - 04:00
     *          2: night        23:00 - 06:00     04:00 - 11:00
     *
     */

    @Test
    public void timeOfDaySessionTest(){

        try{                                           //17, 16, 12, 14, 15, 24, 24,

            GameSession session = new GameSession(Timestamp.valueOf("2015-11-01 03:00:00"), "sessionid", "theGame", "1111111",
                    "", "", "", Timestamp.valueOf("2015-11-01 10:00:00"), 1000, 900, 200, 10, 10, "clientType");

            ReceptivityProfile profile;
            int timeOfDay ;

            profile = new ReceptivityProfile("user", new int[][] {
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
                    {0,0,0},
            });

            profile.registerSession(session);

            System.out.println("Profile:" + profile.toString());

            timeOfDay = profile.getFavouriteTimeOfDay(ReceptivityProfile.SignificanceLevel.GENERAL);
            assertThat("Evening player", timeOfDay, is( EVENING ));


        }catch(Exception e){

            e.printStackTrace();
            assertTrue(false);
        }

    }


}
