package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by Wes on 4/13/17.
 */
public class BluetoothData {
    private Double seconds;
    private Double vitals;

    public BluetoothData(){

    }

    public BluetoothData(Double seconds, Double vitals){
        this.seconds = seconds;
        this.vitals  = vitals;
    }

    public void setSeconds(Double seconds){
        this.seconds = seconds;
    }

    public void setVitals(Double vitals){
        this.vitals = vitals;
    }

    public double getSeconds(){
        return this.seconds;
    }

    public double getVitals(){
        return this.vitals;
    }

    @Override
    public String toString(){
        return ("Seconds: " + this.seconds + "\n"
                + "Vitals: " + this.vitals);
    }
}
