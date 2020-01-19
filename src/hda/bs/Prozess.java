package hda.bs;

import hda.bs.MMU.MMU;
import hda.bs.MMU.Programmspeicher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Prozess extends Thread {

    static Scanner eingabe;
    private boolean debug = false;
    private int sleepTime = 1000;
    private boolean rplMode = false; //Page-Load Modus

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_RED = "\u001B[31m";

    public String configFile;
    public int pid;
    public String pfad;
    //public inputBuffer inpBuf;


    private double accu;
    //private double[] register = new double[16];
    //private ArrayList<Task> Program = new ArrayList<>();
    public Programmspeicher programmspeicher;
    private HashMap<Integer, Buffer> buffs = new HashMap<Integer, Buffer>();

    long start, finish, breakStart, breakFinish, timeElapsed, timeWait = 0; //Prozesslaufzeit in ms

    private MMU mmu;

    public Prozess(String rconfigFile, Scanner scanner, boolean debug, int sleepTime, boolean rplMode) throws IOException {

        this.debug = debug;
        this.sleepTime = sleepTime;
        this.rplMode = rplMode;

        this.accu = 0;
        this.pid = -1;
        this.pfad = "?";
        this.configFile = rconfigFile;

        eingabe = scanner;
        this.mmu = new MMU(rplMode);

        String[] line = new String[2];
        line = rconfigFile.split(" ");
        this.pid = Integer.parseInt(line[0]);
        if (line.length > 1) {
            this.pfad = line[1];
        }

        programmspeicher = new Programmspeicher(this.pfad);

        //this.inpBuf = inpBuf;
    }

    public void run() {
        String befehl;
        double param = 0.0;
        int bausteinID;


        while (programmspeicher.getPc() < programmspeicher.getSize()) {
            Task t = programmspeicher.getNext();
            befehl = t.befehl;
            param = t.param;
            bausteinID = (int) param;

            try {

                switch (befehl) {
                    case "START":
                        start = System.currentTimeMillis();
                        System.out.println("HAL" + pid + " > " + befehl + " | " + pfad);
                        sleep(sleepTime);
                        break;
                    case "STOP":
                        finish = System.currentTimeMillis();
                        timeElapsed = (finish - start) - timeWait;
                        System.out.println("HAL" + pid + " > STOP | LFZ: " + timeElapsed + " ms + Wait: " + timeWait + " ms = " + (timeElapsed + timeWait) + " ms");
                        sleep(sleepTime);
                        break;
                    case "OUT":
                        if (bausteinID == 1) {
                            System.out.println(ANSI_GREEN + "HAL" + pid + " > " + accu + ANSI_RESET);
                        } else {
                            breakStart = System.currentTimeMillis();
                            buffs.get(bausteinID).put(accu);
                            breakFinish = System.currentTimeMillis();
                            timeWait = timeWait + (breakFinish - breakStart);
                        }
                        printDebug(befehl,param);
                        break;
                    case "IN":
                        breakStart = System.currentTimeMillis();
                        if (bausteinID == 0) {
                            System.out.println("HAL" + pid + " > " + befehl + ": ");
                            accu = input();
                        } else {
                            accu = (buffs.get(bausteinID)).get();
                        }
                        breakFinish = System.currentTimeMillis();
                        timeWait = timeWait + (breakFinish - breakStart);
                        //printDebug(befehl,param);
                        break;
                    case "LOAD":
                        accu = mmu.getValue((int) param);
                        //accu = register[(int) param];
                        printDebug(befehl,param);
                        break;
                    case "LOADNUM":
                        accu = param;
                        printDebug(befehl,param);
                        break;
                    case "STORE":
                        mmu.setValue((int) param, accu);
                        //register[(int) param] = accu;
                        printDebug(befehl,param);
                        break;
                    case "JUMPNEG":
                        if (accu < 0) {
                            programmspeicher.setPc((int) param);
                        }
                        printDebug(befehl,param);
                        break;
                    case "JUMPPOS":
                        if (accu > -1) {
                            programmspeicher.setPc((int) param);
                        }
                        printDebug(befehl,param);
                        break;
                    case "JUMPNULL":
                        if (accu == 0) {
                            programmspeicher.setPc((int) param);
                        }
                        printDebug(befehl,param);
                        break;
                    case "JUMP":
                        programmspeicher.setPc((int) param);
                        printDebug(befehl,param);
                        break;
                    case "ADD":
                        accu = accu + mmu.getValue((int) param);
                        printDebug(befehl,param);
                        break;
                    case "ADDNUM":
                        accu = accu + param;
                        printDebug(befehl,param);
                        break;
                    case "SUB":
                        accu = accu - mmu.getValue((int) param);
                        printDebug(befehl,param);
                        break;
                    case "MUL":
                        accu = accu * mmu.getValue((int) param);
                        printDebug(befehl,param);
                        break;
                    case "DIV":
                        accu = accu / mmu.getValue((int) param);
                        printDebug(befehl,param);
                        break;
                    case "SUBNUM":
                        accu = accu - param;
                        printDebug(befehl,param);
                        break;
                    case "MULNUM":
                        accu = accu * param;
                        printDebug(befehl,param);
                        break;
                    case "DIVNUM":
                        accu = accu / param;
                        printDebug(befehl,param);
                        break;
                    case "LOADIND":
                        double temp1 = mmu.getValue((int) param);
                        accu = mmu.getValue((int) temp1);
                        //accu = register[(int) register[(int) param]];
                        printDebug(befehl,param);
                        break;
                    case "STOREIND":
                        double temp2 = mmu.getValue((int) param);
                        mmu.setValue((int) temp2, accu);
                        //register[(int) register[(int) param]] = accu;
                        printDebug(befehl,param);
                        break;
                    case "DUMPREG":
                        dumpreg();
                        printDebug(befehl,param);
                        break;
                    case "DUMPPROG":
                        dumpprog();
                        printDebug(befehl,param);
                        break;
                    default:
                        System.out.println("UNBEKANNTER BEFEHL!");
                        sleep(sleepTime);
                        break;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printDebug(String befehl, double param) throws InterruptedException {
        if(debug){
            if(param==-1) {
                System.out.println("HAL" + pid + " > " + befehl);
                sleep(sleepTime);
            }else{
            System.out.println("HAL" + pid + " > " + befehl + " | " + param);
            sleep(sleepTime);}
        }
    }

    public void connect(Buffer buffer, int BsID) {
        buffs.put(BsID, buffer);
        //BsID = E/A BauStein ID
    }

    public synchronized double input() {
        synchronized (eingabe) {
            return Double.parseDouble(eingabe.nextLine());
        }
    }

    public void dumpreg() throws IOException {
        System.out.println(ANSI_PURPLE + "HAL" + pid + " > DUMPREG:");
        mmu.printRegister();
        System.out.println(ANSI_RESET);
    }

    public void dumpprog() {
        System.out.println(ANSI_RED + "HAL" + pid + " > DUMPPROG:");
        for (int i = 0; i < programmspeicher.getSize(); i++) {
            System.out.println("\t\t" + programmspeicher.get(i).befehl + " \t\t " + (int) programmspeicher.get(i).param);
            //buffs.get(2).put(programmspeicher.get(i).befehl + " " + (int) programmspeicher.get(i).param); Über Kanal 2? Buffer double?
        }
        System.out.println(ANSI_RESET);
    }
}