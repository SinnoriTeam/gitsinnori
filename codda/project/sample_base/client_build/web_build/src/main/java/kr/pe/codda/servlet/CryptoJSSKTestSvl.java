package kr.pe.codda.servlet;
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


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.common.util.HexUtil;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractServlet;

/**
 * 자바 스크립트 CryptoJS 라이브러리에서 제공하는 대칭키 함수와 자바 결과 일치 테스트<br/>
 * 대칭키 함수 목록 (1) AES (2) DES (3)  DESede(=Triple DES)
 * @author Won Jonghoon
 *
 */
@SuppressWarnings("serial")
public class CryptoJSSKTestSvl extends AbstractServlet {
	final String arryPageURL[] = {
			"/menu/testcode/CryptoJSSKTest01.jsp", "/menu/testcode/CryptoJSSKTest02.jsp"
	};

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		
		
		String paramRequestType = req.getParameter(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE);
		if (null == paramRequestType) {		
			firstPage(req, res);			
			return;
		}
		
		if (paramRequestType.equals("view")) {
			firstPage(req, res);
			return;
		} else if (paramRequestType.equals("proc")) {		
			processPage(req, res);
			return;
		} else {
			String errorMessage = "파라미터 '요청종류'의 값이 잘못되었습니다";
			String debugMessage = new StringBuilder("the web parameter \"")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_REQUEST_TYPE)
					.append("\"")
					.append("'s value[")
					.append(paramRequestType)			
					.append("] is not a elment of request type set[view, proc]").toString();
			
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
	}
	
	private void firstPage(HttpServletRequest req, HttpServletResponse res) {
		printJspPage(req, res, "/menu/testcode/CryptoJSSKTest01.jsp");	
	}
	
	private void processPage(HttpServletRequest req, HttpServletResponse res) {
		String paramAlgorithm = req.getParameter("algorithm");		
		String paramPrivateKeyHex = req.getParameter("privateKey");
		String paramIVHex = req.getParameter("iv");
		String paramPlainText = req.getParameter("plainText");
		String paramEncryptedBytesHex = req.getParameter("encryptedBytes");
		
		log.info("paramAlgorithm=[{}]", paramAlgorithm);
		log.info("paramPrivateKeyHex=[{}]", paramPrivateKeyHex);
		log.info("paramIVHex=[{}]", paramIVHex);
		log.info("paramPlainText=[{}]", paramPlainText);
		log.info("paramEncryptedBytesHex=[{}]", paramEncryptedBytesHex);
		
		Hashtable<String,String> symmetricKeyTransformationHash = null;
		symmetricKeyTransformationHash = new Hashtable<String,String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");		
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");
		
		String transformation = symmetricKeyTransformationHash.get(paramAlgorithm);
		
		if (null == transformation) {
			throw new RuntimeException(String.format("don't support the algorithm[%s]", paramAlgorithm));
		}
		
		byte[] privateKeyBytes = HexUtil.getByteArrayFromHexString(paramPrivateKeyHex);
		byte[] ivBytes = HexUtil.getByteArrayFromHexString(paramIVHex);
		byte[] encryptedBytes = HexUtil.getByteArrayFromHexString(paramEncryptedBytesHex);
		
		Cipher symmetricKeyCipher = null;		
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (NoSuchPaddingException e) {
			String errorMessage = "fail to get a Cipher class instance";
			log.warn(errorMessage, e);			
			
			String debugMessage = e.getMessage();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		SecretKeySpec symmetricKey = new SecretKeySpec(privateKeyBytes, paramAlgorithm);
		
		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
		} catch (InvalidKeyException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramAlgorithm=[")
					.append(paramAlgorithm)
					.append("], paramPrivateKeyHex=")
					.append(paramPrivateKeyHex)
					.append("], paramIVHex=[")
					.append(paramIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		} catch (InvalidAlgorithmParameterException e) {
			String errorMessage = "fail to initialize a Cipher class instance with a key and a set of algorithm parameters";
			log.warn(errorMessage, e);			
			
			String debugMessage = new StringBuilder("paramAlgorithm=[")
					.append(paramAlgorithm)
					.append("], paramPrivateKeyHex=")
					.append(paramPrivateKeyHex)
					.append("], paramIVHex=[")
					.append(paramIVHex)
					.append("], errmsg=").append(e.getMessage()).toString();
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		log.info("Cipher.init with IV");
		
		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			log.warn("IllegalBlockSizeException", e);
			throw new RuntimeException("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			log.warn("BadPaddingException", e);
			throw new RuntimeException("BadPaddingException");
		}
		
		String plainTextHex = HexUtil.getHexStringFromByteArray(paramPlainText.getBytes());
		String decryptedBytesHex = HexUtil.getHexStringFromByteArray(decryptedBytes);
		log.info("plainTextHex[{}], decryptedBytes[{}]", plainTextHex, decryptedBytesHex);
		
		
		String decryptedPlainText = new String(decryptedBytes);
		String isSame = String.valueOf(decryptedPlainText.equals(paramPlainText));			
		
		
		req.setAttribute("plainText", paramPlainText);
		req.setAttribute("algorithm", paramAlgorithm);
		req.setAttribute("privateKey", paramPrivateKeyHex);
		req.setAttribute("iv", paramIVHex);
		req.setAttribute("encryptedBytesHex", paramEncryptedBytesHex);
		req.setAttribute("plainTextHex", plainTextHex);
		req.setAttribute("decryptedBytesHex", decryptedBytesHex);
		req.setAttribute("decryptedPlainText", decryptedPlainText);
		req.setAttribute("isSame", isSame);
		printJspPage(req, res, "/menu/testcode/CryptoJSSKTest02.jsp");
	}
}
