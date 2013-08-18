import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * @author Anup Bansod
 */
public class EchoApplet extends Applet {

    private TextField inputField = new TextField();
    private TextField outputField = new TextField();
    private TextArea exceptionArea = new TextArea();

    public void init() {
		setLayout(new GridBagLayout());

		// add title
		Label title = new Label("Stegnography Client", Label.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		add(title, c);

		// add input label, field and send button
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		add(new Label("Input:", Label.RIGHT), c);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		add(inputField, c);
		Button sendButton = new Button("Send");
		c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(sendButton, c);
                
		sendButton.addActionListener(new ActionListener() {
                        @Override
			public void actionPerformed(ActionEvent e) {
				onSendData();
			}
		});

		// add output label and non-editable field
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		add(new Label("Output:", Label.RIGHT), c);
		c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		add(outputField, c);
		outputField.setEditable(false);

		// add exception label and non-editable textarea
		c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		add(new Label("Exception:", Label.RIGHT), c);
		c = new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(exceptionArea, c);
		exceptionArea.setEditable(false);
	}

	/* Get a connection to the servlet */
	private URLConnection getServletConnection()
		throws MalformedURLException, IOException {

		// Connection zum Servlet öffnen
		URL urlServlet = new URL(getCodeBase(), "echo");
		URLConnection con = urlServlet.openConnection();

		// konfigurieren
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestProperty(
			"Content-Type",
			"application/x-java-serialized-object");
		return con;
	}

	/* Send the inputField data to the servlet and show the result in the outputField */
        
	private void onSendData() {
		try {
			// get input data for sending
			String input = inputField.getText();
                        BufferedImage image_encoded = encode(input);
			// send data to the servlet
                        
                        
                        URLConnection con = getServletConnection();
                        OutputStream outstream = con.getOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(outstream);
                        ImageIcon image_encoded2 = new ImageIcon((Image)image_encoded);
                        oos.writeObject(image_encoded2);
                        
                        
		//	URLConnection con = getServletConnection();
		//	OutputStream outstream = con.getOutputStream();
		//	ObjectOutputStream oos = new ObjectOutputStream(outstream);
		//	oos.writeObject(input);
			oos.flush();
			oos.close();
                        
			// receive result from servlet
			InputStream instr = con.getInputStream();
			ObjectInputStream inputFromServlet = new ObjectInputStream(instr);
			String result = (String) inputFromServlet.readObject();
			inputFromServlet.close();
			instr.close();

			// show result
			outputField.setText(result);

		} catch (Exception ex) {
			exceptionArea.setText(ex.toString() + "And msg = " + ex.getMessage() );
		}
	}

        // These are the functions used for encoding and decoding the text
        
        private byte[] encode_text(byte[] image, byte[] addition, int offset)
	    {
	        if(addition.length + offset > image.length)
	            throw new IllegalArgumentException("File not long enough!");
	        for(int i=0; i<addition.length; ++i){
	            int add = addition[i];
	            for(int bit=7; bit>=0; --bit, ++offset){
	                int b = (add >>> bit) & 1;
	                image[offset] = (byte)((image[offset] & 0xFE) | b );
	            }
	        }
	        return image;
	    }

        private byte[] bit_conversion(int i){
	        byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
	        byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
	        byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
	        byte byte0 = (byte)((i & 0x000000FF)       );
	        return(new byte[]{byte3,byte2,byte1,byte0});
	    }
        

        private BufferedImage add_text(BufferedImage image, String text){
	        byte img[]  = get_byte_data(image);
	        byte msg[] = text.getBytes();
	        byte len[]   = bit_conversion(msg.length);
	        try{
	            encode_text(img, len,  0); //0 first positiong
                    encode_text(img, msg, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
	        }catch(Exception e){
	            JOptionPane.showMessageDialog(null,
                    "Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
	        }
	        return image;
	    }

       private byte[] get_byte_data(BufferedImage image){
	        WritableRaster raster   = image.getRaster();
	        DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
	        return buffer.getData();
	    }

       private BufferedImage user_space(BufferedImage image){
                BufferedImage new_img  = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
	        Graphics2D  graphics = new_img.createGraphics();
	        graphics.drawRenderedImage(image, null);
	        graphics.dispose(); //release all allocated memory for this image
	        return new_img;
	    }

        public BufferedImage encode(String input) {
        try{
        //String path2 ="F:/VS Projects/project apoorva/Steganography";
        String path = "http://localhost:8084/ServletAppletDemo2";
        String original = "stego_stego";
        String ext = "png";
        String stegan = "DemoOutput3";
        String message = input;
        String file_name = image_path(path, original, ext);
    
        BufferedImage image_orig =ImageIO.read(new URL(file_name));
        
        if(image_orig==null){
            JOptionPane.showMessageDialog(null,"Image Cannot be read!", "Error",JOptionPane.ERROR_MESSAGE);
        }
        
        BufferedImage image = user_space(image_orig);
        image = add_text(image, message);
        JOptionPane.showMessageDialog(null,"Image Encoded Successfully!", "Error",JOptionPane.ERROR_MESSAGE);
    
        return image;
        }
        catch(Exception e){
           JOptionPane.showMessageDialog(null,e.toString(), "Error",JOptionPane.ERROR_MESSAGE);
           return null;
        }
    } 
    private String image_path(String path, String name, String ext) {
        return path + "/" + name + "." + ext;
    }

}
