import static spark.Spark.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

import helper.FileHelper;

import org.json.JSONObject;
import service.VideoGenService;
import spark.Filter;


public class Main {
	private static VideoGeneratorModel videoGenModel = new VideoGenHelper().loadVideoGenerator(URI.createURI("files/videomodel/example3.videogen"));
	
    public static void main(String[] args) {

    	// Start server
        port(8080);
        
        // Generate random video
        get("/generate-video/:name", (request, response) -> {
        	JSONObject json = new JSONObject();
        	response.header("Access-Control-Allow-Origin", "http://localhost:3000");
            response.header("Access-Control-Allow-Methods", "GET");
        	response.header("Content-Type", "application/json");
        	if(VideoGenService.generateVideo(videoGenModel, request.params("name"))) {
        		json.put("status", "OK");
        		json.put("message", "Video generated.");
        	}else {
        		json.put("status", "KO");
        		json.put("message", "Server Error");
        	}
            return json;
        });
        
        // Get video preview
        get("/getGIF/:name", (request, response) -> {
        	response.header("Content-Type", "image/gif");
        	response.header("Access-Control-Allow-Origin", "http://localhost:3000");
            response.header("Access-Control-Allow-Methods", "GET");
        	String name = request.params("name");
            return FileHelper.fileToHttpServletResponse("files/gif/"+ name + ".gif", response);
        });
        
     // Get video preview
        get("/getMP4/:name", (request, response) -> {
        	response.header("Content-Type", "video/mp4");
        	response.header("Access-Control-Allow-Origin", "http://localhost:3000");
            response.header("Access-Control-Allow-Methods", "GET");
        	String name = request.params("name");
            return FileHelper.fileToHttpServletResponse("files/videos/video_"+ name + ".mp4", response);
        });
        
    }
}