package test;


import org.junit.Test;
import remoteData.dataObjects.User;
import rewards.RewardRepository;

import java.sql.Timestamp;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *                  test Giving coins to a player
 *
 *
 */

public class RewardTest {

    private static final User user       = new User("627716024", "Linus",     "linusdalin@gmail.com", "promo", "game", new Timestamp(2015, 1, 1, 1, 1, 1, 1), 1, 5, 17, 12345, 45678, 1, 1, 1, "A", "male");


    @Test
    public void exampleTest(){

        try{                                           //17, 16, 12, 14, 15, 24, 24,

            boolean claimed = RewardRepository.hasClaimed(user, RewardRepository.homeRun);
            assertThat("Has claimed this ", claimed, is(true));

        }catch(Exception e){

            assertTrue(false);
        }

    }

}
