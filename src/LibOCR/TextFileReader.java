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

public class TextFileReader {

    BufferedReader file;

    public void open(String FileName) throws IOException {
        file = new BufferedReader(new FileReader(FileName));
    }

    public String ReadLine() throws IOException {
        return file.readLine();
    }

    public void close() throws IOException {
        file.close();
    }

}
