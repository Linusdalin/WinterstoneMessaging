package statistics;

/**************************************************************************''
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2015-05-30
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class Display {

    public static String fixedLengthLeft(String string, int length) {

        if(string.length() > length)
            string = string.substring(0, length);

        return String.format("%-"+length+ "s", string);
    }

    public static String fixedLengthLeft(int playerCount, int length) {

        return fixedLengthLeft(String.valueOf(playerCount), length);
    }

    public static String fixedLengthRight(String string, int length) {

        if(string.length() > length)
            string = string.substring(0, length);

        return String.format("%"+length+ "s", string);
    }

    public static String fixedLengthRight(int val, int length) {

        return fixedLengthRight(String.valueOf(val), length);
    }

}
