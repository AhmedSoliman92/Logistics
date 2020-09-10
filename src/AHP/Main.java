package AHP;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public  class Main {
    private static final DecimalFormat df = new DecimalFormat("0.0000");
    private static final DecimalFormat decimalFormat=new DecimalFormat("0.000");
    static int numberOfObjectives;
    static int numberOfSolution;
    static double maxJob;
    static double[][] objectives;
    static double[][] normalA;
    static double[] objectiveWeights;
    static double[] weights;
    static double[][] solutionWeights;
    static Map<Integer,Double> map=new HashMap<>();

    //insert pairwise comparison matrix for each objective..
    public static double[][] insertArray(int number) {
        double[][] array = new double[number][number];
        System.out.println(number + "*" + number);
        System.out.println("Enter all the elements: ");
        Scanner scanner1 = new Scanner(System.in);
        while (true) {
            if (!scanner1.hasNextLine()) break;
            for (int i = 0; i < array.length; i++) {
                String[] line = scanner1.nextLine().trim().split(" ");
                for (int j = 0; j < line.length; j++) {
                    array[i][j] = Double.parseDouble(line[j]);
                }
            }
            break;
        }
        objectives=array.clone();
        return array;
    }

    /*for each A's columns,divide each entry in
     column i of A by the sum of the entries in column i..*/
    public static double[][] calculateNormalA(int number){
        //finding A normal..
        double[][] aNormal =new double[number][number];
        for (int column=0;column<number;column++){
            double sum=0;
            for (int row=0;row<number;row++){
                sum+=objectives[row][column];
            }
            map.put(column,sum);
        }
        for(int i=0;i<number;i++){
            for (int j=0;j<number;j++) aNormal[i][j] = Double.parseDouble(df.format(objectives[i][j] / map.get(j)));
        }
        normalA=aNormal.clone();
        return aNormal;
    }

    /* for each objective: sum of row j divided by columns number*/
    public static void findWeights(int number){
        double[] array=new double[number];
        for (int i=0;i<number;i++){
            double sum=0;
            for (int j=0;j<number;j++){
                sum+=normalA[i][j];
            }
            df.setRoundingMode(RoundingMode.HALF_UP);
            array[i]= Double.valueOf(df.format(sum/number));
        }
        weights=array.clone();
        System.out.println("max weights for each objective: \n"+Arrays.toString(array));
        System.out.println();
    }

    /*checking for consistency
    - compute A*WT
    -compute (1/n)*sum(ith entry in AWT/ith entry in WT)
    /consistency index=(A-n)/(n-1)
     */
    public static void checkConsistency(){
        double[] aNormalW=new double[numberOfObjectives];
        double randomIndex=0;
        double sum=0;
        for (int row=0;row<numberOfObjectives;row++){
            for (int column=0;column<numberOfObjectives;column++){

                sum+=weights[column]*objectives[row][column];
            }
            df.setRoundingMode(RoundingMode.UP);
            aNormalW[row]= Double.valueOf(df.format(sum));
            sum=0;
        }
        double awtDividedByWt=0;
        for (int i=0;i<numberOfObjectives;i++){
            awtDividedByWt+=aNormalW[i]/weights[i];
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            Double.valueOf(decimalFormat.format(awtDividedByWt));
        }
        double reciprocalNumber=(double) 1/numberOfObjectives;
        decimalFormat.setRoundingMode(RoundingMode.UP);
        Double.valueOf(decimalFormat.format(reciprocalNumber));
        double step3=reciprocalNumber*awtDividedByWt;
        decimalFormat.setRoundingMode(RoundingMode.UP);
        double numerator=Double.valueOf(decimalFormat.format(step3))-numberOfObjectives;
        double denominator=numberOfObjectives-1;
        decimalFormat.setRoundingMode(RoundingMode.UP);
        double ci=Double.valueOf(decimalFormat.format(numerator)) /denominator;
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        System.out.println("Consistency Index (CI)= "+Double.valueOf(decimalFormat.format(ci)));
        System.out.println();
        double ciDividedByRI=ci/table23(numberOfObjectives);
        decimalFormat.setRoundingMode(RoundingMode.UP);
        // Double.valueOf(df.format(ciDividedByRI));
        if (ciDividedByRI<0.10){
            randomIndex=ci/table23(numberOfObjectives);
            decimalFormat.setRoundingMode(RoundingMode.UP);
            System.out.println("Random Index (RI)= "+Double.valueOf(decimalFormat.format(randomIndex)));
            System.out.println("degree of consistency is satisfactory !!");
        }else {
            System.out.println("the AHP may not yield meaningful results !!");
        }
        System.out.println();
    }

    // values of the random index..
    public static double table23(int number){
        Map<Integer,Double> RI= new HashMap<>();
        RI.put(2,0.0);
        RI.put(3,0.58);
        RI.put(4,0.90);
        RI.put(5,1.12);
        RI.put(6,1.24);
        RI.put(7,1.32);
        RI.put(8,1.41);
        RI.put(9,1.45);
        RI.put(10,1.51);
        return RI.get(number);
    }

    public static void setScores(){
        double[][] setScore=new double[numberOfObjectives][numberOfSolution];
        maxJob=0;
        objectiveWeights=weights.clone();
        for (int objective=0;objective<numberOfObjectives;objective++){
            insertArray(numberOfSolution);
            calculateNormalA(numberOfSolution);
            findWeights(numberOfSolution);
            for (int i=0;i< numberOfSolution;i++){
                double temp=0;
                temp=weights[i];
                setScore[objective][i]=temp;
            }
            System.out.println(Arrays.deepToString(setScore));
        }
        solutionWeights=setScore.clone();
        System.out.println("solution "+Arrays.deepToString(solutionWeights));
    }
    public static String getMaxJob(){
        double max=0;
        int index=1;
        double[] getScore = new double[numberOfObjectives];
        for (int row=0;row<numberOfSolution;row++){
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            for (int column=0;column<numberOfObjectives;column++){
                getScore[row]+=Double.parseDouble(decimalFormat.format(solutionWeights[column][row]))*Double.parseDouble(decimalFormat.format(objectiveWeights[column]));
            }
        }
        max=getScore[0];
        for (int row=0;row<numberOfSolution;row++){
            if (getScore[row]> max){
                max=getScore[row];
                index+=1;
            }
        }
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        return "AHP indicate that you should accept choice "+index+"\n that have the overall score: "+Double.valueOf(decimalFormat.format(max));
    }

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of objectives:");
        numberOfObjectives = scanner.nextInt();
        System.out.println("Enter the number of solutions:");
        numberOfSolution = scanner.nextInt();
        System.out.println();
        System.out.println("The array of the objectives: \n"+Arrays.deepToString(insertArray(numberOfObjectives)));
        System.out.println();
        System.out.println("A normal array: \n"+Arrays.deepToString(calculateNormalA(numberOfObjectives)));
        System.out.println();
        findWeights(numberOfObjectives);
        checkConsistency();
        setScores();
        System.out.println(getMaxJob());
    }
}
