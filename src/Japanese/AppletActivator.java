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
import com.canon.meap.service.login.*;
import com.canon.meap.service.sa.*;

/**
 * ＢＯＸスキャンサンプルプログラム　AppletActivatorクラス
 *
 * @version     2.01  2004/08/20
 * @author
 */
public class AppletActivator implements BundleActivator {

    private static AppletActivator appletActivator;

    private BundleContext bundleContext;
    private AppletContext appletContext;

    private ServiceReference avsServiceReference;
    private ServiceReference llsServiceReference;
    private ServiceReference saServiceReference;

    private AppletViewerService appletViewerService;
    private LocalLoginService localLoginService;
    private SecurityAgent securityAgent;

    private BoxScanApplet applet;

    private Image iconImageInfo;

    public static Bundle _bundle = null;

    /**
     * コンストラクタ
     */
    public AppletActivator() {

        appletActivator = this;

    }

    /**
     * 開始処理を行います
     *
     * @param      bundleContext バンドルコンテキスト
     *
     * @exception  Exception
     */
    public void start(BundleContext bundleContext) throws Exception {

        Properties properties = null;

        String IMAGE_ICON = "image/ICON.GIF";
        ClassLoader classLoader = null;
        URL url = null;

        this.bundleContext = bundleContext;

        _bundle = bundleContext.getBundle();

        try {

            /* Appletを取得します */
            applet = new BoxScanApplet(bundleContext.getBundle());

            /* タブアイコンのイメージデータを生成します */
            classLoader = getClass().getClassLoader();
            url = classLoader.getResource(IMAGE_ICON);
            iconImageInfo = applet.getToolkit().getImage(url);

            /* AVSを取得します */
            fetchAppletViewerService();

            /* LLSを取得します */
            fetchLocalLoginService();

            /* SecurityAgentを取得します */
            fetchSecurityAgent();

            /* AppletContextを取得します */
            appletContext = appletViewerService.createDefaultAppletContext();

            /* AVSに本アプレットを登録します */
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
     * 終了処理を行います
     *
     * @param      bundleContext バンドルコンテキスト
     *
     * @exception  Exception
     */
    public void stop(BundleContext bundleContext) throws Exception {

        boolean ungetStatus = false;

        /* 本アプレットをAVSから削除します */
        try {
            appletViewerService.unregister("BoxScanApplet");
        } catch(Exception exception) {
            System.out.println("Unregister Applet failed." + exception);
            exception.printStackTrace();
            throw exception;
        }

        /* SecurityAgentを解放します */
        ungetStatus = bundleContext.ungetService(saServiceReference);
        if (ungetStatus == false) {
            System.out.println("SecurityAgent ungetService failed.");
        }

        /* LLSを解放します */
        ungetStatus = bundleContext.ungetService(llsServiceReference);
        if (ungetStatus == false) {
            System.out.println("LLS ungetService failed.");
        }

        /* AVSを解放します */
        ungetStatus = bundleContext.ungetService(avsServiceReference);
        if (ungetStatus == false) {
            System.out.println("AVS ungetService failed.");
        }

        if (iconImageInfo != null) {
            iconImageInfo.flush();
            iconImageInfo = null;
        }
    }

    /**
     * AVSを取得します
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

        /* OSGiフレームワークからAVSを取得します */
        appletViewerService = (AppletViewerService)
                bundleContext.getService(avsServiceReference);
        if (appletViewerService == null) {
            System.out.println("AVS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * LLSを取得します
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

        /* OSGiフレームワークからLLSを取得します */
        localLoginService = (LocalLoginService)
                bundleContext.getService(llsServiceReference);
        if (localLoginService == null) {
            System.out.println("LLS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * SecurityAgentを取得します
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

        /* OSGiフレームワークからSecurityAgentを取得します */
        securityAgent
                = (SecurityAgent)bundleContext.getService(saServiceReference);
        if (securityAgent == null) {
            System.out.println("SecurityAgent getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * AppletActivatorを返します
     *
     * @return  AppletActivator
     */
    public static AppletActivator getAppletActivator() {
        return appletActivator;
    }

    /**
     * LLSを返します
     *
     * @return  LocalLoginService
     */
    public LocalLoginService getLocalLoginService() {
        return localLoginService;
    }

    /**
     * SecurityAgentを返します
     *
     * @return  SecurityAgent
     */
    public SecurityAgent getSecurityAgent() {
        return securityAgent;
    }

}/* end class AppletActivator */

/* end AppletActivator.java */
