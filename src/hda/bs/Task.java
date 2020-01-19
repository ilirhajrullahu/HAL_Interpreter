package hda.bs;

public class Task {

    public String befehl;
    public double param;

    public Task(String input){
        String temp;
        String[] line = new String[2];
        line = input.split(" ");
        this.befehl = line[0].toUpperCase();

        if(line.length >1){
            this.param = Double.parseDouble(line[1]);
        }else{
            this.param =-1.0;
        }
    }
}
