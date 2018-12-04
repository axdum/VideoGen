package helper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.xtext.example.mydsl.videoGen.BlackWhiteFilter;
import org.xtext.example.mydsl.videoGen.FlipFilter;
import org.xtext.example.mydsl.videoGen.NegateFilter;
import org.xtext.example.mydsl.videoGen.VideoDescription;

// Tools to generate FFMPEG commands.
public class FFMPEG {
	/**
	 * Get the duration of a file.
	 * 
	 * @param filePath : File path
	 * @return Duration (seconds)
	 */
	public static double getDuration(String filePath) {
		String command = "ffprobe -v error -select_streams v:0 -show_entries stream=duration -of default=noprint_wrappers=1:nokey=1 "
				+ filePath;
		String durationStr = "";
		try {
			durationStr = CmdHelper.execCmdAndGetOutput(command);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double duration = Double.parseDouble(durationStr);
		return duration;
	}

	/**
	 * Concate clips from a playlist.txt.
	 * 
	 * @param playlistFileName : Playlist name
	 * @return true if success
	 */
	public static boolean concateToMP4(String playlistFileName) {
		System.out.println("Generate MP4 video from " + playlistFileName + ".txt...");
		String command = "ffmpeg -flush_packets 1 -y -f concat -safe 0 -i " + playlistFileName + ".txt -c copy "
				+ "files/videos/video_" + playlistFileName + ".mp4";
		if (CmdHelper.execCmd(command)) {
			System.out.println("files/videos/video_" + playlistFileName + ".mp4 generated\n\n");
			// Delete playlist.txt
			FileHelper.deleteFile(playlistFileName + ".txt");
			return true;
		} else {
			System.out.println("files/videos/video_" + playlistFileName + ".mp4 generation FAILED\n\n");
			// Delete playlist.txt
			FileHelper.deleteFile(playlistFileName + ".txt");
			return false;
		}
	}

	/**
	 * Convert mp4 to GIF
	 * 
	 * @param fileName     : Video file name
	 * @param framesPerSec : Fps
	 * @param scale        : Resolution scaling
	 * @return true if success
	 */
	public static boolean mp4ToGIF(String fileName, int framesPerSec, double scale) {
		System.out.println(
				"Generate GIF (fps: " + framesPerSec + ", scale: " + scale + ") from video_" + fileName + "...");
		String command = "ffmpeg -i " + "files/videos/video_" + fileName + ".mp4 -r 10 -vf scale=" + 1280 * scale + ":"
				+ 720 * scale + " files/gif/" + fileName + ".gif -hide_banner";
		if (CmdHelper.execCmdVerbose(command)) {
			System.out.println(fileName + ".gif generated\n\n");
			return true;
		} else {
			System.out.println(fileName + ".gif generation FAILED\n\n");
			return false;
		}
	}

	/**
	 * Apply All transformation to a clip (concat filters and text commands).
	 * Edited clip is generated in tmp/files/clips.
	 * 
	 * @param desc : Clip Description
	 * @return true if command success
	 */
	public static boolean editClip(VideoDescription desc) {
		String command = "ffmpeg -i " + desc.getLocation() + " -vf ";
		String commandEnd = " -codec:a copy tmp\\" + desc.getLocation();
		if (desc.getFilter() != null) {
			String filter = "";
			if (desc.getFilter() instanceof BlackWhiteFilter) {
				command += "\"hue=0:0\"";
			} else if (desc.getFilter() instanceof NegateFilter) {
				command += "\"hue=0:0\"";
			} else if (desc.getFilter() instanceof FlipFilter) {
				command += "\"hue=0:0\"";
			}
			if (desc.getText() != null) {
				command += ",";
			}
		}
		if (desc.getText() != null) {
			String posX = "(w-text_w)/2";
			String posY = "";
			switch (desc.getText().getPosition()) {
			case "CENTER":
				posY = "(h-text_h)/2";
				break;
			case "TOP":
				posY = "20";
				break;
			case "BOTTOM":
				posY = "h-th-20";
				break;
			default:
				posY = "(h-text_h)/2";
				break;
			}
			int fontSize = desc.getText().getSize();
			command += "drawtext=\"fontfile=NewAmsterdam.ttf:text='" + desc.getText().getContent()
					+ "':fontcolor=white: fontsize=" + fontSize + ": box=1: boxcolor=black@0.5:boxborderw=5: x=" + posX
					+ ": y=" + posY + "\"";
		}
		command += " -codec:a copy tmp\\" + desc.getLocation();
		System.out.println(command);
		if (CmdHelper.execCmd(command)) {
			System.out.println("EDIT COMMAND EXECUTED");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Generate clip thumbnail.
	 * 
	 * @param filePath : Clip path
	 * @return true if success
	 */
	public static boolean generateThumbnail(String filePath, String id) {
		Path path = Paths.get(filePath);
		String fileName = path.getFileName().toString();
		if (fileName.indexOf(".") > 0) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		String command = "ffmpeg -y -i " + filePath + " -r 1 -t 00:00:01 -ss 00:00:02 -f image2 files/thumbnails/"
				+ id + ".png";
		if (CmdHelper.execCmd(command)) {
			System.out.println(id + ".png generated");
			return true;
		} else {
			System.out.println(id + ".png generation failed");
			return false;
		}
	}

}
