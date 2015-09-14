package email;

/*********************************************************************************
 *
 *
 *
 */


public class ReleaseEmail extends AbstractEmail implements EmailInterface {

    private static final String Template = "messageMailTemplate";


    private String title;
    private final String imageURL;
    private final String link;

    public ReleaseEmail(String subject, String title, String html, String plainText, String imageURL, String link){

        super(subject, html, plainText, Template);

        this.title = title;
        this.imageURL = imageURL;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getLink() {
        return link;
    }
}
