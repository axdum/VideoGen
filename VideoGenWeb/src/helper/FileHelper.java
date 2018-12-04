package helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.Filter;
import org.xtext.example.mydsl.videoGen.Image;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;

import spark.Response;

// Tools to manage files. 
public class FileHelper {
	/**
	 * Print content in a file (UTF-8).
	 * 
	 * @param content : Content
	 * @param path    : File Path
	 */
	public static void writeFile(String content, String path) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(path, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File '" + path + "' generated :\n");
		// System.out.println(content);
	}

	/**
	 * Create a vlc playlist (.m3u) from a video model.
	 * 
	 * @param videoGenModel    : Video model
	 * @param playlistFileName : Playlist file name
	 */
	public static void createVlcPlaylist(VideoGeneratorModel videoGenModel, String playlistFileName) {
		String playlist = "";
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					String location = ((MandatoryVideoSeq) vseq).getDescription().getLocation();
					playlist += location + "\n";
				} else if (vseq instanceof OptionalVideoSeq) {
					boolean random = new Random().nextBoolean();
					if (random) {
						String location = ((OptionalVideoSeq) vseq).getDescription().getLocation();
						playlist += location + "\n";
					}
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					int size = videos.size();
					int indexOfSelectedVideo = new Random().nextInt(size);
					VideoDescription selectedVideo = videos.get(indexOfSelectedVideo);
					playlist += selectedVideo.getLocation() + "\n";
				}
			}
		}
		writeFile(playlist, playlistFileName + ".m3u");
	}

	/**
	 * Create a text playlist (.txt) from a video model.
	 * 
	 * @param videoGenModel    : Video model
	 * @param playlistFileName : Playlist file name
	 */
	public static void createTxtPlaylist(VideoGeneratorModel videoGenModel, String playlistFileName) {
		String playlist = "";
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					String location = ((MandatoryVideoSeq) vseq).getDescription().getLocation();
					playlist += "file 'tmp\\" + location + "'\n";
				} else if (vseq instanceof OptionalVideoSeq) {
					boolean random = new Random().nextBoolean();
					if (random) {
						String location = ((OptionalVideoSeq) vseq).getDescription().getLocation();
						playlist += "file 'tmp\\" + location + "'\n";
					}
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					int size = videos.size();
					int indexOfSelectedVideo = new Random().nextInt(size);
					VideoDescription selectedVideo = videos.get(indexOfSelectedVideo);
					playlist += "file 'tmp\\" + selectedVideo.getLocation() + "'\n";
				}
			}
		}
		writeFile(playlist, playlistFileName + ".txt");
	}

	/**
	 * Convert a file to HttpServletResponse
	 * 
	 * @param filePath : File path
	 * @param response : API Response
	 * @return HttpServletResponse
	 */
	public static HttpServletResponse fileToHttpServletResponse(String filePath, Response response) {
		Path path = Paths.get(filePath);
		File f = new File(path.toString());
		byte[] data = null;
		try {
			data = Files.readAllBytes(path);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		HttpServletResponse raw = response.raw();
		response.header("Content-Disposition", "attachment; filename=" + f.getName());
		response.type("application/force-download");
		try {
			raw.getOutputStream().write(data);
			raw.getOutputStream().flush();
			raw.getOutputStream().close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return raw;
	}

	/**
	 * Copy directory content to an other directory
	 * 
	 * @param sourcePath      : Source path
	 * @param DestinationPath : Destination Path
	 */
	public static void copyDirectoryContent(String sourcePath, String DestinationPath) {
		File source = new File(sourcePath);
		File dest = new File(DestinationPath);
		try {
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete file.
	 * 
	 * @param fileLocation : File to delete location
	 */
	public static void deleteFile(String fileLocation) {
		File f = new File(fileLocation);
		try {
			if (f.delete()) {
				System.out.println(f.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete All files in directory
	 * 
	 * @param dirLocation
	 */
	public static void cleanDirectory(String dirLocation) {
		File dir = new File(dirLocation);
		for(File file: dir.listFiles()) 
		    if (!file.isDirectory()) 
		        file.delete();
	}
}
