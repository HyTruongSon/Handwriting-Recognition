// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package Continuous_Handwriting;

import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import LibOCR.*;

public class ContinuousWord {

    static String folder = "Segmentation/";
    static int countSegmentation = 0;
    
    static String Cost;
    static int nAnfabets = 26;
    static int MaxnLines = 1000;
    static int MaxnWords = 200000;

    static int MinimumLength = 5;
    static int nIntervals = 5;

    static int width;
    static int height;

    static int n1;
    static int n2;
    static int nClass;

    static int nLines;
    static int Line[] = new int[MaxnLines];
    static boolean used[] = new boolean[MaxnLines];

    static int nSegments;

    static class aSegment {

        public int u, v;
    }
    static aSegment Segment[];

    static Pattern Classifier;

    static char ClassName[] = {'A', 'B', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    static double probability[] = new double[ClassName.length];
    static double prob[][][];

    static double dp[][] = new double[100][100];

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

    public static int RGB(int red, int green, int blue) {
        return (0xff000000) | (red << 16) | (green << 8) | blue;
    }

    public static void DrawLine(BufferedImage image, int x1, int y1, int x2, int y2, String Colour) {
        Graphics2D g2d = image.createGraphics();
        if (Colour.equals("RED")) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(Color.GREEN);
        }
        g2d.drawLine(x1, y1, x2, y2);
    }

    public static void Segmentation(int InfoGray[][], int x1, int y1, int x2, int y2, int threshold) {
        int i, j, v, x, sum, average, Min;
        boolean condition;

        int w = x2 - x1 + 1;
        int h = y2 - y1 + 1;
        int count[] = new int[w];

        sum = 0;
        for (i = x1; i <= x2; i++) {
            count[i - x1] = 0;
            for (j = y1; j <= y2; j++) {
                if (InfoGray[i][j] < threshold) {
                    count[i - x1]++;
                }
            }
            sum += count[i - x1];
        }
        average = sum / w;

        nLines = 0;
        i = x1;
        while (i <= x2) {
            if (count[i - x1] >= average) {
                i++;
                continue;
            }
            Min = i;
            v = i;
            for (j = i; j <= x2; j++) {
                if (count[j - x1] < average) {
                    v = j;
                    if (count[j - x1] < count[Min - x1]) {
                        Min = j;
                    }
                } else {
                    break;
                }
            }
            i = v + 1;
            Line[nLines] = Min;
            nLines++;
        }

        for (i = 0; i < nLines; i++) {
            used[i] = false;
        }

        i = 0;
        while (i < nLines) {
            if (i == 0) {
                x = x1;
            } else {
                x = Line[i - 1] + 1;
            }
            v = i;
            for (j = i; j < nLines; j++) {
                v = j;
                condition = false;
                if (Line[j] - x + 1 < MinimumLength) {
                    condition = true;
                }
                if ((j + 1 < nLines) && (Line[j + 1] - Line[j] < MinimumLength)) {
                    condition = true;
                }

                if (!condition) {
                    if (x2 - Line[j] >= MinimumLength) {
                        used[j] = true;
                    }
                    break;
                }
            }
            i = v + 1;
        }

        nSegments = 1;
        for (i = 0; i < nLines; i++) {
            if (used[i]) {
                nSegments++;
            }
        }

        Segment = new aSegment[nSegments];
        j = 0;
        v = x1;
        for (i = 0; i < nLines; i++) {
            if (used[i]) {
                Segment[j] = new aSegment();
                Segment[j].u = v;
                Segment[j].v = Line[i];
                j++;
                v = Line[i] + 1;
            }
        }
        Segment[nSegments - 1] = new aSegment();
        Segment[nSegments - 1].u = v;
        Segment[nSegments - 1].v = x2;
    }

    public static void DrawSegmentation(int InfoGray[][], int x1, int y1, int x2, int y2) throws IOException {
        ++countSegmentation;
        String fileName = folder + Integer.toString(countSegmentation) + ".jpg";
        File file = new File(fileName);
        BufferedImage image = new BufferedImage(x2 - x1 + 1, y2 - y1 + 1, BufferedImage.TYPE_INT_RGB);

        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                image.setRGB(i - x1, j - y1, RGB(InfoGray[i][j], InfoGray[i][j], InfoGray[i][j]));
            }
        }

        for (int i = 0; i < nLines; i++) {
            if (used[i]) {
                DrawLine(image, Line[i] - x1, 0, Line[i] - x1, y2 - y1, "RED");
            }
        }

        ImageIO.write(image, "jpg", file);
    }

    public static String Recognition(int InfoGray[][], int x1, int y1, int x2, int y2) throws IOException {
        int i, j, v, t, c;

        int threshold = 0;
        for (i = x1; i <= x2; i++) {
            for (j = y1; j <= y2; j++) {
                threshold += InfoGray[i][j];
            }
        }
        threshold /= (x2 - x1 + 1) * (y2 - y1 + 1);

        Segmentation(InfoGray, x1, y1, x2, y2, threshold);

        prob = new double[nSegments][nIntervals][nAnfabets];

        for (i = 0; i < nSegments; i++) {
            for (j = 0; j < nIntervals; j++) {
                if (i + j < nSegments) {
                    Classifier.Recognition(InfoGray, Segment[i].u, y1, Segment[i + j].v, y2, threshold, probability);

                    for (v = 0; v < nAnfabets; v++) {
                        prob[i][j][v] = 0.0;
                    }

                    for (v = 0; v < nClass; v++) {
                        if ((ClassName[v] >= 'A') && (ClassName[v] <= 'Z')) {
                            prob[i][j][(int) (ClassName[v]) - 'A'] += probability[v];
                        } else {
                            prob[i][j][(int) (ClassName[v]) - 'a'] += probability[v];
                        }
                    }
                }
            }
        }

        String Best = Word[0];
        double HighestProb = 0.0;

        for (t = 0; t < nWords; t++) {
            if (Word[t].length() > nSegments) {
                continue;
            }
            for (i = 0; i < nSegments; i++) {
                for (j = 0; j < Word[t].length(); j++) {
                    dp[i][j] = 0.0;
                }
            }

            for (j = 0; j < nIntervals; j++) {
                if (j < nSegments) {
                    c = (int) (Word[t].charAt(0)) - (int) ('a');
                    dp[j][0] = prob[0][j][c];
                }
            }

            for (v = 1; v < Word[t].length(); v++) {
                for (i = 1; i < nSegments; i++) {
                    if (dp[i - 1][v - 1] > 0.0) {
                        for (j = 0; j < nIntervals; j++) {
                            if (i + j < nSegments) {
                                c = (int) (Word[t].charAt(v)) - (int) ('a');
                                dp[i + j][v] = Math.max(dp[i + j][v], dp[i - 1][v - 1] + prob[i][j][c]);
                            }
                        }
                    }
                }
            }

            if (dp[nSegments - 1][Word[t].length() - 1] > HighestProb) {
                HighestProb = dp[nSegments - 1][Word[t].length() - 1];
                Best = Word[t];
            }
        }

        DrawSegmentation(InfoGray, x1, y1, x2, y2);

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
