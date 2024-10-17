//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// PrintJob.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnacceptableException;
import com.canon.meap.imi.box.userbox.UserBoxDocument;
import com.canon.meap.imi.data.AutoFeedTray;
import com.canon.meap.imi.data.Copies;
import com.canon.meap.imi.data.FeedTray;
import com.canon.meap.imi.data.TrayType;
import com.canon.meap.imi.data.UserBoxDocumentList;
import com.canon.meap.imi.data.UserFeedTray;
import com.canon.meap.imi.device.tray.Tray;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManager;
import com.canon.meap.imi.job.boxprint.BoxPrintRequest;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�v�����g�W���u�N���X
 *
 * @version     1.01  2004/06/16
 * @author
 */
public class PrintJob {

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * �R���X�g���N�^
     */
    public PrintJob() {
        super();

    }

    /**
     * �v�����g���J�n���܂�
     *
     * @param  folderObjectHandle �t�H���_�I�u�W�F�N�g
     */
    public void startPrint(UserBoxDocument folderObjectHandle) {

        try {

            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();

            /* �W���u�X�N���v�g�𓊓����܂� */
            performJobScript(folderObjectHandle);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return;
    }

    /**
     * �v�����g���I�����܂�
     */
    public void endPrint() {
    }

    /**
     * AccessControlToken���擾���܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private void fetchAccessControlToken() throws OperationFailureException {

        BoxScanApplet boxScanApplet = null;
        LoginContext loginContext = null;
        AppletActivator appletActivator = null;
        SecurityAgent securityAgent = null;

        /* Applet���擾���܂� */
        boxScanApplet = BoxScanApplet.getBoxScanApplet();

        /* Applet����LoginContext���擾���܂� */
        loginContext = boxScanApplet.getLoginContext();

        /* AppletActivator���擾���܂� */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivator����SecurityAgent���擾���܂� */
        securityAgent = appletActivator.getSecurityAgent();

        /* SecurityAgent����AccessControlToken���擾���܂� */
        accessControlToken = securityAgent.getAccessControlToken(loginContext);

        if (accessControlToken == null) {
            throw new OperationFailureException("ACT is null. ");
        }

        return;
    }


    /**
     * �W���u�X�N���v�g�𓊓����܂�
     *
     * @param      folderObjectHandle �t�H���_�I�u�W�F�N�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private void performJobScript(UserBoxDocument folderObjectHandle)
            throws OperationFailureException {

        try {

            /*
             * �{�b�N�X�v�����g�@�\�ւ̃��N�G�X�g���s�����߂̃C���X�^���X��
             * �������܂�
             */
            BoxPrintRequest request =
                    BoxPrintRequest.createInstance(accessControlToken);

            /* �{�b�N�X�����̈ꗗ�𐶐����܂� */
            UserBoxDocumentList list = new UserBoxDocumentList();

            /* �{�b�N�X������ǉ����܂� */
            list.add(folderObjectHandle);

            /* ������镶����ݒ肵�܂� */
            request.setPrintDocument(accessControlToken, list);

            /* �u�R�s�[��(Copies)�v��ݒ肵�܂� */
            request.setCopies(accessControlToken, getNumberOfCopies());

            /* �u�o�̓��[�h(PrintEjecter)�v��ݒ肵�܂� */
            /* request.setPrintEjecter(accessControlToken, getOutput()); */

            /* �u�����g���C(FeedTray)�v��ݒ肵�܂� */
            request.setFeedTray(accessControlToken, getInputTray());

            /* �W���u�Ǘ����擾���܂� */
            BoxPrintJobManager manager =
                    BoxPrintJobManager.getInstance(AppletActivator._bundle,
                                                   accessControlToken);
            /* �W���u�𓊓����܂� */
            manager.send(accessControlToken, request);

        } catch (UnacceptableException ce) {
            throw new OperationFailureException(
                    " operation failed. " + ce.getMessage());
        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     *�u�ʒm��ƃC�x���g(id_att_notification_profile)�v�̐ݒ��Ԃ��܂�
     *
     * @return �u�ʒm��ƃC�x���g�v�̐ݒ�
     *
    private NotificationProfile[] getNotificationProfileList() {
    }*/

    /**
     *�u�C���v���b�V������(id_att_impressions_2)�v�̐ݒ��Ԃ��܂�
     *
     * @return �u�C���v���b�V�������v�̐ݒ�
     *
    private Long getNumberOfimpressions() {
    }*/

    /**
     *�u�R�s�[��(id_att_copies)�v�̐ݒ��Ԃ��܂�
     *
     * @return �u�R�s�[���v�̐ݒ�
     */
    private Copies getNumberOfCopies() {
        return new Copies(1);
    }

    /**
     *�u�����g���C(id_att_input_tray)�v�̐ݒ��Ԃ��܂�
     *
     * @return �u�����g���C�v�̐ݒ�
     */
    private FeedTray getInputTray() {

        try {

            Tray[] tray = Tray.getInstance(AppletActivator._bundle,
                                           accessControlToken);

            for (int i = 0; i < tray.length; i ++) {

                TrayType trayType = tray[i].getTrayType(accessControlToken);

                if (trayType.getType() != TrayType.TYPE_CASSETTE) {
                    continue;
                }

                if (tray[i].getTrayNumber(accessControlToken) == 1) {
                    return new UserFeedTray(tray[i]);
                }
            }

        } catch (OperationFailureException oe) {
        }

        return new AutoFeedTray();
    }

    /**
     *�u���f�B�A(id_att_medium)�v�̐ݒ��Ԃ��܂�
     *
     * @return �u���f�B�A�v�̐ݒ�
     *
    private Medium getMedium() {
    }*/

}/* end class PrintJob */

/* end PrintJob.java */
