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

package kr.pe.codda.impl.message.BoardDetailRes;

import kr.pe.codda.common.message.AbstractMessage;

/**
 * BoardDetailRes message
 * @author Won Jonghoon
 *
 */
public class BoardDetailRes extends AbstractMessage {
	private short boardID;
	private long boardNo;
	private long groupNo;
	private int groupSeq;
	private long parentNo;
	private short depth;
	private int viewCount;
	private String boardSate;
	private String nickname;
	private int votes;
	private String subject;
	private String contents;
	private String writerID;
	private String writerIP;
	private java.sql.Timestamp registeredDate;
	private String lastModifierIP;
	private String lastModifierID;
	private String lastModifierNickName;
	private java.sql.Timestamp lastModifiedDate;
	private short nextAttachedFileSeq;
	private int attachedFileCnt;

	public static class AttachedFile {
		private short attachedFileSeq;
		private String attachedFileName;
		private long attachedFileSize;

		public short getAttachedFileSeq() {
			return attachedFileSeq;
		}

		public void setAttachedFileSeq(short attachedFileSeq) {
			this.attachedFileSeq = attachedFileSeq;
		}
		public String getAttachedFileName() {
			return attachedFileName;
		}

		public void setAttachedFileName(String attachedFileName) {
			this.attachedFileName = attachedFileName;
		}
		public long getAttachedFileSize() {
			return attachedFileSize;
		}

		public void setAttachedFileSize(long attachedFileSize) {
			this.attachedFileSize = attachedFileSize;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("AttachedFile[");
			builder.append("attachedFileSeq=");
			builder.append(attachedFileSeq);
			builder.append(", attachedFileName=");
			builder.append(attachedFileName);
			builder.append(", attachedFileSize=");
			builder.append(attachedFileSize);
			builder.append("]");
			return builder.toString();
		}
	}

	private java.util.List<AttachedFile> attachedFileList;

	public short getBoardID() {
		return boardID;
	}

	public void setBoardID(short boardID) {
		this.boardID = boardID;
	}
	public long getBoardNo() {
		return boardNo;
	}

	public void setBoardNo(long boardNo) {
		this.boardNo = boardNo;
	}
	public long getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(long groupNo) {
		this.groupNo = groupNo;
	}
	public int getGroupSeq() {
		return groupSeq;
	}

	public void setGroupSeq(int groupSeq) {
		this.groupSeq = groupSeq;
	}
	public long getParentNo() {
		return parentNo;
	}

	public void setParentNo(long parentNo) {
		this.parentNo = parentNo;
	}
	public short getDepth() {
		return depth;
	}

	public void setDepth(short depth) {
		this.depth = depth;
	}
	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public String getBoardSate() {
		return boardSate;
	}

	public void setBoardSate(String boardSate) {
		this.boardSate = boardSate;
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getWriterID() {
		return writerID;
	}

	public void setWriterID(String writerID) {
		this.writerID = writerID;
	}
	public String getWriterIP() {
		return writerIP;
	}

	public void setWriterIP(String writerIP) {
		this.writerIP = writerIP;
	}
	public java.sql.Timestamp getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(java.sql.Timestamp registeredDate) {
		this.registeredDate = registeredDate;
	}
	public String getLastModifierIP() {
		return lastModifierIP;
	}

	public void setLastModifierIP(String lastModifierIP) {
		this.lastModifierIP = lastModifierIP;
	}
	public String getLastModifierID() {
		return lastModifierID;
	}

	public void setLastModifierID(String lastModifierID) {
		this.lastModifierID = lastModifierID;
	}
	public String getLastModifierNickName() {
		return lastModifierNickName;
	}

	public void setLastModifierNickName(String lastModifierNickName) {
		this.lastModifierNickName = lastModifierNickName;
	}
	public java.sql.Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(java.sql.Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public short getNextAttachedFileSeq() {
		return nextAttachedFileSeq;
	}

	public void setNextAttachedFileSeq(short nextAttachedFileSeq) {
		this.nextAttachedFileSeq = nextAttachedFileSeq;
	}
	public int getAttachedFileCnt() {
		return attachedFileCnt;
	}

	public void setAttachedFileCnt(int attachedFileCnt) {
		this.attachedFileCnt = attachedFileCnt;
	}
	public java.util.List<AttachedFile> getAttachedFileList() {
		return attachedFileList;
	}

	public void setAttachedFileList(java.util.List<AttachedFile> attachedFileList) {
		this.attachedFileList = attachedFileList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("boardDetailRes[");
		builder.append("boardID=");
		builder.append(boardID);
		builder.append(", boardNo=");
		builder.append(boardNo);
		builder.append(", groupNo=");
		builder.append(groupNo);
		builder.append(", groupSeq=");
		builder.append(groupSeq);
		builder.append(", parentNo=");
		builder.append(parentNo);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", viewCount=");
		builder.append(viewCount);
		builder.append(", boardSate=");
		builder.append(boardSate);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append(", votes=");
		builder.append(votes);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", contents=");
		builder.append(contents);
		builder.append(", writerID=");
		builder.append(writerID);
		builder.append(", writerIP=");
		builder.append(writerIP);
		builder.append(", registeredDate=");
		builder.append(registeredDate);
		builder.append(", lastModifierIP=");
		builder.append(lastModifierIP);
		builder.append(", lastModifierID=");
		builder.append(lastModifierID);
		builder.append(", lastModifierNickName=");
		builder.append(lastModifierNickName);
		builder.append(", lastModifiedDate=");
		builder.append(lastModifiedDate);
		builder.append(", nextAttachedFileSeq=");
		builder.append(nextAttachedFileSeq);
		builder.append(", attachedFileCnt=");
		builder.append(attachedFileCnt);

		builder.append(", attachedFileList=");
		if (null == attachedFileList) {
			builder.append("null");
		} else {
			int attachedFileListSize = attachedFileList.size();
			if (0 == attachedFileListSize) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < attachedFileListSize; i++) {
					AttachedFile attachedFile = attachedFileList.get(i);
					if (0 == i) {
						builder.append("attachedFile[");
					} else {
						builder.append(", attachedFile[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(attachedFile.toString());
				}
				builder.append("]");
			}
		}
		builder.append("]");
		return builder.toString();
	}
}