import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author mathiaslindblom
 */
public class Main {
    private static double[][] transisionMatrix;
    private static double[][] emissionMatrix;
    private static double[]   initialStatePDVector; //Says matrix in assignment but should/could be vector;
    private static int[]     emissionSequence;
    private static double    totalProb = 0;
    private static int[]     mostLikelyPath;
    private static double   probOfMostLikelyPath;


    public static void main(String[] args) throws IOException {


        ArrayList<String[]> lines = new ArrayList<String[]>();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while ((line = in.readLine()) != null) {
            if (line.contains(";")) {
                break;
            }

            lines.add(line.split(" "));
        }

        for (int n = 0; n < 4; n++) {
            String[] stringArray = lines.get(n);
            if (n == 3) {
                int size = Integer.parseInt(stringArray[0]);
                emissionSequence = new int[size];
                for (int i = 0; i < size; i++) {
                    emissionSequence[i] = Integer.parseInt(stringArray[1 + i]);
                }
                break;
            }
            int row = Integer.parseInt(stringArray[0]);
            int col = Integer.parseInt(stringArray[1]);
            double[][] matrix = new double[row][col];

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    matrix[i][j] = Double.parseDouble(stringArray[col * i + j + 2]);
                }
            }
            if (n == 0) { transisionMatrix = matrix; }
            if (n == 1) { emissionMatrix = matrix; }
            if (n == 2) {
                if(matrix.length> 1){
                    throw new RuntimeException();
                }
                initialStatePDVector = matrix[0];
            }
        }
        probOfMostLikelyPath = -1;
//        totalProb *= computeEmissionProb(emissionMatrix, initialStatePDVector)[emissionSequence[0]];
//        initialStatePDVector = computeNextStateProb(initialStatePDVector, transisionMatrix, emissionMatrix);

        mostLikelyPath =  new int[emissionSequence.length];
        for(int i = 0; i< transisionMatrix.length; i++){
            double currentProb = initialStatePDVector[i] * emissionMatrix[i][emissionSequence[0]];
            if(currentProb != 0){
                double returnedProb  = calculateSequenceProp(transisionMatrix, emissionMatrix, emissionSequence, i, 1, currentProb);
                if(returnedProb == probOfMostLikelyPath){
                    mostLikelyPath[0] = i;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int step: mostLikelyPath){
            sb.append(step + " ");
        }
        System.out.print(sb.toString());




//                double[] currentStatePDVector = initialStatePDVector;
//        for (int currentEmission: emissionSequence) {
//            totalProb *= computeEmissionProb(emissionMatrix, currentStatePDVector)[emissionSequence[currentEmission]];
//            ArrayList<Integer> possibleStates = new ArrayList<Integer>();
//            double distribute = 0;
//            for (int i = 0; i < currentStatePDVector.length; i++) {
//                if (emissionMatrix[i][currentEmission] > 0) {
//                    possibleStates.add(i);
//                } else {
//                    distribute += currentStatePDVector[i];
//                    currentStatePDVector[i] = 0;
//                }
//            }
//            if(distribute > 0){
//                for(int state: possibleStates){
//                    currentStatePDVector[state] += currentStatePDVector[state]*distribute/(1.0-distribute);
//                }
//            }
//
//            currentStatePDVector = computeNextStateProb(currentStatePDVector, transisionMatrix, emissionMatrix);
//        }
//        System.out.println(totalProb);

        //        for(double test: nextStateVector){
        //            System.out.print(test + " ");
        //        }
        //        System.out.println();


        //        StringBuilder resultBuilder = new StringBuilder();
        //        resultBuilder.append("1 " + emissionPVector.length + " ");
        //        for (double emissionProp : emissionPVector) {
        //            resultBuilder.append(emissionProp + " ");
        //        }
        //        System.out.println(resultBuilder.toString().trim());
    }

    private static double calculateSequenceProp(double[][] transisionMatrix, double[][] emissionMatrix, int[] emissionSequence, int currentState, int sequenceIndex, double ackProb){
        double returnProb = -2;
        if(emissionSequence.length == sequenceIndex){
            if (ackProb > probOfMostLikelyPath){
                probOfMostLikelyPath = ackProb;
            }
            return ackProb;
        }
        for(int i = 0; i< transisionMatrix.length; i++){
            double currentProb = ackProb*transisionMatrix[currentState][i] * emissionMatrix[i][emissionSequence[sequenceIndex]];
            if(currentProb > 0){
                double returnedProb  = calculateSequenceProp(transisionMatrix, emissionMatrix, emissionSequence, i, sequenceIndex+1, currentProb);
                if(returnedProb == probOfMostLikelyPath){
                    returnProb = returnedProb;
                    mostLikelyPath[sequenceIndex] =i;
                }
            }
        }
        return returnProb;
    }

    private static double[] computeNextStateProb(double[] initialStatePDVector, double[][] transisionMatrix, double[][] emissionMatrix) {
        double[] nextStateVector = new double[initialStatePDVector.length];
        for (int transitionRow = 0; transitionRow < transisionMatrix.length; transitionRow++) {
            for (int transitionCol = 0; transitionCol < transisionMatrix.length; transitionCol++) {
                nextStateVector[transitionCol] += initialStatePDVector[transitionRow] * transisionMatrix[transitionRow][transitionCol];
            }
        }
        return nextStateVector;
    }

    private static double[] computeEmissionProb(double[][] emissionMatrix, double[] stateVector) {
        double[] emissionPVector = new double[emissionMatrix[0].length];

        for (int emissionIndex = 0; emissionIndex < emissionPVector.length; emissionIndex++) {
            for (int stateProbIndex = 0; stateProbIndex < stateVector.length; stateProbIndex++) {
                emissionPVector[emissionIndex] += stateVector[stateProbIndex] * emissionMatrix[stateProbIndex][emissionIndex];
            }
        }
        return emissionPVector;
    }

    public static String test() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transission Matrix");
        for (int row = 0; row < transisionMatrix.length; row++) {
            sb.append('\n');
            for (int col = 0; col < transisionMatrix.length; col++) {
                sb.append(transisionMatrix[row][col]);
                sb.append('\t');
            }
        }
        sb.append('\n');
        sb.append('\n');
        sb.append("Emission Matrix");
        for (int row = 0; row < emissionMatrix.length; row++) {
            sb.append('\n');
            for (int col = 0; col < emissionMatrix[0].length; col++) {
                sb.append(emissionMatrix[row][col]);
                sb.append('\t');
            }
        }
        sb.append('\n');
        sb.append('\n');
        return sb.toString();
    }
}
