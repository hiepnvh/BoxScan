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
 * �a�n�w�X�L�����T���v���v���O�����@�A�v���b�g�N���X
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

    /* �t�H���_���X�g��ʃN���X */
    private FolderListPanel folderListPanel;

    /* ���O�C��EventListener */
    private UserEventAdapter userEventAdapter;

    /* LoginContext */
    private LoginContext loginContext;

    /* ���O�A�E�g��� */
    private boolean logoutState;

    /**
     * �R���X�g���N�^
     *
     * @param   bundle Bundle
     */
    public BoxScanApplet(Bundle bundle) {
        super();

        this.bundle = bundle;
    }

    /**
     * ���������s���܂�
     */
    public void init() {
        super.init();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);

        boxScanApplet = this;

        try {

            /* IMI�����������܂� */
            IMI.initialize(bundle);

            /* �t�H���_���X�g��ʂ𐶐��A�ǉ����܂� */
            folderListPanel = new FolderListPanel();
            folderListPanel.setLocation(0, 0);
            add(folderListPanel);

            /* ���O�C��EventListener���`���܂� */
            addLoginEventListener();

        } catch (Throwable se) {
            System.out.println(se.getMessage());
        }

        return;
    }

    /**
     * �I���������s���܂�
     */
    public void destroy() {
        super.destroy();

        /* �A�v���b�g���I�����܂� */
        if (logoutState == false) {
            stopApplet();
            logoutState = true;
        }

        /* ���O�C��EventListener���폜���܂� */
        removeLoginEventListener();

        folderListPanel = null;

        try {

        /* IMI���I�����܂� */
        IMI.terminate(bundle);

        } catch (Throwable th) {
            System.out.println(th.getMessage());
        }

        return;
    }

    /**
     * �A�v���b�g���J�n���܂�
     */
    private void startApplet() {

        /* �t�H���_���X�g��ʂ�\�����܂� */
        folderListPanel.display();

        return;
    }

    /**
     * �A�v���b�g���I�����܂�
     */
    private void stopApplet() {

        /* �t�H���_���X�g��ʂ𖳌������܂� */
        folderListPanel.unDisplay();

        return;
    }

    /**
     * Applet��Ԃ��܂�
     *
     * @return  BoxScanApplet
     */
    public static BoxScanApplet getBoxScanApplet() {
        return boxScanApplet;
    }

    /**
     * LoginContext��Ԃ��܂�
     *
     * @return  LoginContext
     */
    public LoginContext getLoginContext() {
        return loginContext;
    }

    /**
     * ���O�C��EventListener���`���܂�
     */
    private void addLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* AppletActivator���擾���܂� */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivator����LLS���擾���܂� */
        localLoginService = appletActivator.getLocalLoginService();

        /* ���O�C��EventListener���`���܂� */
        userEventAdapter = new UserEventAdapter();
        localLoginService.addUserEventListener(userEventAdapter);

        return;
    }

    /**
     * ���O�C��EventListener���폜���܂�
     */
    private void removeLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* AppletActivator���擾���܂� */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivator����LLS���擾���܂� */
        localLoginService = appletActivator.getLocalLoginService();

        /* ���O�C��EventListener���폜���܂� */
        localLoginService.removeUserEventListener(userEventAdapter);
        userEventAdapter = null;

        return;
    }


    /**
     *  ���O�C���^���O�A�E�g�C�x���g��M�N���X
     */
    public class UserEventAdapter implements UserEventListener {

        /**
�@       * ���O�C���C�x���g�������̏������s���܂�
         *
         * @param   userEvent ���[�U���
         */
        public void login(UserEvent userEvent) {

            /* UserEvent����LoginContext���擾���܂� */
            loginContext = userEvent.getLoginContext();

            /* �A�v���b�g���J�n���܂� */
            startApplet();
            logoutState = false;

            return;
        }

        /**
�@       * ���O�A�E�g�C�x���g�������̏������s���܂�
         *
         * @param   userEvent ���[�U���
         */
        public void logout(UserEvent userEvent) {

            /* �A�v���b�g���I�����܂� */
            stopApplet();
            logoutState = true;

            return;
        }

    }/* end class UserEventAdapter */


}/* end class BoxScanApplet */

/* end BoxScanApplet.java */
