// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package Separated_Handwriting;

import java.io.*;
import LibOCR.*;

public class SeparatedWord {

    static String Cost;
    static int nAnfabets = 26;
    static int MaxLength = 1000;
    static int MaxnWords = 200000;

    static int width;
    static int height;

    static int n1;
    static int n2;
    static int nClass;

    static Pattern Classifier;

    static char ClassName[] = {'A', 'B', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    static double probability[] = new double[ClassName.length];
    static double prob[][] = new double[MaxLength][nAnfabets];

    static int nWords = 0;
    static String Word[] = new String[MaxnWords];

    protected static String infoFileName;
    protected static int sampleWidth;
    protected static int sampleHeight;
    protected static int hiddenLayer;
    protected static int outputLayer;
    protected static String dictionaryFile;
    protected static String modelFile;
    protected static String importedImage;
    protected static String exportedImage;
    protected static String exportedOutput;
    
    public static void Init(String infoParametersFileName) throws IOException {
        infoFileName = infoParametersFileName;
        infoParametersReading();
        
        width = sampleWidth;
        height = sampleHeight;
        n1 = width * height;
        n2 = hiddenLayer;
        nClass = outputLayer;
        Cost = modelFile; 
        
        Classifier = new Pattern(width, height, Cost, n1, n2, nClass);

        TextFileReader file = new TextFileReader();
        file.open(dictionaryFile);

        String aWord;
        nWords = 0;
        while (true) {
            aWord = file.ReadLine();
            if (aWord == null) {
                break;
            }
            Word[nWords] = aWord;
            nWords++;
        }

        file.close();
    }

    public static String Recognition(int InfoGray[][], aRect Rect[], int start, int finish, int y1, int y2, int threshold) throws IOException {
        String res;

        int Length = finish - start + 1;

        for (int i = 0; i < Length; i++) {
            for (int j = 0; j < nAnfabets; j++) {
                prob[i][j] = 0.0;
            }
        }

        for (int i = start; i <= finish; i++) {
            Classifier.Recognition(InfoGray, Rect[i].x1, y1, Rect[i].x2, y2, threshold, probability);
            for (int j = 0; j < nClass; j++) {
                if ((ClassName[j] >= 'A') && (ClassName[j] <= 'Z')) {
                    prob[i - start][(int) (ClassName[j]) - 'A'] += probability[j];
                } else {
                    prob[i - start][(int) (ClassName[j]) - 'a'] += probability[j];
                }
            }
        }

        double SumProb;
        double HighestProb = 0.0;
        String Best = Word[0];
        boolean found = false;

        for (int i = 0; i < nWords; i++) {
            if (Word[i].length() == Length) {
                SumProb = 0.0;
                for (int j = 0; j < Length; j++) {
                    SumProb += prob[j][(int) (Word[i].charAt(j)) - 'a'];
                }

                if (SumProb > HighestProb) {
                    found = true;
                    HighestProb = SumProb;
                    Best = Word[i];
                }
            }
        }

        if (!found) {
            for (int i = 0; i < nWords; i++) {
                SumProb = 0.0;
                for (int j = 0; j < Math.min(Length, Word[i].length()); j++) {
                    SumProb += prob[j][(int) (Word[i].charAt(j)) - 'a'];
                }

                if (SumProb > HighestProb) {
                    HighestProb = SumProb;
                    Best = Word[i];
                }
            }
        }

        return Best;
    }

    private static void infoParametersReading() throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(infoFileName));
        file.readLine();
        sampleWidth = Integer.parseInt(file.readLine());
        file.readLine();
        sampleHeight = Integer.parseInt(file.readLine());
        file.readLine();
        hiddenLayer = Integer.parseInt(file.readLine());
        file.readLine();
        outputLayer = Integer.parseInt(file.readLine());
        file.readLine();
        dictionaryFile = file.readLine();
        file.readLine();
        modelFile = file.readLine();
        file.readLine();
        importedImage = file.readLine();
        file.readLine();
        exportedImage = file.readLine();
        file.readLine();
        exportedOutput = file.readLine();
        file.close();
        
        System.out.println("Sample width:");
        System.out.println(sampleWidth);
        System.out.println("Sample height:");
        System.out.println(sampleHeight);
        System.out.println("Hidden layer:");
        System.out.println(hiddenLayer);
        System.out.println("Output layer:");
        System.out.println(outputLayer);
        System.out.println("Dictionary file:");
        System.out.println(dictionaryFile);
        System.out.println("Model file:");
        System.out.println(modelFile);
        System.out.println("Imported image:");
        System.out.println(importedImage);
        System.out.println("Exported image:");
        System.out.println(exportedImage);
        System.out.println("Exported text:");
        System.out.println(exportedOutput);
    }
    
}
