package hda.bs.MMU;

public class Page {


    private int pagenr;
    private int framenr;
    private int physicalSize;
    private final double[] registerStorage;
    public boolean refbit;

    public Page(int rpagenr) {
        pagenr = rpagenr;
        framenr = -1;
        physicalSize = 1024;
        registerStorage = new double[physicalSize];
        refbit = false;
    }

    public int getPagenr() {
        return pagenr;
    }

    public int getFramenr() {
        return framenr;
    }

    public boolean isRefbit() {
        return refbit;
    }

    public void setRefAndFrameNr(boolean y, int x) {
        this.refbit = y;
        this.framenr = x;
    }

    public void setWert(int stelle, double wert) {
        registerStorage[stelle] = wert;
    }

    public double getWert(int stelle) {
        return registerStorage[stelle];
    }

}