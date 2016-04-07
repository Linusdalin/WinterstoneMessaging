package transfer;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-03-15
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */
public class PaymentBehavior {

        public final int averageBet;
        public final int spins;

        PaymentBehavior(int averageBet, int spins){


            this.averageBet = averageBet;
            this.spins = spins;
        }

        public String toString(){

            return "Bet:" + averageBet + " Spins:" + spins + " (Total: "+ getTotal()/1000+")";
        }

        int getTotal() {

            return averageBet * spins;
        }

}
