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

public class BinaryFileReader {

    BufferedInputStream f;

    public boolean open(String name) throws IOException {
        try {
            f = new BufferedInputStream(new FileInputStream(name));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public int read() throws IOException {
        return f.read();
    }

    public int available() throws IOException {
        return f.available();
    }

    public void close() throws IOException {
        f.close();
    }

}
