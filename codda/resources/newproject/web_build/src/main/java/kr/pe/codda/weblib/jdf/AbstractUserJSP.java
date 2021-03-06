package kr.pe.codda.weblib.jdf;

import javax.servlet.http.HttpServletRequest;

import kr.pe.codda.weblib.sitemenu.UserSiteMenuManger;

public abstract class AbstractUserJSP extends AbstractJSP {

	private static final long serialVersionUID = 6189100903301427726L;

	private UserSiteMenuManger userSiteMenuManger = UserSiteMenuManger.getInstance();

	@Override
	public String getSiteNavbarString(HttpServletRequest request) {
		return userSiteMenuManger.getSiteNavbarString(getGroupRequestURL(request), isUserLoginedIn(request));
	}
}
