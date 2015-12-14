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

public class ContinuousHandwriting {

    protected static int MaxWidth = 5000;
    protected static int MaxHeight = 5000;
    protected static int MaxnRects = 2000;

    protected static int WhiteThreshold = 100;
    protected static int MinimumPoints = 30;
    protected static int radius = 3;
    protected static int IntersectPercent = 30;

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

    protected static int threshold;
    protected static int width, height;
    protected static int rear, front;
    protected static int nRects, nLines, nWords;

    public static class aPoint {

        public int x, y;
    }

    public static class aLine {

        public int y1, y2;
        public int nRects;
        public aRect Rect[];
    }

    protected static int parent[];
    protected static aRect Rect[] = new aRect[MaxnRects];
    protected static aLine Line[];
    protected static aRect Word[] = new aRect[MaxnRects];

    protected static int InfoGray[][] = new int[MaxWidth][MaxHeight];
    protected static aPoint queue[];
    protected static boolean inqueue[][] = new boolean[MaxWidth][MaxHeight];

    public static String getType(String FileName) {
        int i, j;
        String res;
        j = 0;
        for (i = 0; i < FileName.length(); i++) {
            if (FileName.charAt(i) == '.') {
                j = i;
                break;
            }
        }
        res = "";
        for (i = j + 1; i < FileName.length(); i++) {
            res += FileName.charAt(i);
        }
        return res;
    }

    public static void Input(BufferedImage image) throws IOException {
        int RGB, R, G, B;

        width = image.getWidth(null);
        height = image.getHeight(null);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                RGB = image.getRGB(i, j);
                R = (RGB & 0x00ff0000) >> 16;
                G = (RGB & 0x0000ff00) >> 8;
                B = RGB & 0x000000ff;
                InfoGray[i][j] = (R * 299 + G * 587 + B * 114) / 1000;
            }
        }
    }

    public static double sqr(double x) {
        return x * x;
    }

    public static void FindThreshold() {
        int Limit = 256;
        int Histogram[] = new int[Limit];

        for (int i = 0; i < Limit; i++) {
            Histogram[i] = 0;
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Histogram[InfoGray[i][j]]++;
            }
        }

        int Sum = Histogram[0];
        for (int i = 1; i < Limit; i++) {
            if (50 * Sum >= width * height) {
                threshold = i;
                break;
            }
            Sum += Histogram[i];
        }

        threshold = 200;
    }

    public static void push(int x, int y) {
        queue[front].x = x;
        queue[front].y = y;
        front++;
        inqueue[x][y] = true;
    }

    public static void BFS(int x, int y) {
        int u, v, i, j;
        rear = 0;
        front = 0;
        push(x, y);

        int x1 = x;
        int y1 = y;
        int x2 = x;
        int y2 = y;

        while (rear < front) {
            x = queue[rear].x;
            y = queue[rear].y;
            rear++;

            for (i = -radius; i <= radius; i++) {
                for (j = -radius; j <= radius; j++) {
                    u = x + i;
                    v = y + j;
                    if ((u >= 0) && (u < width) && (v >= 0) && (v < height)) {
                        if (InfoGray[u][v] < threshold) {
                            if (!inqueue[u][v]) {
                                push(u, v);
                                x1 = Math.min(x1, u);
                                y1 = Math.min(y1, v);
                                x2 = Math.max(x2, u);
                                y2 = Math.max(y2, v);
                            }
                        }
                    }
                }
            }
        }

        if (front < MinimumPoints) {
            return;
        }

        Rect[nRects] = new aRect();
        Rect[nRects].nPoints = front;
        Rect[nRects].x1 = x1;
        Rect[nRects].y1 = y1;
        Rect[nRects].x2 = x2;
        Rect[nRects].y2 = y2;
        nRects++;
    }

    public static void Init() {
        int count = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (InfoGray[i][j] < threshold) {
                    count++;
                }
            }
        }

        queue = new aPoint[count];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                inqueue[i][j] = false;
            }
        }

        for (int i = 0; i < count; i++) {
            queue[i] = new aPoint();
        }

        nRects = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (InfoGray[i][j] < threshold) {
                    if (!inqueue[i][j]) {
                        BFS(i, j);
                    }
                }
            }
        }
    }

    public static int RGB(int red, int green, int blue) {
        return (0xff000000) | (red << 16) | (green << 8) | blue;
    }

    public static void DrawRect(BufferedImage image, int x1, int y1, int x2, int y2, String Colour) {
        Graphics2D g2d = image.createGraphics();
        if (Colour.equals("RED")) {
            g2d.setColor(Color.RED);
        } else {
            g2d.setColor(Color.GREEN);
        }
        g2d.drawLine(x1, y1, x2, y1);
        g2d.drawLine(x2, y1, x2, y2);
        g2d.drawLine(x2, y2, x1, y2);
        g2d.drawLine(x1, y2, x1, y1);
    }

    public static void DrawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.drawLine(x1, y1, x2, y2);
        g2d.drawLine(x1, y1 - 1, x2, y2 - 1);
        g2d.drawLine(x1, y1 + 1, x2, y2 + 1);
    }

    public static int findset(int v) {
        if (parent[v] < 0) {
            return v;
        }
        parent[v] = findset(parent[v]);
        return parent[v];
    }

    public static void union(int u, int v) {
        u = findset(u);
        v = findset(v);
        if (u != v) {
            if (Math.abs(parent[u]) < Math.abs(parent[v])) {
                parent[v] += parent[u];
                parent[u] = v;
            } else {
                parent[u] += parent[v];
                parent[v] = u;
            }
        }
    }

    public static boolean Intersect(int i, int j) {
        if (Math.abs(Rect[i].x1 - Rect[j].x1) > 4 * (Rect[i].y2 - Rect[i].y1 + Rect[j].y2 - Rect[j].y1 + 2)) {
            return false;
        }

        if ((Rect[i].y1 >= Rect[j].y1) && (Rect[i].y1 <= Rect[j].y2)) {
            return true;
        }
        if ((Rect[i].y2 >= Rect[j].y1) && (Rect[i].y2 <= Rect[j].y2)) {
            return true;
        }
        if ((Rect[j].y1 >= Rect[i].y1) && (Rect[j].y1 <= Rect[i].y2)) {
            return true;
        }
        if ((Rect[j].y2 >= Rect[i].y1) && (Rect[j].y2 <= Rect[i].y2)) {
            return true;
        }
        return false;
    }

    public static void LineSegmentation() {
        int i, j, v, IntersectLength;
        boolean condition;

        parent = new int[nRects];
        for (i = 0; i < nRects; i++) {
            parent[i] = -1;
        }

        //Union 2 parts of character i
        for (i = 0; i < nRects; i++) {
            for (j = 0; j < nRects; j++) {
                if ((i != j) && (Rect[i].y1 > Rect[j].y2) && (Rect[j].nPoints <= 50) && (Rect[i].y1 - Rect[j].y2 <= 15)) {
                    condition = false;
                    if ((Rect[j].x1 >= Rect[i].x1) && (Rect[j].x1 <= Rect[i].x2)) {
                        condition = true;
                    }
                    if ((Rect[j].x2 >= Rect[i].x1) && (Rect[j].x2 <= Rect[i].x2)) {
                        condition = true;
                    }

                    if (condition) {
                        union(i, j);
                    }
                }
            }
        }

        for (i = 0; i < nRects; i++) {
            for (j = i + 1; j < nRects; j++) {
                if (Intersect(i, j)) {
                    IntersectLength = Math.min(Rect[i].y2, Rect[j].y2) - Math.max(Rect[i].y1, Rect[j].y1);

                    condition = false;
                    if (IntersectLength * 100 >= IntersectPercent * (Rect[i].y2 - Rect[i].y1 + 1)) {
                        condition = true;
                    }
                    if (IntersectLength * 100 >= IntersectPercent * (Rect[j].y2 - Rect[j].y1 + 1)) {
                        condition = true;
                    }

                    if (condition) {
                        union(i, j);
                    }
                }
            }
        }

        nLines = 0;
        for (i = 0; i < nRects; i++) {
            if (parent[i] < 0) {
                nLines++;
            }
        }

        Line = new aLine[nLines];
        nLines = 0;
        for (i = 0; i < nRects; i++) {
            if (parent[i] < 0) {
                Line[nLines] = new aLine();
                Line[nLines].y1 = Rect[i].y1;
                Line[nLines].y2 = Rect[i].y2;
                Line[nLines].Rect = new aRect[Math.abs(parent[i])];
                Line[nLines].nRects = 0;

                for (j = 0; j < nRects; j++) {
                    if (findset(j) == i) {
                        Line[nLines].y1 = Math.min(Line[nLines].y1, Rect[j].y1);
                        Line[nLines].y2 = Math.max(Line[nLines].y2, Rect[j].y2);
                        Line[nLines].Rect[Line[nLines].nRects] = Rect[j];
                        Line[nLines].nRects++;
                    }
                }

                nLines++;
            }
        }
    }

    public static void WordSegmentation(String OutputName) throws IOException {
        int i, j, v, t, k, MinY, distance;
        int x1, y1, x2, y2;
        boolean started, merge;
        String res;

        TextFilePrinter file = new TextFilePrinter();
        file.Rewrite(OutputName);
        System.out.println("Results:");

        ContinuousWord.Init(infoFileName);

        boolean used[] = new boolean[nLines];
        for (i = 0; i < nLines; i++) {
            used[i] = false;
        }

        nWords = 0;
        for (t = 0; t < nLines; t++) {
            v = 0;
            MinY = height;
            for (i = 0; i < nLines; i++) {
                if (!used[i]) {
                    if (Line[i].y1 < MinY) {
                        MinY = Line[i].y1;
                        v = i;
                    }
                }
            }

            used[v] = true;
            for (i = 0; i < Line[v].nRects; i++) {
                for (j = i + 1; j < Line[v].nRects; j++) {
                    if (Line[v].Rect[i].x1 > Line[v].Rect[j].x1) {
                        aRect temp = Line[v].Rect[i];
                        Line[v].Rect[i] = Line[v].Rect[j];
                        Line[v].Rect[j] = temp;
                    }
                }
            }

            while (true) {
                merge = false;
                for (i = 0; i < Line[v].nRects; i++) {
                    for (j = i + 1; j < Line[v].nRects; j++) {
                        if (Line[v].Rect[j].x1 <= Line[v].Rect[i].x2) {
                            merge = true;

                            Line[v].Rect[i].y1 = Math.min(Line[v].Rect[i].y1, Line[v].Rect[j].y1);
                            Line[v].Rect[i].x2 = Math.max(Line[v].Rect[i].x2, Line[v].Rect[j].x2);
                            Line[v].Rect[i].y2 = Math.max(Line[v].Rect[i].y2, Line[v].Rect[j].y2);

                            Line[v].nRects--;
                            for (k = j; k < Line[v].nRects; k++) {
                                Line[v].Rect[k] = Line[v].Rect[k + 1];
                            }

                            break;
                        }
                    }
                    if (merge) {
                        break;
                    }
                }
                if (!merge) {
                    break;
                }
            }

            distance = 0;
            for (i = 0; i < Line[v].nRects - 1; i++) {
                distance = Math.max(distance, Line[v].Rect[i + 1].x1 - Line[v].Rect[i].x2);
            }

            started = false;
            i = 0;
            while (i < Line[v].nRects) {
                x1 = Line[v].Rect[i].x1;
                y1 = Line[v].Rect[i].y1;
                x2 = Line[v].Rect[i].x2;
                y2 = Line[v].Rect[i].y2;

                k = i;
                for (j = i + 1; j < Line[v].nRects; j++) {
                    int d = Line[v].Rect[j].x1 - x2;
                    if (2 * d > distance) {
                        if (d * 1.5 > Line[v].Rect[j].y2 - Line[v].Rect[j].y1) {
                            if (d * 1.5 > Line[v].Rect[j - 1].y2 - Line[v].Rect[j - 1].y1) {
                                break;
                            }
                        }
                    }
                    k = j;
                    y1 = Math.min(y1, Line[v].Rect[j].y1);
                    x2 = Math.max(x2, Line[v].Rect[j].x2);
                    y2 = Math.max(y2, Line[v].Rect[j].y2);
                }

                if (started) {
                    file.print(" ");
                    System.out.print(" ");
                }
                started = true;
                res = ContinuousWord.Recognition(InfoGray, x1, y1, x2, y2);
                file.print(res);
                System.out.print(res);

                i = k + 1;
                Word[nWords] = new aRect();
                Word[nWords].nPoints = t;
                Word[nWords].x1 = x1;
                Word[nWords].y1 = y1;
                Word[nWords].x2 = x2;
                Word[nWords].y2 = y2;
                nWords++;
            }

            file.println();
            System.out.println();
        }

        file.close();
    }

    public static void Recognition(BufferedImage image) throws IOException {
        Input(image);
        System.out.println("[Finish Image Reading]");

        FindThreshold();
        Init();
        System.out.println("[Finish Image Processing]");

        LineSegmentation();
        WordSegmentation(exportedOutput);
        
        

        for (int i = 0; i < nLines; i++) {
            for (int j = 0; j < Line[i].nRects; j++) {
                DrawRect(image, Line[i].Rect[j].x1, Line[i].Rect[j].y1, Line[i].Rect[j].x2, Line[i].Rect[j].y2, "GREEN");
            }
        }

        for (int i = 0; i < nWords; i++) {
            DrawRect(image, Word[i].x1, Word[i].y1, Word[i].x2, Word[i].y2, "RED");
        }

        boolean used[][] = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                used[i][j] = false;
            }
        }

        for (int i = 0; i < nWords; i++) {
            for (int x = Word[i].x1; x <= Word[i].x2; x++) {
                for (int y = Word[i].y1; y <= Word[i].y2; y++) {
                    used[x][y] = true;
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!used[i][j]) {
                    image.setRGB(i, j, RGB(0, 0, 0));
                }
            }
        }

        for (int i = 0; i < nWords - 1; i++) {
            if (Word[i].nPoints == Word[i + 1].nPoints) {
                DrawLine(image, Word[i].x2, (Word[i].y1 + Word[i].y2) / 2, Word[i + 1].x1, (Word[i + 1].y1 + Word[i + 1].y2) / 2);
            }
        }

        File file = new File(exportedImage);
        ImageIO.write(image, getType(exportedImage), file);
    }
    
    public static void Recognition(String infoParametersFileName) throws IOException {
        infoFileName = infoParametersFileName;
        infoParametersReading();

        File file = new File(importedImage);
        BufferedImage image = ImageIO.read(file);

        Recognition(image);
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
