// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package LibOCR;

import java.io.*;

public class Pattern {

    protected int MaxWidth = 500;
    protected int MaxHeight = 500;

    protected int width, height;

    protected int First[][];
    protected int Second[][];

    protected double Input[];
    protected double Output[];

    protected int nClass;
    protected MLP ANN;

    public Pattern(int w, int h, String Cost, int n1, int n2) throws IOException {
        width = w;
        height = h;

        First = new int[MaxWidth][MaxHeight];
        Second = new int[width][height];

        nClass = n2;
        Input = new double[n1];
        Output = new double[nClass];

        ANN = new MLP(n1, n2);
        ANN.SetCost(Cost);
    }

    public Pattern(int w, int h, String Cost, int n1, int n2, int n3) throws IOException {
        width = w;
        height = h;

        First = new int[MaxWidth][MaxHeight];
        Second = new int[width][height];

        nClass = n3;
        Input = new double[n1];
        Output = new double[nClass];

        ANN = new MLP(n1, n2, n3);
        ANN.SetCost(Cost);
    }

    public Pattern(int w, int h, String Cost, int n1, int n2, int n3, int n4) throws IOException {
        width = w;
        height = h;

        First = new int[MaxWidth][MaxHeight];
        Second = new int[width][height];

        nClass = n4;
        Input = new double[n1];
        Output = new double[nClass];

        ANN = new MLP(n1, n2, n3, n4);
        ANN.SetCost(Cost);
    }

    public Pattern(int w, int h, String Cost, int n1, int n2, int n3, int n4, int n5) throws IOException {
        width = w;
        height = h;

        First = new int[MaxWidth][MaxHeight];
        Second = new int[width][height];

        nClass = n5;
        Input = new double[n1];
        Output = new double[nClass];

        ANN = new MLP(n1, n2, n3, n4, n5);
        ANN.SetCost(Cost);
    }

    public Pattern(int w, int h, String Cost, int n1, int n2, int n3, int n4, int n5, int n6) throws IOException {
        width = w;
        height = h;

        First = new int[MaxWidth][MaxHeight];
        Second = new int[width][height];

        nClass = n6;
        Input = new double[n1];
        Output = new double[nClass];

        ANN = new MLP(n1, n2, n3, n4, n5, n6);
        ANN.SetCost(Cost);
    }

    public boolean FoundAPixelOnRow(int InfoGray[][], int j, int x1, int x2, int threshold) {
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
        }
        for (int i = x1; i <= x2; i++) {
            if (InfoGray[i][j] < threshold) {
                return true;
            }
        }
        return false;
    }

    public boolean FoundAPixelOnColumn(int InfoGray[][], int i, int y1, int y2, int threshold) {
        if (y1 > y2) {
            int temp = y1;
            y1 = y2;
            y2 = temp;
        }
        for (int j = y1; j <= y2; j++) {
            if (InfoGray[i][j] < threshold) {
                return true;
            }
        }
        return false;
    }

    public void Recognition(int InfoGray[][], int x1, int y1, int x2, int y2, int threshold, double probability[]) {
        int i, j;

        for (j = y1; j <= y2; j++) {
            if (FoundAPixelOnRow(InfoGray, j, x1, x2, threshold)) {
                y1 = j;
                break;
            }
        }

        for (j = y2; j >= y1; j--) {
            if (FoundAPixelOnRow(InfoGray, j, x1, x2, threshold)) {
                y2 = j;
                break;
            }
        }

        for (i = x1; i <= x2; i++) {
            if (FoundAPixelOnColumn(InfoGray, i, y1, y2, threshold)) {
                x1 = i;
                break;
            }
        }

        for (i = x2; i >= x1; i--) {
            if (FoundAPixelOnColumn(InfoGray, i, y1, y2, threshold)) {
                x2 = i;
                break;
            }
        }

        int w = x2 - x1 + 1;
        int h = y2 - y1 + 1;

        for (i = x1; i <= x2; i++) {
            for (j = y1; j <= y2; j++) {
                First[i - x1][j - y1] = InfoGray[i][j];
            }
        }

        Normalization.Standardize(First, Second, w, h, width, height);

        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (Second[i][j] < threshold) {
                    Input[i + j * width] = 1.0;
                } else {
                    Input[i + j * width] = 0.0;
                }
            }
        }

        ANN.Classification(Input, Output);

        double SumProb = 0.0;
        for (i = 0; i < nClass; i++) {
            SumProb += Output[i];
        }
        for (i = 0; i < nClass; i++) {
            probability[i] = Output[i]; //probability[i] = Output[i] / SumProb;
        }
    }

}
