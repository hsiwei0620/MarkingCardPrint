package markingcardprint;

import com.onbarcode.barcode.Code128;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

/**
 * @Date:  2018.08.24
 * @Author:  Hsiwei
 * @Note: using class path to read and write image
 * @classpath:  .. \ MarkingCardPrint \ build \ classes 
 */
public class MarkingCardPrint {   
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, Exception {

        //Get Date Time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss_");
        LocalDateTime now = LocalDateTime.now();
        String dateTime = dtf.format(now);

        // Initial data
        String lot_id = "U111708210001";
        String mtrl_lot_id = "RL17824015";

        // Read source picture
        BufferedImage lotIDImage, mtrlLotIDImage = null;
        BufferedImage bufImage = ImageIO.read(MarkingCardPrint.class.getResource("/doc/markCard.jpg"));
        
        generateBarcode(dateTime, lot_id, mtrl_lot_id);
        
        lotIDImage = getBarcodeImage("lot_id/", lot_id, dateTime);
        mtrlLotIDImage = getBarcodeImage("mtrl_lot_id/", mtrl_lot_id, dateTime);
        
        drawBarcode(bufImage, lotIDImage , mtrlLotIDImage);
        drawText(bufImage);
        
        //printImage(bufImage);
        
        // Save Output
        ImageIO.write(bufImage, "JPEG", new File("build\\classes\\doc\\output\\" + lot_id + "_" + mtrl_lot_id + ".jpg" ));
        
        // Resize And Show In Frame ( For Debug Use )
        BufferedImage resized = resize(bufImage, 375, 519);
        display(resized);
    }

    /* 
            @fileName :  lot_id\\
            @dateTime : 2018-09-03_14.30.41_
            @barcodeName : U111708210001
     */
    private static BufferedImage getBarcodeImage(String fileName, String barcodeName, String dateTime) {
        BufferedImage image = null;
        try {
            image = (BufferedImage) ImageIO.read(
                MarkingCardPrint.class.getResource("/barcode/" + fileName + dateTime + barcodeName + ".jpg")
            );
        } catch (IOException e) {
            System.out.println("Barcode Image Reading Error");
        }
        return image;
    }

    private static void drawBarcode(BufferedImage src, BufferedImage lot_barcode, BufferedImage mtrl_barcode) {
        Graphics g = src.getGraphics();
        g.drawImage(lot_barcode, 500, 900, null);
        g.drawImage(mtrl_barcode, 850, 1900, null);
        g.dispose(); 
    }

    private static void generateBarcode(String dateTime, String lot_id, String mtrl_lot_id) {
        try {
            // Barcode 
            Code128 barcode = new Code128();
            barcode.setShowText(true);
            barcode.setTextFont(new Font("default", 0, 50));
            barcode.setTextMargin(10);

            // Setting Lot ID  
            barcode.setX(6f);
            barcode.setBarcodeWidth(700f);
            barcode.setY(180f);
            barcode.setBarcodeHeight(200f);
            barcode.setData(lot_id);
            barcode.drawBarcode("build\\classes\\barcode\\lot_id\\" + dateTime + lot_id + ".jpg");

            // Setting Material Lot ID
            
            barcode.setX(4f);
            barcode.setBarcodeWidth(500f);
            barcode.setY(150f);
            barcode.setBarcodeHeight(180f);
            barcode.setData(mtrl_lot_id);
            barcode.drawBarcode("build\\classes\\barcode\\mtrl_lot_id\\" + dateTime + mtrl_lot_id + ".jpg");

        } catch (Exception e) {
            throw new ArithmeticException("Generate barcode error");
        }

    }

    private static void drawText(BufferedImage image) {
        String part_no = "199-0903";
        String date = "20180903";
        String weight = "200.00";
        String shift = "乙班";
        String product_id = "79700-1";

        Graphics g = image.getGraphics();
        g.setFont(new Font("default", Font.PLAIN, 100));
        g.setColor(Color.BLACK);
        g.drawString(part_no, 410, 1290);
        g.drawString(date, 410, 1450);
        g.dispose();

        Graphics g2 = image.getGraphics();
        g2.setFont(new Font("Sanserif", Font.PLAIN, 80));
        g2.setColor(Color.BLACK);
        g2.drawString(weight, 1190, 1290);
        g2.drawString(shift, 1210, 1450);
        g2.drawString(product_id, 335, 1615);
        g2.dispose();
    }

    private static void display(BufferedImage image) {
        // Use a label to display the image
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("Preview Printing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void printImage(BufferedImage image) {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                // Get the upper left corner that it printable
                int x = (int) Math.ceil(pageFormat.getImageableX());
                int y = (int) Math.ceil(pageFormat.getImageableY());
                if (pageIndex != 0) {
                    return NO_SUCH_PAGE;
                }
                graphics.drawImage(image, x, y, 375, 519, null);
                return PAGE_EXISTS;
            }
        });
        try {
            printJob.print();
        } catch (PrinterException e1) {
            e1.printStackTrace();
        }
    }

    private static BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

}
