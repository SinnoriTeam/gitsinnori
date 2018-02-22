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
package kr.pe.sinnori.server.classloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

/**
 * 서버용 동적 클래스들 로딩및 관리를 담당하는 "동적 클래스 로더". @{link ServerObjectManager } 에 종속 된다.
 * 
 * @author Won Jonghoon
 * 
 */
public class ServerClassLoader extends ClassLoader {
	private Logger log = LoggerFactory.getLogger(ServerClassLoader.class);

	//private final Object monitor = new Object();

	private String projectName = null;
	private String firstPrefixDynamicClassFullName = null;
	

	private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	private String classPathString = null;	
	private HashSet<String> systemloaderClassFullNameSet = new HashSet<String>();
	
	
	public ServerClassLoader(String projectName, 
			IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) throws SinnoriConfigurationException {
		super(ClassLoader.getSystemClassLoader());

		this.projectName = projectName;
		this.firstPrefixDynamicClassFullName = ioPartDynamicClassNameUtil.getFirstPrefixDynamicClassFullName();
		
		String sinnoriInstalledPathString = System
				.getProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
		
		if (null == sinnoriInstalledPathString) {
			String errorMessage = String.format("the system environment variable[%s] for the path where Sinnori is installed is not defined. -D%s not defined",
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH, 
					CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH);
			throw new SinnoriConfigurationException(errorMessage);
		}
		
		String serverAPPINFPathString = BuildSystemPathSupporter
				.getServerAPPINFPathString(sinnoriInstalledPathString, projectName);
		
		File serverAPPINFPath = new File(serverAPPINFPathString);
		
		if (!serverAPPINFPath.exists()) {
			String errorMessage = String.format("the server APP-INF path[%s] doesn't exist", serverAPPINFPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		if (!serverAPPINFPath.isDirectory()) {
			String errorMessage = String.format("the server APP-INF path[%s] isn't a directory", serverAPPINFPathString);
		 	throw new SinnoriConfigurationException(errorMessage);
		}
		
		this.classPathString = new StringBuilder(serverAPPINFPathString).append(File.separator).append("classes")
				.toString();
		
		String[] noTaskMessageIDListForSystemloader = {
				"SelfExnRes"
		};
		
		for (String messageIDForSystemloader : noTaskMessageIDListForSystemloader) {
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getClientMessageCodecClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageDecoderClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageEncoderClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getServerMessageCodecClassFullName(messageIDForSystemloader));
		}
		
		String[] taskMessageIDListForSystemloader = {
				"Empty"
		};
		
		for (String messageIDForSystemloader : taskMessageIDListForSystemloader) {
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getClientMessageCodecClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageDecoderClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getMessageEncoderClassFullName(messageIDForSystemloader));
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getServerMessageCodecClassFullName(messageIDForSystemloader));
			
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getClientTaskClassFullName(messageIDForSystemloader));
			
			systemloaderClassFullNameSet.add(ioPartDynamicClassNameUtil
					.getServerTaskClassFullName(messageIDForSystemloader));
		}
		
		
		log.info("projectName[{}] ServerClassLoader hashCode=[{}] create", projectName, this.hashCode());
	}

	/**
	 * 동적으로 로딩할 주어진 클래스 이름을 가지는 클래스 파일 경로를 반환한다.
	 * 
	 * @param classFullName
	 *            클래스 파일 경로를 얻고자 하는 클래스 이름
	 * @return 주어진 클래스 이름을 가지는 클래스 파일 경로
	 */
	public String getClassFileName(String classFullName) {
		String classFileName = new StringBuilder(classPathString).append(File.separator)
				.append(classFullName.replace(".", File.separator)).append(".class").toString();
		return classFileName;
	}

	/**
	 * Warning! 효율을 위해서 이 메소드는 thread safe 를 지원하지 않는다. 하여 외부에서 이를 보장해야 한다.  
	 */
	@Override
	public Class<?> loadClass(String classFullName) throws ClassNotFoundException {
		// log.info("SinnoriClassLoader hashCode=[{}], classFullName=[{}]::call
		// loadClass(String)", this.hashCode(), classFullName);

		// log.info("SinnoriClassLoader classLoaderClassPackagePrefixName=[{}]",
		// classLoaderClassPackagePrefixName);

		Class<?> retClass = null;
		// try {
		//synchronized (monitor) {
			retClass = findLoadedClass(classFullName);
			if (null == retClass) {

				if (-1 == classFullName.indexOf(firstPrefixDynamicClassFullName)) {
					/** 서버 동적 클래스 비 대상 클래스 */
					return systemClassLoader.loadClass(classFullName);
				}
				
				if (systemloaderClassFullNameSet.contains(classFullName)) {
					return systemClassLoader.loadClass(classFullName);
				}

				// log.info("SinnoriClassLoader hashCode=[{}], messageID=[{}],
				// classFullName=[{}]::주어진 클래스명은 서버 동적 클래스 대상", this.hashCode(),
				// messageID, classFullName);

				// log.info("classFullName 파일 경로 변환 문자열={}",
				// classFullName.replace(".", File.separator));

				String classFileName = getClassFileName(classFullName);

				// log.info("classFileName={}", classFileName);

				File classFileObj = new File(classFileName);
				if (!classFileObj.exists()) {
					String errorMessage = String.format(
							"SinnoriClassLoader hashCode=[%d], classFullName=[%s]" + "::클래스 파일[%s]이 존재하지 않습니다.",
							this.hashCode(), classFullName, classFileName);

					log.warn(errorMessage);

					throw new ClassNotFoundException(errorMessage);
				}

				if (classFileObj.isDirectory()) {
					// String errorMessage = String.format("클래스 파일[%s]이 일반 파일이
					// 아닌 경로입니다.", classFileName);
					String errorMessage = String.format(
							"SinnoriClassLoader hashCode=[%d], classFullName=[%s]" + "::클래스 파일[%s]이 일반 파일이 아닌 경로입니다.",
							this.hashCode(), classFullName, classFileName);
					log.warn(errorMessage);
					throw new ClassNotFoundException(errorMessage);
				}

				if (!classFileObj.canRead()) {
					// String errorMessage = String.format("클래스 파일[%s]이 일반 파일이
					// 아닌 경로입니다.", classFileName);
					String errorMessage = String.format(
							"SinnoriClassLoader hashCode=[%d], classFullName=[%s]" + "::클래스 파일[%s]을 읽을 수 없습니다.",
							this.hashCode(), classFullName, classFileName);

					log.warn(errorMessage);
					throw new ClassNotFoundException(errorMessage);
				}

				retClass = loadClass(classFullName, classFileObj);
			} else {
				log.info("retClass[{}] is not null", classFullName);
			}
		//}
		/*
		 * } finally { log.
		 * info("SinnoriClassLoader hashCode=[{}], classFullName=[{}]::end loadClass(String)"
		 * , this.hashCode(), classFullName); }
		 */

		return retClass;
	}

	/**
	 * 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체를 반환한다.
	 * 
	 * @param className
	 *            클래스명
	 * @param classFile
	 *            클래스 파일
	 * @return 지정된 클래스명와 클래스 파일로 부터 얻어진 클래스 객체
	 * @throws ClassNotFoundException
	 *             클래스를 발견하지 못했을때 던지는 예외, 상단 검사로 나올 수 없는 예외
	 */
	private Class<?> loadClass(String classFullName, File classFile) throws ClassNotFoundException {
		// log.info("SinnoriClassLoader hashCode=[{}], messageID=[{}],
		// classFullName=[{}], classFile=[{}]::call loadClass(String, File)",
		// this.hashCode(), messageID, classFullName,
		// classFile.getAbsolutePath());

		/**
		 * <pre>
		 *  
		 * 상속 받은 loadClass 는 파라미터로 문자열만 받기 때문에, 
		 * 클래스명 대응 클래스 파일을 외부에서 지정하기 위한 편법으로 아래와 같이 코딩하였다.
		 * 
		 * </pre>
		 */
		/*
		 * if (null == classFullName) { String errorMessage =
		 * "파라미터 클래스명이 널입니다."; log.warn(errorMessage); throw new
		 * IllegalArgumentException(errorMessage); }
		 */

		Class<?> retClass = null;

		// try {

		/** 서버 비지니스 로직 클래스 */
		try {
			long fileSize = classFile.length();

			if (fileSize > Integer.MAX_VALUE) {
				throw new ClassFormatError("over max size of file");
			}

			ByteBuffer fileBuffer = ByteBuffer.allocate((int) fileSize);
			// byte[] classData = new byte[(int)fileSize];
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(classFile);
				FileChannel fc = fis.getChannel();

				fc.read(fileBuffer);
			} finally {
				if (null != fis) {
					fis.close();
				}
			}

			retClass = defineClass(classFullName, fileBuffer.array(), 0, fileBuffer.capacity());

			/*
			 * Message2ClassFileInfo messageClassGroupInfo =
			 * messageID2InfoHash.get(messageID); if (null ==
			 * messageClassGroupInfo) { messageClassGroupInfo = new
			 * Message2ClassFileInfo(messageID); }
			 * 
			 * messageClassGroupInfo.addClassFile(classFullName, classFile);
			 */

		} catch (IOException e) {
			String errorMessage = String.format(
					"SinnoriClassLoader hashCode=[%d], classFullName=[%s], classFile=[{}]" + "::IOException %s",
					this.hashCode(), classFullName, classFile.getAbsolutePath(), e.getMessage());

			log.warn(errorMessage, e);

			throw new ClassFormatError(errorMessage);

		} catch (ClassFormatError e) {
			String errorMessage = String.format(
					"SinnoriClassLoader hashCode=[%d], classFullName=[%s], classFile=[{}]" + "::ClassFormatError %s",
					this.hashCode(), classFullName, classFile.getAbsolutePath(), e.getMessage());

			log.warn(errorMessage, e);

			throw new ClassFormatError(errorMessage);
		}

		/*
		 * } finally { log.
		 * info("SinnoriClassLoader hashCode=[{}], messageID=[{}], classFullName=[{}], classFile=[{}]::end loadClass(String, File)"
		 * , this.hashCode(), messageID, classFullName,
		 * classFile.getAbsolutePath()); }
		 */

		return retClass;
	}

	/**
	 * <pre>
	 * kr/pe/sinnori/impl/mybatis/memberMapper.xml 로 시작되는 mybatis 리소스 파일의 InputStream 을 반환한다.
	 * </pre>
	 */
	// @Override
	/*
	 * public InputStream getResourceAsStream(String name) { InputStream is =
	 * null;
	 * 
	 * String realResourceFilePathString =
	 * CommonStaticUtil.getFilePathStringFromResourcePathAndRelativePathOfFile(
	 * resourcesPathString, name); File realResourceFile = new
	 * File(realResourceFilePathString);
	 * 
	 * if (realResourceFile.exists()) { try { is = new
	 * FileInputStream(realResourceFile); } catch (Exception e) { log.warn(new
	 * StringBuilder("the resource[") .append(name).append("] file[")
	 * .append(realResourceFilePathString)
	 * .append("] fail to get a object of FileInputStream").toString(), e);
	 * return null; } } else { is = super.getResourceAsStream(name); }
	 * 
	 * return is; }
	 */

	/*
	 * public URL getResource(String name) { URL url = null;
	 * 
	 * String realResourceFilePathString =
	 * CommonStaticUtil.getFilePathStringFromResourcePathAndRelativePathOfFile(
	 * resourcesPathString, name); File realResourceFile = new
	 * File(realResourceFilePathString);
	 * 
	 * if (realResourceFile.exists()) { try { url =
	 * realResourceFile.toURI().toURL(); } catch (Exception e) { log.warn(new
	 * StringBuilder("the resource[") .append(name).append("] file[")
	 * .append(realResourceFilePathString)
	 * .append("] fail to convert to url").toString(), e); return null; } } else
	 * { url = super.getResource(name); }
	 * 
	 * 
	 * return url; }
	 */

	@Override
	protected void finalize() throws Throwable {
		// FIXME! 메모리 회수 확인용으로 삭제하지 마세요!
		log.info("ServerClassLoader[{}] destroy", this.hashCode());
	}

	public String getProjectName() {
		return projectName;
	}
}
