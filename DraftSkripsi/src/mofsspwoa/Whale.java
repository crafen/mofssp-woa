package mofsspwoa;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * @author Craven Sachio Saputra
 */
public class Whale {
    private int[] postValue;
    private int msValue;
    private int tfValue;
    private double mcValue;
    
    /**
     * Constructor dari kelas Whale untuk menginisialisasi variabel-variabel yang ada.
     * @param size 
     */
    public Whale(int size){
        this.postValue = new int[size];
        this.msValue = Integer.MAX_VALUE;
        this.tfValue = Integer.MAX_VALUE;
        this.mcValue = Double.MAX_VALUE;
    }
    
    /**
     * Method untuk menghasilkan posisi pertama dari paus secara acak.
     */
    public void generateFirstPost(){
        for (int i = 0; i < postValue.length; i++) {
            postValue[i] = i;
        }
        
        List<Integer> listPost  = Arrays.stream( postValue ).boxed().collect( Collectors.toList() );
        Collections.shuffle(listPost);
        
        postValue = listPost.stream().mapToInt(i->i).toArray();
    }
    
    public int[] getPost(){
        return this.postValue;
    }

    public void setPost(int[] postValue) {
        this.postValue = postValue;
    } 
    
    public int getMsValue(){
        return this.msValue;
    }
    
    public int getTfValue(){
        return this.tfValue;
    }
    
    public void setMsValue(int msValue){
        this.msValue = msValue;
    }
    
    public void setTfValue(int tfValue){
        this.tfValue = tfValue;
    }

    public void setMcValue(double mcValue) {
        this.mcValue = mcValue;
    }

    @Override
    public String toString() {
        String resultText = "Posisi whale terbaik: ";
        
        for (int i = 0; i < postValue.length; i++) {
            if(i == postValue.length - 1) resultText += postValue[i];
            else resultText += postValue[i] + ",";
        }
        
        resultText += System.lineSeparator();
        resultText += "Nilai makespan: " + msValue + System.lineSeparator();
        resultText += "Nilai total-flowtime: " + tfValue + System.lineSeparator() + System.lineSeparator();
          
        return resultText;
    }
    
}
