package kr.pe.sinnori.source_file_builder;

import java.util.ArrayList;

import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.message.AbstractItemInfo;
import kr.pe.sinnori.message.ArrayInfo;
import kr.pe.sinnori.message.ItemGroupInfoIF;
import kr.pe.sinnori.message.SingleItemInfo;


public class DecoderSourceFileBuilder extends AbstractSourceFileBuildre {

	private String getCountVarName(int depth) {
		StringBuilder countVarNameBuilder = new StringBuilder();
		for (int i=0; i <= depth; i++) {
			countVarNameBuilder.append("i");
		}
		return countVarNameBuilder.toString();
	}
	
	public String toBody(int depth, String path, String varName, ItemGroupInfoIF arrayInfo, String middleObjVarName) {
		StringBuilder stringBuilder = new StringBuilder();
		
		ArrayList<AbstractItemInfo> itemInfoList = arrayInfo.getItemInfoList();
		for (AbstractItemInfo itemInfo:itemInfoList) {
			if (itemInfo.getLogicalItemGubun() == CommonType.LOGICAL_ITEM_GUBUN.SINGLE_ITEM) {
				SingleItemInfo singleItemInfo = (SingleItemInfo)itemInfo;
				
				// allDataType.setByteVar1((Byte)
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t");
				stringBuilder.append(varName);
				stringBuilder.append(".set");
				stringBuilder.append(singleItemInfo.getFirstUpperItemName());
				stringBuilder.append("((");
				stringBuilder.append(singleItemInfo.getItemTypeForJavaLangClassCasting());
				stringBuilder.append(")");
				
				// singleItemDecoder.getValueFromMiddleReadObj(sigleItemPath0
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tsingleItemDecoder.getValueFromMiddleReadObj(sigleItemPath");
				stringBuilder.append(depth);
				
				// , "byteVar1" // itemName
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, \"");
				stringBuilder.append(singleItemInfo.getItemName());
				stringBuilder.append("\" // itemName");
				
				// , 0 // itemType
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append(singleItemInfo.getItemTypeID());
				stringBuilder.append(" // itemTypeID");
				
				// itemTypeName
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, \"");
				stringBuilder.append(singleItemInfo.getItemType());
				stringBuilder.append("\" // itemTypeName");
				
				// itemSizeForLang
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append(singleItemInfo.getItemSizeForLang());
				stringBuilder.append(" // itemSizeForLang");
				
				// itemCharsetForLang
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				/**
				 * <pre>
				 * 타입이 고정 문자열 크기일 경우만 문자셋을 지정할 수 있다.
				 * 문자셋 이름이 null 값으로 넘어가면 
				 * 프로토콜 단일 항목 인코더에서는 파라미터로 넘어가는 프로젝트 문자셋으로 지정되며,
				 * 문자셋 이름이 null 이 아니면 
				 * 프로토콜 단일 항목 인코더에서는 문자셋 객체로 변환된다.
				 * 따라서 메시지 정보를 꾸릴때 문자셋 이름이 있을 경우 반듯이 문자셋 객체로 바꿀수 있는 바른 이름인지 검사를 수행해야 한다.  
				 * </pre>
				 */
				
				if (singleItemInfo.getItemType().equals("fixed length string")) {
					String itemCharset = singleItemInfo.getItemCharset();
					if (null == itemCharset) {
						stringBuilder.append("null");
					} else {
						stringBuilder.append("\"");
						stringBuilder.append(itemCharset);
						stringBuilder.append("\"");
					}
				} else {
					stringBuilder.append("null");
				}
				stringBuilder.append(" // itemCharset,");
				
				// charsetOfProject
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, charsetOfProject");
				
				// , middleReadObj));
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t, ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append("));");
				
			} else {
				ArrayInfo arrayInfoOfChild = (ArrayInfo) itemInfo;
				
				// Object memberMiddlerReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath0, "Member", allDataType.getCnt(), middleReadObj);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadArray = singleItemDecoder.getArrayObjFromMiddleReadObj(sigleItemPath");
				stringBuilder.append(depth);
				stringBuilder.append(", \"");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("\", ");
				/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()");
				} else {
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
				}
				stringBuilder.append(", ");
				stringBuilder.append(middleObjVarName);
				stringBuilder.append(");");
				
				// AllDataType.Member[] memberList = new AllDataType.Member[allDataType.getCnt()];
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("[] ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List = new ");
				stringBuilder.append(path);
				stringBuilder.append(".");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("[");
				/** 배열 크기 지정 방식에 따른 배열 크기 지정 */
				if (arrayInfoOfChild.getArrayCntType().equals("reference")) {
					stringBuilder.append(varName);
					stringBuilder.append(".get");
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(0, 1).toUpperCase());
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue().substring(1));
					stringBuilder.append("()");
				} else {
					stringBuilder.append(arrayInfoOfChild.getArrayCntValue());
				}
				stringBuilder.append("];");
				
				// for (int i=0; i < memberList.length; i++) {
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\tfor (int ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("=0; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(" < ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List.length; ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("++) {");
				
				// String sigleItemPath1 = new StringBuilder(sigleItemPath0).append(".").append("Member[").append(i).append("]").toString();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tString sigleItemPath");
				stringBuilder.append(depth+1);
				stringBuilder.append(" = new StringBuilder(sigleItemPath");
				stringBuilder.append(depth);
				stringBuilder.append(").append(\".\").append(\"");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("[\").append(");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(").append(\"]\").toString();");
				
				// Object memberMiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath1, memberMiddleReadArray, i);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\tObject ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadObj = singleItemDecoder.getMiddleReadObjFromArrayObj(sigleItemPath");
				stringBuilder.append(depth+1);
				stringBuilder.append(", ");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("MiddleReadArray, ");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append(");");
				
				// memberList[i] = allDataType.new Member();
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t\t");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List[");
				stringBuilder.append(getCountVarName(depth));
				stringBuilder.append("] = ");
				stringBuilder.append(varName);
				stringBuilder.append(". new ");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("();");
				
				stringBuilder.append(toBody(depth+1, path+"."+arrayInfoOfChild.getFirstUpperItemName(), 
						arrayInfoOfChild.getItemName()+"List["+getCountVarName(depth)+"]", 
						arrayInfoOfChild, arrayInfoOfChild.getItemName()+ "MiddleReadObj"));
				
				// allDataType.setMemberList(memberList);
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append(varName);
				stringBuilder.append(".set");
				stringBuilder.append(arrayInfoOfChild.getFirstUpperItemName());
				stringBuilder.append("List(");
				stringBuilder.append(arrayInfoOfChild.getItemName());
				stringBuilder.append("List);");				
				
				// FIXME!
				// }
				stringBuilder.append(CommonStaticFinalVars.NEWLINE);
				for (int i=0; i < depth; i++) {
					stringBuilder.append("\t");
				}
				stringBuilder.append("\t\t}");
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String toString(String messageID,
			String author,
			kr.pe.sinnori.message.MessageInfo messageInfo) {
		String firstLowerMessageID =  messageID.substring(0, 1).toLowerCase() + messageID.substring(1);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append(getLincenseString());
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("package ");
		stringBuilder.append(dynamicClassBasePackageName);
		stringBuilder.append(messageID);
		stringBuilder.append(";");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		// import java.nio.charset.Charset;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("import java.nio.charset.Charset;");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("import kr.pe.sinnori.common.exception.BodyFormatException;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("import kr.pe.sinnori.common.message.AbstractMessage;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("import kr.pe.sinnori.common.message.codec.MessageDecoder;");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("import kr.pe.sinnori.common.protocol.SingleItemDecoderIF;");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지 디코더");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" * @author ");
		stringBuilder.append(author);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" *");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(" */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("public final class ");
		stringBuilder.append(messageID);
		stringBuilder.append("Decoder extends MessageDecoder {");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t/**");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * <pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * ");
		// "단일항목 디코더"를 이용하여 "중간 다리 역활 읽기 객체" 에서 추출된 Echo 메시지를 반환한다.
		stringBuilder.append(" \"단일항목 디코더\"를 이용하여 \"중간 다리 역활 읽기 객체\" 에서 추출된 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지를 반환한다.");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * </pre>");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param singleItemDecoder 단일항목 디코더");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param charsetOfProject 프로젝트 문자셋");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @param middleReadObj 중간 다리 역활 읽기 객체");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @return \"단일항목 디코더\"를 이용하여 \"중간 다리 역활 읽기 객체\" 에서 추출된 ");
		stringBuilder.append(messageID);
		stringBuilder.append(" 메시지");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @throws OutOfMemoryError 메모리 확보 실패시 던지는 예외");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t * @throws BodyFormatException 바디 디코딩 실패시 던지는 예외");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t */");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t@Override");
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\tprotected AbstractMessage decodeBody(SingleItemDecoderIF singleItemDecoder, Charset charsetOfProject, Object  middleReadObj) throws OutOfMemoryError, BodyFormatException {");
		
		// AllDataType allDataType = new AllDataType();
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\t");
		stringBuilder.append(messageID);
		stringBuilder.append(" ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(" = new ");
		stringBuilder.append(messageID);
		stringBuilder.append("();");
		
		if (messageInfo.getItemInfoList().size() > 0) {
			// String sigleItemPath0 = "AllDataType";
			stringBuilder.append(CommonStaticFinalVars.NEWLINE);
			stringBuilder.append("\t\tString sigleItemPath0 = \"");
			stringBuilder.append(messageID);
			stringBuilder.append("\";");
			
			stringBuilder.append(toBody(0, messageID, firstLowerMessageID, messageInfo, "middleReadObj"));
		}
		
		
		// return allDataType;
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t\treturn ");
		stringBuilder.append(firstLowerMessageID);
		stringBuilder.append(";");
		
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("\t}");
		
		stringBuilder.append(CommonStaticFinalVars.NEWLINE);
		stringBuilder.append("}");
		
		return stringBuilder.toString();
	}

}