package kr.pe.sinnori.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.weblib.AbstractServlet;
import kr.pe.sinnori.common.weblib.WebCommonStaticFinalVars;
import kr.pe.sinnori.impl.message.BoardUploadFileInDTO.BoardUploadFileInDTO;
import kr.pe.sinnori.impl.message.BoardUploadFileOutDTO.BoardUploadFileOutDTO;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.impl.message.SeqValueInDTO.SeqValueInDTO;
import kr.pe.sinnori.impl.message.SeqValueOutDTO.SeqValueOutDTO;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@SuppressWarnings("serial")
public class BoardUploadSvl extends AbstractServlet {

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String goPage = "/board/BoardUpload01.jsp";
		
		if (! isLogin(req)) {
			String errorMessage = new StringBuilder("파일 업로드는 로그인 서비스 입니다. 로그인 하시기 바랍니다.").toString();		
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);			
			return;
		}
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		if (!isMultipart) {
			String errorMessage = new StringBuilder("Form Type is not multipart").toString();		
			log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);			
			return;
		}
		
		
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Set factory constraints
		factory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);
		factory.setRepository(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_TEMP_DIR);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		// upload.setHeaderEncoding("UTF-8");
		
		// FIXME!
		log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());

		// Set overall request size constraint
		upload.setSizeMax(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_SIZE);

		// Parse the request
		List<FileItem> fileItemList = upload.parseRequest(req);
		try {
			// int fileItemListSize = fileItemList.size();		
			
			int newAttachFileListSize = 0;	
			List<BoardUploadFileInDTO.NewAttachFile> newAttachFileList = new ArrayList<BoardUploadFileInDTO.NewAttachFile>();
					
			int selectedOldAttachFileListSize = 0;
			List<BoardUploadFileInDTO.SelectedOldAttachFile> selectedOldAttachFileList = new ArrayList<BoardUploadFileInDTO.SelectedOldAttachFile>();
			
			List<FileItem> newAttachFileItemList = new ArrayList<FileItem>();
			
			HashMap<String, Object> parmHash = new HashMap<String, Object>();
			
			for (FileItem fileItem : fileItemList) {
				// FIXME!
				log.debug("fileItem={}, userId={}, ip={}", fileItem.toString(), getUserId(req), req.getRemoteAddr());
				
				if (fileItem.isFormField()) {
					String name = fileItem.getFieldName();
					String value = fileItem.getString();
					// FIXME!, 업로드는 업로드에만 집중하기때문에 파라미터 받을 이유 없지만 디버깅용으로 남김
					log.info("폼 타입이 필드, name={}, value={}", name, value);
					
					if (name.equals("oldAttachSeq")) {
						short selectedOldAttachSeq = 0;
						try {
							selectedOldAttachSeq = Short.parseShort(value);
						} catch(NumberFormatException e) {
							String errorMessage = new StringBuilder("자바 short 타입 변수인 업로드 파일 순번(attachSeq) 값[")
							.append(value).append("]이 잘못되었습니다.").toString();
							log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
							
							req.setAttribute("errorMessage", errorMessage);
							printJspPage(req, res, goPage);
							return;
						}
						
						BoardUploadFileInDTO.SelectedOldAttachFile selectedOldAttachFile = 
								new BoardUploadFileInDTO.SelectedOldAttachFile();
						
						selectedOldAttachFile.setAttachSeq(selectedOldAttachSeq);
						
						selectedOldAttachFileList.add(selectedOldAttachFile);														
					} else {
						parmHash.put(name, value);
					}
					
							
					
				} else {
					// String fieldName = fileItem.getFieldName();					
					String fileName = fileItem.getName();
					String contentType = fileItem.getContentType();
					// boolean isInMemory = fileItem.isInMemory();
					long sizeInBytes = fileItem.getSize();
					
					// FIXME!
					log.info("contentType={}", contentType);
					
					if (sizeInBytes == 0) {
						// FIXME!, 파일 크기 0 인 경우 디버깅용으로 남김
						log.info("file size is zero, fileItem={}, userId={}, ip={}", fileItem.toString(), getUserId(req), req.getRemoteAddr());
						continue;
					}
					
					String lowerCaseFileName = fileName.toLowerCase();
					
					if (!lowerCaseFileName.endsWith(".jpg")
							&& !lowerCaseFileName.endsWith(".gif") 
							&& !lowerCaseFileName.endsWith(".png")) {
						
						String errorMessage = new StringBuilder("업로드 파일[")
						.append(fileName)
						.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.").toString();
						log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
						
						req.setAttribute("errorMessage", errorMessage);
						printJspPage(req, res, goPage);
						return;
					}					
					
					if (!contentType.equals("image/jpeg") 
							&& !contentType.equals("image/png")
							&& !contentType.equals("image/gif")) {
						String errorMessage = new StringBuilder("업로드 파일[")
						.append(fileName)
						.append("][")
						.append(contentType)
						.append("]는 이미지 jpg, gif, png 만 올 수 있습니다.").toString();
						log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
						
						req.setAttribute("errorMessage", errorMessage);
						printJspPage(req, res, goPage);
						return;
					}
					
					
					BoardUploadFileInDTO.NewAttachFile newAttachFile = new BoardUploadFileInDTO.NewAttachFile();
					newAttachFile.setAttachFileName(fileName);
					newAttachFileList.add(newAttachFile);
					
					newAttachFileItemList.add(fileItem);
				}
			}
						
			String parmAttachId = (String)parmHash.get("attachId");
			if (null == parmAttachId) {
				String errorMessage = "자바 long 타입 변수인 업로드 파일 식별자(attachId) 값을 넣어주세요.";
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			
			long attachId = -1;		
			try {
				attachId = Long.parseLong(parmAttachId);
			}catch (NumberFormatException nfe) {
				String errorMessage = new StringBuilder("자바 long 타입 변수인 업로드 파일 식별자(attachId) 값[")
				.append(parmAttachId).append("]이 잘못되었습니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			if (0 > attachId) {
				String errorMessage = new StringBuilder("업로드 파일 식별자(attachId) 값[")
				.append(parmAttachId).append("]은 0 보다 크거나 같아야 합니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			if (CommonStaticFinalVars.MAX_UNSIGNED_INT < attachId) {
				String errorMessage = new StringBuilder("업로드 파일 식별자 값[")
				.append(parmAttachId).append("]은 ")
				.append(CommonStaticFinalVars.MAX_UNSIGNED_INT)
				.append(" 값 보다 작거나 같아야합니다.").toString();
				log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				
				req.setAttribute("errorMessage", errorMessage);
				printJspPage(req, res, goPage);
				return;
			}
			
			newAttachFileListSize = newAttachFileList.size();
			
			if (0 == attachId) {
				/** 신규 추가 */
				selectedOldAttachFileListSize = selectedOldAttachFileList.size();
				
				if (selectedOldAttachFileListSize > 0) {
					String errorMessage = new StringBuilder("신규 추가중에는 기존 업로드 파일들에 대한 사용자 선택을 할 수 없습니다.")
					.append("기존 업로드 파일들에 대한 사용자 선택 갯수=")
					.append(selectedOldAttachFileListSize).toString();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}			
				
				if (newAttachFileListSize == 0) {
					String errorMessage = new StringBuilder("1.업로드 파일이 없습니다. 1개 이상 요구됩니다.").toString();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}
				
				if (newAttachFileListSize > WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT) {
					String errorMessage = new StringBuilder("신규 업로드 파일 갯수[")
					.append(newAttachFileListSize)
					.append("]가 최대 업로드 파일 갯수[")
					.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
					.append("]를 초과 하였습니다.").toString();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}
				
				
			} else {
				/** 수정 */
				selectedOldAttachFileListSize = selectedOldAttachFileList.size();
				int totalAttachFile = newAttachFileListSize + selectedOldAttachFileListSize;
				
				/*if (0 == totalAttachFile) {
					String errorMessage = new StringBuilder("2.업로드 파일이 없습니다. 1개 이상 요구됩니다.").toString();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}*/
				
				if (WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT < totalAttachFile) {
					String errorMessage = new StringBuilder("선택한 기존 업로드 파일 갯수[")
					.append(selectedOldAttachFileListSize)
					.append("]와 신규 추가한 업로드 파일 개수[")
					.append(newAttachFileListSize)
					.append("]의 합이 최대 업로드 파일 개수[")
					.append(WebCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
					.append("]를 초과 하였습니다.").toString();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}
			}			
			
			BoardUploadFileInDTO bardUploadFileInDTO = new BoardUploadFileInDTO();
			bardUploadFileInDTO.setUserId(getUserId(req));
			bardUploadFileInDTO.setIp(req.getRemoteAddr());
			bardUploadFileInDTO.setAttachId(attachId);
			bardUploadFileInDTO.setNewAttachFileCnt(newAttachFileListSize);
			bardUploadFileInDTO.setNewAttachFileList(newAttachFileList);
			bardUploadFileInDTO.setSelectedOldAttachFileCnt(selectedOldAttachFileListSize);
			bardUploadFileInDTO.setSelectedOldAttachFileList(selectedOldAttachFileList);
						
			// FIXME!
			log.info("1.{}", bardUploadFileInDTO.toString());			
					
			String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			String errorMessage = "";
			AbstractMessage messageFromServer = null;
			
			if (newAttachFileListSize > 0) {
				SeqValueInDTO seqValueInDTO = new SeqValueInDTO();
				seqValueInDTO.setSeqTypeId(WebCommonStaticFinalVars.UPLOAD_FILENAME_SEQ_TYPE_ID);
				seqValueInDTO.setWantedSize((short)newAttachFileListSize);
				
				// FIXME!
				log.info("seqValueInDTO={}, bardUploadFileInDTO={}", seqValueInDTO.toString(), bardUploadFileInDTO.toString());
				
				messageFromServer = clientProject.sendSyncInputMessage(seqValueInDTO);			
							
				if (messageFromServer instanceof SeqValueOutDTO) {
					SeqValueOutDTO seqValueOutDTO = (SeqValueOutDTO)messageFromServer;
					long uploadFileNameSeqValue = seqValueOutDTO.getSeqValue();				
					// for (FileItem fileItem : avaiableFileItemList) {
					
					for (int i=0; i < newAttachFileListSize; i++) {
						BoardUploadFileInDTO.NewAttachFile newAttachFile = newAttachFileList.get(i);
						FileItem newAttachFileItem = newAttachFileItemList.get(i);
								
						String attachSystemFullFileName = getAttachSystemFullFileName(uploadFileNameSeqValue);
						File uploadFile = new File(attachSystemFullFileName);
						try {
							newAttachFileItem.write(uploadFile);
						} catch(Exception e) {
							errorMessage = "게시판 업로드 파일 저장 처리가 실패하였습니다.";
							
							log.warn("{} 번째 게시판 개별 업로드 파일[attachSystemFullFileName={}][{}] 저장 실패, userId={}, ip={}", 
									i, attachSystemFullFileName, newAttachFileItem.toString(), 
									getUserId(req), req.getRemoteAddr());
							req.setAttribute("errorMessage", errorMessage);
							printJspPage(req, res, goPage);
							return;
						}
						
						newAttachFile.setSystemFileName(attachSystemFullFileName);
						
						log.debug("{}/{} 번째 게시판 개별 업로드 파일[attachSystemFullFileName={}][{}] 저장 성공, userId={}, ip={}", 
								i, newAttachFileListSize, attachSystemFullFileName, newAttachFileItem.toString(), 
								getUserId(req), req.getRemoteAddr());
						
						uploadFileNameSeqValue++;
					}	
					
					// FIXME!
					log.info("2.bardUploadFileInDTO={}", bardUploadFileInDTO.toString());
				} else {
					errorMessage = "파일명 시퀀스 조회가 실패하였습니다.";
					
					if (messageFromServer instanceof SelfExn) {
						log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", bardUploadFileInDTO.toString(), messageFromServer.toString());
					} else {
						log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", bardUploadFileInDTO.toString(), messageFromServer.toString());
					}
					
					req.setAttribute("errorMessage", errorMessage);
					printJspPage(req, res, goPage);
					return;
				}
			}			
						
			messageFromServer = clientProject.sendSyncInputMessage(bardUploadFileInDTO);		
			
			if (messageFromServer instanceof BoardUploadFileOutDTO) {
				BoardUploadFileOutDTO boardUploadFileOutDTO = (BoardUploadFileOutDTO)messageFromServer;
				req.setAttribute("boardUploadFileOutDTO", boardUploadFileOutDTO);				
			} else {				
				// log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				if (messageFromServer instanceof MessageResult) {
					MessageResult messageResultOutObj = (MessageResult)messageFromServer;
					errorMessage = messageResultOutObj.getResultMessage();
					log.warn("{}, userId={}, ip={}", errorMessage, getUserId(req), req.getRemoteAddr());
				} else {
					errorMessage = "게시판 업로드 파일 처리가 실패하였습니다.";
					
					if (messageFromServer instanceof SelfExn) {
						log.warn("입력 메시지[{}]의 응답 메시지로 SelfExn 메시지 도착, 응답 메시지=[{}]", bardUploadFileInDTO.toString(), messageFromServer.toString());
					} else {
						log.warn("입력 메시지[{}]의 응답 메시지로 알 수 없는 메시지 도착, 응답 메시지=[{}]", bardUploadFileInDTO.toString(), messageFromServer.toString());
					}
				}							
			}		
			
			req.setAttribute("errorMessage", errorMessage);
			printJspPage(req, res, goPage);
		} finally {			
			for (FileItem fileItem : fileItemList) {
				if (!fileItem.isFormField()) {
					// FIXME!
					log.info("게시판 개별 업로드 파일[{}] 삭제", fileItem.toString());
					try {
						fileItem.delete();
					} catch(Exception e) {
						log.warn("게시판 개별 업로드 파일[{}] 삭제 실패", fileItem.toString());
					}
				}			
			}
		}
	}
}