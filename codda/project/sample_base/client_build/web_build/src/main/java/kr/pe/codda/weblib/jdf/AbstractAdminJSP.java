package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;

import kr.pe.codda.weblib.sitemenu.AdminSiteMenuManger;

public abstract class AbstractAdminJSP extends AbstractJSP {
	private static final long serialVersionUID = 7979973550279558200L;
	
	private AdminSiteMenuManger adminSiteMenuManger = AdminSiteMenuManger.getInstance();

	@Override
	public String getMenuNavbarString(HttpServletRequest request) {
		return adminSiteMenuManger.getMenuNavbarString(getGroupRequestURL(request), isAdminLoginedIn(request));
	}
}
