package com.tiemens.secretshare.main.cli;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
* @author Crunchify.com
*/

public class MainQrCode {

    // Tutorial: http://zxing.org/w/docs/javadoc/index.html

    public static void main(String[] args, InputStream in, PrintStream out) {
        try {
            main(args);
        } catch (IOException e) {
            e.printStackTrace(out);
        }
    }

    // TODO: this is just sample "generate a qr-code image" code
    public static void main(String[] args) throws IOException {
        String myCodeText = "http://www.abc.com/";
        String inputFilePath = "build/qr-input.txt";
        // myCodeText = new String(Files.readAllBytes(Paths.get(inputFilePath)), StandardCharsets.UTF_8); // jdk1.7
        myCodeText = new Scanner( new File(inputFilePath) ).useDelimiter("\\A").next();
        String filePath;
        filePath =  "build/qr-output.png";
        for (int i = 0, n = args.length; i < n; i++)
        {
            if (args[i] == null)
            {
                continue;
            }

            filePath = args[0];
        }

        int size = 125;
        String fileType = "png";
        File myFile = new File(filePath);
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText,BarcodeFormat.QR_CODE, size, size, hintMap);
            int CrunchifyWidth = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,
                    BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < CrunchifyWidth; i++) {
                for (int j = 0; j < CrunchifyWidth; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }
            ImageIO.write(image, fileType, myFile);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\n\nYou have successfully created QR Code.");
    }

}

