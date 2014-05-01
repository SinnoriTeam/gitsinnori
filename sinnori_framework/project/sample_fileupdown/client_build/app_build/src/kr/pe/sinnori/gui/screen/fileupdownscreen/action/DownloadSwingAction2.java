
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

package kr.pe.sinnori.gui.screen.fileupdownscreen.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.gui.lib.AbstractFileTreeNode;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.screen.fileupdownscreen.FileUpDownScreenIF;

/**
 * 다운로드 이벤트 처리 버전2 클래스
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class DownloadSwingAction2 extends AbstractAction implements CommonRootIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private FileUpDownScreenIF fileUpDownScreen = null;
	private JTree localTree = null;
	private LocalFileTreeNode localRootNode = null;
	private JTree remoteTree = null;
	private RemoteFileTreeNode remoteRootNode = null;
	private String remotePathSeperator = null;

	/**
	 * 생성자
	 * @param mainFrame 메인 프레임
	 * @param mainController 메인 제어자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 * @param localTree 로컬 트리
	 * @param localRootNode 로컬 루트 노드
	 * @param remoteTree 원격지 트리
	 * @param remoteRootNode 원격지 루트 노드
	 * @param remotePathSeperator 원격지 파일 구분자. 참고) 원격지 파일 목록을 요청하기전에 생성시에는 null 값이다.
	 */
	public DownloadSwingAction2(JFrame mainFrame,
			MainControllerIF mainController,
			FileUpDownScreenIF fileUpDownScreen,
			JTree localTree,
			LocalFileTreeNode localRootNode,
			JTree remoteTree,
			RemoteFileTreeNode remoteRootNode, 
			String remotePathSeperator) {
		super();

		this.mainFrame = mainFrame;
		this.mainController = mainController;
		this.fileUpDownScreen = fileUpDownScreen;
		this.localTree = localTree;
		this.localRootNode = localRootNode;
		this.remoteTree = remoteTree;
		this.remoteRootNode = remoteRootNode;
		this.remotePathSeperator = remotePathSeperator;
		
		
		putValue(NAME, "downlaod");
		putValue(SHORT_DESCRIPTION, "download remote file to client");
	}

	/**
	 * 다운로드 이어받기/덮어쓰기/취소 여부를 묻는 창
	 * @param remoteFileName 사용자가 다운로드 하겠다고 선택한 원격지 파일 이름
	 * @param localWorkPathName 로컬 파일 작업 경로
	 * @return 사용자의 이어받기/덮어쓰기/취소 선택값, 디폴트 이어받기, 단 로컬에 원격지에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION,  
	 *  
	 */
	private int getYesNoCancel(String remoteFileName,  String localWorkPathName) {
		Object[] options = {"이어받기",
		"덮어쓰기",
		"취소"};
		int yesNoCancelOption = JOptionPane.showOptionDialog(mainFrame,
				String
				.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
						remoteFileName, localWorkPathName),
		"이어받기 확인창",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
		
		return yesNoCancelOption;
	}
	
	/**
	 * 로컬에 원격지에서 선택한 파일과 같은 파일 이름이 있고 파일 크기가 0 보다 크다면,
	 * 사용자에게 이어받기/덮어쓰기/취소 여부를 묻는다.
	 * 단, 로컬에 원격지에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다. 
	 *   
	 * @param remoteFileName 사용자가 다운로드 하겠다고 선택한 원격지 파일 이름
	 * @param localWorkPathName 로컬 파일 작업 경로
	 * @return 사용자의 이어받기/덮어쓰기/취소 선택값, 디폴트 이어받기, 단 로컬에 원격지에서 선택한 파일과 같은 이름이 없거나 있어도 파일 크기가 0일 경우에는 덮어쓰기값으로 설정된다.
	 * 참고) 이어받기:JOptionPane.YES_OPTION, 덮어쓰기:JOptionPane.NO_OPTION, 취소:JOptionPane.CANCEL_OPTION
	 */
	private int getYesNoCancelOfLocalRootNode(String remoteFileName,  String localWorkPathName) {
		int cntOfChild = localRootNode.getChildCount();
		for (int i=0;i < cntOfChild; i++) {
			LocalFileTreeNode localFileTreeNode = (LocalFileTreeNode)localRootNode.getChildAt(i);
			String localTempFileName = localFileTreeNode.getFileName();
			long localTempFileSize = localFileTreeNode.getFileSize();
			if (localTempFileName.equals(remoteFileName)) {
				/*int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
						.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
								remoteFileName, localWorkPathName), "덮어쓰기 확인창",
						JOptionPane.YES_NO_OPTION);
				if (JOptionPane.NO_OPTION == yesOption) return;
				break;*/
				if (localTempFileSize > 0) {
					int yesNoCancel = getYesNoCancel(remoteFileName, localWorkPathName);
					return yesNoCancel;
				}
				break;
			}
		}
		return JOptionPane.NO_OPTION;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));

		if (null == remotePathSeperator) {
			remotePathSeperator = fileUpDownScreen.getRemotePathSeperator();
		}
		
		
		TreePath remoteSelectedPath = remoteTree.getSelectionPath();
		if (null == remoteSelectedPath) {
			JOptionPane.showMessageDialog(mainFrame, "원격지 파일을 선택해 주세요.");
			return;
		}

		RemoteFileTreeNode remoteSelectedNode = (RemoteFileTreeNode) remoteSelectedPath
				.getLastPathComponent();

		if (remoteSelectedNode.isDirectory()) {
			JOptionPane.showMessageDialog(mainFrame,
					"원격지 디렉토리를 선택하였습니다. 원격지 파일을 선택해 주세요.");
			return;
		}

		boolean append = false;
		String localFilePathName = (String)localRootNode.getUserObject();
		String localFileName = "";
		long localFileSize = 0L;
		String remoteFilePathName = remoteRootNode.getFileName();
		String remoteFileName = remoteSelectedNode.getFileName();
		long remoteFileSize = remoteSelectedNode.getFileSize();
		if (0 == remoteFileSize) {
			String errorMessage = "다운 로드할 파일 크기가 0 입니다.";
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		
		
		int fileBlockSize = mainController.getFileBlockSize();

		TreePath localSelectedPath = localTree.getSelectionPath();
		if (null != localSelectedPath) {
			LocalFileTreeNode localSelectedNode = (LocalFileTreeNode) localSelectedPath
					.getLastPathComponent();
			
			if (localSelectedNode.isRoot()) {
				/*int cntOfChild = localRootNode.getChildCount();
				for (int i=0;i < cntOfChild; i++) {
					LocalFileTreeNode localFileTreeNode = (LocalFileTreeNode)localRootNode.getChildAt(i);
					String localTempFileName = localFileTreeNode.getFileName();
					if (localTempFileName.equals(remoteFileName)) {
						int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
								.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
										remoteFileName, localFilePathName), "덮어쓰기 확인창",
								JOptionPane.YES_NO_OPTION);
						if (JOptionPane.NO_OPTION == yesOption) return;
						break;
					}
				}*/
				int yesNoCancelOption = getYesNoCancelOfLocalRootNode(remoteFileName, localFilePathName);
				/** 취소 */
				if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
				
				if (JOptionPane.NO_OPTION == yesNoCancelOption) {
					/** 덮어쓰기 */
					append = false;
				} else {
					/** 이어 받기 */
					append = true;
				}
			} else {
				if (AbstractFileTreeNode.FileType.File == localSelectedNode
						.getFileType()) {
					/*int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
							.format("원격지 파일[%s]을 로컬 파일[%s]에 덮어 쓰시겠습니까?",
									remoteFileName,
									localSelectedNode.getFileName()), "덮어쓰기 확인창",
							JOptionPane.YES_NO_OPTION);
					if (JOptionPane.NO_OPTION == yesOption)
						return;
	
					localFileName = localSelectedNode.getFileName();*/
					localFileName = localSelectedNode.getFileName();
					localFileSize = localSelectedNode.getFileSize();
					
					if (0 == localFileSize) {
						/** 덮어쓰기 */
						append = false;
					} else {
						int yesNoCancelOption = getYesNoCancelOfLocalRootNode(remoteFileName, localFilePathName);
						/** 취소 */
						if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
						
						if (JOptionPane.NO_OPTION == yesNoCancelOption) {
							/** 덮어쓰기 */
							append = false;
						} else {
							/** 이어 받기 */
							append = true;
						}
					}
					
				} else {
					StringBuilder targetPathBuilder = new StringBuilder(localFilePathName);
					targetPathBuilder.append(File.separator);
					targetPathBuilder.append(localSelectedNode.getFileName());
					localFilePathName = targetPathBuilder.toString();
				}
			}
		} else {
			/*int cntOfChild = localRootNode.getChildCount();
			for (int i=0;i < cntOfChild; i++) {
				LocalFileTreeNode localFileTreeNode = (LocalFileTreeNode)localRootNode.getChildAt(i);
				String localTempFileName = localFileTreeNode.getFileName();
				if (localTempFileName.equals(remoteFileName)) {
					int yesOption = JOptionPane.showConfirmDialog(mainFrame, String
							.format("원격지 파일[%s]과 동일한 파일이 로컬 작업 경로[%s]에 존재합니다. 파일을 덮어 쓰시겠습니까?",
									remoteFileName, localFilePathName), "덮어쓰기 확인창",
							JOptionPane.YES_NO_OPTION);
					if (JOptionPane.NO_OPTION == yesOption) return;
					break;
				}
			}*/
			int yesNoCancelOption = getYesNoCancelOfLocalRootNode(remoteFileName, localFilePathName);
			/** 취소 */
			if (JOptionPane.CANCEL_OPTION == yesNoCancelOption) return;
			
			if (JOptionPane.NO_OPTION == yesNoCancelOption) {
				/** 덮어쓰기 */
				append = false;
			} else {
				/** 이어 받기 */
				append = true;
			}
		}
		
		
		// FIXME!
		log.info(String.format("copy remoteFilePathName[%s] remoteFileName[%s] to localFilePathName[%s] localFileName[%s]",
				remoteFilePathName, remoteFileName, localFilePathName,  localFileName));
		
		
		OutputMessage downFileInfoResulOutObj = mainController
				.readyDownloadFile(append, localFilePathName, localFileName, localFileSize,
						remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
		
		if (null == downFileInfoResulOutObj) {
			mainController.freeLocalTargetFileResource();
			return;
		}
		
		// FIXME!
		log.info(downFileInfoResulOutObj.toString());
		
		int serverSourceFileID = -1;
		try {
			serverSourceFileID = (Integer)downFileInfoResulOutObj.getAttribute("serverSourceFileID");
		} catch (MessageItemException e1) {
			log.warn("MessageItemException", e1);
			
			JOptionPane.showMessageDialog(mainFrame, e1.getMessage());
			return;
		}
		
		/**
		 * 다운로드 하기전에 로컬 목적지 파일 크기 재조정, 만약 중복 받기이면 0으로 이어받기이면 아무 동작 안한다.
		 */
		if (!mainController.truncateLocalTargetFileResource()) return;
		
		mainController.openDownloadProcessDialog(serverSourceFileID, new StringBuilder(remoteFileName).append(" 다운로드 중...").toString(), remoteFileSize);
	}
}