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

import com.gokhanozg.wordhasher.WordHasher;

@RunWith(JMockit.class)
public class ParsingFileTest {

	// private final String ROOTFOLDERPATH = "/";
	// private final String ROOTFOLDERPATH = "C:\\Users\\N56834\\Desktop";
	// private final String DESKTOP_FOLDER = "C:\\";
	private final String DESKTOP_FOLDER = "C:\\Users\\Masraf2\\Desktop";
	private final String ROOTFOLDERPATH = "C:\\";

	// private final String DESKTOP_FOLDER = "/home/gokhanabi";

	@Ignore
	public void testParsingFileNames() throws NotAFolderException, SearcherTimeoutExceededException {
		File rootFolder = new File(ROOTFOLDERPATH);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		List<String> fileNameList = Deencapsulation.getField(fnp, "fileNames");
		assertTrue(fileNameList != null && !fileNameList.isEmpty());
	}

	@Test
	public void testFindingFileByWildCard() throws NotAFolderException, SearcherTimeoutExceededException {
		File rootFolder = new File(ROOTFOLDERPATH);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		List<File> foundFiles = fnp.getFileByFullFileName("REST-Project-2-soapui-project.xml");
		assertTrue(foundFiles != null && foundFiles.size() > 0);
		foundFiles = fnp.getFileByWildCard("REST");
		assertTrue(foundFiles != null && foundFiles.size() > 0);

	}

	/**
	 * Beware, might take real long....
	 * 
	 * @throws SearcherTimeoutExceededException
	 * @throws NotAFolderException
	 */
	@Ignore
	public void testWaitingForWordHasher() throws SearcherTimeoutExceededException, NotAFolderException {
		File rootFolder = new File(ROOTFOLDERPATH);
		FileNameParser fnp = new FileNameParser(rootFolder);
		fnp.parseFileNames();
		WordHasher wh = Deencapsulation.getField(fnp, "wordHasher");
		while (wh == null) {
			wh = Deencapsulation.getField(fnp, "wordHasher");
		}
		long start = System.currentTimeMillis();
		List<File> foundFiles = fnp.getFileByWildCard("REST");
		assertTrue(foundFiles != null && foundFiles.size() > 0);
		long end = System.currentTimeMillis();
		long differ = end - start;
		assertTrue(differ < 200l);
	}

	@Test
	public void listRoots() {
		File[] roots = File.listRoots();
		for (File file : roots) {
			System.err.println(file.getAbsolutePath());
		}
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
