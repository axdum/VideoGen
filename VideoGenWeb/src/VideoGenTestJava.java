import static org.junit.Assert.*;

import org.eclipse.emf.common.util.URI;
import org.junit.Test;
import org.xtext.example.mydsl.videoGen.VideoGeneratorModel;

public class VideoGenTestJava {
	
	@Test
	public void testInJava1() {
		VideoGeneratorModel videoGenModel = new VideoGenHelper().loadVideoGenerator(URI.createURI("example1.videogen"));
		assertNotNull(videoGenModel);
		System.out.println(videoGenModel.getInformation().getAuthorName());
		assertEquals(11, videoGenModel.getMedias().size());
	}
}
