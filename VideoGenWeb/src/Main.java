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
		port(8080);

		enableCORS("*", "*", "*");

		// Generate random video
		get("/generate-random-video/:name/:fps/:scale", (request, response) -> {
			JSONObject json = new JSONObject();
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
			String name = request.params("name");
			return FileHelper.fileToHttpServletResponse("files/gif/" + name + ".gif", response);
		});

		// Get video preview
		get("/getVariantsCSV", (request, response) -> {
			return FileHelper.fileToHttpServletResponse("files/csv/variants.csv", response);
		});

		// Get video
		get("/getMP4/:name", (request, response) -> {
			response.header("Content-Type", "video/mp4");
			String name = request.params("name");
			return FileHelper.fileToHttpServletResponse("files/videos/video_" + name + ".mp4", response);
		});

		// Get max duration
		get("/analyzeDurations", (request, response) -> {
			response.header("Content-Type", "application/json");
			double[] durations = AnalysisTools.getDurations(videoGenModel);
			JSONObject json = new JSONObject();
			json.put("min", (int) (durations[0] + 0.5f));
			json.put("max", (int) (durations[1] + 0.5f));
			return json;
		});

		// Get all variants analysis
		get("/getVariants", (request, response) -> {
			response.header("Content-Type", "application/json");
			return AnalysisTools.getAllVariants(videoGenModel);
		});

		// Get model
		get("/getModel", (request, response) -> {
			System.out.println("TEST");
			response.header("Content-Type", "application/json");
			FileHelper.cleanDirectory("files/thumbnails");
			VideoGenService.generateThumbnails(videoGenModel);
			return VideoGenService.videoModelToJson(videoGenModel).toString();
		});

		// Get video preview
		get("/getThumbnail/:id", (request, response) -> {
			System.out.println("/getThumbnail/" + request.params("id"));
			response.header("Content-Type", "image/gif");
			String id = request.params("id");
			return FileHelper.fileToHttpServletResponse("files/thumbnails/" + id + ".png", response);
		});

		// Generate video
		post("/generate-video/:name/:fps/:scale", (request, response) -> {

			JSONObject json = new JSONObject();
			response.header("Content-Type", "application/json");
			int fps = Integer.parseInt(request.params("fps"));
			double scale = Double.parseDouble(request.params("scale")) / 100;
			JSONObject clips = new JSONObject(request.body());
			System.out.println(request.body());
			if (VideoGenService.generateVideo(videoGenModel, request.params("name"), fps, scale, clips)) {
				json.put("status", "OK");
				json.put("message", "Video generated.");
			} else {

				json.put("status", "KO");
				json.put("message", "Server Error");
			}

			return json;
		});

	}

	private static void enableCORS(final String origin, final String methods, final String headers) {

		options("/*", (request, response) -> {

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Request-Method", methods);
			response.header("Access-Control-Allow-Headers", headers);
			// Note: this may or may not be necessary in your particular application
			response.type("application/json");
		});
	}
}