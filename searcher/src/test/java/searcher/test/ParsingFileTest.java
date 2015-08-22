package searcher.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import mockit.Deencapsulation;
import mockit.integration.junit4.JMockit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import searcher.exception.NotAFolderException;
import searcher.exception.SearcherTimeoutExceededException;
import searcher.parser.FileNameParser;

@RunWith(JMockit.class)
public class ParsingFileTest {

	// private final String ROOTFOLDERPATH = "/";
	private final String ROOTFOLDERPATH = "C:\\Users\\N56834\\Desktop";
	private final String DESKTOP_FOLDER = "C:\\";

	// private final String DESKTOP_FOLDER = "/home/gokhanabi";

	@Ignore
	public void testParsingFileNames() throws NotAFolderException, SearcherTimeoutExceededException {
		File rootFolder = new File(ROOTFOLDERPATH);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		List<String> fileNameList = Deencapsulation.getField(fnp, "fileNames");
		assertTrue(fileNameList != null && !fileNameList.isEmpty());
	}

	@Ignore
	public void testFindingFileByWildCard() throws NotAFolderException, SearcherTimeoutExceededException {
		File rootFolder = new File(ROOTFOLDERPATH);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		fnp.getFileByWildCard("3e");
	}

	@Test
	public void testFindingAFile() throws NotAFolderException, SearcherTimeoutExceededException {
		File rootFolder = new File(DESKTOP_FOLDER);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		List<String> fileNameList = Deencapsulation.getField(fnp, "fileNames");
		for (String string : fileNameList) {
			if (string.contains("REST-Project-2-soapui-project.xml")) {
				System.err.println("Filename is in the list....");
			}
		}
		assertTrue(fileNameList != null && !fileNameList.isEmpty());
		List<File> foundFiles = fnp.getFileByFullFileName("REST-Project-2-soapui-project.xml");
		assertTrue(foundFiles != null && foundFiles.size() > 0);
		System.err.println(foundFiles);
	}

}
