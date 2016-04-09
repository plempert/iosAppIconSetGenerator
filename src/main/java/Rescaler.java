import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Patrick on 2/13/16.
 */
public class Rescaler {
    public static void main(String [] args){
        BufferedImage testImage = ImageUtils.readImage("src/main/resources/testImage/test.png");
        Rescaler r = new Rescaler();
        r.generateAppSet(testImage, UUID.randomUUID().toString().replaceAll("-", ""));
    }

    private class SetSize{
        int size;
        String desc;
        public SetSize(int size, String desc) {
            this.size = size;
            this.desc = desc;
        }
    }

    private SetSize [] appSetSizes = {new SetSize(29*2,"_29pt_2x"),
                                            new SetSize(29*3,"_29pt_3x"),
                                            new SetSize(40*2,"_40pt_2x"),
                                            new SetSize(40*3,"_40pt_3x"),
                                            new SetSize(60*2,"_60pt_2x"),
                                            new SetSize(60*3,"_60pt_3x"),
                                            new SetSize(29,  "_29pt_1x"),
                                            new SetSize(40,  "_40pt_1x"),
                                            new SetSize(76,  "_76pt_1x"),
                                            new SetSize(76*2,"_76pt_2x"),
                                            new SetSize((int)83.5*2,"_83.5pt_2x")};

    private String path_prefix = "src/main/resources/images/";

    public String generateAppSet(BufferedImage img, String folderName){
        // String folderName = UUID.randomUUID().toString().replaceAll("-", "");
        // System.out.println(folderName);
        // If img size isn't correct return error
        if(img.getHeight() == img.getWidth() && img.getHeight() >= 100){
            // populate folder with app icon set
            writeAppSetToFolder(img, folderName);
            // zip it up
            generateZipFile(path_prefix+folderName+".zip", path_prefix+folderName);
            // delete folder
            deleteFolder(path_prefix+folderName);
            return "Success";
        } else {
            return "Error: Image height does not match its width, or image height is less than 1000 px.";
        }
    }
    
    private void writeAppSetToFolder(BufferedImage img, String folderName){
        new File(path_prefix+folderName).mkdir();
        ArrayList<BufferedImage> img_arr = new ArrayList<>();
        for(SetSize i: appSetSizes){
            BufferedImage new_img = resize(img, i.size, i.size);
            ImageUtils.writeImage(new_img, path_prefix+folderName+"/"+i.desc+".png");
        }
    }
    
    private void generateZipFile(String zipFileName, String sourceFolderPath){
        //String OUTPUT_ZIP_FILE = folderName+".zip";
        ZipUtils appZip = new ZipUtils(zipFileName, sourceFolderPath);
        appZip.generateFileList(new File(sourceFolderPath));
        appZip.zipIt(zipFileName);
    }
    
    private void deleteFolder(String folderPath){
        File folder_to_delete = new File(folderPath);
        try {
            FileUtils.deleteDirectory(folder_to_delete);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        // http://stackoverflow.com/questions/9417356/bufferedimage-resize
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

}
