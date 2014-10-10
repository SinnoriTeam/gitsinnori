package kr.pe.sinnori.gui.lib;

public interface MessageInfoManagerIF {
	public void readAllMessageInfo();
	
	public void retry(int row, kr.pe.sinnori.common.message.MessageInfo messageInfo);
	
	public void readMessageInfoWithSearchKeyword();
}
