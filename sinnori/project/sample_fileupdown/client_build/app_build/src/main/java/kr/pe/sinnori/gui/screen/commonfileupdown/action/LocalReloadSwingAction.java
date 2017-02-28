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

package kr.pe.sinnori.gui.screen.commonfileupdown.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.gui.screen.commonfileupdown.FileUpDownScreenIF;

/**
 * 로컬 경로 재 갱신 이벤트 처리 클래스
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class LocalReloadSwingAction extends AbstractAction {
	private Logger log = LoggerFactory.getLogger(LocalReloadSwingAction.class);
	
	
	private FileUpDownScreenIF fileUpDownScreen = null;
	
	
	/**
	 * 생성자
	 * @param fileUpDownScreen 파일 송수신 화면을 제어하는 기능 제공 인터페이스
	 */
	public LocalReloadSwingAction(FileUpDownScreenIF fileUpDownScreen) {
		this.fileUpDownScreen = fileUpDownScreen;
		
		putValue(NAME, "reload");
		putValue(SHORT_DESCRIPTION, "로컬 작업 경로의 파일 목록 재 읽기 이벤트");
	}

	public void actionPerformed(ActionEvent e) {
		log.debug(String.format("e.getID=[%d]", e.getID()));
		/*
		localRootNode.removeAllChildren();
		fileUpDownScreen.makeLocalTreeNode(localRootNode);
		fileUpDownScreen.repaintTree(localTree);
		*/
		fileUpDownScreen.reloadLocalFileList();
	}
}
