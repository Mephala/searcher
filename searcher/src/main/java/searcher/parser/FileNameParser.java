package searcher.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import searcher.exception.ExceptionFactory;
import searcher.exception.NotAFolderException;
import searcher.exception.SearcherTimeoutExceededException;

import com.gokhanozg.wordhasher.WordHasher;

public class FileNameParser {

	private final File rootFolderToParse;
	private final int PARSE_THREADS = 8;
	private final List<String> fileNames = new ArrayList<>();
	private long parsePerformance = 0l;
	private final long PARSE_TIMEOUT = 1000000l;
	private final Logger logger = Logger.getLogger(getClass());
	private final Map<String, List<File>> filenameToFileMap = new HashMap<>();
	private Future<WordHasher> wordhasherFuture;
	private WordHasher wordHasher;

	public FileNameParser(File rootFolderToParse) throws NotAFolderException {
		this.rootFolderToParse = rootFolderToParse;
		if (!rootFolderToParse.isDirectory())
			throw ExceptionFactory.createNotAFolderException("Chosen root file is not a folder.");
	}

	public void parseFileNames() throws SearcherTimeoutExceededException {
		long start = System.currentTimeMillis();
		final ThreadPoolExecutor executor = new ThreadPoolExecutor(PARSE_THREADS, PARSE_THREADS, 5l, TimeUnit.SECONDS, new LinkedBlockingDeque());
		parseFileNamesMultiThread(executor, rootFolderToParse.listFiles());

		while (executor.getActiveCount() > 0)
			;
		final List<String> toBeHashedFileNames = new ArrayList<>();
		synchronized (fileNames) {
			for (String fileName : fileNames) {
				if (fileName.contains("REST-Project-2-soapui-project.xml"))
					System.err.println("Mevlit");
				int lastSeparatorIndex = fileName.lastIndexOf(File.separator);
				if (lastSeparatorIndex == -1) {
					logger.fatal("Encountered a file path without file separator ????");
					continue;
				}
				String fileNameWithoutPath = fileName.substring(lastSeparatorIndex + 1);
				toBeHashedFileNames.add(fileNameWithoutPath);
				List<File> filesWithThisName = filenameToFileMap.get(fileNameWithoutPath);
				if (filesWithThisName == null) {
					filesWithThisName = new ArrayList<>();
				}
				filesWithThisName.add(new File(fileName));
				filenameToFileMap.put(fileNameWithoutPath, filesWithThisName);
			}
		}
		ExecutorService wordHasherExecutor = Executors.newCachedThreadPool();
		wordhasherFuture = wordHasherExecutor.submit(new Callable<WordHasher>() {

			@Override
			public WordHasher call() throws Exception {
				return new WordHasher(toBeHashedFileNames);
			}
		});
		this.parsePerformance = System.currentTimeMillis() - start;
	}

	public long getParsePerformance() {
		return parsePerformance;
	}

	public List<File> getFileByWildCard(String pattern) {
		if (pattern == null || pattern.length() == 0)
			return Collections.emptyList();
		List<File> retval = new ArrayList<>();
		if (pattern.length() > 5) {
			fillInManually(pattern, retval);
		} else if (wordHasher == null) {
			if (wordhasherFuture.isDone()) {
				try {
					wordHasher = wordhasherFuture.get();
					fillFromWH(pattern, retval);
				} catch (InterruptedException | ExecutionException e) {
					logger.fatal("Failed to obtain word hasher due to thread fault.", e);
				}
			} else {
				fillInManually(pattern, retval);
			}
		} else {
			fillFromWH(pattern, retval);
		}
		return retval;
	}

	private void fillFromWH(String pattern, List<File> retval) {
		Set<String> fileNames = wordHasher.search(pattern);
		for (String fileName : fileNames) {
			retval.addAll(filenameToFileMap.get(fileName));
		}
	}

	private void fillInManually(String pattern, List<File> retval) {
		Set<String> fileNameSet = filenameToFileMap.keySet();
		for (String filename : fileNameSet) {
			if (filename == null)
				continue;
			if (filename.toLowerCase().contains(pattern.toLowerCase())) {
				retval.addAll(filenameToFileMap.get(filename));
			}
		}
	}

	public List<File> getFileByFullFileName(String fullFileName) {
		return filenameToFileMap.get(fullFileName);
	}

	private void parseFileNamesMultiThread(final ExecutorService executor, File[] innerFiles) {
		if (innerFiles != null) {
			for (final File file : innerFiles) {
				if (file.isDirectory()) {
					executor.submit(new Runnable() {

						@Override
						public void run() {
							readFileInitRecursively(file);
						}

						private void readFileInitRecursively(File file) {
							parseFileNamesMultiThread(executor, file.listFiles());
						}
					});
				} else {
					synchronized (fileNames) {
						fileNames.add(file.getAbsolutePath());
					}
				}

			}
		}
	}

}
