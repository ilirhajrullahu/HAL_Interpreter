package hda.bs.MMU;

import java.util.LinkedList;
import java.util.Random;

public class Register {

    //private final double[] registerStorage;
    //private int regID;

    private final LinkedList<Page> seitenRahmen;

    public Register() {
        seitenRahmen = new LinkedList<>();
    }

    public int getSize(){
        return seitenRahmen.size();
    }

    public boolean setWert(int pageFrameNo, int rest, double wert) {
        for (int i = 0; i < seitenRahmen.size();++i){
            if (seitenRahmen.get(i).getFramenr()==pageFrameNo){
                seitenRahmen.get(i).setWert(rest,wert);
                return true;
            }
        }
        return false;
    }

    public double getWert(int pageFrameNo, int rest){
        for (int i = 0; i < seitenRahmen.size();++i){
            if (seitenRahmen.get(i).getFramenr()==pageFrameNo){
                return seitenRahmen.get(i).getWert(rest);
            }
        }
        return -1;
    }

    public void setPageToTop(int pagenr) {
        for (int i = 0; i < seitenRahmen.size(); ++i) {
            if (seitenRahmen.get(i).getPagenr() == pagenr) {
                Page extract = seitenRahmen.get(i);
                seitenRahmen.remove(i);
                seitenRahmen.addFirst(extract);
            }
        }
    }

    public Page removeOldestPage() {
            return seitenRahmen.removeLast();
    }

    public Page removeRandom() {
        Random random = new Random();
        int r = random.nextInt(4);
        //System.out.println("\t\t -> RandomRemoved: PageFrameNo " + r);
        return seitenRahmen.remove(r);
    }

    public void addNewPage(Page p){
        seitenRahmen.addFirst(p);
    }

/*    public Page getPage(int virtualPageNo) {

        return null;
    }*/

}
