import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

/**
 * Created by Patrick on 2/13/16.
 */
public class Endpoint {
    public static void main(String [] args){
        port(8080);
        clearCachePeriodically();
        staticFileLocation("/images");
        staticFileLocation("/public");
        get("/hello/:name", (req, res) -> "Hello " + req.params(":name") + "!");
        post("/api/v1/iOS/generateAppIconSet", Endpoint::generateAppIconSet);

    }

    private static void clearCachePeriodically(){
        Runnable clearCachePeriodically = () -> {
            try {
                File directory = new File("src/main/resources/images");
                while(true) {
                    Thread.sleep(5000);
                    if(directory.list().length>0) {
                        FileUtils.cleanDirectory(directory);
                        System.out.println("Cache has been cleared");
                    }
                }

            } catch(Exception v) {
                System.out.println(v);
            }
        };
        new Thread(clearCachePeriodically).start();
    }

    private static Object generateAppIconSet(Request req, Response res){
        String pathName = "src/main/resources/images";
        String folder_file_alias = UUID.randomUUID().toString().replaceAll("-", "");

        saveImgFile(req, pathName, folder_file_alias);
        generateZipFile(pathName+"/"+folder_file_alias+".png", folder_file_alias);
        deleteImg(pathName, folder_file_alias);
        return getZipFile(pathName+"/"+folder_file_alias+".zip", res);
        // zip file gets deleted momentarily in a separate thread...
    }

    private static void saveImgFile(Request req, String saveToFolder, String folder_file_alias){
        try {
            final File upload = new File(saveToFolder);
            if (!upload.exists() && !upload.mkdirs()) {
                throw new RuntimeException("Failed to create directory " + upload.getAbsolutePath());
            }

            // apache commons-fileupload to handle file upload
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(upload);
            ServletFileUpload fileUpload = new ServletFileUpload(factory);
            // List<FileItem> items = fileUpload.parseRequest(req.raw());
            List<FileItem> items = fileUpload.parseRequest(req.raw());

            // image is the field name that we want to save
            FileItem item = items.stream()
                    .filter(e -> "image".equals(e.getFieldName()))
                    .findFirst().get();

            item.write(new File(saveToFolder, folder_file_alias + ".png"));
        } catch(Exception e){e.printStackTrace();}
    }

    private static void generateZipFile(String imgPath, String folderName){
        BufferedImage testImage = ImageUtils.readImage(imgPath);
        Rescaler r = new Rescaler();
        r.generateAppSet(testImage, folderName);
    }

    private static void deleteImg(String pathName, String folder_file_alias){
        try {
            FileUtils.forceDelete(new File(pathName, folder_file_alias+".png"));
        } catch(Exception e){e.printStackTrace();}
    }

    private static Object getZipFile(String zipFilePath, Response res){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(zipFilePath));
            res.raw().getOutputStream().write(bytes);
            res.raw().getOutputStream().flush();
            res.raw().getOutputStream().close();
        } catch (Exception e){
            e.printStackTrace();
        }
        return res.raw();
    }
}
