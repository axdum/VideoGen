package service;

import java.util.ArrayList;
import java.util.Random;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.json.JSONArray;
import org.json.JSONObject;
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

// Tools to generate video.
public class VideoGenService {
	/**
	 * Generate random video.
	 * 
	 * @param videoGenModel
	 * @param playlistFileName
	 * @param fps
	 * @param scale
	 * @return
	 */
	public static boolean generateVideo(VideoGeneratorModel videoGenModel, String playlistFileName, int fps, double scale) {
		boolean res = false;
		// Edit clips and write playlist.txt
		editClips(videoGenModel, playlistFileName);
		// Concats clips in a video
		res = FFMPEG.concateToMP4(playlistFileName);
		if (res) {
			// Generate video Preview
			res = FFMPEG.mp4ToGIF(playlistFileName, fps, scale);
		}
		return res;
	}
	
	/**
	 * Generate video from json
	 * 
	 * @param videoGenModel
	 * @param playlistFileName
	 * @param fps
	 * @param scale
	 * @param json
	 * @return
	 */
	public static boolean generateVideo(VideoGeneratorModel videoGenModel, String playlistFileName, int fps, double scale, JSONObject json) {
		boolean res = false;
		// Edit clips and write playlist.txt
		editClips(videoGenModel, playlistFileName, json);
		// Concats clips in a video
		res = FFMPEG.concateToMP4(playlistFileName);
		if (res) {
			// Generate video Preview
			res = FFMPEG.mp4ToGIF(playlistFileName, fps, scale);
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
					playlist += "file '" + editClip(((MandatoryVideoSeq) vseq).getDescription()) + "'\n";
					System.out.println("PLAYLIST: " + playlist);
				} else if (vseq instanceof OptionalVideoSeq) {
					boolean random = new Random().nextBoolean();
					if (random) {
						playlist += "file '" + editClip(((OptionalVideoSeq) vseq).getDescription()) + "'\n";
						System.out.println("PLAYLIST: " + playlist);
					}
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					int size = videos.size();
					int indexOfSelectedVideo = new Random().nextInt(size);
					VideoDescription selectedVideo = videos.get(indexOfSelectedVideo);
					playlist += "file '" + editClip(selectedVideo) + "'\n";
					System.out.println("PLAYLIST: " + playlist);
				}
			}
		}
		FileHelper.writeFile(playlist, playlistFileName + ".txt");
		return true;
	}
	
	/**
	 * Edit All clips and generate playlist.txt
	 * 
	 * @param videoGenModel    : Video model
	 * @param playlistFileName : Playlist name
	 * @return true if video is generated
	 */
	private static boolean editClips(VideoGeneratorModel videoGenModel, String playlistFileName, JSONObject json) {
		String playlist = "";
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					playlist += "file '" + editClip(((MandatoryVideoSeq) vseq).getDescription()) + "'\n";
					System.out.println("PLAYLIST: " + playlist);
				} else if (vseq instanceof OptionalVideoSeq) {
					boolean random = new Random().nextBoolean();
					if (random) {
						playlist += "file '" + editClip(((OptionalVideoSeq) vseq).getDescription()) + "'\n";
						System.out.println("PLAYLIST: " + playlist);
					}
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					int size = videos.size();
					int indexOfSelectedVideo = new Random().nextInt(size);
					VideoDescription selectedVideo = videos.get(indexOfSelectedVideo);
					playlist += "file '" + editClip(selectedVideo) + "'\n";
					System.out.println("PLAYLIST: " + playlist);
				}
			}
		}
		FileHelper.writeFile(playlist, playlistFileName + ".txt");
		return true;
	}

	/**
	 * Apply filters and text on clips.
	 * 
	 * @param desc : Clip desc
	 * @return Clip location (edited copy placed in a tmp folder)
	 */
	private static String editClip(VideoDescription desc) {
		/*
		 * if(desc.getFilter() == null && desc.getText() == null) { return
		 * desc.getLocation(); }else { FFMPEG.editClip(desc); return "tmp\\" +
		 * desc.getLocation(); }
		 */
		return desc.getLocation();
	}

	/**
	 * convert Model to JSON.
	 * 
	 * @param videoGenModel
	 * @return
	 */
	public static JSONObject videoModelToJson(VideoGeneratorModel videoGenModel) {
		JSONObject res = new JSONObject();
		JSONArray mediasJson = new JSONArray();
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					JSONObject seqM = new JSONObject();
					seqM.put("type", "mandatory");
					seqM.put("id", ((MandatoryVideoSeq) vseq).getDescription().getVideoid());
					mediasJson.put(seqM);
				} else if (vseq instanceof OptionalVideoSeq) {
					JSONObject seqO = new JSONObject();
					seqO.put("type", "optional");
					seqO.put("id", ((OptionalVideoSeq) vseq).getDescription().getVideoid());
					mediasJson.put(seqO);
				} else if (vseq instanceof AlternativeVideoSeq) {
					JSONObject seqA = new JSONObject();
					seqA.put("type", "alternative");
					seqA.put("id", ((AlternativeVideoSeq) vseq).getVideoid());
					JSONArray seqAs = new JSONArray();
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					for (VideoDescription desc : videos) {
						JSONObject seqA2 = new JSONObject();
						seqA2.put("id", desc.getVideoid());
						seqAs.put(seqA2);
					}
					seqA.put("alts", seqAs);
					mediasJson.put(seqA);
				}
			}
		}
		res.put("model", mediasJson);
		return res;
	}

	/**
	 * Generate thumbnails.
	 * 
	 * @param videoGenModel : Video model
	 */
	public static void generateThumbnails(VideoGeneratorModel videoGenModel) {
		String playlist = "";
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					VideoDescription desc = ((MandatoryVideoSeq) vseq).getDescription();
					FFMPEG.generateThumbnail(desc.getLocation(), desc.getVideoid());
				} else if (vseq instanceof OptionalVideoSeq) {
					VideoDescription desc = ((OptionalVideoSeq) vseq).getDescription();
					FFMPEG.generateThumbnail(desc.getLocation(), desc.getVideoid());
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					for (VideoDescription desc : videos) {
						FFMPEG.generateThumbnail(desc.getLocation(), desc.getVideoid());
					}
				}
			}
		}
	}
}
