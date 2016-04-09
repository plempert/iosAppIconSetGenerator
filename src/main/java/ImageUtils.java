import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Patrick on 2/13/16.
 */
public class ImageUtils {
    public static BufferedImage readImage(String str){
        BufferedImage img;
        try {
            img = ImageIO.read(new File(str));
            return img;
        } catch (IOException e) {
            // ...
        }
        return null;
    }

    public static void writeImage(BufferedImage img, String str){
        try {
            // retrieve image
            File outputfile = new File(str);
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            // ...
        }
    }
}
