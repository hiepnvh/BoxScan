//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// ScanJob.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnacceptableException;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.data.Orientation;
import com.canon.meap.imi.data.StandardSize;
import com.canon.meap.imi.data.StandardSizeId;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�X�L�����W���u�N���X
 *
 * @version     2.02  2004/06/21
 * @author
 */
public class ScanJob {

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * �R���X�g���N�^
     */
    public ScanJob() {
        super();

    }

    /**
     * �X�L�������J�n���܂�
     *
     * @param      fileBoxObjectHandle �t�@�C���{�b�N�X�I�u�W�F�N�g
     */
    public boolean startScan(UserBox fileBoxObjectHandle) {

        try {

            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();

            /* �W���u�X�N���v�g�𓊓����܂� */
            performJobScript(fileBoxObjectHandle);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());

            return false;
        }

        return true;
    }

    /**
     * �X�L�������I�����܂�
     */
    public void endScan() {
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
     * @param      fileBoxObjectHandle �t�@�C���{�b�N�X�I�u�W�F�N�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private void performJobScript(UserBox fileBoxObjectHandle)
            throws OperationFailureException {

        try {

            /*
             * �{�b�N�X�X�L�����@�\�ւ̃��N�G�X�g���s�����߂̃C���X�^���X��
             * �������܂�
             */
            BoxScanRequest request =
                    BoxScanRequest.createInstance(accessControlToken);

            /* �ۑ���̃{�b�N�X��ݒ肵�܂� */
            request.setBox(accessControlToken, fileBoxObjectHandle);

            /* ���e�T�C�Y��ݒ肵�܂� */
            request.setScanSize(
                accessControlToken,
                new StandardSize(
                    new StandardSizeId(StandardSizeId.SIZE_ISO_A4),
                    new Orientation(Orientation.ORIENTATION_LONG_EDGE_FEED)));

            /* �W���u�Ǘ����擾���܂� */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator._bundle,
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


}/* end class ScanJob */

/* end ScanJob.java */
