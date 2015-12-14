// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package handwriting.recognition;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class TextFileViewer extends JFrame {
    
    private ArrayList<String> text;
    private int widthFrame;
    private int heightFrame;
    private int heightComponent = 30;
    private int marginFrame = 30;
    
    public TextFileViewer(String fileName) {
        try {
            input(fileName);
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(null, "Error when reading the output text file!");
            return;
        }
        
        setTitle(fileName);
        
        int maxLength = 0;
        for (int i = 0; i < text.size(); ++i) {
            maxLength = Math.max(maxLength, text.get(i).length());
        }
        
        widthFrame = maxLength * 8;
        heightFrame = text.size() * heightComponent + marginFrame;
        
        setSize(widthFrame, heightFrame);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        
        for (int i = 0; i < text.size(); ++i) {
            int y = i * heightComponent;
            JTextField field = new JTextField();
            field.setEditable(false);
            field.setText(text.get(i));
            field.setBounds(0, y, widthFrame, heightComponent);
            add(field);
        }
        
        setVisible(true);
    }
    
    private void input(String fileName) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(fileName));
        text = new ArrayList<>();
        while (true) {
            String str;
            try {
                str = file.readLine();
            } catch (IOException exc) {
                break;
            }
            if ((str == null) || (str.length() == 0)) {
                break;
            }
            text.add(str);
        }
        file.close();
    }
    
}
