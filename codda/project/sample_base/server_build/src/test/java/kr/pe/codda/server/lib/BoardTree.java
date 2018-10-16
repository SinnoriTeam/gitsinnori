package kr.pe.codda.server.lib;

import java.util.ArrayList;
import java.util.List;

public class BoardTree {
	private List<BoardTreeNode> rootBoardTreeNodeList = new ArrayList<BoardTreeNode>();

	public List<BoardTreeNode> getRootBoardTreeNodeList() {
		return rootBoardTreeNodeList;
	}

	public void setRootBoardTreeNodeList(List<BoardTreeNode> rootBoardTreeNodeList) {
		this.rootBoardTreeNodeList = rootBoardTreeNodeList;
	}
	
	public int getTotal() {
		int total = 0;
		
		if (null != rootBoardTreeNodeList) {
			for (BoardTreeNode rootBoardTreeNode : rootBoardTreeNodeList) {
				total += rootBoardTreeNode.getTotal();
			}
		}
		
		return total;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BoardTree [rootBoardTreeNodeList=");
		builder.append(rootBoardTreeNodeList);
		builder.append("]");
		return builder.toString();
	}
}
