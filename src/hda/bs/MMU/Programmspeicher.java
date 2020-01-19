package hda.bs.MMU;

import hda.bs.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Programmspeicher {

    private final Task[] storage = new Task[4000];

    private int pc;
    private int size;
    private String befehldatei;


    public Programmspeicher(String datei) throws IOException {
        pc = 0;
        size = 0;
        befehldatei = datei;
        einlesen();
    }

    public void add(Task t) {
        storage[size] = t;
        ++size;
    }

    public int getSize() {
        return size;
    }

    public Task get(int PC) {
        return storage[PC];
    }

    public Task getNext() {
        Task t = storage[pc];
        ++pc;
        return t;
    }

    public boolean einlesen() throws IOException {

        // String datei = pfad;
        String input;

        //.txt prüfen
        if (!befehldatei.contains(".txt")) {
            befehldatei = befehldatei + ".txt";
        }

        //file exist
        File file = new File(befehldatei);
        if (!file.canRead() || !file.isFile()) {
            return false;
        }

        //Programm einlesen, Tasks anlegen und in Array sichern
        FileReader fr = new FileReader(befehldatei);
        BufferedReader br = new BufferedReader(fr);

        while ((input = br.readLine()) != null) {
            Task t = new Task(input);
            add(t);
        }
        br.close();
        return true;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getPc() {
        return pc;
    }

}
