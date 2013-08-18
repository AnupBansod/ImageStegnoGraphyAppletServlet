/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Chetan
 */
public class Testing {

    public static void main(String args[]) throws MalformedURLException, IOException {
    //    Testing t1=new Testing();
      //  t1.encode();
        System.out.println("done");
    }

    public boolean encode() throws MalformedURLException, IOException {
        String path = "http://www.ljes.org/app/webroot/userfiles/68/Image/science_labwork(1).gif";
        String path2 = "http://www.ljes.org/app/webroot/userfiles/68/Image/science_labwork(1).gif";
        String original = "stego_stego";
        String ext1 = "png";
        String stegan = "DemoOutput2";
        String message = "helloTesting2";
        String file_name = image_path(path2, original, ext1);
      
        BufferedImage image = ImageIO.read(new URL(path));
        image = add_text(image, message);

        return (setImage(image, new File(image_path("D:/", "anup", "png")), "png"));
    }

    private String image_path(String path, String name, String ext) {
        return path + "/" + name + "." + ext;
    }

     private BufferedImage getImage(String f)
	    {
        BufferedImage   image   = null;
	        File        file    = new File(f);

	        try
	        {
	            image = ImageIO.read(file);
	        }
	        catch(Exception ex)
	        {
	            JOptionPane.showMessageDialog(null,
	                "Image could not be read!","Error",JOptionPane.ERROR_MESSAGE);
	        }
	        return image;
	    }

      private BufferedImage user_space(BufferedImage image)
	    {
	        //create new_img with the attributes of image
	        BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	        Graphics2D  graphics = new_img.createGraphics();
	        graphics.drawRenderedImage(image, null);
	        graphics.dispose(); //release all allocated memory for this image
	        return new_img;
	    }

       private BufferedImage add_text(BufferedImage image, String text)
	    {
	        //convert all items to byte arrays: image, message, message length
	        byte img[]  = get_byte_data(image);
	        byte msg[] = text.getBytes();
	        byte len[]   = bit_conversion(msg.length);
	        try
        {
	            encode_text(img, len,  0); //0 first positiong
            encode_text(img, msg, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
	        }
	        catch(Exception e)
	        {
	            JOptionPane.showMessageDialog(null,
	"Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
	        }
	        return image;
	    }
       private byte[] get_byte_data(BufferedImage image)
	    {
	        WritableRaster raster   = image.getRaster();
	        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
	        return buffer.getData();
	    }

        private byte[] bit_conversion(int i)
	    {
	        //originally integers (ints) cast into bytes
	        //byte byte7 = (byte)((i & 0xFF00000000000000L) >>> 56);
	        //byte byte6 = (byte)((i & 0x00FF000000000000L) >>> 48);
        //byte byte5 = (byte)((i & 0x0000FF0000000000L) >>> 40);
	        //byte byte4 = (byte)((i & 0x000000FF00000000L) >>> 32);

	        //only using 4 bytes
	        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
	        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
	        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
	        byte byte0 = (byte)((i & 0x000000FF)       );
	        //{0,0,0,byte0} is equivalent, since all shifts >=8 will be 0
	        return(new byte[]{byte3,byte2,byte1,byte0});
	    }

        private byte[] encode_text(byte[] image, byte[] addition, int offset)
	    {
        //check that the data + offset will fit in the image
	        if(addition.length + offset > image.length)
	        {
	            throw new IllegalArgumentException("File not long enough!");
	        }
	        //loop through each addition byte
	        for(int i=0; i<addition.length; ++i)
	        {
	            //loop through the 8 bits of each byte
	            int add = addition[i];
	            for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
	            {
	                //assign an integer to b, shifted by bit spaces AND 1
	                //a single bit of the current byte
	                int b = (add >>> bit) & 1;
	                //assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
	                //changes the last bit of the byte in the image to be the bit of addition
	                image[offset] = (byte)((image[offset] & 0xFE) | b );
	            }
	        }
	        return image;
	    }

         private boolean setImage(BufferedImage image, File file, String ext)
	    {
	        try
	        {
	            file.delete(); //delete resources used by the File
	            ImageIO.write(image,ext,file);
	            return true;
	        }
	        catch(Exception e)
	        {
	            JOptionPane.showMessageDialog(null,
	                "File could not be saved!","Error",JOptionPane.ERROR_MESSAGE);
	            return false;
	        }
	    }
}
