package mofsspwoa;

/**
 *
 * @author Craven Sachio Saputra
 */
public class Flowshop {
    private Job[] jobs;
    
    /**
     * Constructor dari kelas Whale untuk menginisialisasi variabel-variabel yang ada.
     * @param numJobs 
     */
    public Flowshop(int numJobs){
        this.jobs = new Job[numJobs];
    }
    
    /**
     * Method untuk membalikkan sebuah job / pekerjaan pada urutan ke-idx dari array jobs.
     * @param idx
     * @return Jobs dengan indeks idx
     */
    public Job getJobIdx(int idx){
        return this.jobs[idx];
    }
    
    /**
     * Method untuk membuat sebuah job baru pada array jobs
     * dengan urutan ke-idx, dengan numOper jumlah operasi.
     * @param idx
     * @param numOper 
     */
    public void setJobIdx(int idx, int numOper){
        this.jobs[idx] = new Job(numOper);
    }
       
}
