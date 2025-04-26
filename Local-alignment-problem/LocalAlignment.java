import java.util.*;
import java.io.*;

//Implementation of the Local Alignment Problem 
// Inputs are as follows:
//    matchReward: integer representing reward for correct alignment
//    mismachPenalty: integer representing penalty for inncorrect alignment
//    indelPenalty: integer representing penalty cost for insertion and deletion
//    s & t: strings to be compared


    public static Tuple LocalAlignment(int matchReward, int mismatchPenalty, int indelPenalty, String s, String t) {
        int m = s.length();
        int n = t.length();

        // create matrtix that will be used for sorting
        int[][] scoreMatrix = new int[m+1][n+1];
        int maxScore = 0;
        int maxI = 0, maxJ = 0;

        // fill the matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int match;
                //compare characters to determine the score
                if (s.charAt(i-1) == t.charAt(j-1)) { //match
                    match = scoreMatrix[i-1][j-1] + matchReward;
                } else {                              //no match
                    match = scoreMatrix[i-1][j-1] - mismatchPenalty;
                }
                // fills current cell using score
                scoreMatrix[i][j] = Math.max(0, Math.max(match, Math.max(scoreMatrix[i-1][j] - indelPenalty, scoreMatrix[i][j-1] - indelPenalty)));

                // update max score
                if (scoreMatrix[i][j] > maxScore) {
                    maxScore = scoreMatrix[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }
        StringBuilder alignedS = new StringBuilder();
        StringBuilder alignedT = new StringBuilder();

        int i = maxI;
        int j = maxJ;
        // trace back while keeping bounds and positive score 
        while (i > 0 && j > 0 && scoreMatrix[i][j] > 0) {
            // checks for match and will move accordingly
            if (scoreMatrix[i][j] == scoreMatrix[i-1][j-1] + (s.charAt(i-1) == t.charAt(j-1) ? matchReward : -mismatchPenalty)) {
                alignedS.append(s.charAt(i-1));
                alignedT.append(t.charAt(j-1));
                i--; // move up and to the left
                j--;
            } else if (scoreMatrix[i][j] == scoreMatrix[i-1][j] - indelPenalty) {
                alignedS.append(s.charAt(i-1));
                alignedT.append('-');
                i--; // move up
            } else {
                alignedS.append('-');
                alignedT.append(t.charAt(j-1));
                j--; // move left
            }
        }

        // reverse the strings 
        alignedS.reverse();
        alignedT.reverse();
        
        return new Tuple(maxScore, alignedS.toString(), alignedT.toString());
    }
    
    // Tuple class to hold the results of the local alignment
    public static class Tuple {
        int alignmentScore;
        String alignedS;
        String alignedT;

        public Tuple(int alignmentScore, String alignedS, String alignedT) {
            this.alignmentScore = alignmentScore;
            this.alignedS = alignedS;
            this.alignedT = alignedT;
        }
    }
