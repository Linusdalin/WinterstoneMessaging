package test;


import org.junit.Test;
import receptivity.ReceptivityProfile;

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

    private static final int NO_DAY     = -1;

    @Test
    public void exampleTest(){

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

}
