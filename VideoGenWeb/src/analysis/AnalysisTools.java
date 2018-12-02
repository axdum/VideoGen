package analysis;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.xtext.example.mydsl.videoGen.AlternativeVideoSeq;
import org.xtext.example.mydsl.videoGen.Image;
import org.xtext.example.mydsl.videoGen.MandatoryVideoSeq;
import org.xtext.example.mydsl.videoGen.Media;
import org.xtext.example.mydsl.videoGen.OptionalVideoSeq;
import org.xtext.example.mydsl.videoGen.VideoDescription;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;
import org.xtext.example.mydsl.videoGen.VideoSeq;

import helper.FFMPEG;
import helper.FileHelper;

public class AnalysisTools {
	/**
	 * Calculate the max duration of a video from model.
	 * 
	 * @param videoGenModel : Video model
	 * @return Max duration (seconds)
	 */
	public static double getMaxDuration(VideoGeneratorModel videoGenModel) {
		System.out.println("Calculate Max duration...");
		double maxDuration = 0;
		EList<Media> medias = videoGenModel.getMedias();
		for (Media media : medias) {
			if (media instanceof Image) {
				// nothing, cya later
			} else if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					String location = ((MandatoryVideoSeq) vseq).getDescription().getLocation();
					maxDuration += FFMPEG.getDuration(location);
				} else if (vseq instanceof OptionalVideoSeq) {
					String location = ((OptionalVideoSeq) vseq).getDescription().getLocation();
					maxDuration += FFMPEG.getDuration(location);
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					double maxAltDuration = 0;
					for (VideoDescription video : videos) {
						String location = video.getLocation();
						double duration = FFMPEG.getDuration(location);
						if (duration > maxAltDuration) {
							maxAltDuration = duration;
						}
					}
					maxDuration += maxAltDuration;
				}
			}
		}
		System.out.println("Max Duration = " + maxDuration + " sec");
		return maxDuration;
	}

	/**
	 * Generate all Variants of a video from model in a CSV file.
	 * @param videoGenModel : Video model
	 */
	public static void getAllVariants(VideoGeneratorModel videoGenModel) {
		EList<Media> medias = videoGenModel.getMedias();
		// list of sequences Ids
		ArrayList<String> seqIds = new ArrayList<String>();
		// list of variants (variant = list of sequences)
		ArrayList<ArrayList<VideoDescription>> variants = new ArrayList<ArrayList<VideoDescription>>();
		variants.add(new ArrayList<VideoDescription>());
		for (Media media : medias) {
			if (media instanceof VideoSeq) {
				VideoSeq vseq = (VideoSeq) media;
				if (vseq instanceof MandatoryVideoSeq) {
					VideoDescription desc = ((MandatoryVideoSeq) vseq).getDescription();
					// Add Id to SeqIds list
					seqIds.add(desc.getVideoid());
					// Add to All variants
					for (ArrayList<VideoDescription> variant : variants) {
						variant.add(desc);
					}
				} else if (vseq instanceof OptionalVideoSeq) {
					VideoDescription desc = ((OptionalVideoSeq) vseq).getDescription();
					// Add Id to SeqIds list
					seqIds.add(desc.getVideoid());
					// Clone variants list
					ArrayList<ArrayList<VideoDescription>> clone = clone(variants);
					// Add sequence to all variants in clone
					for (ArrayList<VideoDescription> variant : clone) {
						variant.add(desc);
					}
					// Add clone to variants list
					variants.addAll(clone);
				} else if (vseq instanceof AlternativeVideoSeq) {
					AlternativeVideoSeq valt = (AlternativeVideoSeq) vseq;
					EList<VideoDescription> videos = valt.getVideodescs();
					// Create clean list of variants
					ArrayList<ArrayList<VideoDescription>> newVariants = new ArrayList<ArrayList<VideoDescription>>();
					for (VideoDescription desc : videos) {
						// Add Id to SeqIds list
						seqIds.add(desc.getVideoid());
						// Clone variants list
						ArrayList<ArrayList<VideoDescription>> clone = clone(variants);
						// Add Sequence to All variants in clone
						for (ArrayList<VideoDescription> variant : clone) {
							variant.add(desc);
						}
						// Add clone to clean variants list
						newVariants.addAll(clone);
					}
					variants = newVariants;
				}
			}
		}
		generateAllVariantsCSV(variants, seqIds);
	}
	
	/**
	 * Generate the CSV file from a list of all variants.
	 * 
	 * @param variants : Variants list
	 * @param seqIds : Sequences IDs list
	 */
	private static void generateAllVariantsCSV(ArrayList<ArrayList<VideoDescription>> variants,
			ArrayList<String> seqIds) {
		System.out.println("Generate variants CSV...\n");
		ArrayList<ArrayList<String>> variantsIds = variantsVideoDescToIds(variants);
		// header
		String csvStr = "id,";
		for (String id : seqIds) {
			csvStr += id + ",";
		}
		csvStr += "Size, Real size\n";
		for (int i = 0; i < variantsIds.size(); i++) {
			ArrayList<String> variant = variantsIds.get(i);
			csvStr += (i + 1) + ",";
			long variantSize = 0;
			String playlistTxt = "";
			for (String id : seqIds) {
				int index = variant.indexOf(id);
				if (index != -1) {
					csvStr += "TRUE,";
					String location = variants.get(i).get(index).getLocation();
					File f = new File(location);
					variantSize = variantSize + f.length();
					playlistTxt += "file '" + location + "'\n";
				} else {
					csvStr += "FALSE,";
				}
			}
			String name = "variant" + (i + 1);
			FileHelper.writeFile(playlistTxt, name + ".txt");
			FFMPEG.concateToMP4(name);
			// TODO : WAIT
			File f = new File("C:\\ws\\VideoGen\\VideoGenWeb\\files\\videos\\video_" + name + ".mp4");
			csvStr += Long.toString(variantSize) + ",";
			csvStr += Long.toString(f.length()) + "\n";
		}
		FileHelper.writeFile(csvStr, "csv/variants.csv");
	}
	
	/**
	 * Clone a Variants list.
	 * @param target : Original variants list
	 * @return Variants list clone
	 */
	static ArrayList<ArrayList<VideoDescription>> clone(ArrayList<ArrayList<VideoDescription>> target) {
		ArrayList<ArrayList<VideoDescription>> clone = new ArrayList<>();
		for (ArrayList<VideoDescription> variant : target) {
			ArrayList<VideoDescription> seqs = new ArrayList<>();
			variant.forEach(v -> seqs.add(v));
			clone.add(seqs);
		}
		return clone;
	}
	
	/**
	 * Replace VideoDescription by their Id.  
	 * 
	 * @param target : Variants list (VideoDescription)
	 * @return Variants list (Id)
	 */
	private static ArrayList<ArrayList<String>> variantsVideoDescToIds(ArrayList<ArrayList<VideoDescription>> target) {
		ArrayList<ArrayList<String>> clone = new ArrayList<>();
		for (ArrayList<VideoDescription> variant : target) {
			ArrayList<String> seqs = new ArrayList<>();
			variant.forEach(v -> seqs.add(v.getVideoid()));
			clone.add(seqs);
		}
		return clone;
	}
}
