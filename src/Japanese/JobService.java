//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// JobService.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�W���u�T�[�r�X�N���X
 *
 * @version     2.01  2004/04/27
 * @author
 */
public class JobService {

    /* AccessControlToken */
    public AccessControlToken accessControlToken;

    /**
     * �R���X�g���N�^
     */
    public JobService() {
        super();

        try {
            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();
        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * �W���u�T�[�r�X�̏�Ԃ��擾���܂�
     *
     * @return  �W���u�T�[�r�X�̏��
     */
    public short getJobServiceState() {
        return 0;
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
     * �W���u�T�[�r�X�̏�Ԃ𕶎���\���ɕϊ����܂�
     *
     * @param   state �W���u�T�[�r�X�̏��
     *
     * @return  �W���u�T�[�r�X�̏�Ԃ̕�����\��
     */
    public String toJobServiceStateString(short state) {
        return null;
    }

    /**
     * �W���u�������\���ǂ����𒲂ׂ܂�
     *
     * @return  �����\�ȏꍇ�� true�A�����s�\�ȏꍇ�� false
     */
    public boolean isSendAvailable() {

        try {

            /* �W���u�Ǘ����擾���܂� */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator._bundle,
                                                  accessControlToken);

            /* �W���u�������\���ǂ����𒲂ׂ܂� */
            return manager.isSendAvailable(accessControlToken);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return false;
    }

}/* end class JobService */

/* end JobService.java */
