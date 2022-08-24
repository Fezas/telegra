/*
 * Copyright (c) $originalComment.match("Copyright \(c\) (\d+)", 1, "-")2021. Stepantsov P.V.
 */

package fezas.telegra.util;

import com.google.zxing.*;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Инструменты для генерации и чтения QR-кодов
 *
 */
public class QrCodeCreateUtil {
    /**
     * Функция генерации изображений QR-кода, содержащих строковую информацию
     *
     * @param outputStream Путь к выходному потоку файла
     * @param content QR-код, несущий информацию
     * @param qrCodeSize Размер изображения QR-кода
     * @param imageFormat формат QR-кода
     * @throws WriterException
     * @throws IOException
     */
    private static final Logger logger = LogManager.getLogger();
    public boolean createQrCode(OutputStream outputStream, String content, int qrCodeSize, String imageFormat) throws WriterException, IOException{
        // Установить уровень исправления ошибок QR-кода MAP
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put (EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // Уровень исправления ошибок
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        AztecWriter aztecWriter = new AztecWriter();
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        // Создание QR-строки битовой матрицы (битовая матрица)
        BitMatrix byteMatrix = aztecWriter.encode(content, BarcodeFormat.AZTEC, qrCodeSize, qrCodeSize, hints);
        // Сделать BufferedImage разграничить QRCode (matrixWidth это пиксель строки QR-кода)
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Используем битовую матрицу для рисования и сохранения изображения
        graphics.setColor(Color.BLACK);
        for (int i = 0; i < matrixWidth; i++){
            for (int j = 0; j < matrixWidth; j++){
                if (byteMatrix.get(i, j)){
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return ImageIO.write(image, imageFormat, outputStream);
    }

    /**
     * Функция чтения QR-код и вывода несущей информации
     *
     * @param inputStream поток ввода изображения
     */
    public void readQrCode(InputStream inputStream) throws IOException{
        // Получить строковую информацию из входного потока
        BufferedImage image = ImageIO.read(inputStream);
        // Преобразование изображения в двоичный источник растрового изображения
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result = null ;
        try {
            result = reader.decode(bitmap);
        } catch (ReaderException e) {
            logger.error("Error", e);
        }
    }
}
