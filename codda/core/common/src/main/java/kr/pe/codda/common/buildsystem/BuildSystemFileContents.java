package kr.pe.codda.common.buildsystem;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.type.LogType;

public abstract class BuildSystemFileContents {
	
	/** server_build/build.xml */
	public static String getServerAntBuildXMLFileContent(String mainProjectName) {
		return ServerAntBuildXMLFileContenetsBuilder.build(mainProjectName);
	}

	/** client_build/app_build/build.xml */
	public static String getAppClientAntBuildXMLFileContents(String mainProjectName) {		
		return AppClientAntBuildXMLFileContenetsBuilder.build(mainProjectName);
	}

	/** client_build/web_build/build.xml */
	public static String getWebClientAntBuildXMLFileContents(String mainProjectName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<project name=\"");
		stringBuilder.append(mainProjectName);
		stringBuilder.append("\" default=\"compile.webclass.only\" basedir=\".\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<!-- set global properties for this build -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"debuglevel\" value=\"source,lines,vars\"/>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.src\" location=\"src/main/java\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.build\" location=\"build\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.dist\" location=\"dist\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.corelib\" location=\"corelib\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.mainlib\" location=\"lib/main\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.core.build\" location=\"../../../../core_build\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.core.mainlib\" location=\"${dir.core.build}/lib/main\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.webclass\" location=\"../../web_app_base/ROOT/WEB-INF/classes\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"dir.weblib\" location=\"../../web_app_base/ROOT/WEB-INF/lib\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property file=\"webAnt.properties\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"webclient.core.jar\" value=\"SinnoriWebLib.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<property name=\"java.complile.option.debug\" value=\"on\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<condition property=\"is.windows.yes\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<os family=\"windows\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</condition>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<condition property=\"is.unix.yes\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<os family=\"unix\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</condition>\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"init.dos\" if=\"is.windows.yes\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<property name=\"weblib\" location=\"${dos.weblib}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"init.unix\" if=\"is.unix.yes\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<property name=\"weblib\" location=\"${unix.weblib}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"init.var\" depends=\"init.dos, init.unix\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t  <!-- Create the time stamp -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<tstamp />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t  <echo message=\"java.complile.option.debug=${java.complile.option.debug}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t  <echo message=\"is.windows.yes=${is.windows.yes}, is.unix.yes=${is.unix.yes}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<echo message=\"servlet.systemlib.path=${servlet.systemlib.path}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<!-- lib directory is a user define directory -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.mainlib}/ex\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"make.unixcore\" if=\"is.unix.yes\" depends=\"init.var\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<exec dir=\"${dir.core.build}\" executable=\"ant\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"make.doscore\" if=\"is.windows.yes\" depends=\"init.var\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<exec dir=\"${dir.core.build}\" executable=\"cmd\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<arg value=\"/c\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<arg value=\"ant.bat\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</exec>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"make.core\" depends=\"make.doscore, make.unixcore\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<union id=\"core.common.jarlibs\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<fileset file=\"${dir.core.mainlib}/ex/json-simple-1.1.1.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<fileset file=\"${dir.core.mainlib}/ex/commons-io-2.6.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<fileset file=\"${dir.core.mainlib}/ex/commons-collections4-4.1.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<fileset file=\"${dir.core.mainlib}/ex/commons-codec-1.11.jar\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</union>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"copy.core\" depends=\"make.core\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t  <!-- core directory -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<delete dir=\"${dir.corelib}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.corelib}/ex\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<copy todir=\"${dir.corelib}/ex\" verbose=\"true\" overwrite=\"true\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<fileset file=\"${dir.core.build}/dist/sinnori-core.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</copy>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<copy todir=\"${dir.corelib}/ex\" verbose=\"true\" overwrite=\"true\">");
		stringBuilder.append(System.getProperty("line.separator"));
		
		stringBuilder.append("\t\t\t<union refid=\"core.common.jarlibs\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		
		stringBuilder.append("\t\t</copy>\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"compile.weblib\" depends=\"copy.core\">\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<delete dir=\"${dir.build}/weblib\" />\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.build}/weblib\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<javac debug=\"${java.complile.option.debug}\" debuglevel=\"${debuglevel}\" encoding=\"UTF-8\" includeantruntime=\"false\" srcdir=\"${dir.src}\" destdir=\"${dir.build}/weblib\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<include name=\"kr/pe/sinnori/weblib/**\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<classpath>\t\t  ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<pathelement location=\"${dir.webclass}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${servlet.systemlib.path}\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*-api.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.corelib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.mainlib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t</classpath>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</javac>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"make.weblib\" depends=\"compile.weblib\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t <delete dir=\"${dir.dist}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.dist}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<jar jarfile=\"${dir.dist}/${webclient.core.jar}\" basedir=\"${dir.build}/weblib\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<restrict>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<name name=\"**/*.class\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<archives>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<!--");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\twebclient.core.jar is loaded by tomcat dynamic classloader.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tsinnori core must not be loaded by tomcat dynamic classloader because of singleton pattern.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\tso webclient.core.jar doesn't inlucde sinnori core and sinnori core's extern libiary.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\ttomcat referes to them using setenv.sh or setenv.bat.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t-->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</archives>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t</restrict>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</jar>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"dist.weblib\" depends=\"make.weblib\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t <mkdir dir=\"${dir.weblib}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<copy todir=\"${dir.weblib}\" verbose=\"true\" overwrite=\"true\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<fileset file=\"${dir.dist}/${webclient.core.jar}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<fileset file=\"${dir.mainlib}/ex/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</copy>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"compile.webclass\" depends=\"dist.weblib\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t <!-- webclass directory is a user define directory that has the web-application dynamic classes -->");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<delete dir=\"${dir.webclass}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.webclass}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<javac debug=\"${java.complile.option.debug}\" debuglevel=\"${debuglevel}\" encoding=\"UTF-8\" ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\tincludeantruntime=\"false\" srcdir=\"${dir.src}\" destdir=\"${dir.webclass}\"");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\texcludes=\"kr/pe/sinnori/weblib/**\" >");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<classpath>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.dist}\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"${webclient.core.jar}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${servlet.systemlib.path}/\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"*-api.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.corelib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.mainlib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t</classpath>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</javac>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"all\" depends=\"compile.webclass\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<echo message=\"is.windows.yes=${is.windows.yes}, is.unix.yes=${is.unix.yes}, java.debug=${java.complile.option.debug}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t<target name=\"clean.webclass\" depends=\"init.var\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<delete dir=\"${dir.webclass}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<mkdir dir=\"${dir.webclass}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("<target name=\"compile.webclass.only\" depends=\"init.var\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t<javac debug=\"${java.complile.option.debug}\" debuglevel=\"${debuglevel}\" encoding=\"UTF-8\" ");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\tincludeantruntime=\"false\" srcdir=\"${dir.src}\" destdir=\"${dir.webclass}\"");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\texcludes=\"kr/pe/sinnori/common/**\" >");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t<classpath>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.dist}\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"${webclient.core.jar}\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${servlet.systemlib.path}/\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"*-api.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.corelib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t<fileset dir=\"${dir.mainlib}\\ex\">");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t\t<include name=\"**/*.jar\" />");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t\t</fileset>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t\t</classpath>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t\t</javac>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("\t</target>");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("</project>");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}

	/**
	 * server_build/<main project name>.sh or client_build/app_build/<main
	 * project name>Client.sh
	 */
	public static String getDosShellContents(String sinnoriInstalledPathString, String mainProjectName, 

			String jvmOptions, LogType logType, String workingPathString, String relativeExecutabeJarFileName) {
		final String dosShellLineSeparator = "^";
		
		String commonPartOfShellContents = getCommonPartOfShellContents(sinnoriInstalledPathString, mainProjectName, 

				jvmOptions, dosShellLineSeparator, logType, relativeExecutabeJarFileName);

		StringBuilder shellContentsBuilder = new StringBuilder();
		shellContentsBuilder.append("set OLDPWD=%CD%");
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd /D ");
		shellContentsBuilder.append(workingPathString);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append(commonPartOfShellContents);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd /D %OLDPWD%");
		return shellContentsBuilder.toString();
	}

	/**
	 * server_build/<main project name>Server.bat or
	 * client_build/app_build/<main project name>Client.bat
	 */
	public static String getUnixShellContents(String sinnoriInstalledPathString, String mainProjectName, 

			String jvmOptions, LogType logType, String workingPathString, String relativeExecutabeJarFileName) {

		final String unixShellLineSeparator = "\\";
		
		String commonPartOfShellContents = getCommonPartOfShellContents(sinnoriInstalledPathString, mainProjectName, 
				jvmOptions, unixShellLineSeparator, logType, relativeExecutabeJarFileName);

		StringBuilder shellContentsBuilder = new StringBuilder();
		shellContentsBuilder.append("cd ");
		shellContentsBuilder.append(workingPathString);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append(commonPartOfShellContents);
		shellContentsBuilder.append(System.getProperty("line.separator"));

		shellContentsBuilder.append("cd -");
		return shellContentsBuilder.toString();
	}

	// FIXME!
	private static String getCommonPartOfShellContents(String sinnoriInstalledPathString, String mainProjectName, 
			String jvmOptions, String shellLineSeparator, LogType logType, String relativeExecutabeJarFileName) {
		StringBuilder commandPartBuilder = new StringBuilder();

		commandPartBuilder.append("java ");
		commandPartBuilder.append(jvmOptions);
		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE);
		commandPartBuilder.append("=").append(
				BuildSystemPathSupporter.getProjectLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName));

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOG_PATH);
		commandPartBuilder.append("=").append(
				BuildSystemPathSupporter.getProjectLogPathString(sinnoriInstalledPathString, mainProjectName, logType));

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_INSTALLED_PATH);
		commandPartBuilder.append("=").append(sinnoriInstalledPathString);

		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		commandPartBuilder.append("-D");
		commandPartBuilder.append(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_RUNNING_PROJECT_NAME);
		commandPartBuilder.append("=").append(mainProjectName);
		commandPartBuilder.append(" ").append(shellLineSeparator).append(System.getProperty("line.separator"));

		// -jar
		// /home/madang01/gitsinnori/sinnori/project/sample_test/server_build/dist/SinnoriServerMain.jar
		commandPartBuilder.append("-jar ").append(relativeExecutabeJarFileName);

		return commandPartBuilder.toString();
	}
}