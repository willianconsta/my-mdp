package mymdp.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import mymdp.core.MDP;
import mymdp.problem.MDPBuilder;

public class MdpXmlWriterAndReaderTest
{
	private final MdpXmlReader reader = new MdpXmlReader();
	private final MdpXmlWriter writer = new MdpXmlWriter();
	private final MDP testMdp = MDPBuilder.newBuilder()
			.states(ImmutableSet.of("s1", "s2"))
			.action("a", ImmutableSet.of(new String[]{"s1", "s2", "1.0"}))
			.action("b", ImmutableSet.of(new String[]{"s2", "s1", "0.7"}, new String[]{"s2", "s2", "0.3"}))
			.rewards(ImmutableMap.of("s1", 1.0, "s2", 2.0))
			.discountRate(0.9)
			.build();

	@Test
	public void writeAndRead() throws IOException, JAXBException {
		final File file = File.createTempFile("test", "mdp");
		file.deleteOnExit();
		file.createNewFile();

		try ( final FileOutputStream stream = new FileOutputStream(file) ) {
			writer.writeTo(stream, testMdp);
		}
		try ( final FileInputStream stream = new FileInputStream(file) ) {
			final MDP readMdp = reader.read(stream);
			assertThat(readMdp).isEqualTo(testMdp);
		}
	}
}
