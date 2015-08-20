package searcher.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gokhanozg.wordhasher.WordHasher;

import searcher.exception.ExceptionFactory;
import searcher.exception.NotAFolderException;
import searcher.exception.SearcherTimeoutExceededException;

public class FileNameParser {

	private final File rootFolderToParse;
	private final int PARSE_THREADS = 8;
	private final List<String> fileNames = new ArrayList<>();
	private long parsePerformance = 0l;
	private final long PARSE_TIMEOUT = 1000000l;
	private final Logger logger = Logger.getLogger(getClass());
	private final Map<String, List<File>> filenameToFileMap = new HashMap<>();
	private Future<WordHasher> wordhasherFuture;

	public FileNameParser(File rootFolderToParse) throws NotAFolderException {
		this.rootFolderToParse = rootFolderToParse;
		if (!rootFolderToParse.isDirectory())
			throw ExceptionFactory.createNotAFolderException("Chosen root file is not a folder.");
	}

	public void parseFileNames() throws SearcherTimeoutExceededException {
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newFixedThreadPool(PARSE_THREADS);
		parseFileNamesMultiThread(executor, rootFolderToParse.listFiles());
		executor.shutdown();
		try {
			executor.awaitTermination(PARSE_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Error while waiting for parsing", e);
			throw ExceptionFactory.createSearcherTimeoutExceededException("Parsing file timeout has been reached.");
		}
		final List<String> toBeHashedFileNames = new ArrayList<>();
		for (String fileName : fileNames) {
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
		while (!wordhasherFuture.isDone())
			;
		return null;

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
