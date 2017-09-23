package org.ea.service;

import java.lang.Exception;

public class App
{
    public static void main( String[] args )
    {
        while(true) {
          System.out.print( "Ahh " );
          sleep(400);
          System.out.print( "Ahh " );
          sleep(400);
          System.out.print( "Ahh " );
          sleep(400);
          System.out.print( "Ahh " );
          sleep(400);
          System.out.println( "Staying alive!" );
          System.out.println();
          System.out.println();
          System.out.println();
          System.out.println();
          System.out.println();
          sleep(1000);

        }
    }

    public static void sleep(int nanos) {
      try {
        Thread.sleep(nanos);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
