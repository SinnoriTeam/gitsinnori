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

package kr.pe.sinnori.client;

import java.util.HashMap;
import java.util.List;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NoMoreOutputMessageQueueException;
import kr.pe.sinnori.common.exception.NotFoundProjectException;
import kr.pe.sinnori.common.lib.CommonRootIF;

/**
 * 클라이언트 프로젝트 관리자
 * 
 * @author Won Jonghoon
 * 
 */
public final class ClientProjectManager implements CommonRootIF {
	/** 모니터 객체 */
	// private final Object monitor = new Object();
	
	private HashMap<String, ClientProject> clientProjectHash = new HashMap<String, ClientProject>();
	
	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class ClientProjectManagerHolder {
		static final ClientProjectManager singleton = new ClientProjectManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static ClientProjectManager getInstance() {
		return ClientProjectManagerHolder.singleton;
	}
	
	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private ClientProjectManager() {
		@SuppressWarnings("unchecked")
		List<String> projectNamelist = (List<String>)conf.getResource("project.name_list.value");
		for (String projectName : projectNamelist) {
			ClientProject clientProject=null;
			try {
				clientProject = new ClientProject(projectName);
			} catch (InterruptedException e) {
				log.error("InterruptedException", e);
				System.exit(1);
			} catch (NoMoreDataPacketBufferException e) {				
				log.error("NoMoreDataPacketBufferException", e);
				System.exit(1);
			} catch (NoMoreOutputMessageQueueException e) {
				log.error("NoMoreOutputMessageQueueException", e);
				System.exit(1);
			}
			clientProjectHash.put(projectName, clientProject);
		}
	}
	
	/**
	 * 프로젝트 이름에 해당하는 외부에서 바라보는 시각을 가지는 클라이언트 프로젝트를 얻는다. 
	 * @param projectName 프로젝트 이름
	 * @return 프로젝트 이름에 해당하는 외부 시각 클라이언트 프로젝트
	 * @throws NotFoundProjectException 
	 */
	public ClientProject getClientProject(String projectName) throws NotFoundProjectException {
		ClientProject clientProject =  clientProjectHash.get(projectName);
		if (null == clientProject) {
			StringBuilder errorBuilder = new StringBuilder("신놀이 프레임 워크 환경설정 파일에 찾고자 하는 클라이언트 프로젝트[");
			errorBuilder.append(projectName);
			errorBuilder.append("] 가 존재하지 않습니다.");
			log.error(errorBuilder.toString());
			throw new NotFoundProjectException(errorBuilder.toString());
			// System.exit(1);
		}
		
		return clientProjectHash.get(projectName);
	}
}