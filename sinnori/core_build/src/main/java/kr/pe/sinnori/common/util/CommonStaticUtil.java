package kr.pe.sinnori.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.CommonType.READ_WRITE_MODE;


public abstract class CommonStaticUtil {	
	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다. 주의점) 주어진 문자열이 빈 문자열일 경우 true 를 던진다.
	 * @param value 앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부
	 * @throws IllegalArgumentException null 주어진 문자열이 null 인 경우 던진다.
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value) throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}
		
		
		String trimValue = value.trim();		
		boolean returnValue = !trimValue.equals(value);
		
				
		return returnValue;
	}
	
	public static String getFilePathStringFromResourcePathAndRelativePathOfFile(String resourcesPathString, String relativePath) {
		if (null == resourcesPathString) {
			throw new IllegalArgumentException("the paramter resourcesPathString is null");
		}
		
		if (null == relativePath) {
			throw new IllegalArgumentException("the paramter relativePath is null");
		}
		
		String realResourceFilePathString = null;
		
		String headSeparator = null;
		if (relativePath.indexOf("/") == 0) headSeparator = "";
		else headSeparator = File.separator;
		
		String subRealPathString = null;		
		if (File.separator.equals("/")) {
			subRealPathString = relativePath;
		} else {
			subRealPathString = relativePath.replaceAll("/", "\\\\");
		}		
		
		realResourceFilePathString = new StringBuilder(resourcesPathString)
		.append(headSeparator).append(subRealPathString).toString();
		
		return realResourceFilePathString;
	}
	
	/**
	 * 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열을 반환한다.
	 * @param sourceString 변환을 원하는 문자열
	 * @param lineSeparatorGubun 지정한 칼럼 마다 삽입을 원하는 문자열 구분, BR: <br/>, NEWLINE: newline
	 * @param wantedColumnSize 원하는 문자열 가로 칼럼수
	 * @return 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열
	 */
	public static String splitString(String sourceString, CommonType.LINE_SEPARATOR_GUBUN lineSeparatorGubun, int wantedColumnSize) {
		if (null == sourceString) {
			throw new IllegalArgumentException("the paramter sourceString is null");
		}
		
		if (sourceString.equals("")) {
			throw new IllegalArgumentException("the paramter sourceString is a empty string");
		}
		
		if (hasLeadingOrTailingWhiteSpace(sourceString)) {
			throw new IllegalArgumentException("the paramter sourceString has leading or tailing white space");
		}
		
		if (null == lineSeparatorGubun) {
			throw new IllegalArgumentException("the paramter lineSeparatorGubun is null");
		}		
		
		if (wantedColumnSize <= 0) {
			throw new IllegalArgumentException("the paramter wantedColumnSize is less or equals to zero");
		}		
		
		String lineSeparator = null;
		if (lineSeparatorGubun == CommonType.LINE_SEPARATOR_GUBUN.BR) {
			lineSeparator ="<br/>";
		} else {
			lineSeparator = CommonStaticFinalVars.NEWLINE;
		}
		
		int size = sourceString.length();
		StringBuilder resultStringBuilder = new StringBuilder();
		int i=0;
		for (; i+wantedColumnSize < size; i+=wantedColumnSize) {
			resultStringBuilder.append(sourceString.substring(i, i+wantedColumnSize));
			resultStringBuilder.append(lineSeparator);
		}
		resultStringBuilder.append(sourceString.substring(i));
		return resultStringBuilder.toString();
	}
	
	public static String getMultiLineToolTip(String message, int colSize) {
		if (null == message) {
			throw new IllegalArgumentException("the parameter 'message' is null");
		}
		
		String tooltip = new StringBuilder("<html>")
		.append(CommonStaticUtil.splitString(message, CommonType.LINE_SEPARATOR_GUBUN.BR, colSize))
		.append("</html>").toString();
		return tooltip;
	}
	
	public static void copyTransferToFile(File sourceFile, File targetFile) throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException("the parameter 'sourceFile' is null");
		}
		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		
		
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);

			FileChannel souceFileChannel = fis.getChannel();
			FileChannel targetFileChannel = fos.getChannel();

			souceFileChannel.transferTo(0, souceFileChannel.size(), targetFileChannel);
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (Exception e) {
				// log.warn("fail to close source file[" + sourceFile.getAbsolutePath() + "] input stream", e);
			}
			try {
				if (null != fos)
					fos.close();
			} catch (Exception e) {
				Logger log = LoggerFactory.getLogger(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}
	
	public static File getValidPath(String sourcePathString, READ_WRITE_MODE	readWriteMode) throws RuntimeException {
		if (null == sourcePathString) {
			throw new IllegalArgumentException("the parameter 'sourcePathString' is null");
		}
		
		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s] doesn't exist",  sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s] is not a directory", 
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (readWriteMode.equals(READ_WRITE_MODE.ONLY_READ) || readWriteMode.equals(READ_WRITE_MODE.READ_WRITE)) {
			if (!sourcePath.canRead()) {
				String errorMessage = String.format("The path[%s] has a permission to read", 
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		
		
		if (readWriteMode.equals(READ_WRITE_MODE.ONLY_WRITE) || readWriteMode.equals(READ_WRITE_MODE.READ_WRITE)) {
			if (!sourcePath.canWrite()) {
				String errorMessage = String.format("The path[%s] has a permission to write", 
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		return sourcePath;
	}
	
	
	public static void saveFileWithOverwriting( File targetFile, String contents, Charset targetCharset) throws IOException {		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}
		
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		

		if (!targetFile.canWrite()) {
			String errorMessage = String.format("the file[%s] can not be written", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream", fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				Logger log = LoggerFactory.getLogger(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}
}
