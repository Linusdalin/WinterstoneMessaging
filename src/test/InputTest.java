package test;


import core.CampaignEngine;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/*****************************************************************************''
 *
 *
 *
 */

public class InputTest {



    public static void main2(String[] args){

        try{

            System.out.println("\n>");
            String command = CampaignEngine.waitReturn();
            System.out.println("Got: " + command);

        }catch(Exception e){

            assertTrue(false);
        }

    }


    public static void main(String[] args){

        java.io.InputStreamReader reader = new java.io.InputStreamReader(System.in);

        String token = null;
        char[] buffer = new char[1024];

        try {

            while(token == null){

                if(reader.ready()) {

                    reader.read(buffer);
                    token = new String(buffer);
                    System.out.println("Got: " + token);
                }
                else{

                    Thread.sleep(2000);
                    System.out.print(".");
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

}

}
