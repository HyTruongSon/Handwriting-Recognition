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
import java.util.Random;

public class MLP {

    double Epochs = 300;
    double LearningRate = 0.001;
    double Momentum = 0.9;
    double Epsilon = 0.0001;

    int nLayer;

    class aLayer {

        int n;
        double out[], theta[];
        double delta[][], cost[][];
    };
    aLayer Layer[];

    double Expected[];

    Random generator = new Random();

    BufferedInputStream FileInput;

    public int RandomInt(int n) {
        return generator.nextInt(n);
    }

    public void InitMemory(int n) {
        nLayer = n;
        Layer = new aLayer[nLayer];
        for (int i = 0; i < nLayer; i++) {
            Layer[i] = new aLayer();
        }
    }

    public void InitCoefficient() {
        int i, j, v, t;
        Expected = new double[Layer[nLayer - 1].n];

        for (i = 0; i < nLayer; i++) {
            if (i != 0) {
                Layer[i].theta = new double[Layer[i].n];
            }

            Layer[i].out = new double[Layer[i].n];

            if (i != nLayer - 1) {
                Layer[i].cost = new double[Layer[i].n][Layer[i + 1].n];
                Layer[i].delta = new double[Layer[i].n][Layer[i + 1].n];
            }
        }

        for (i = 0; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                for (v = 0; v < Layer[i + 1].n; v++) {
                    Layer[i].cost[j][v] = (double) (RandomInt(10)) / (10 * Layer[i + 1].n);
                    t = RandomInt(2);
                    if (t == 1) {
                        Layer[i].cost[j][v] = -Layer[i].cost[j][v];
                    }
                }
            }
        }
    }

    public MLP(int InputLayer, int OutputLayer) {
        InitMemory(2);

        Layer[0].n = InputLayer;
        Layer[1].n = OutputLayer;

        InitCoefficient();
    }

    public MLP(int InputLayer, int HiddenLayer, int OutputLayer) {
        InitMemory(3);

        Layer[0].n = InputLayer;
        Layer[1].n = HiddenLayer;
        Layer[2].n = OutputLayer;

        InitCoefficient();
    }

    public MLP(int InputLayer, int HiddenLayer1, int HiddenLayer2, int OutputLayer) {
        InitMemory(4);

        Layer[0].n = InputLayer;
        Layer[1].n = HiddenLayer1;
        Layer[2].n = HiddenLayer2;
        Layer[3].n = OutputLayer;

        InitCoefficient();
    }

    public MLP(int InputLayer, int HiddenLayer1, int HiddenLayer2, int HiddenLayer3, int OutputLayer) {
        InitMemory(5);

        Layer[0].n = InputLayer;
        Layer[1].n = HiddenLayer1;
        Layer[2].n = HiddenLayer2;
        Layer[3].n = HiddenLayer3;
        Layer[4].n = OutputLayer;

        InitCoefficient();
    }

    public MLP(int InputLayer, int HiddenLayer1, int HiddenLayer2, int HiddenLayer3, int HiddenLayer4, int OutputLayer) {
        InitMemory(6);

        Layer[0].n = InputLayer;
        Layer[1].n = HiddenLayer1;
        Layer[2].n = HiddenLayer2;
        Layer[3].n = HiddenLayer3;
        Layer[4].n = HiddenLayer4;
        Layer[5].n = OutputLayer;

        InitCoefficient();
    }

    public double ReadDouble() throws IOException {
        int aByte;
        char aChar;
        String s = "";

        while (true) {
            aByte = FileInput.read();
            aChar = (char) (aByte);
            if (aChar != ' ') {
                s += aChar;
                break;
            }
        }

        while (true) {
            aByte = FileInput.read();
            if (aByte == -1) {
                break;
            }
            aChar = (char) (aByte);
            if (aChar == ' ') {
                break;
            }
            s += aChar;
        }

        return Double.parseDouble(s);
    }

    public void SetCost(String FileName) throws IOException {
        int i, j, v;
        FileInput = new BufferedInputStream(new FileInputStream(FileName));

        for (i = 0; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                for (v = 0; v < Layer[i + 1].n; v++) {
                    Layer[i].cost[j][v] = ReadDouble();
                }
            }
        }

        FileInput.close();
    }

    public void SetMomentum(double value) {
        Momentum = value;
    }

    public void SetEpochs(int value) {
        Epochs = value;
    }

    public void SetLearningRate(double value) {
        LearningRate = value;
    }

    public void SetEpsilon(double value) {
        Epsilon = value;
    }

    public double GetMomentum() {
        return Momentum;
    }

    public double GetEpochs() {
        return Epochs;
    }

    public double GetLearningRate() {
        return LearningRate;
    }

    public double GetEpsilon() {
        return Epsilon;
    }

    public void WriteCost(String FileName) throws IOException {
        int i, j, v;

        FileWriter FileOutput = new FileWriter(FileName);
        PrintWriter Writer = new PrintWriter(FileOutput);

        for (i = 0; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                for (v = 0; v < Layer[i + 1].n; v++) {
                    Writer.print(Layer[i].cost[j][v] + " ");
                }
            }
        }

        Writer.close();
    }

    public double Sigmoid(double x) {
        return (double) (1) / (1 + Math.exp(-x));
    }

    public void Perceptron() {
        int i, j, v;
        double net;

        for (i = 1; i < nLayer; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                net = 0;
                for (v = 0; v < Layer[i - 1].n; v++) {
                    net += Layer[i - 1].out[v] * Layer[i - 1].cost[v][j];
                }
                Layer[i].out[j] = Sigmoid(net);
            }
        }
    }

    public double SquareError() {
        int i;
        double diff;
        double res = 0;

        for (i = 0; i < Layer[nLayer - 1].n; i++) {
            diff = Expected[i] - Layer[nLayer - 1].out[i];
            res += 0.5 * diff * diff;
        }

        return res;
    }

    public void BackPropagation() {
        int i, j, v;
        double sum, out, delta, theta;

        for (i = 0; i < Layer[nLayer - 1].n; i++) {
            out = Layer[nLayer - 1].out[i];
            Layer[nLayer - 1].theta[i] = out * (1 - out) * (Expected[i] - out);
        }

        for (i = 1; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                sum = 0;
                for (v = 0; v < Layer[i + 1].n; v++) {
                    sum += Layer[i + 1].theta[v] * Layer[i].cost[j][v];
                }
                out = Layer[i].out[j];
                Layer[i].theta[j] = out * (1 - out) * sum;
            }
        }

        for (i = 0; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                for (v = 0; v < Layer[i + 1].n; v++) {
                    delta = Layer[i].delta[j][v];
                    out = Layer[i].out[j];
                    theta = Layer[i + 1].theta[v];

                    Layer[i].delta[j][v] = LearningRate * theta * out + Momentum * delta;
                    Layer[i].cost[j][v] += Layer[i].delta[j][v];
                }
            }
        }
    }

    public void Study(double Input[], double Output[]) {
        int i, j, v;

        for (i = 0; i < Layer[0].n; i++) {
            Layer[0].out[i] = Input[i];
        }

        for (i = 0; i < Layer[nLayer - 1].n; i++) {
            Expected[i] = Output[i];
        }

        for (i = 0; i < nLayer - 1; i++) {
            for (j = 0; j < Layer[i].n; j++) {
                for (v = 0; v < Layer[i + 1].n; v++) {
                    Layer[i].delta[j][v] = 0;
                }
            }
        }

        for (i = 0; i < Epochs; i++) {
            Perceptron();
            BackPropagation();
            if (SquareError() < Epsilon) {
                break;
            }
        }
    }

    public void Classification(double Input[], double Output[]) {
        int i;

        for (i = 0; i < Layer[0].n; i++) {
            Layer[0].out[i] = Input[i];
        }

        Perceptron();

        for (i = 0; i < Layer[nLayer - 1].n; i++) {
            Output[i] = Layer[nLayer - 1].out[i];
        }
    }

}
