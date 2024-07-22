package test.svg;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SvgTest {
    static final int DEFAULT_MARGIN = 8;
    //.......
    private BitMatrix createBitMatrix(String qrCodeText, int size) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, DEFAULT_MARGIN);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        return new QRCodeWriter().encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hints);
    }
    private void bitMatrixToSVG(BitMatrix bitMatrix, File outputFileSVG) throws IOException {
        int matrixWidth = bitMatrix.getWidth();
        int matrixHeight = bitMatrix.getHeight();
        int size = Math.max(matrixHeight,matrixWidth);
        SVGGraphics2D g2 = new SVGGraphics2D(matrixWidth, matrixWidth);
        g2.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixHeight; j++) {
                if (bitMatrix.get(i, j)) {
                    g2.fillRect(i, j, 1, 1);
                }
            }
        }
        SVGUtils.writeToSVG(outputFileSVG, g2.getSVGElement());
    }
    public File generate() throws IOException, WriterException {
        BitMatrix bitMatrix = createBitMatrix("https://medium.com/@thieunguyenhung", 100);
        File outputFileSVG = File.createTempFile("example", "qr.svg");
        bitMatrixToSVG(bitMatrix, outputFileSVG);
        return outputFileSVG;
    }

    public static void main(String[] args) throws IOException, WriterException {
        File generate = new SvgTest().generate();
        System.out.println(generate);
    }
}
