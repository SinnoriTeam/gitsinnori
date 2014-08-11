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
package kr.pe.sinnori.common.classload;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeSet;

import kr.pe.sinnori.common.lib.CommonRootIF;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;

/**
 * <pre>
 * 로더와 클래스 이름을 키로하는 객체에 대한 캐쉬 관리자
 * 최대 갯수만큼 객체를 캐쉬한다. 
 * 만약 지정된 최대 갯수를 넘어 서면 가장 오래 동안 사용 안한 객체를 캐쉬에서 삭제한다.
 * 최대 갯수는 초기 환경변수에서 읽어온 값으로 설정되며
 * 최대 갯수를 변경할 수 있는 메소드를 제공한다.
 * 참고) 이글 쓰는 2014.08.10 기준으로 신놀이에서는 메시지 입출력과 관련된 메시지 서버/클라이언트 코덱 객체만 다룬다.
 * </pre>
 *  
 * @author "Jonghoon Won"
 * 
 */
public class LoaderAndName2ObjectManager implements CommonRootIF {
	private final Object monitor = new Object();
	private int cachedObjectSeq = Integer.MIN_VALUE;
	private HashMap<ClassLoader, HashMap<String, CachedObject>> loaderHash = null;
	TreeSet<CachedObject> treeSet = null;
	private int maxSize = 10;
	// private int objectValueCnt = 0;
	

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 */
	private LoaderAndName2ObjectManager() {
		maxSize = 10;
		// objectValueCnt = 0;
		loaderHash = new HashMap<ClassLoader, HashMap<String, CachedObject>>(maxSize);
		treeSet = new TreeSet<CachedObject>(new LoaderAndClassNameComparator());
	}	
	
	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class SystemClassManagerHolder {
		static final LoaderAndName2ObjectManager singleton = new LoaderAndName2ObjectManager();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static LoaderAndName2ObjectManager getInstance() {
		return SystemClassManagerHolder.singleton;
	}

	class LoaderAndClassNameComparator implements Comparator<CachedObject> {
		@Override
		public int compare(CachedObject o1, CachedObject o2) {
			if (o1.seq > o2.seq) return 1;
			else if (o1.seq == o2.seq) return 0;
			else return -1;
		}
	}
	
	public void setMaxSize(int newMaxSize) throws IllegalArgumentException {
		if (newMaxSize < 1) {
			String errorMessage = "parameter newMaxSize[" + newMaxSize
					+ "] is less than one";
			log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		synchronized (monitor) {
			maxSize = newMaxSize;

			while (treeSet.size() > newMaxSize) {
				CachedObject firstCachedObject = treeSet.first();
				treeSet.remove(firstCachedObject);
				HashMap<String, CachedObject> classNameHash = loaderHash.get(firstCachedObject.classLoader);
				classNameHash.remove(firstCachedObject.classFullName);
				if (classNameHash.isEmpty()) {
					loaderHash.remove(firstCachedObject.classLoader);
				}
			}
			
			log.info("새로 조정된 최대 크기[{}", maxSize);
		}
	}

	public Object getObjectFromHash(ClassLoader classLoader,
			String classFullName) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		if (null == classLoader) {
			String errorMessage = "parameter classLoader is null";
			log.debug(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == classFullName) {
			String errorMessage = "parameter classFullName is null";
			log.debug(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		if (classFullName.indexOf("kr.pe.sinnori.impl.message.SelfExn") == 0) {
			if (classFullName
					.equals("kr.pe.sinnori.impl.message.SelfExn.SelfExnClientCodec")) {
				return CommonStaticFinalVars.SELFEXN_CLIENT_CODEC;
			} else if (classFullName
					.equals("kr.pe.sinnori.impl.message.SelfExn.SelfExnServerCodec")) {
				return CommonStaticFinalVars.SELFEXN_SERVER_CODEC;
			} else {
				/** FIXME! 코덱과 관련 없는 SelfExn 메시지 관련 객체에 대해서 접근 방지 처리 */
				String errorMessage = "SelfExn 메시지 접근";
				IllegalAccessException e = new IllegalAccessException(errorMessage);
				log.warn(errorMessage, e);
				throw e;
			}
		}
		
		Object returnObj = null;
		
		synchronized (monitor) {
			CachedObject cachedObject = null;
			HashMap<String, CachedObject> classNameHash = loaderHash.get(classLoader);
			int objectValueCnt = treeSet.size();
			
			if (null == classNameHash) {
				/** classLoader 미 등재 */
				Class<?> objClass = classLoader.loadClass(classFullName);
				returnObj = objClass.newInstance();
				if (Integer.MAX_VALUE == cachedObjectSeq) {
					log.warn("classLoader 미 등재::사용된 시간 개념의 순번 소진으로 해쉬및 트리에 저장하지 않음");
					return returnObj;
				}
				
				/** 해쉬및 트리에 추가 */				
				if (objectValueCnt == maxSize && !treeSet.isEmpty()) {
					/** 최대 갯수를 유지하기 위한 가장 오래동안 사용하지 않는 각체 삭제 수행 */					
					CachedObject firstCachedObject = treeSet.first();
					treeSet.remove(firstCachedObject);
					HashMap<String, CachedObject> firstClassNameHash = loaderHash.get(firstCachedObject.classLoader);
					firstClassNameHash.remove(firstCachedObject.classFullName);
					if (firstClassNameHash.isEmpty()) {
						loaderHash.remove(firstCachedObject.classLoader);
					}
					log.warn("classLoader 미 등재::가장 오래동안 사용하지 않는 객체[{}] 삭제", firstCachedObject.toString());
				}
				
				classNameHash = new LinkedHashMap<String, CachedObject>();
				cachedObject = new CachedObject(classLoader, classFullName, cachedObjectSeq, returnObj);
				cachedObjectSeq++;
				classNameHash.put(classFullName, cachedObject);
				loaderHash.put(classLoader, classNameHash);
				treeSet.add(cachedObject);
				log.warn("classLoader 미 등재::신규 객체[{}] 추가", cachedObject.toString());
			} else {
				cachedObject = classNameHash.get(classFullName);
				if (null == cachedObject) {
					/** classFullName 미 등재 */
					Class<?> objClass = classLoader.loadClass(classFullName);
					returnObj = objClass.newInstance();
					
					if (Integer.MAX_VALUE == cachedObjectSeq) {
						log.warn("classFullName 미 등재::사용된 시간 개념의 순번 소진으로 해쉬및 트리에 저장하지 않음");
						return returnObj;
					}
					
					/** 해쉬및 트리에 추가 */					
					if (objectValueCnt == maxSize && !treeSet.isEmpty()) {
						/** 최대 갯수를 유지하기 위한 가장 오래동안 사용하지 않는 각체 삭제 수행 */
						CachedObject firstCachedObject = treeSet.first();
						treeSet.remove(firstCachedObject);
						HashMap<String, CachedObject> firstClassNameHash = loaderHash.get(firstCachedObject.classLoader);
						firstClassNameHash.remove(firstCachedObject.classFullName);
						if (firstClassNameHash.isEmpty()) {
							loaderHash.remove(firstCachedObject.classLoader);
						}
						log.warn("classFullName 미 등재::가장 오래동안 사용하지 않는 객체[{}] 삭제", firstCachedObject.toString());
					}
					
					cachedObject = new CachedObject(classLoader, classFullName, cachedObjectSeq, returnObj);
					cachedObjectSeq++;						
					classNameHash.put(classFullName, cachedObject);
					treeSet.add(cachedObject);
					log.warn("classFullName 미 등재::신규 객체[{}] 추가", cachedObject.toString());
				} else {
					/**
					 * 캐쉬에 있는 객체를 반환하기전 사용된 시간 개념의 순번을 갱신한후
					 * 가장 오래동안 사용하지 않는 객체를 첫번째로 얻을 수 있는 트리에 반영하다. 
					 */
					// log.info("사용된 시간 개념의 순번 갱신전 객체[{}]", cachedObject.toString());
					
					returnObj = cachedObject.cachedObj;
					treeSet.remove(cachedObject);
					cachedObject.updateSeq(cachedObjectSeq);
					cachedObjectSeq++;
					treeSet.add(cachedObject);
					
					// log.info("사용된 시간 개념의 순번 갱신후 객체[{}]", cachedObject.toString());
				}
			}
		}

		return returnObj;
	}
}
