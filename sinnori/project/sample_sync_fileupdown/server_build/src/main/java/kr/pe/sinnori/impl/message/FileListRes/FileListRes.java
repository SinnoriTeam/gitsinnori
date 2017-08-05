/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.pe.sinnori.impl.message.FileListRes;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * FileListRes 메시지
 * @author Won Jonghoon
 *
 */
public class FileListRes extends AbstractMessage {
	private String requestPathName;
	private String pathSeperator;
	private String isSuccess;
	private String resultMessage;
	private int cntOfDriver;
	public static class Driver {
		private String driverName;

		public String getDriverName() {
			return driverName;
		}

		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Driver[");
			builder.append("driverName=");
			builder.append(driverName);
			builder.append("]");
			return builder.toString();
		}
	};
	private java.util.List<Driver> driverList;
	private int cntOfChildFile;
	public static class ChildFile {
		private String fileName;
		private long fileSize;
		private byte fileType;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public long getFileSize() {
			return fileSize;
		}

		public void setFileSize(long fileSize) {
			this.fileSize = fileSize;
		}
		public byte getFileType() {
			return fileType;
		}

		public void setFileType(byte fileType) {
			this.fileType = fileType;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("ChildFile[");
			builder.append("fileName=");
			builder.append(fileName);
			builder.append(", fileSize=");
			builder.append(fileSize);
			builder.append(", fileType=");
			builder.append(fileType);
			builder.append("]");
			return builder.toString();
		}
	};
	private java.util.List<ChildFile> childFileList;

	public String getRequestPathName() {
		return requestPathName;
	}

	public void setRequestPathName(String requestPathName) {
		this.requestPathName = requestPathName;
	}
	public String getPathSeperator() {
		return pathSeperator;
	}

	public void setPathSeperator(String pathSeperator) {
		this.pathSeperator = pathSeperator;
	}
	public String getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public int getCntOfDriver() {
		return cntOfDriver;
	}

	public void setCntOfDriver(int cntOfDriver) {
		this.cntOfDriver = cntOfDriver;
	}

	public java.util.List<Driver> getDriverList() {
		return driverList;
	}

	public void setDriverList(java.util.List<Driver> driverList) {
		this.driverList = driverList;
	}
	public int getCntOfChildFile() {
		return cntOfChildFile;
	}

	public void setCntOfChildFile(int cntOfChildFile) {
		this.cntOfChildFile = cntOfChildFile;
	}

	public java.util.List<ChildFile> getChildFileList() {
		return childFileList;
	}

	public void setChildFileList(java.util.List<ChildFile> childFileList) {
		this.childFileList = childFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class FileListRes[");
		builder.append("requestPathName=");
		builder.append(requestPathName);
		builder.append(", pathSeperator=");
		builder.append(pathSeperator);
		builder.append(", isSuccess=");
		builder.append(isSuccess);
		builder.append(", resultMessage=");
		builder.append(resultMessage);
		builder.append(", cntOfDriver=");
		builder.append(cntOfDriver);
		builder.append(", driverList=");
		if (null == driverList) {
			builder.append("null");
		} else {
			int driverListSize = driverList.size();
			if (0 == driverListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < driverListSize; i++) {
					Driver driver = driverList.get(i);
					if (0 == i) {
						builder.append("driver[");
					} else {
						builder.append(", driver[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(driver.toString());
				}
				builder.append("]");
			}
		}

		builder.append(", cntOfChildFile=");
		builder.append(cntOfChildFile);
		builder.append(", childFileList=");
		if (null == childFileList) {
			builder.append("null");
		} else {
			int childFileListSize = childFileList.size();
			if (0 == childFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < childFileListSize; i++) {
					ChildFile childFile = childFileList.get(i);
					if (0 == i) {
						builder.append("childFile[");
					} else {
						builder.append(", childFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(childFile.toString());
				}
				builder.append("]");
			}
		}

		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}