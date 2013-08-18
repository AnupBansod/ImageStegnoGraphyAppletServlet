/**
 * @author Anup Bansod
 */
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import sun.awt.image.ToolkitImage;
public class EchoServlet extends HttpServlet{

@Override
public void doPost(
HttpServletRequest request,
HttpServletResponse response)
throws ServletException, IOException {
	try {
		response.setContentType("application/x-java-serialized-object");
                InputStream in = request.getInputStream();
                ObjectInputStream inputFromApplet = new ObjectInputStream(in);
                ImageIcon image = (ImageIcon) inputFromApplet.readObject();
                Image encoded_image = image.getImage();
                BufferedImage encoded_image2 = ((ToolkitImage) encoded_image).getBufferedImage();
                boolean status = saveImage(encoded_image2, new File(image_path("D:/", "stegan", "png")), "png");
                inputFromApplet.close();
                String result = decode("D:/", "stegan");
                System.out.println("Recieved image =  " + encoded_image.toString());
                
                if (status )
                    result = "Text Decoded = " + result;
                else
                    result += "Failed to Decode";

                OutputStream outstr = response.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outstr);
                oos.writeObject(result);
                oos.flush();
                oos.close();
                } catch (Exception e) {
                	e.printStackTrace();
                }
        }

        private String image_path(String path, String name, String ext) {
            return path + "/" + name + "." + ext;
        }
        private boolean saveImage(BufferedImage image, File file, String ext){
	        try{
	            file.delete(); 
	            ImageIO.write(image,ext,file);
	            return true;
	        }
	        catch(Exception e)
	        {
                    System.out.println("File could not be saved!");
	            return false;
	        }
	    }
        
       private byte[] decode_text(byte[] image){
            int length = 0;
	    int offset  = 32;
	    for(int i=0; i<32; ++i){
	            length = (length << 1) | (image[i] & 1);
	    }
	    byte[] result = new byte[length];
	    for(int b=0; b<result.length; ++b ){
	            for(int i=0; i<8; ++i, ++offset){
	                result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
	            }
	        }
	        return result;
	    }
       
        public String decode(String path, String name)
	    {
	        byte[] decode;
	        try
	        {
	            //user space is necessary for decrypting
	            BufferedImage image  = user_space(getImage(image_path(path,name,"png")));
	            decode = decode_text(get_byte_data(image));
	            return(new String(decode));
	        }
	        catch(Exception e){
                    System.out.println("There is no hidden message in this image!");
	            return "";
	        }
	    }
         private BufferedImage getImage(String f){
                BufferedImage   image   = null;
	        File file = new File(f);
	        try{
	            image = ImageIO.read(file);
	        }
	        catch(Exception ex){
                    System.out.println("Image could not be read!");
	        }
	        return image;
	    }
         private BufferedImage user_space(BufferedImage image){
            BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            Graphics2D  graphics = new_img.createGraphics();
            graphics.drawRenderedImage(image, null);
            graphics.dispose(); //release all allocated memory for this image
            return new_img;
	 }
         private byte[] get_byte_data(BufferedImage image)
	 {
            WritableRaster raster   = image.getRaster();
	    DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
	    return buffer.getData();
	 }
}
