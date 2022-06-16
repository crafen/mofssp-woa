package mofsspwoa;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
/**
 *
 * @author Craven Sachio Saputra
 */
public class WOAScheduler {
    private Whale[] whales;
    private int numWhales;
    private Flowshop flowShop;
    private int maxIter;
    private Whale optWhale;
    private int numJobs;
    private int numOpers;
    private MainFrame mainFrame;
    
    /**
     * Constructor dari kelas WOAScheduler untuk menginisialisasi variabel-variabel yang ada.
     * @param numWhales
     * @param maxIter
     * @param numJobs
     * @param numOpers
     * @param mainFrame 
     */
    public WOAScheduler(int numWhales, int maxIter, int numJobs, int numOpers, MainFrame mainFrame){
        this.numWhales = numWhales;
        this.whales = new Whale[numWhales];
        this.flowShop = new Flowshop(numJobs);      
        this.optWhale = new Whale(numJobs);
        this.numJobs = numJobs;
        this.numOpers = numOpers;
        this.maxIter = maxIter;
        this.mainFrame = mainFrame;
    }
    
    /**
     * Method untuk membangkitkan populasi awal paus.
     */
    public void populateWhales(){
        for (int i = 0; i < numWhales; i++) {
            whales[i] = new Whale(numJobs);
            whales[i].generateFirstPost();
        }
        this.optWhale = new Whale(numJobs);
        this.optWhale.generateFirstPost();
        
        this.findBestWhale();
    }
     
    /**
     * Method untuk mengisi waktu operasi yang ada untuk pekerjaan yang ada.
     * @param idxJob
     * @param operTimes 
     */
    public void fillJobs(int idxJob, int[] operTimes){
       flowShop.setJobIdx(idxJob, numOpers);
       flowShop.getJobIdx(idxJob).setOperTimes(operTimes);
    }
    
    /**
     * Method untuk melakukan optimasi menggunakan WOA untuk mencari paus dengan
     * solusi terbaik.
     */
    public void whaleOptimization(){
        int currIter = 0;
        double step = 2.0;
        double rateA = step / (maxIter - 1); // menggunakan rumus 2 /(n-1) 
        Random random = new Random();
        
        while(currIter < maxIter){
            //perbarui nilai a, C, l, p      
            double[] a = new double[numJobs]; 
            double[] r = new double[numJobs]; //random vector [0,1]
            double[] C = new double[numJobs]; // 2 * vector r
            for (int i = 0; i < numJobs; i++) {
                a[i] = step;
                r[i] = Math.random();
                C[i] = 2 * r[i];
            }
            //berkurang linear untuk vector a
            step -= rateA;
                
            double p = Math.random(); //[0,1]
            double l = Math.random() * 2 - 1; //[-1,1]
            
            for (int i = 0; i < numWhales; i++) {
                double[] newOrder = new double[numJobs];
                int[] newPost = new int[numJobs];
                int[] bestPost = optWhale.getPost();
                
                //calculate vector C
                for (int j = 0; j < numJobs; j++) {
                    C[j] = 2 * r[j];
                }
                
                //calculate vector A
                double[] A = new double[numJobs];
                for (int j = 0; j < numJobs; j++) {
                    A[j] = 2 * a[j] * r[j] - a[j];
                }
                
                if(p < 0.5){
                    if(this.absVector(A) < 1 ){
                        double[] D = new double[numJobs];
                        int[] currPost = whales[i].getPost();
                        for (int idx = 0;  idx < numJobs; idx++) {
                            D[idx] = Math.abs(C[idx] * bestPost[idx] - currPost[idx]);
                            
                            newOrder[idx] = bestPost[idx] - A[idx] * D[idx];                            
                        }
                        newPost = this.sortPost(newOrder, whales[i]);
                        whales[i].setPost(newPost);
                    }
                    else if(this.absVector(A) >= 1){
                        int randIdx = random.nextInt(numWhales);
                        Whale randWhale = whales[randIdx];
                        int[] randWhalePost = randWhale.getPost();
                        
                        double[] D = new double[numJobs];
                        int[] currPost = whales[i].getPost();
                        for (int idx = 0;  idx < numJobs; idx++) {
                            D[idx] = Math.abs(C[idx] * randWhalePost[idx] - currPost[idx]);
                            
                            newOrder[idx] = randWhalePost[idx] - A[idx] * D[idx];
                        }
                        
                        newPost = this.sortPost(newOrder, whales[i]);
                        whales[i].setPost(newPost);
                    }
                }else if(p >= 0.5){
                    double[] D = new double[numJobs];
                    double b = random.nextDouble();
                    int[] currPost = whales[i].getPost();
                    
                    for (int idx = 0;  idx < numJobs; idx++) {
                        D[idx] = Math.abs(bestPost[idx] - currPost[idx]);  
                         
                        newOrder[idx] = (D[idx] * Math.pow(Math.E, (b*l)) * Math.cos(2 * Math.PI * l)) + bestPost[idx];
                    }
                    
                    newPost = this.sortPost(newOrder, whales[i]);
                    whales[i].setPost(newPost);
                }
            }
            this.findBestWhale();
            System.out.println(optWhale);
            mainFrame.setTfAreaText("Nomor iterasi: " + (currIter+1) + System.lineSeparator() + optWhale.toString());
            currIter++;
        }
    }
    
    /**
     * Method untuk mencari nilai absolut dari sebuah vektor.
     * @param vector
     * @return Nilai absolut dari sebuah vektor.
     */
    public double absVector(double[] vector){
        double temp = 0.0;
        for (int i = 0; i < vector.length; i++) {
            temp += Math.pow(vector[i], 2);
        }
        double abs = Math.sqrt(temp);
        return abs;
    }
    
    /**
     * Method untuk menghitung nilai multicriterion dari semua paus yang ada,
     * dibandingkan dengan nilai paus terbaik, dengan memanfaatkan nilai makespan
     * dan total flow-time.
     */
    public void findBestWhale(){
        double r1 = 0.0, r2 = 0.0;
        int ms1 = 0, ms2 = 0;
        int tf1 = 0, tf2 = 0;
        
        for (int i = 0; i < numWhales; i++) {
            this.countMsAndTfValue(whales[i]); 
            ms1 = whales[i].getMsValue();
            tf1 = whales[i].getTfValue();
            ms2 = optWhale.getMsValue();
            tf2 = optWhale.getTfValue();
            r1 = ((ms1 - Math.min(ms1, ms2))/(double)(Math.min(ms1, ms2))) + ((tf1 - Math.min(tf1, tf2))/(double)(Math.min(tf1, tf2)));
            r2 = ((ms2 - Math.min(ms1, ms2))/(double)(Math.min(ms1, ms2))) + ((tf2 - Math.min(tf1, tf2))/(double)(Math.min(tf1, tf2)));
            
            if(r1 <= r2){               
                optWhale.setPost(whales[i].getPost());
                optWhale.setMsValue(ms1);
                optWhale.setTfValue(tf1);
                optWhale.setMcValue(r1);
            }
        }
    }
    
    /**
     * Method untuk melakukan sorting nilai-nilai hasil akhir perhitungan posisi baru paus.
     * @param newOrder
     * @param whale
     * @return Array urutan pekerjaan untuk paus.
     */
    public int[] sortPost(double[] newOrder, Whale whale){
        ArrayList<AbstractMap.SimpleEntry<Double, Integer>> pair = new ArrayList<>();
        for (int i = 0; i < numJobs; i++) {
            AbstractMap.SimpleEntry<Double, Integer> postIdx = new AbstractMap.SimpleEntry<>(newOrder[i],i);
            pair.add(postIdx); 
        }    
        
        pair.sort((var p1, var p2) -> Double.compare(p1.getKey(), p2.getKey()));
        
        int[] currPost = whale.getPost();
        int[] newPost = new int[numJobs];
        
        for (int i = 0; i < pair.size(); i++) {
            newPost[i] = currPost[pair.get(i).getValue()];
        }
        
        return newPost;
    }
    
    /**
     * Method untuk menghitung nilai makespan dan total flow-time dari sebuah paus.
     * @param whale 
     */
    public void countMsAndTfValue(Whale whale){
        int numOpers = this.numOpers;
        int[] whalePost = whale.getPost();
        int[][] completTime = new int[numJobs][numOpers];
        
        //idx i dan j dimulai dari 0
        completTime[0][0] = flowShop.getJobIdx(whalePost[0]).getOperTimes(0);
        
        //menghitung completion time ke samping kanan
        for (int j = 1; j < numOpers; j++) {
            completTime[0][j] = completTime[0][j-1] + flowShop.getJobIdx(whalePost[0]).getOperTimes(j);
        }
        
        //menghitung completion time ke bawah
        for (int i = 1; i < numJobs; i++) {
            completTime[i][0] = completTime[i-1][0] + flowShop.getJobIdx(whalePost[i]).getOperTimes(0);
        }
        
        //menghitung completion time dari baris 1 kolom 1
        for (int i = 1; i < numJobs; i++) {
            for (int j = 1; j < numOpers; j++) {
                int completTimeMaxBefore = Integer.max(completTime[i-1][j], completTime[i][j-1]);
                completTime[i][j] = completTimeMaxBefore + flowShop.getJobIdx(whalePost[i]).getOperTimes(j);
            }
        }
        
        int makeSpan = completTime[numJobs-1][numOpers-1];
        int totalFlowTime = 0;
        for (int i = 0; i < numJobs; i++) {
            totalFlowTime += completTime[i][numOpers-1];
        }
        
        whale.setMsValue(makeSpan);
        whale.setTfValue(totalFlowTime);
    }
    
}
