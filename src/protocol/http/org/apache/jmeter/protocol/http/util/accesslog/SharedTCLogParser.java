package org.apache.jmeter.protocol.http.util.accesslog;

import java.io.IOException;

import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestCloneable;
import org.apache.jmeter.testelement.TestElement;

public class SharedTCLogParser extends TCLogParser implements TestCloneable {

	public SharedTCLogParser() {
		super();
	}

	public SharedTCLogParser(String source) {
		super(source);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		SharedTCLogParser parser = new SharedTCLogParser();
		parser.FILENAME = FILENAME;
		parser.FILTER = FILTER;
		return parser;
	}

	/* (non-Javadoc)
	 * @see org.apache.jmeter.protocol.http.util.accesslog.TCLogParser#parse(org.apache.jmeter.testelement.TestElement, int)
	 */
	public int parse(TestElement el, int parseCount) {
		FileServer fileServer = FileServer.getFileServer();
		fileServer.reserveFile(FILENAME);
		try {
			return parse(fileServer, el, parseCount);
		} catch (Exception exception) {
			log.error("Problem creating samples", exception);
		}
		return -1;// indicate that an error occured
	}

	/**
	 * The method is responsible for reading each line, and breaking out of the
	 * while loop if a set number of lines is given.
	 * 
	 * @param breader
	 */
	protected int parse(FileServer breader, TestElement el, int parseCount) {
		int actualCount = 0;
		String line = null;
		try {
			// read one line at a time using
			// BufferedReader
			line = breader.readLine(FILENAME);
			while (line != null) {
				if (line.length() > 0) {
					actualCount += this.parseLine(line, el);
				}
				// we check the count to see if we have exceeded
				// the number of lines to parse. There's no way
				// to know where to stop in the file. Therefore
				// we use break to escape the while loop when
				// we've reached the count.
				if (parseCount != -1 && actualCount >= parseCount) {
					break;
				}
				line = breader.readLine(FILENAME);
			}
			if (line == null) {
				breader.closeFile(FILENAME);
				// this.READER = new BufferedReader(new
				// FileReader(this.SOURCE));
				// parse(this.READER,el);
			}
		} catch (IOException ioe) {
			log.error("Error reading log file", ioe);
		}
		return actualCount;
	}

	public void close() {
		try {
			FileServer.getFileServer().closeFile(FILENAME);
		} catch (IOException e) {
			// do nothing
		}
	}
	
	

}
