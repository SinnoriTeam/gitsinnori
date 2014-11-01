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
package kr.pe.sinnori.impl.message.BoardDetailOutDTO;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * BoardDetailOutDTO 메시지
 * @author Won Jonghoon
 *
 */
public class BoardDetailOutDTO extends AbstractMessage {
	private long boardNo;
	private long groupNo;
	private int groupSeq;
	private long parentNo;
	private short depth;
	private String subject;
	private String content;
	private String writerId;
	private String nickname;
	private int viewCount;
	private int votes;
	private String deleteFlag;
	private String ip;
	private java.sql.Timestamp registerDate;
	private java.sql.Timestamp modifiedDate;
	private String memberGubunName;
	private byte memberState;

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
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public String getWriterId() {
		return writerId;
	}

	public void setWriterId(String writerId) {
		this.writerId = writerId;
	}
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
	public String getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public java.sql.Timestamp getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(java.sql.Timestamp registerDate) {
		this.registerDate = registerDate;
	}
	public java.sql.Timestamp getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(java.sql.Timestamp modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getMemberGubunName() {
		return memberGubunName;
	}

	public void setMemberGubunName(String memberGubunName) {
		this.memberGubunName = memberGubunName;
	}
	public byte getMemberState() {
		return memberState;
	}

	public void setMemberState(byte memberState) {
		this.memberState = memberState;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class BoardDetailOutDTO[");
		builder.append("boardNo=");
		builder.append(boardNo);
		builder.append(", groupNo=");
		builder.append(groupNo);
		builder.append(", groupSeq=");
		builder.append(groupSeq);
		builder.append(", parentNo=");
		builder.append(parentNo);
		builder.append(", depth=");
		builder.append(depth);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", writerId=");
		builder.append(writerId);
		builder.append(", nickname=");
		builder.append(nickname);
		builder.append(", viewCount=");
		builder.append(viewCount);
		builder.append(", votes=");
		builder.append(votes);
		builder.append(", deleteFlag=");
		builder.append(deleteFlag);
		builder.append(", ip=");
		builder.append(ip);
		builder.append(", registerDate=");
		builder.append(registerDate);
		builder.append(", modifiedDate=");
		builder.append(modifiedDate);
		builder.append(", memberGubunName=");
		builder.append(memberGubunName);
		builder.append(", memberState=");
		builder.append(memberState);
		builder.append(", messageHeaderInfo=");
		builder.append(messageHeaderInfo.toString());
		builder.append("]");
		return builder.toString();
	}
}