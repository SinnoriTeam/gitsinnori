package kr.pe.sinnori.screen;

public interface SourceManagerIF {
	public boolean createSourceFile(boolean isSelectedIO, boolean isSelectedDirection, kr.pe.sinnori.gui.message.builder.info.MessageInfo messageInfo);
	public void createAllSourceFiles();
}
