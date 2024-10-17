//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// BoxScanApplet.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.applet.Applet;

import org.osgi.framework.Bundle;

import com.canon.meap.imi.IMI;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.login.LocalLoginService;
import com.canon.meap.service.login.event.UserEvent;
import com.canon.meap.service.login.event.UserEventListener;

/**
 * Box scan sample program, BoxScan applet class
 *
 * @version     0.01  2002/06/18
 * @author
 */
public class BoxScanApplet extends Applet {

    /**
	 * version ID for serialized form.
	 */
	private static final long serialVersionUID = -3093283682069856894L;

	/* Bundle */
    private Bundle bundle;

    /* BoxScanApplet */
    private static BoxScanApplet boxScanApplet;

    /* Folder list window class  */
    private FolderListPanel folderListPanel;

    /* Login EventListener */
    private UserEventAdapter userEventAdapter;

    /* LoginContext */
    private LoginContext loginContext;

    /* state of logout */
    private boolean logoutState;

    /**
     * Constructor
     *
     * @param   bundle Bundle
     */
    public BoxScanApplet(Bundle bundle) {
        super();

        this.bundle = bundle;
    }

    /**
     * Perform the initialization
     */
    public void init() {
        super.init();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);

        boxScanApplet = this;

        try {

            /* Initialize the IMI  */
            IMI.initialize(bundle);

            /* Create and add the folder list window */
            folderListPanel = new FolderListPanel();
            folderListPanel.setLocation(0, 0);
            add(folderListPanel);

            /* Define the login eventListener  */
            addLoginEventListener();

        } catch (Throwable se) {
            System.out.println(se.getMessage());
        }

        return;
    }

    /**
     * Perform the End process
     */
    public void destroy() {
        super.destroy();

        /* End the applet */
        if (logoutState == false) {
            stopApplet();
            logoutState = true;
        }

        /* Delete the login EventListener */
        removeLoginEventListener();

        folderListPanel = null;

        try {

        /* Terminate the IMI  */
        IMI.terminate(bundle);

        } catch (Throwable th) {
            System.out.println(th.getMessage());
        }

        return;
    }

    /**
     * Start the applet
     */
    private void startApplet() {

        /* Display the folder list window */
        folderListPanel.display();

        return;
    }

    /**
     * End the applet
     */
    private void stopApplet() {

        /* Invalidate the folder list window  */
        folderListPanel.unDisplay();

        return;
    }

    /**
     * Return Applet
     *
     * @return  BoxScanApplet
     */
    public static BoxScanApplet getBoxScanApplet() {
        return boxScanApplet;
    }

    /**
     * Return loginContext
     *
     * @return  LoginContext
     */
    public LoginContext getLoginContext() {
        return loginContext;
    }

    /**
     * Define the login eventListener
     */
    private void addLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* Acquire the appletActivator */
        appletActivator = AppletActivator.getAppletActivator();

        /* Acquire the LLS from AppletActivator */
        localLoginService = appletActivator.getLocalLoginService();

        /* Define the login eventListener  */
        userEventAdapter = new UserEventAdapter();
        localLoginService.addUserEventListener(userEventAdapter);

        return;
    }

    /**
     * Delete the login EventListener
     */
    private void removeLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* Acquire the appletActivator */
        appletActivator = AppletActivator.getAppletActivator();

        /* Acquire the LLS from AppletActivator */
        localLoginService = appletActivator.getLocalLoginService();

        /* Delete the login EventListener */
        localLoginService.removeUserEventListener(userEventAdapter);
        userEventAdapter = null;

        return;
    }


    /**
     *  Login/logout  event receiver class
     */
    public class UserEventAdapter implements UserEventListener {

        /**
         * Perform the process when login event occurs.
         *
         * @param   userEvent  User information
         */
        public void login(UserEvent userEvent) {

            /* Acquire the loginContext from userEvent  */
            loginContext = userEvent.getLoginContext();

            /* Start the applet */
            startApplet();
            logoutState = false;

            return;
        }

        /**
         * Perform the process when logout event occurs.
         *
         * @param   userEvent  User information
         */
        public void logout(UserEvent userEvent) {

            /* End the applet */
            stopApplet();
            logoutState = true;

            return;
        }

    }/* end class UserEventAdapter */


}/* end class BoxScanApplet */

/* end BoxScanApplet.java */
