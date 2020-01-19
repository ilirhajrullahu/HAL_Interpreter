package hda.bs;

public class Buffer {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    private boolean available = false;
    private double data;
    private String BID = "N/A";

    public Buffer(int ppid, int cpid) { //producer, consumer
        this.BID = String.valueOf(ppid) + String.valueOf(cpid);
    }

    public synchronized void put(double x) {
        while (available) {
            try {
                System.out.println(ANSI_YELLOW + " - Buffer " + BID + " wartet auf put." + ANSI_RESET);
                wait();
            } catch (InterruptedException e) {
            }
        }
        data = x;
        System.out.println(ANSI_YELLOW + " - Buffer " + BID + " mit " + x + " beschrieben" + ANSI_RESET);
        available = true;
        notify();
    }

    public synchronized double get() {
        while (!available) {
            try {
                System.out.println(ANSI_YELLOW + " - Buffer " + BID + " wartet auf get." + ANSI_RESET);
                wait();
            } catch (InterruptedException e) {
            }
        }
        available = false;
        notify();
        System.out.println(ANSI_YELLOW + " - Buffer " + BID + " gelesen" + ANSI_RESET);
        return data;
    }

}