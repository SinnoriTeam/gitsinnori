package kr.pe.sinnori.gui.helper.iobuilder.screen;

import kr.pe.sinnori.common.message.builder.info.MessageInfo;

public interface BuildFunctionManagerIF {
	public boolean saveIOFileSet(boolean isSelectedIO, boolean isSelectedDirection, MessageInfo messageInfo);
	//public void createAllSourceFiles();
}