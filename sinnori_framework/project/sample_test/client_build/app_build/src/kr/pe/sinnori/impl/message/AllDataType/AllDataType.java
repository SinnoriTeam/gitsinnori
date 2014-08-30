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
package kr.pe.sinnori.impl.message.AllDataType;

import kr.pe.sinnori.common.message.AbstractMessage;
/**
 * AllDataType 메시지
 * @author Jonghoon won
 *
 */
public final class AllDataType extends AbstractMessage {
	private byte byteVar1;
	private byte byteVar2;
	private byte byteVar3;
	private short unsignedByteVar1;
	private short unsignedByteVar2;
	private short unsignedByteVar3;
	private short shortVar1;
	private short shortVar2;
	private short shortVar3;
	private int unsignedShortVar1;
	private int unsignedShortVar2;
	private int unsignedShortVar3;
	private int intVar1;
	private int intVar2;
	private int intVar3;
	private long unsignedIntVar1;
	private long unsignedIntVar2;
	private long unsignedIntVar3;
	private long longVar1;
	private long longVar2;
	private long longVar3;
	private String strVar1;
	private String strVar2;
	private String strVar3;
	private byte[] bytesVar1;
	private byte[] bytesVar2;
	private int cnt;
	public class Member {
		private String memberID;
		private String memberName;
		private int cnt;
		public class Item {
			private String itemID;
			private String itemName;
			private int itemCnt;

			public String getItemID() {
				return itemID;
			}

			public void setItemID(String itemID) {
				this.itemID = itemID;
			}
			public String getItemName() {
				return itemName;
			}

			public void setItemName(String itemName) {
				this.itemName = itemName;
			}
			public int getItemCnt() {
				return itemCnt;
			}

			public void setItemCnt(int itemCnt) {
				this.itemCnt = itemCnt;
			}

			@Override
			public String toString() {
				StringBuilder builder = new StringBuilder();
				builder.append("Item[");
				builder.append("itemID=");
				builder.append(itemID);
				builder.append(", itemName=");
				builder.append(itemName);
				builder.append(", itemCnt=");
				builder.append(itemCnt);
				builder.append("]");
				return builder.toString();
			}
		};
		private Item[] itemList;

		public String getMemberID() {
			return memberID;
		}

		public void setMemberID(String memberID) {
			this.memberID = memberID;
		}
		public String getMemberName() {
			return memberName;
		}

		public void setMemberName(String memberName) {
			this.memberName = memberName;
		}
		public int getCnt() {
			return cnt;
		}

		public void setCnt(int cnt) {
			this.cnt = cnt;
		}

		public Item[] getItemList() {
			return itemList;
		}

		public void setItemList(Item[] itemList) {
			this.itemList = itemList;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Member[");
			builder.append("memberID=");
			builder.append(memberID);
			builder.append(", memberName=");
			builder.append(memberName);
			builder.append(", cnt=");
			builder.append(cnt);
			builder.append(", itemList=");
			if (null == itemList) {
				builder.append("null");
			} else {
				if (0 == itemList.length) {
					builder.append("empty");
				} else {
					builder.append("[");
					for (int i=0; i < itemList.length; i++) {
						Item item = itemList[i];
						if (0 == i) {
							builder.append("item[");
						} else {
							builder.append(", item[");
						}
						builder.append(i);
						builder.append("]=");
						builder.append(item.toString());
					}
					builder.append("]");
				}
			}
	
			builder.append("]");
			return builder.toString();
		}
	};
	private Member[] memberList;

	public byte getByteVar1() {
		return byteVar1;
	}

	public void setByteVar1(byte byteVar1) {
		this.byteVar1 = byteVar1;
	}
	public byte getByteVar2() {
		return byteVar2;
	}

	public void setByteVar2(byte byteVar2) {
		this.byteVar2 = byteVar2;
	}
	public byte getByteVar3() {
		return byteVar3;
	}

	public void setByteVar3(byte byteVar3) {
		this.byteVar3 = byteVar3;
	}
	public short getUnsignedByteVar1() {
		return unsignedByteVar1;
	}

	public void setUnsignedByteVar1(short unsignedByteVar1) {
		this.unsignedByteVar1 = unsignedByteVar1;
	}
	public short getUnsignedByteVar2() {
		return unsignedByteVar2;
	}

	public void setUnsignedByteVar2(short unsignedByteVar2) {
		this.unsignedByteVar2 = unsignedByteVar2;
	}
	public short getUnsignedByteVar3() {
		return unsignedByteVar3;
	}

	public void setUnsignedByteVar3(short unsignedByteVar3) {
		this.unsignedByteVar3 = unsignedByteVar3;
	}
	public short getShortVar1() {
		return shortVar1;
	}

	public void setShortVar1(short shortVar1) {
		this.shortVar1 = shortVar1;
	}
	public short getShortVar2() {
		return shortVar2;
	}

	public void setShortVar2(short shortVar2) {
		this.shortVar2 = shortVar2;
	}
	public short getShortVar3() {
		return shortVar3;
	}

	public void setShortVar3(short shortVar3) {
		this.shortVar3 = shortVar3;
	}
	public int getUnsignedShortVar1() {
		return unsignedShortVar1;
	}

	public void setUnsignedShortVar1(int unsignedShortVar1) {
		this.unsignedShortVar1 = unsignedShortVar1;
	}
	public int getUnsignedShortVar2() {
		return unsignedShortVar2;
	}

	public void setUnsignedShortVar2(int unsignedShortVar2) {
		this.unsignedShortVar2 = unsignedShortVar2;
	}
	public int getUnsignedShortVar3() {
		return unsignedShortVar3;
	}

	public void setUnsignedShortVar3(int unsignedShortVar3) {
		this.unsignedShortVar3 = unsignedShortVar3;
	}
	public int getIntVar1() {
		return intVar1;
	}

	public void setIntVar1(int intVar1) {
		this.intVar1 = intVar1;
	}
	public int getIntVar2() {
		return intVar2;
	}

	public void setIntVar2(int intVar2) {
		this.intVar2 = intVar2;
	}
	public int getIntVar3() {
		return intVar3;
	}

	public void setIntVar3(int intVar3) {
		this.intVar3 = intVar3;
	}
	public long getUnsignedIntVar1() {
		return unsignedIntVar1;
	}

	public void setUnsignedIntVar1(long unsignedIntVar1) {
		this.unsignedIntVar1 = unsignedIntVar1;
	}
	public long getUnsignedIntVar2() {
		return unsignedIntVar2;
	}

	public void setUnsignedIntVar2(long unsignedIntVar2) {
		this.unsignedIntVar2 = unsignedIntVar2;
	}
	public long getUnsignedIntVar3() {
		return unsignedIntVar3;
	}

	public void setUnsignedIntVar3(long unsignedIntVar3) {
		this.unsignedIntVar3 = unsignedIntVar3;
	}
	public long getLongVar1() {
		return longVar1;
	}

	public void setLongVar1(long longVar1) {
		this.longVar1 = longVar1;
	}
	public long getLongVar2() {
		return longVar2;
	}

	public void setLongVar2(long longVar2) {
		this.longVar2 = longVar2;
	}
	public long getLongVar3() {
		return longVar3;
	}

	public void setLongVar3(long longVar3) {
		this.longVar3 = longVar3;
	}
	public String getStrVar1() {
		return strVar1;
	}

	public void setStrVar1(String strVar1) {
		this.strVar1 = strVar1;
	}
	public String getStrVar2() {
		return strVar2;
	}

	public void setStrVar2(String strVar2) {
		this.strVar2 = strVar2;
	}
	public String getStrVar3() {
		return strVar3;
	}

	public void setStrVar3(String strVar3) {
		this.strVar3 = strVar3;
	}
	public byte[] getBytesVar1() {
		return bytesVar1;
	}

	public void setBytesVar1(byte[] bytesVar1) {
		this.bytesVar1 = bytesVar1;
	}
	public byte[] getBytesVar2() {
		return bytesVar2;
	}

	public void setBytesVar2(byte[] bytesVar2) {
		this.bytesVar2 = bytesVar2;
	}
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public Member[] getMemberList() {
		return memberList;
	}

	public void setMemberList(Member[] memberList) {
		this.memberList = memberList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("class AllDataType[");
		builder.append("byteVar1=");
		builder.append(byteVar1);
		builder.append(", byteVar2=");
		builder.append(byteVar2);
		builder.append(", byteVar3=");
		builder.append(byteVar3);
		builder.append(", unsignedByteVar1=");
		builder.append(unsignedByteVar1);
		builder.append(", unsignedByteVar2=");
		builder.append(unsignedByteVar2);
		builder.append(", unsignedByteVar3=");
		builder.append(unsignedByteVar3);
		builder.append(", shortVar1=");
		builder.append(shortVar1);
		builder.append(", shortVar2=");
		builder.append(shortVar2);
		builder.append(", shortVar3=");
		builder.append(shortVar3);
		builder.append(", unsignedShortVar1=");
		builder.append(unsignedShortVar1);
		builder.append(", unsignedShortVar2=");
		builder.append(unsignedShortVar2);
		builder.append(", unsignedShortVar3=");
		builder.append(unsignedShortVar3);
		builder.append(", intVar1=");
		builder.append(intVar1);
		builder.append(", intVar2=");
		builder.append(intVar2);
		builder.append(", intVar3=");
		builder.append(intVar3);
		builder.append(", unsignedIntVar1=");
		builder.append(unsignedIntVar1);
		builder.append(", unsignedIntVar2=");
		builder.append(unsignedIntVar2);
		builder.append(", unsignedIntVar3=");
		builder.append(unsignedIntVar3);
		builder.append(", longVar1=");
		builder.append(longVar1);
		builder.append(", longVar2=");
		builder.append(longVar2);
		builder.append(", longVar3=");
		builder.append(longVar3);
		builder.append(", strVar1=");
		builder.append(strVar1);
		builder.append(", strVar2=");
		builder.append(strVar2);
		builder.append(", strVar3=");
		builder.append(strVar3);
		builder.append(", bytesVar1=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(bytesVar1, 0, Math.min(bytesVar1.length, 7)));
		builder.append(", bytesVar2=");
		builder.append(kr.pe.sinnori.common.util.HexUtil.getHexStringFromByteArray(bytesVar2, 0, Math.min(bytesVar2.length, 7)));
		builder.append(", cnt=");
		builder.append(cnt);
		builder.append(", memberList=");
		if (null == memberList) {
			builder.append("null");
		} else {
			if (0 == memberList.length) {
				builder.append("empty");
			} else {
				builder.append("[");
				for (int i=0; i < memberList.length; i++) {
					Member member = memberList[i];
					if (0 == i) {
						builder.append("member[");
					} else {
						builder.append(", member[");
					}
					builder.append(i);
					builder.append("]=");
					builder.append(member.toString());
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