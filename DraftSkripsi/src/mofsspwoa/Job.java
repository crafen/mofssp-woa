package mofsspwoa;

/**
 *
 * @author Craven Sachio Saputra
 */
public class Job {
    private int numOper;
    private int[] operTimes;
    
    /**
     * Constructor dari kelas Job untuk menginisialisasi variabel-variabel yang ada.
     * @param numOper 
     */
    public Job(int numOper){
        this.numOper = numOper;
        this.operTimes = new int[numOper];
    }

    public void setOperTimes(int[] operTimes){
        this.operTimes = operTimes;
    }

    public int getOperTimes(int idx) {
        return operTimes[idx];
    }
    
}
