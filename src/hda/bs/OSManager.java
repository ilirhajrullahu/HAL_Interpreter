package hda.bs;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class OSManager {
    static Scanner eingabe = new Scanner(System.in);
    static Scanner inputAkt1 = new Scanner(System.in);
    static Scanner inputAkt2 = new Scanner(System.in);

    private ArrayList<Prozess> ProzessListe = new ArrayList<>();

    private boolean debug = false;
    private int sleeptime = 1000;
    private boolean rplMode = false; //PageLoad Modus

    public OSManager() {
        System.out.print(" > Debug-Modus aktivieren? (true/false) : ");
        debug = inputAkt1.nextBoolean();
        if (debug) {
            System.out.print(" > Ausführungsgeschindigkeit in ms:  ");
            sleeptime = inputAkt1.nextInt();
        }

        System.out.print(" > RandomPageLoad-Modus aktivieren? (true/false) : ");
        rplMode = inputAkt2.nextBoolean();
        if (rplMode) {
            System.out.println("   > FiFo-Mode: deaktiviert");
            System.out.println("   > RPL-Mode:  aktiviert");
        }else{
            System.out.println("   > FiFo-Mode: aktiviert");
            System.out.println("   > RPL-Mode:  deaktiviert");
        }
    }

    public boolean configEinlesen(String datei) throws IOException {

        String input;

        //.txt prüfen
        if (!datei.contains(".txt")) {
            datei = datei + ".txt";
        }

        //file exist
        File file = new File(datei);
        if (!file.canRead() || !file.isFile()) {
            System.out.println(" > Konfigfile konnte nicht gefunden werden.");
            return false;
        }

        //Config einlesen
        FileReader fr = new FileReader(datei);
        BufferedReader br = new BufferedReader(fr);

        while ((input = br.readLine()) != null) {
            if (input.contains(".txt")) {
                //Prozesse initialisieren
                initProzess(input);
            } else if (input.contains(">")) {
                //Prozesse verbinden
                connect(input);
            }
        }
        return true;
    }

    public void modus(boolean debug) {
        this.debug = debug;
    }


    public boolean initProzess(String input) throws IOException {
        Prozess p;
        //Buffer buffer = new Buffer();
        //inputBuffer inpBuf = new inputBuffer();
        p = new Prozess(input, eingabe, debug, sleeptime, rplMode);


        if (!pExist(p)) {
            //einlesen

            ProzessListe.add(p); //Prozess hinzufügen
        } else {
            System.out.println(" > Fehler beim hinzufürgen von " + input);
        }
        return true;
    }

    public boolean connect(String input) {
        //Prozesse verbinden

        //input zerlegen
        String[] lines = input.split(">", 2);
        String[] producer = lines[0].split(":", 2);
        String[] consumer = lines[1].split(":", 2);

        //Leerzeichen entfernen & in int parsen
        int ppid = Integer.parseInt(producer[0].replace(" ", ""));
        int pea = Integer.parseInt(producer[1].replace(" ", ""));
        int cpid = Integer.parseInt(consumer[0].replace(" ", ""));
        int cea = Integer.parseInt(consumer[1].replace(" ", ""));

        if (!pidExist(ppid) && !pidExist(cpid)) {
            System.out.println(" !> Angegebener Prozess der Verbindung existiert nicht.");
            return false;
        }

        Buffer b = new Buffer(ppid, cpid);

        for (Prozess p : ProzessListe) {
            if (p.pid == ppid) {
                p.connect(b, pea);
            }
            if (p.pid == cpid) {
                p.connect(b, cea);
            }
        }
        return true;
    }

    public boolean pidExist(int pid) {

        for (int i = 0; i < ProzessListe.size(); i++) {
            if (ProzessListe.get(i).pid == pid) {
                return true;
            }
        }
        return false;
    }

    public boolean pExist(Prozess p) {

        for (int i = 0; i < ProzessListe.size(); i++) {
            if (ProzessListe.get(i).pid == p.pid) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        Collections.reverse(ProzessListe);
        for (Prozess p : ProzessListe) {
            p.start();
        }
    }


}
