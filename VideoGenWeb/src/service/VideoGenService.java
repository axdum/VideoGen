package service;

import java.util.Random;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.BlackWhiteFilter;
import org.xtext.example.mydsl.videoGen.Filter;
import org.xtext.example.mydsl.videoGen.FlipFilter;
import org.xtext.example.mydsl.videoGen.Image;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.NegateFilter;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;

import helper.FFMPEG;
import helper.FileHelper;

public class VideoGenService {
	public static boolean generateVideo(VideoGeneratorModel videoGenModel, String playlistFileName) {
		boolean res = false;
		// Edit clips and write playlist.txt
		System.out.println("INFO: EDIT CLIPS");
		editClips(videoGenModel, playlistFileName);
		// concats clips in a video
		System.out.println("INFO: CONCAT CLIPS");
		res = FFMPEG.concateToMP4(playlistFileName);
		if (res) {
			System.out.println("INFO: GENERATE GIF");
			res = FFMPEG.mp4ToGIF(playlistFileName, 5, 0.3);
		}
		return res;
	}
	
	/**
	 * Edit All clips and generate playlist.txt
	 * 
	 * @param videoGenModel    : Video model
	 * @param playlistFileName : Playlist name
	 * @return true if video is generated
	 */
	private static boolean editClips(VideoGeneratorModel videoGenModel, String playlistFileName) {
		String playlist = "";
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					playlist += "file '" + editClip(((MandatoryVideoSeq) vseq).getDescription())+ "'\n";
					System.out.println("PLAYLIST: " + playlist);
				} else if (vseq instanceof OptionalVideoSeq) {
					boolean random = new Random().nextBoolean();
					if (random) {
						playlist += "file '" + editClip(((OptionalVideoSeq) vseq).getDescription())+ "'\n";
						System.out.println("PLAYLIST: " + playlist);
					}
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					int size = videos.size();
					int indexOfSelectedVideo = new Random().nextInt(size);
					VideoDescription selectedVideo = videos.get(indexOfSelectedVideo);
					playlist += "file '"+ editClip(selectedVideo)+ "'\n";
					System.out.println("PLAYLIST: " + playlist);
				}
			}
		}
		FileHelper.writeFile(playlist, playlistFileName + ".txt");
		return true;
	}

	private static String editClip(VideoDescription desc) {
		/*if(desc.getFilter() == null && desc.getText() == null) {
			return desc.getLocation();
		}else {
			FFMPEG.editClip(desc);
			return "tmp\\" + desc.getLocation();
		}*/
		return desc.getLocation();
	}
}
