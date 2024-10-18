//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// AppletActivator.java
//
// MEAP SDK
//
// Version 2.10
//
//***************************************************************************

import java.awt.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;
import com.canon.meap.service.avs.*;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.login.*;
import com.canon.meap.service.sa.*;

/**
 * Box Scan sample program
 * AppletActivator class
 *
 * @version     2.01  2004/08/20
 * @author
 */
public class AppletActivator implements BundleActivator {

    private static AppletActivator appletActivator;

    public static BundleContext bundleContext;
    private AppletContext appletContext;

    private ServiceReference avsServiceReference;
    private ServiceReference llsServiceReference;
    private ServiceReference saServiceReference;

    private AppletViewerService appletViewerService;
    private LocalLoginService localLoginService;
    private SecurityAgent securityAgent;
    
    private ServiceReference logServiceRef;
    private LogService logServiceInstance;

    private BoxScanApplet applet;

    private Image iconImageInfo;

    /**
     * constructor
     */
    public AppletActivator() {

        appletActivator = this;

    }

    /**
     * Perform the start process
     *
     * @param      bundleContext  BundleContext
     *
     * @exception  Exception
     */
    public void start(BundleContext bundleContext) throws Exception {

        Properties properties = null;

        String IMAGE_ICON = "image/ICON.GIF";
        ClassLoader classLoader = null;
        URL url = null;

        this.bundleContext = bundleContext;

//        _bundle = bundleContext.getBundle();

        try {

            /* Acquire the applet */
            applet = new BoxScanApplet(bundleContext.getBundle());

            /* Create the image data of tab icon */
            classLoader = getClass().getClassLoader();
            url = classLoader.getResource(IMAGE_ICON);
            iconImageInfo = applet.getToolkit().getImage(url);

            /* Acquire the AVS */
            fetchAppletViewerService();

            /* Acquire the LLS */
            fetchLocalLoginService();

            /* Acquire the SecurityAgent */
            fetchSecurityAgent();
            
            fetchLogService();

            /* Acquire the AppletContext */
            appletContext = appletViewerService.createDefaultAppletContext();

            /* Register this applet to AVS */
            properties = new Properties();
            appletViewerService.registerApplet("BoxScanApplet", applet,
                    "sample7", iconImageInfo, properties, appletContext);

        } catch(Exception exception) {
            System.out.println("Register Applet failed." + exception);
            exception.printStackTrace();
            throw exception;
        }
    }

    /**
     * Perform the end process
     *
     * @param      bundleContext  BundleContext
     *
     * @exception  Exception
     */
    public void stop(BundleContext bundleContext) throws Exception {

        boolean ungetStatus = false;

        /* Delete this applet from AVS */
        try {
            appletViewerService.unregister("BoxScanApplet");
        } catch(Exception exception) {
            System.out.println("Unregister Applet failed." + exception);
            exception.printStackTrace();
            throw exception;
        }

        /* Release the SecurityAgent */
        ungetStatus = bundleContext.ungetService(saServiceReference);
        if (ungetStatus == false) {
            System.out.println("SecurityAgent ungetService failed.");
        }

        /* Release the LLS */
        ungetStatus = bundleContext.ungetService(llsServiceReference);
        if (ungetStatus == false) {
            System.out.println("LLS ungetService failed.");
        }

        /* Release the AVS */
        ungetStatus = bundleContext.ungetService(avsServiceReference);
        if (ungetStatus == false) {
            System.out.println("AVS ungetService failed.");
        }
        
        if (logServiceRef != null) {
          ungetStatus = bundleContext.ungetService(logServiceRef);
          if (!ungetStatus) {
              throw new IllegalArgumentException("");
          }
          logServiceRef = null;
          logServiceInstance = null;
      }

        if (iconImageInfo != null) {
            iconImageInfo.flush();
            iconImageInfo = null;
        }
    }

    /**
     * Acquire the AVS
     *
     * @exception  Exception
     */
    private void fetchAppletViewerService() throws Exception {

        String APPLET_VIEWER_SERVICE
                = "com.canon.meap.service.avs.AppletViewerService";

        avsServiceReference
                = bundleContext.getServiceReference(APPLET_VIEWER_SERVICE);
        if (avsServiceReference == null) {
            System.out.println("AVS getServiceReference failed.");
            throw new Exception("");
        }

        /* Acquire AVS from OSGi framework */
        appletViewerService = (AppletViewerService)
                bundleContext.getService(avsServiceReference);
        if (appletViewerService == null) {
            System.out.println("AVS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * Acquire the LLS
     *
     * @exception  Exception
     */
    private void fetchLocalLoginService() throws Exception {

        String LOCAL_LOGIN_SERVICE
                = "com.canon.meap.service.login.LocalLoginService";

        llsServiceReference
                = bundleContext.getServiceReference(LOCAL_LOGIN_SERVICE);
        if (llsServiceReference == null) {
            System.out.println("LLS getServiceReference failed.");
            throw new Exception("");
        }

        /* Acquire LLS from OSGi framework */
        localLoginService = (LocalLoginService)
                bundleContext.getService(llsServiceReference);
        if (localLoginService == null) {
            System.out.println("LLS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * Acquire the SecurityAgent
     *
     * @exception  Exception
     */
    private void fetchSecurityAgent() throws Exception {

        String SECURITY_AGENT
                = "com.canon.meap.service.sa.SecurityAgent";

        saServiceReference = bundleContext.getServiceReference(SECURITY_AGENT);
        if (saServiceReference == null) {
            System.out.println("SecurityAgent getServiceReference failed.");
            throw new Exception("");
        }

        /* Acquire SecurityAgent from OSGi framework */
        securityAgent
                = (SecurityAgent)bundleContext.getService(saServiceReference);
        if (securityAgent == null) {
            System.out.println("SecurityAgent getService failed.");
            throw new Exception("");
        }

        return;
    }
    
    /**
     * Obtains LogService
     *
     * @throws Exception
     * When an exception preventing continuation of processing occurred and service acquisition failed
     */
    private void fetchLogService() throws Exception {

        final String LOGSERVICE_NAME = LogService.class.getName();

        logServiceRef = bundleContext.getServiceReference(LOGSERVICE_NAME);
        if (logServiceRef == null) {
            throw new Exception("");
        }

        //Obtains LogService from OSGi framework
        logServiceInstance = (LogService) bundleContext.getService(logServiceRef);
        if (logServiceInstance == null) {
            throw new Exception("");
        }
        return;
    }

    /**
     * Return the AppletActivator
     *
     * @return  AppletActivator
     */
    public static AppletActivator getAppletActivator() {
        return appletActivator;
    }

    /**
     * Return the LLS
     *
     * @return  LocalLoginService
     */
    public LocalLoginService getLocalLoginService() {
        return localLoginService;
    }

    /**
     * Return the SecurityAgent
     *
     * @return  SecurityAgent
     */
    public SecurityAgent getSecurityAgent() {
        return securityAgent;
    }
    
    public LogService getLogService() {
      return logServiceInstance;
  }

}/* end class AppletActivator */

/* end AppletActivator.java */
