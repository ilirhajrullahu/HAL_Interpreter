package hda.bs.MMU;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

public class MMU {

    private final LinkedList<Page> pageTable;
    private final Page[] speicher; //Festplatte

    private Register register;

    FileWriter fw;
    BufferedWriter bw;

    private boolean rplMode = false;


    public MMU(boolean rplMode) throws IOException {

        this.rplMode = rplMode;

        fw = new FileWriter("pageErrors.txt");
        bw = new BufferedWriter(fw);

        speicher = new Page[64];
        pageTable = new LinkedList<>();
        register = new Register();

        //basis speicher & pageTable init
        for (int i = 0; i < 64; ++i) {
            Page page = new Page(i);
            speicher[i] = page;
            pageTable.add(page);
        }
        //erste vier Seiten in RAM lasen
/*        for (int j = 0; j < 4; ++j) {
            register.addNewPage(speicher[j]);
            pageTable.get(j).setRefAndFrameNr(true, j);
        }*/
    }


    public int findPageFrame(int rpagenr) throws IOException {

        if (pageTable.isEmpty()) {
            return -1;
        } else {
            for (int i = 0; i < pageTable.size(); ++i) {
                if (pageTable.get(i).getPagenr() == rpagenr) {
                    return pageTable.get(i).getFramenr();
                }
            }
        }
        return -1;
    }

    public Page pageError(int rpagenr) throws IOException {

        Date date = new Date();
        bw.write("PageError für Page " + rpagenr + " - " + date.toString());
        bw.newLine();
        bw.flush();


        for (int i = 0; i < speicher.length; ++i) {
            if (speicher[i].getPagenr() == rpagenr) {
                return speicher[i];
            }
        }
        return null;
    }

    public void fifoRotation(Page loadPage) {
        if (register.getSize() == 4) {
            Page p;
            if (rplMode) {
                p = register.removeRandom();
            } else {
                p = register.removeOldestPage();
            }
            int removedFrameNr = p.getFramenr();
            p.setRefAndFrameNr(false, -1);
            speicher[p.getPagenr()] = p;

            loadPage.setRefAndFrameNr(true, removedFrameNr);
        } else {
            loadPage.setRefAndFrameNr(true, loadPage.getPagenr());
        }

        register.addNewPage(loadPage);
    }

    public int identifyPageNr(int vAddress) {
        if (vAddress <= 65535) {
            //int memoryAdress = Math.round(param);
            //return (short) memoryAdress;
            return (int) Math.floor((vAddress / 1024));
        }
        return -1;
    }

    public int identifyOffset(int vAdress, int physicalSize) {
        return vAdress % physicalSize;
    }

    public boolean identifyRefBit(int pageno) {

        for (int i = 0; i < pageTable.size(); ++i) {
            if (pageTable.get(i).getPagenr() == pageno) {
                return pageTable.get(i).isRefbit();
            }
        }
        return false;
    }

    public boolean setValue(int vAdress, double wert) throws IOException {
        int pagenr = identifyPageNr(vAdress);
        int pageFrameNo = findPageFrame(pagenr);
        boolean refBit = identifyRefBit(pagenr);
        int rest = identifyOffset(vAdress, 1024);
        int pAdress = pageFrameNo * 1024 + rest;

        Page loadPage;

/*        if (pageFrameNo == -1) {
            System.out.println("VIRTUELLER SPEICHERBEREICH bei set ÜBERSCHRITTEN");
            return false;
        }*/

        if (refBit == false || pageFrameNo == -1) {
            loadPage = pageError(pagenr);
            fifoRotation(loadPage);
            pageFrameNo = loadPage.getFramenr();
        } else {
            register.setPageToTop(pagenr);
        }


        register.setWert(pageFrameNo, rest, wert);
        //System.out.println("setvalue vor rest");
        return true;
    }


    public double getValue(int vAdress) throws IOException {
        int pagenr = identifyPageNr(vAdress);
        int pageFrameNo = findPageFrame(pagenr);
        boolean refBit = identifyRefBit(pagenr);

        int rest = identifyOffset(vAdress, 1024);
        int pAdress = pageFrameNo * 1024 + rest;

        Page loadPage;


        if (refBit == false || pageFrameNo == -1) {
            loadPage = pageError(pagenr);
            fifoRotation(loadPage);
            pageFrameNo = loadPage.getFramenr();
        } else {
            register.setPageToTop(pagenr);
        }

        return register.getWert(pageFrameNo, rest);
    }


    public void printRegister() {

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 1024; ++j) {
                System.out.println("\t\tRegister " + i + "." + j + "\t > " + register.getWert(i, j));
            }
        }
    }
}