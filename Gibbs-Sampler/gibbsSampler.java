import java.util.Scanner;
import java.util.ArrayList; 
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.*;


// Implementation of a Gibbs Sampler: 
// dna : collection of strings representing a dna sequence 
// k : length of the motif
// t : number of strings in the lnput
// n : number of iterations (higher number usally brings more accurate values however
//       there is a point of deminishing returns based on input size)


public class Main { 
    public static List<String> GibbsSampler(List<String> dna, int k, int t, int N) {
        List<String> bestMotifs = new ArrayList<>(); 
        Random rand = new Random(); 
        
        // Randomly pick some of the k-mers from the dna strings
        for (String sequence : dna) {
            // randomly choose starting position 
            int start = rand.nextInt(sequence.length() - k + 1);
            // add the k-mer to the motif list
            bestMotifs.add(sequence.substring(start, start + k));
        }
        int bestScore = score(bestMotifs); 
        // created new list of bestmotifs to keep track of the motifs found
        
        // set it to go for 1000 random starts 
        for (int start = 0; start < 1000; start++) {
            List<String> motifs = new ArrayList<>(bestMotifs); 
            //set it for N times (pre-determined value)
            for (int j = 0; j < N; j++) {
                int i = rand.nextInt(t);
                List<String> newMotifs = new ArrayList<>(motifs);
                newMotifs.remove(i);
        
                double[][] prof = createProf(newMotifs, k, t - 1);
                String newMotif = profRandom(dna.get(i), k, prof, rand);
                newMotifs.add(i, newMotif);
        
                int newScore = score(newMotifs);
                if (newScore < bestScore) {
                    bestMotifs = new ArrayList<>(newMotifs);
                    bestScore = newScore;
                }
            }
        }
    return bestMotifs;
}
    
    // function to create profile matrix 
    private static double[][] createProf(List<String> motifs, int k, int t) {
        // 4 to represent A,C,G,T
        double[][] prof = new double[4][k]; 

        // counts the times each is displayed in the string and 
        // incremnts it in prof arr
        for (String motif : motifs) {
            for (int i = 0; i < k; i++) { 
                char letCount = motif.charAt(i); 
                switch (letCount) {
                    case 'A': prof[0][i]++; break;
                    case 'C': prof[1][i]++; break;
                    case 'G': prof[2][i]++; break;
                    case 'T': prof[3][i]++; break;
                }
            }
        }

        // the part where I remeber to use pseudocounts
        for (int i = 0; i < k; i++) { 
            for (int j = 0; j < 4; j++) {
                prof[j][i] = (prof[j][i] + 1.0) / (t + 4.0);
            }
        }

        return prof; // return new profile matrix 
    }

    // given prof matrix context will select a k-mer
    private static String profRandom(String sequence, int k, double[][] prof, Random rand) {
        // max starting index
        int maxIndex = sequence.length() - k; 
        // stores the probablities for each matrix
        double[] prob = new double[maxIndex + 1]; 
        double totalProb = 0; 

        // calculations for the probablities
        for (int i = 0; i <= maxIndex; i++) {
            prob[i] = 1.0; 
            
            for (int j = 0; j < k; j++) { 
                // grabs the letter (A,C,G,T)
                char dnaLet = sequence.charAt(i + j); 
                // finds index representation for the char
                int index = findIndex(dnaLet); 
                //determines prob by multiplying
                prob[i] *= prof[index][j]; 
            }
            // sum of all
            totalProb += prob[i]; 
        }

        // make all the data more consistent
        for (int i = 0; i <= maxIndex; i++) {
            prob[i] /= totalProb; 
        }

        
        double r = rand.nextDouble(); 
        double cumulative = 0.0; 
        
        //using random num grabs k-mer based on the probablity 
        for (int i = 0; i <= maxIndex; i++) {
            cumulative += prob[i]; 
            if (r < cumulative) { 
                return sequence.substring(i, i + k); 
            }
        }
        
        // returns the last k-mer 
        return sequence.substring(maxIndex, maxIndex + k); 
    }

    // this will convert the chars into indexs we can use 
    private static int findIndex(char dnaLet) {
        switch (dnaLet) {
            case 'A': return 0; 
            case 'C': return 1; 
            case 'G': return 2; 
            case 'T': return 3; 
            
            // need for code to work but given that we 
            // have predetermined inputs should never happen
            default : return -1; 
        }
    }

    // function to calculate the scores
    private static int score(List<String> motifs) {
        int score = 0; 
        int k = motifs.get(0).length(); 
        int t = motifs.size(); 
        // score each position of the motifs
        for (int i = 0; i < k; i++) { 
            int[] count = new int[4]; 
            for (int j = 0; j < t; j++) { 
                //grab each dna char and increment it 
                char dnaLet = motifs.get(j).charAt(i);
                count[findIndex(dnaLet)]++; 
            }
            // score and move into next
            score += (t - Arrays.stream(count).max().orElse(0)); 
        }
        
        return score; 
    }
