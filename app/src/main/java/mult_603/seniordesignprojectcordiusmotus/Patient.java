package mult_603.seniordesignprojectcordiusmotus;

/**
 * Created by Wes on 10/1/16.
 */
public class Patient {

    public String patientUserName;
    private double heartRate;
    private double temperature;

    public Patient(){

    }

    public double getHeartRate(){
        return heartRate;
    }

    public double getTemperature(){
        return temperature;
    }

    public String getPatientUserName(){
        return patientUserName;
    }

    public void setHeartRate(double hr){
        this.heartRate = hr;
    }

    public void setTemperature(double temp){
        this.temperature = temp;
    }

    public void setPatientUserName(String uName){
        this.patientUserName = uName;
    }

    @Override
    public String toString(){
        String str = patientUserName + " " + heartRate + " " + temperature + " ";
        return str;
    }

}
