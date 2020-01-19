package hda.bs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class HalOS {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";

    static Scanner eingabe = new Scanner(System.in);

    public static void main(String[] args) throws IOException, InterruptedException {


        String input = "?";

        System.out.println(ANSI_RED + "\t\t * * * * * * * * * * * * * *");
        System.out.println("\t\t *          HAL-OS         *");
        System.out.println("\t\t * incl.Speicherverwaltung *");
        System.out.println("\t\t * * * * * * * * * * * * * *" + ANSI_RESET);
        System.out.println();

        OSManager manager = new OSManager();
        System.out.println(" > Parameter werden initialisiert...");

        if(args.length!=0){
            input = args[0];
        }

        while(!manager.configEinlesen(input)){
            System.out.print(" > Konfigdatei: ");
            input = eingabe.nextLine();
        }

        manager.start();

        System.out.println(" > HAL-OS wird beendet...");
        System.out.println();

    }
}