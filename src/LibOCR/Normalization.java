// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package LibOCR;

public class Normalization {

    protected static int MaxWidth = 300;
    protected static int MaxHeight = 300;

    protected static int X, Y, u1, v1, u2, v2;
    protected static int sum[][] = new int[MaxWidth][MaxHeight];
    protected static int h[][] = new int[MaxWidth][MaxHeight];

    public static int GCD(int u, int v) {
        while ((u > 0) && (v > 0)) {
            if (u >= v) {
                u %= v;
            } else {
                v %= u;
            }
        }
        if (u > 0) {
            return u;
        }
        return v;
    }

    public static int SCM(int u, int v) {
        int t = GCD(u, v);
        return (u / t) * v;
    }

    public static int FindSum(int u1, int v1, int u2, int v2) {
        u1++;
        v1++;
        u2++;
        v2++;
        return sum[u2][v2] - sum[u1 - 1][v2] - sum[u2][v1 - 1] + sum[u1 - 1][v1 - 1];
    }

    public static int FindT(int i) {
        return i / u1;
    }

    public static int FindK(int j) {
        return j / v1;
    }

    public static int left(int i) {
        int t;
        t = i / u1;
        return t * u1;
    }

    public static int right(int i) {
        int t;
        t = i / u1;
        return (t + 1) * u1 - 1;
    }

    public static int up(int j) {
        int k;
        k = j / v1;
        return k * v1;
    }

    public static int down(int j) {
        int k;
        k = j / v1;
        return (k + 1) * v1 - 1;
    }

    public static int calculate(int f[][], int i1, int j1, int i2, int j2) {
        int t1, k1, t2, k2, i3, j3, i4, j4, temp, res;

        if (i1 > i2) {
            temp = i1;
            i1 = i2;
            i2 = temp;
        }

        if (j1 > j2) {
            temp = j1;
            j1 = j2;
            j2 = temp;
        }

        t1 = FindT(i1);
        k1 = FindK(j1);
        t2 = FindT(i2);
        k2 = FindK(j2);

        if ((t1 == t2) && (k1 == k2)) {
            return f[t1][k1] * (i2 - i1 + 1) * (j2 - j1 + 1);
        }

        if ((t1 == t2) && (k1 == k2 - 1)) {
            j3 = down(j1);
            res = calculate(f, i1, j1, i2, j3);

            j4 = up(j2);
            res += calculate(f, i1, j2, i2, j4);

            return res;
        }

        if ((t1 == t2) && (k2 - k1 >= 2)) {
            j3 = down(j1);
            res = calculate(f, i1, j1, i2, j3);

            j4 = up(j2);
            res += calculate(f, i1, j2, i2, j4);
            res += FindSum(t1, k1 + 1, t1, k2 - 1) * v1 * (i2 - i1 + 1);

            return res;
        }

        if ((k1 == k2) && (t1 == t2 - 1)) {
            i3 = right(i1);
            res = calculate(f, i1, j1, i3, j2);

            i4 = left(i2);
            res += calculate(f, i4, j1, i2, j2);

            return res;
        }

        if ((k1 == k2) && (t2 - t1 >= 2)) {
            i3 = right(i1);
            res = calculate(f, i1, j1, i3, j2);

            i4 = left(i2);
            res += calculate(f, i4, j1, i2, j2);
            res += FindSum(t1 + 1, k1, t2 - 1, k1) * u1 * (j2 - j1 + 1);

            return res;
        }

        if (t1 == t2 - 1) {
            i3 = right(i1);
            res = calculate(f, i1, j1, i3, j2);

            i4 = left(i2);
            res += calculate(f, i4, j1, i2, j2);

            return res;
        }

        if (k1 == k2 - 1) {
            j3 = down(j1);
            res = calculate(f, i1, j1, i2, j3);

            j4 = up(j2);
            res += calculate(f, i1, j4, i2, j2);

            return res;
        }

        i3 = right(i1);
        res = calculate(f, i1, j1, i3, j2);

        i4 = left(i2);
        res += calculate(f, i4, j1, i2, j2);

        j3 = down(j1);
        res += FindSum(t1 + 1, k1, t2 - 1, k1) * u1 * (j3 - j1 + 1);

        j4 = up(j2);
        res += FindSum(t1 + 1, k2, t2 - 1, k2) * u1 * (j2 - j4 + 1);
        res += FindSum(t1 + 1, k1 + 1, t2 - 1, k2 - 1) * u1 * v1;

        return res;
    }

    public static void Resize(int f[][], int g[][], int x1, int y1, int x2, int y2) {
        int i, j, i1, j1, i2, j2;

        X = SCM(x1, x2);
        Y = SCM(y1, y2);

        u1 = X / x1;
        v1 = Y / y1;
        u2 = X / x2;
        v2 = Y / y2;

        for (i = 0; i <= x1; i++) {
            sum[i][0] = 0;
        }
        for (j = 0; j <= y1; j++) {
            sum[0][j] = 0;
        }

        for (i = 1; i <= x1; i++) {
            for (j = 1; j <= y1; j++) {
                sum[i][j] = sum[i - 1][j] + sum[i][j - 1] + f[i - 1][j - 1] - sum[i - 1][j - 1];
            }
        }

        for (i = 0; i < x2; i++) {
            for (j = 0; j < y2; j++) {
                i1 = i * u2;
                j1 = j * v2;

                i2 = (i + 1) * u2 - 1;
                j2 = (j + 1) * v2 - 1;

                g[i][j] = calculate(f, i1, j1, i2, j2);
                g[i][j] /= (u2 * v2);
            }
        }
    }

    public static void Standardize(int f[][], int g[][], int x1, int y1, int x2, int y2) {
        double t;
        int X, Y, i, j, xmid1, ymid1, xmid2, ymid2;

        t = (double) (x2 - 2) / (double) (x1);
        if ((double) (y2 - 2) / (double) (y1) < t) {
            t = (double) (y2 - 2) / (double) (y1);
        }

        X = (int) (t * (double) (x1));
        Y = (int) (t * (double) (y1));

        for (i = 0; i < x2; i++) {
            for (j = 0; j < y2; j++) {
                g[i][j] = 255;
            }
        }

        Resize(f, h, x1, y1, X, Y);

        xmid1 = (X - 1) / 2;
        ymid1 = (Y - 1) / 2;
        xmid2 = (x2 - 1) / 2;
        ymid2 = (y2 - 1) / 2;

        for (i = 0; i < X; i++) {
            for (j = 0; j < Y; j++) {
                g[i - xmid1 + xmid2][j - ymid1 + ymid2] = h[i][j];
            }
        }
    }

}
