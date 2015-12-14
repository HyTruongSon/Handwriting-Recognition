// Software: Handwriting Recognition Software in Java
// Author: Hy Truong Son
// Major: BSc. Computer Science
// Class: 2013 - 2016
// Institution: Eotvos Lorand University
// Email: sonpascal93@gmail.com
// Website: http://people.inf.elte.hu/hytruongson/
// Copyright 2015 (c), all rights reserved. Only use for academic purposes.

package handwriting.recognition;

import LibOCR.Normalization;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class AboutAuthor extends JFrame {
    
    private final String titleFrame = "Author: Hy Truong Son";
    private final int widthFrame = 1024;
    private final int heightFrame = 512;
    private final int marginFrame = 20;
    private final int widthLabel = 150;
    private final int widthText = widthFrame / 2 - widthLabel - 2 * marginFrame;
    private final int heightComponent = 30;
    private final int sizeButton = 80;
    private final int sizeImage = widthFrame / 2 - 2 * marginFrame;
    
    private final String imageFolder = "my-images/";
    private final int nImages = 5;
    private final BufferedImage buttonImage[] = new BufferedImage [nImages + 1];
    private int currentImage = 1;
    
    private final String info[] = {
        "Author:", "Hy Truong Son",
        "Nationality:", "Vietnamese",
        "Date of birth:", "November 12th, 1993",
        "Home city:", "Hanoi, Vietnam",
        "Education:", "BSc. Computer Science",
        "Class:", "2013 - 2016",
        "Institution:", "Eotvos Lorand University, Budapest, Hungary",
        "Email:", "sonpascal93@gmail.com",
        "Website:", "http://people.inf.elte.hu/hytruongson/",
        "Research interests:", "Computer vision and Machine Learning"
    };
    
    private JButton pictureButton;    
    
    public AboutAuthor() {
        setTitle(titleFrame);
        setSize(widthFrame, heightFrame);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        int lastY = addInformationText();
            
        readImages();
        addImages(lastY);
        
        pictureButton = new JButton();
        add(pictureButton);
        
        drawPicture();
        setVisible(true);
    }
    
    private void drawPicture() {
        String imageName = imageFolder + Integer.toString(currentImage) + "_image.jpg";
        BufferedImage image = null;
      
        try {
            image = ImageIO.read(new File(imageName));
        } catch (IOException exc) {
            System.err.println(exc.toString());
            JOptionPane.showMessageDialog(null, "Cannot find the image " + imageName);
        }
        
        int widthImage = image.getWidth(null);
        int heightImage = image.getHeight(null);
            
        int x = widthFrame / 2 + (widthFrame / 2 - widthImage) / 2;
        int y = (heightFrame - heightImage - marginFrame) / 2;
        
        pictureButton.setBounds(x, y, widthImage, heightImage);
        pictureButton.setIcon(new ImageIcon(image.getScaledInstance(widthImage, heightImage, Image.SCALE_DEFAULT)));
    }
    
    private int addInformationText() {
        int x = marginFrame;
        int y = marginFrame;
        int i = 0;
        
        while (i < info.length) {
            JLabel label = new JLabel(info[i]);
            label.setBounds(x, y, widthLabel, heightComponent);
            label.setForeground(Color.blue);
            add(label);
            
            ++i;
            
            JTextField text = new JTextField(info[i]);
            text.setBounds(x + widthLabel, y, widthText, heightComponent);
            text.setEditable(false);
            add(text);
            
            ++i;
            y += heightComponent;
        }
        
        JLabel label = new JLabel("Images:");
        label.setBounds(x, y, widthLabel, heightComponent);
        label.setForeground(Color.blue);
        add(label);
        
        return y + heightComponent;
    }
    
    private void addImages(int y) {
        int x0 = marginFrame;
        for (int i = 1; i <= nImages; ++i) {           
            JButton button = new JButton();
            
            int heightImage = buttonImage[i].getHeight(null);
            int widthImage = buttonImage[i].getWidth(null);
            button.setIcon(new ImageIcon(buttonImage[i]));
            
            // String buttonName = imageFolder + Integer.toString(i) + "_button.jpg";
            // button.setIcon(new ImageIcon(buttonName));
            
            button.setLocation(x0, y + (heightFrame - y - heightImage) / 2);
            button.setSize(widthImage, heightImage);
            button.putClientProperty("ID", i);
            button.addActionListener(ImageButtonAction);
            add(button);
            // buttonPanel.add(button);
            
            x0 += widthImage + marginFrame;
        }        
    }
    
    private final ActionListener ImageButtonAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)(e.getSource());
            int ID = (int)(button.getClientProperty("ID"));
            currentImage = ID;
            drawPicture();
        }
    };
    
    private void readImages() {
        for (int i = 1; i <= nImages; ++i) {
            String buttonName = imageFolder + Integer.toString(i) + "_button.jpg";
            
            boolean hasButton = true;
            buttonImage[i] = null;
            try {
                buttonImage[i] = ImageIO.read(new File(buttonName));
            } catch (IOException exc) {
                System.err.println(exc.toString());
                hasButton = false;
            }
            
            if (!hasButton) {
                String originName = imageFolder + Integer.toString(i) + ".jpg";
                BufferedImage originImage;
                try {
                    originImage = ImageIO.read(new File(originName));
                } catch (IOException exc) {
                    System.err.println(exc.toString());
                    JOptionPane.showMessageDialog(null, "Cannot find the image " + originName);
                    continue;
                }

                resizeImage(originImage, buttonName, sizeButton);
                
                try {
                    buttonImage[i] = ImageIO.read(new File(buttonName));
                } catch (IOException exc) {
                    System.err.println(exc.toString());
                }
            }
        }
    }
    
    private int RGB(int red, int green, int blue){
        return (0xff000000) | (red << 16) | (green << 8) | blue;
    }
    
    private void resizeImage(BufferedImage inputImage, String imageName, int size) {
        int widthImage = inputImage.getWidth(null);
        int heightImage = inputImage.getHeight(null);
        
        int r1[][] = new int [widthImage][heightImage];
        int g1[][] = new int [widthImage][heightImage];
        int b1[][] = new int [widthImage][heightImage];
        
        double ratio;
        if (widthImage > heightImage) {
            ratio = (double)(size) / widthImage;
        } else {
            ratio = (double)(size) / heightImage;
        }
        
        int w = (int)(widthImage * ratio);
        int h = (int)(heightImage * ratio);
        
        int r2[][] = new int [w][h];
        int g2[][] = new int [w][h];
        int b2[][] = new int [w][h];
                
        for (int i = 0; i < widthImage; ++i) {
            for (int j = 0; j < heightImage; ++j) {
                int RGB = inputImage.getRGB(i, j);
                r1[i][j] = (RGB & 0x00ff0000) >> 16;
		g1[i][j] = (RGB & 0x0000ff00) >> 8;
		b1[i][j] = RGB & 0x000000ff;
            }
        }
        
        Normalization.Resize(r1, r2, widthImage, heightImage, w, h);
        Normalization.Resize(g1, g2, widthImage, heightImage, w, h);
        Normalization.Resize(b1, b2, widthImage, heightImage, w, h);
        
	BufferedImage outputImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	for (int i = 0; i < w; ++i)
            for (int j = 0; j < h; ++j) {
                outputImage.setRGB(i, j, RGB(r2[i][j], g2[i][j], b2[i][j]));
            }
        
        try {
            ImageIO.write(outputImage, "jpg", new File(imageName));
        } catch (IOException exc) {
            System.err.println(exc.toString());
        }
    }
    
}
