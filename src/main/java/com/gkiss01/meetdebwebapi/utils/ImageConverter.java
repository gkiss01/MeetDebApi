package com.gkiss01.meetdebwebapi.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageConverter {

    public static boolean convertFormat(InputStream inputStream, String outputImagePath, String formatName) throws IOException {
        BufferedImage inputImage = ImageIO.read(inputStream);
        OutputStream outputStream = new FileOutputStream(outputImagePath);

        boolean result = ImageIO.write(inputImage, formatName, outputStream);

        outputStream.close();
        return result;
    }
}