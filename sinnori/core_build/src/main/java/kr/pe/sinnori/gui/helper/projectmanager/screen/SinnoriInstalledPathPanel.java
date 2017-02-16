/*
 * Created by JFormDesigner on Sat Oct 17 17:48:18 KST 2015
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;
import kr.pe.sinnori.gui.util.PathSwingAction;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class SinnoriInstalledPathPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(SinnoriInstalledPathPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	private JFileChooser sinnoriInstalledPathChooser = null;
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message,
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}
	

	/**
	 * @param sourcePathTextField TextField whose value is path   
	 * @param sourcePathTextFieldName parameter sourcePathTextField's name
	 * @return the writable and readable path. but if parameter sourceTextField's value is not a valid path then return null.
	 */
	private File getWitableAndReadablePathFromTextField(JTextField sourcePathTextField, String sourcePathTextFieldName) {
		String sourcePathString = sourcePathTextField.getText();
		if ( null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is nul", sourcePathTextFieldName);
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);
		
		if (sourcePathString.equals("")) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is empty", sourcePathTextFieldName);
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s][%s] doesn't exist", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s][%s] is not a directory", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.canRead()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to read", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		if (!sourcePath.canWrite()) {
			String errorMessage = String.format("The path[%s][%s] has a permission to write", sourcePathTextFieldName, sourcePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sourcePathTextField.requestFocusInWindow();
			return null;
		}
		
		
		return sourcePath;
	}
	
	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		sinnoriInstalledPathChooser = new JFileChooser();
		sinnoriInstalledPathChooser.setMultiSelectionEnabled(false);
		sinnoriInstalledPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, sinnoriInstalledPathChooser, sinnoriInstalledPathTextField);		
		sinnoriInstalledPathButton.setAction(pathAction);
		nextStepButton.setToolTipText(
			"<html>update all project'config and overwrite all project'shells based on installed path</html>");
	}
	
	public SinnoriInstalledPathPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		initComponents();		
	}	
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sinnoriInstallPathLinePanel = new JPanel();
		sinnoriInstalledPathLabel = new JLabel();
		sinnoriInstalledPathTextField = new JTextField();
		sinnoriInstalledPathButton = new JButton();
		nextStepLinePanel = new JPanel();
		prevStepButton = new JButton();
		nextStepButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, [600px,pref]:grow, $ugap",
			"2*($lgap, default), $lgap"));
		/** Post-initialization Code start */
		postInitComponents();
		/** Post-initialization Code end */

		//======== sinnoriInstallPathLinePanel ========
		{
			sinnoriInstallPathLinePanel.setLayout(new FormLayout(
				"78dlu, $lcgap, [156dlu,pref]:grow, $lcgap, default",
				"default"));

			//---- sinnoriInstalledPathLabel ----
			sinnoriInstalledPathLabel.setText("Sinnori installed path :");
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathLabel, CC.xy(1, 1));
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathTextField, CC.xy(3, 1));

			//---- sinnoriInstalledPathButton ----
			sinnoriInstalledPathButton.setText("path");
			sinnoriInstalledPathButton.addActionListener(e -> sinnoriInstalledPathButtonActionPerformed(e));
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathButton, CC.xy(5, 1));
		}
		add(sinnoriInstallPathLinePanel, CC.xy(2, 2));

		//======== nextStepLinePanel ========
		{
			nextStepLinePanel.setLayout(new FormLayout(
				"default, $ugap, default",
				"default:grow"));

			//---- prevStepButton ----
			prevStepButton.setText("Prev");
			prevStepButton.addActionListener(e -> prevStepButtonActionPerformed(e));
			nextStepLinePanel.add(prevStepButton, CC.xy(1, 1));

			//---- nextStepButton ----
			nextStepButton.setText("Next");
			nextStepButton.addActionListener(e -> nextStepButtonActionPerformed(e));
			nextStepLinePanel.add(nextStepButton, CC.xy(3, 1));
		}
		add(nextStepLinePanel, CC.xy(2, 4, CC.CENTER, CC.DEFAULT));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel sinnoriInstallPathLinePanel;
	private JLabel sinnoriInstalledPathLabel;
	private JTextField sinnoriInstalledPathTextField;
	private JButton sinnoriInstalledPathButton;
	private JPanel nextStepLinePanel;
	private JButton prevStepButton;
	private JButton nextStepButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
	private void sinnoriInstalledPathButtonActionPerformed(ActionEvent e) {
		File sinnoriInstalledPath = getWitableAndReadablePathFromTextField(sinnoriInstalledPathTextField, "the Sinnori installed path");
		if (null == sinnoriInstalledPath) {
			return;
		}
		
		sinnoriInstalledPathChooser.setCurrentDirectory(sinnoriInstalledPath);
	}
	
	private void nextStepButtonActionPerformed(ActionEvent e) {
		File sinnoriInstalledPath = getWitableAndReadablePathFromTextField(sinnoriInstalledPathTextField, "the Sinnori installed path");
		if (null == sinnoriInstalledPath) {
			return;
		}
		
		String sinnoriInstalledPathString= null;
		try {
			sinnoriInstalledPathString = sinnoriInstalledPath.getCanonicalPath();
		} catch (IOException e1) {
			String errorMessage = String.format("fail to get the canonical pathname of the Sinnori installed path[%s]", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		sinnoriInstalledPathTextField.setText(sinnoriInstalledPathString);
		
		screenManagerIF.moveToAllMainProjectManagerScreen(sinnoriInstalledPathString);
	}

	private void prevStepButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}
}
