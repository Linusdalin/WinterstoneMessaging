package test;

import java.util.Base64;

/**
 * Created with IntelliJ IDEA.
 * User: Linus
 * Date: 2016-03-14
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class temp2 {

    private static final String code =
            "SGVqIG9jaCB0YWNrIGbDtnIgZGl0dCBtZWpsLiANCg0KU3R5cmVsc2VuIHZpbGwgaMOkcm1lZCBi" +
            "ZWtyw6RmdGEgYXR0IHZpIG1vdHRhZ2l0IGRpdHQgbWVqbCBvY2gga29tbWVyIGJlaGFuZGxhIMOk" +
            "cmVuZGV0IHPDpSBzbmFydCBtw7ZqbGlnaGV0IGdlcy4gDQoNClDDpSBmw7ZyZW5pbmdlbnMgaGVt" +
            "c2lkYSwgd3d3Lmtub3BwZW4yMC5zZSwgaGFyIHZpIHNhbWxhdCBwcmFrdGlzayBpbmZvcm1hdGlv" +
            "biBmw7ZyIHbDpXJhIG1lZGxlbW1hciBzw6V2w6RsIHNvbSBmw7ZyIGFuZHJhIGludHJlc3NlbnRl" +
            "ci4gDQoNClZpZCBha3V0YSBmYXN0aWdoZXRzw6RyZW5kZW4gYmVyIHZpIHbDpXJhIG1lZGxlbW1h" +
            "ciBhdHQgdsOkbmRhIHNpZyB0aWxsIGbDtnJlbmluZ2VucyB0ZWtuaXNrYSBmw7ZydmFsdGFyZSBT" +
            "dG9yaG9sbWVuIEbDtnJ2YWx0bmluZyB1bmRlciBvcmRpbmFyaWUgdGlkZXIsIG9jaCB0aWxsIEJL" +
            "IEZhc3RpZ2hldHNzZXJ2aWNlIHVuZGVyIMO2dnJpZ2EgdGltbWFyLiBLb250YWt0aW5mbyBmaW5u" +
            "ZXIgbmkgaMOkciAtIGh0dHA6Ly93d3cua25vcHBlbjIwLnNlL21lZGxlbXNpbmZvLw0KDQpIw6Rs" +
            "c25pbmdhciwgDQoNClN0eXJlbHNlbiBmw7ZyIEtub3BwZW4gMjA=";

    public static void main(String[] args){


        byte[] decoded = Base64.getDecoder().decode(code);

        for (byte b : decoded) {

            System.out.print((char)b);
        }


        System.out.println();


    }
}
