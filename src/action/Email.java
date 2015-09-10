package action;

/***********************************************************************
 *
 *              The representation of an email
 */

public class Email {

    public final String subject;
    public final String html;
    public final String plainText;

    public Email(String subject, String html, String plainText){

        this.subject = subject;
        this.html = html;
        this.plainText = plainText;
    }

}
