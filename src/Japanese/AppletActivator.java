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
 * �a�n�w�X�L�����T���v���v���O�����@AppletActivator�N���X
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
     * �R���X�g���N�^
     */
    public AppletActivator() {

        appletActivator = this;

    }

    /**
     * �J�n�������s���܂�
     *
     * @param      bundleContext �o���h���R���e�L�X�g
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

            /* Applet���擾���܂� */
            applet = new BoxScanApplet(bundleContext.getBundle());

            /* �^�u�A�C�R���̃C���[�W�f�[�^�𐶐����܂� */
            classLoader = getClass().getClassLoader();
            url = classLoader.getResource(IMAGE_ICON);
            iconImageInfo = applet.getToolkit().getImage(url);

            /* AVS���擾���܂� */
            fetchAppletViewerService();

            /* LLS���擾���܂� */
            fetchLocalLoginService();

            /* SecurityAgent���擾���܂� */
            fetchSecurityAgent();

            /* AppletContext���擾���܂� */
            appletContext = appletViewerService.createDefaultAppletContext();

            /* AVS�ɖ{�A�v���b�g��o�^���܂� */
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
     * �I���������s���܂�
     *
     * @param      bundleContext �o���h���R���e�L�X�g
     *
     * @exception  Exception
     */
    public void stop(BundleContext bundleContext) throws Exception {

        boolean ungetStatus = false;

        /* �{�A�v���b�g��AVS����폜���܂� */
        try {
            appletViewerService.unregister("BoxScanApplet");
        } catch(Exception exception) {
            System.out.println("Unregister Applet failed." + exception);
            exception.printStackTrace();
            throw exception;
        }

        /* SecurityAgent��������܂� */
        ungetStatus = bundleContext.ungetService(saServiceReference);
        if (ungetStatus == false) {
            System.out.println("SecurityAgent ungetService failed.");
        }

        /* LLS��������܂� */
        ungetStatus = bundleContext.ungetService(llsServiceReference);
        if (ungetStatus == false) {
            System.out.println("LLS ungetService failed.");
        }

        /* AVS��������܂� */
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
     * AVS���擾���܂�
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

        /* OSGi�t���[�����[�N����AVS���擾���܂� */
        appletViewerService = (AppletViewerService)
                bundleContext.getService(avsServiceReference);
        if (appletViewerService == null) {
            System.out.println("AVS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * LLS���擾���܂�
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

        /* OSGi�t���[�����[�N����LLS���擾���܂� */
        localLoginService = (LocalLoginService)
                bundleContext.getService(llsServiceReference);
        if (localLoginService == null) {
            System.out.println("LLS getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * SecurityAgent���擾���܂�
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

        /* OSGi�t���[�����[�N����SecurityAgent���擾���܂� */
        securityAgent
                = (SecurityAgent)bundleContext.getService(saServiceReference);
        if (securityAgent == null) {
            System.out.println("SecurityAgent getService failed.");
            throw new Exception("");
        }

        return;
    }

    /**
     * AppletActivator��Ԃ��܂�
     *
     * @return  AppletActivator
     */
    public static AppletActivator getAppletActivator() {
        return appletActivator;
    }

    /**
     * LLS��Ԃ��܂�
     *
     * @return  LocalLoginService
     */
    public LocalLoginService getLocalLoginService() {
        return localLoginService;
    }

    /**
     * SecurityAgent��Ԃ��܂�
     *
     * @return  SecurityAgent
     */
    public SecurityAgent getSecurityAgent() {
        return securityAgent;
    }

}/* end class AppletActivator */

/* end AppletActivator.java */
