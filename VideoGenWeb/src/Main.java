import static spark.Spark.*;

import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

import helper.FileHelper;

import org.json.JSONObject;

import service.AnalysisTools;
import service.VideoGenService;

public class Main {
	private static VideoGeneratorModel videoGenModel = new VideoGenHelper()
			.loadVideoGenerator(URI.createURI("files/videomodel/example3.videogen"));

	public static void main(String[] args) {
		// Start server
		port(8090);

		// Generate random video
		get("/generate-random-video/:name/:fps/:scale", (request, response) -> {
			JSONObject json = new JSONObject();
			response.header("Access-Control-Allow-Origin", "http://localhost:3000");
			response.header("Access-Control-Allow-Methods", "POST, GET");
			response.header("Content-Type", "application/json");
			int fps = Integer.parseInt(request.params("fps"));
			double scale = Double.parseDouble(request.params("scale")) / 100;
			if (VideoGenService.generateVideo(videoGenModel, request.params("name"), fps, scale)) {
				json.put("status", "OK");
				json.put("message", "Video generated.");
			} else {
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
			return FileHelper.fileToHttpServletResponse("files/gif/" + name + ".gif", response);
		});
		
		// Get video preview
		get("/getVariantsCSV", (request, response) -> {
					response.header("Access-Control-Allow-Origin", "http://localhost:3000");
					response.header("Access-Control-Allow-Methods", "GET");
					return FileHelper.fileToHttpServletResponse("files/csv/variants.csv", response);
				});

		// Get video
		get("/getMP4/:name", (request, response) -> {
			response.header("Content-Type", "video/mp4");
			response.header("Access-Control-Allow-Origin", "http://localhost:3000");
			response.header("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
			String name = request.params("name");
			return FileHelper.fileToHttpServletResponse("files/videos/video_" + name + ".mp4", response);
		});

		// Get max duration
		get("/analyzeDurations", (request, response) -> {
			response.header("Content-Type", "application/json");
			response.header("Access-Control-Allow-Origin", "http://localhost:3000");
			response.header("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
			double[] durations = AnalysisTools.getDurations(videoGenModel);
			JSONObject json = new JSONObject();
			json.put("min", (int) (durations[0] + 0.5f));
			json.put("max", (int) (durations[1] + 0.5f));
			return json;
		});

		// Get all variants analysis
		get("/getVariants", (request, response) -> {
			response.header("Content-Type", "application/json");
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
			return AnalysisTools.getAllVariants(videoGenModel);
		});

		// Get model
		get("/getModel", (request, response) -> {
			System.out.println("TEST");
			response.header("Content-Type", "application/json");
			response.header("Access-Control-Allow-Origin", "http://localhost:3000");
			response.header("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
			FileHelper.cleanDirectory("files/thumbnails");
			VideoGenService.generateThumbnails(videoGenModel);
			return VideoGenService.videoModelToJson(videoGenModel).toString();
		});

		// Get video preview
		get("/getThumbnail/:id", (request, response) -> {
			System.out.println("/getThumbnail/" + request.params("id"));
			response.header("Content-Type", "image/gif");
			response.header("Access-Control-Allow-Origin", "http://localhost:3000");
			response.header("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
			String id = request.params("id");
			return FileHelper.fileToHttpServletResponse("files/thumbnails/" + id + ".png", response);
		});
		
		// Generate video
				post("/generate-video/:name/:fps/:scale", (request, response) -> {

					JSONObject json = new JSONObject();
					response.header("Access-Control-Allow-Origin", "http://localhost:3000");
					response.header("Access-Control-Allow-Methods", "GET, POST");
					response.header("Content-Type", "application/json");
					//int fps = Integer.parseInt(request.params("fps"));
					//double scale = Double.parseDouble(request.params("scale")) / 100;
					System.out.println("body : \n" + request.body());
					//JSONObject clips = new JSONObject(request.body());
					/*if (VideoGenService.generateVideo(videoGenModel, request.params("name"), fps, scale, clips)) {
						json.put("status", "OK");
						json.put("message", "Video generated.");
					} else {*/
						json.put("status", "KO");
						json.put("message", "Server Error");/*
					}*/
					return json;
				});

	}
}