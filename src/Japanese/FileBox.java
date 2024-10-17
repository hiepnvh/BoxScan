//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// FileBox.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.util.Calendar;

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.box.BoxManager;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.box.userbox.UserBoxDocument;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�t�@�C���{�b�N�X�N���X
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FileBox {

    /* �t�H���_���N���X */
    private FolderInfo folderInfo;

    /* �t�@�C���{�b�N�X�I�u�W�F�N�g */
    private UserBox objectHandle;

    /* �t�@�C���{�b�N�X�ԍ� */
    private int fileBoxNo;

    /* �I���t�H���_�ԍ� */
    private int selectFolderNo;

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * �R���X�g���N�^
     */
    public FileBox() {
        super();

    }

    /**
     * �t�@�C���{�b�N�X�����������܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    public void activate() throws OperationFailureException {

        fileBoxNo = Integer.MAX_VALUE;
        selectFolderNo = Integer.MAX_VALUE;

        try {

            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();

            /* �t�@�C���{�b�N�X��I�肵�܂� */
            selectFileBox();

            /* �t�H���_���𐶐����܂� */
            folderInfo = new FolderInfo();

            /* �t�H���_�����X�V���܂� */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �t�@�C���{�b�N�X���I�����܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     *
    public void deactivate() throws OperationFailureException {
    }*/

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
     * �t�@�C���{�b�N�X��I�肵�܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private void selectFileBox() throws OperationFailureException {

        UserBox[] outParamListObjects2 = null;

        try {

            /* �t�@�C���{�b�N�X���X�g���擾���܂� */
            outParamListObjects2 = fetchFileBoxList();

            /* �t�@�C���{�b�N�X����������Ă���ΑI����s���܂� */
            if (outParamListObjects2.length > 0) {

                /* �t�@�C���{�b�N�X��I�肵�܂� */
                choiceFileBox(outParamListObjects2);

                /* �t�@�C���{�b�N�X�I�����ʂ��`�F�b�N���܂� */
                if (fileBoxNo == Integer.MAX_VALUE) {
                    throw new OperationFailureException(
                            "FileBox cannot be used");
                }

            } else {
                throw new OperationFailureException("There is no FileBox");
            }

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �t�@�C���{�b�N�X���X�g���擾���܂�
     *
     * @return     �t�@�C���{�b�N�X���X�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private UserBox[] fetchFileBoxList()
            throws OperationFailureException {

        UserBox[] operationResult = null;

        /* �t�@�C���{�b�N�X��񃊃X�g�擾�I�y���[�V�����𓊓����܂� */
        try {
            /* �{�b�N�X�Ǘ����擾���܂� */
            BoxManager manager =
                        BoxManager.getInstance(AppletActivator._bundle,
                                               accessControlToken);
            /* ���[�U�{�b�N�X�ꗗ���擾���܂� */
            operationResult = manager.getUserBoxList(AppletActivator._bundle,
                                                     accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return operationResult;
    }

    /**
     * �t�@�C���{�b�N�X�������X�g���擾���܂�
     *
     * @param      outParamListFiles �t�@�C���{�b�N�X���X�g
     *
     * @return     �t�@�C���{�b�N�X�������X�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     *
    private ObjAttribListInfo2[] fetchFileBoxAttribute(
            OutParamListObjects2 outParamListObjects2)
            throws OperationFailureException {
    }*/

    /**
     * �t�@�C���{�b�N�X��I�肵�܂�
     *
     * @param  objAttribListInfo �t�@�C���{�b�N�X�������X�g
     */
    private void choiceFileBox(UserBox[] objAttribListInfo2) {

        /* �t�@�C���{�b�N�X��I�肵�܂� */
        for (int i = 0; (i < objAttribListInfo2.length)
                && (fileBoxNo == Integer.MAX_VALUE); i++) {

            try {

                /* �p�X���[�h�����ł���Ό��肵�܂� */
                if (!objAttribListInfo2[i].isPasswordProtected(
                                                accessControlToken)) {

                /* �t�@�C���{�b�N�X�ԍ���ݒ肵�܂� */
                fileBoxNo = i;

                /* �t�@�C���{�b�N�X�I�u�W�F�N�g��ݒ肵�܂� */
                objectHandle = objAttribListInfo2[i];

                    break;
                }

            } catch (Throwable e) {
            }
        }

        return;
    }

    /**
     * �t�@�C���{�b�N�X�����l��Ԃ��܂�
     *
     * @param   attribListInfo �t�@�C���{�b�N�X�������X�g
     * @param   attribute �t�@�C���{�b�N�X����
     *
     * @return  �t�@�C���{�b�N�X�����l
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * �t�@�C���{�b�N�X���e�ύX�C�x���g��o�^���܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     *
    private void appendFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * �t�@�C���{�b�N�X���e�ύX�C�x���g�𖕏����܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     *
    private void deleteFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * �t�@�C���{�b�N�X�I�u�W�F�N�g��Ԃ��܂�
     *
     * @return  �t�@�C���{�b�N�X�I�u�W�F�N�g
     */
    public UserBox getObjectHandle() {
        return objectHandle;
    }

    /**
     * �t�H���_�����X�V���܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    public void updateFolderInfo() throws OperationFailureException {

        try {

            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();

            /* �t�H���_�����X�V���܂� */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �t�H���_�I�u�W�F�N�g���폜���܂�
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    public void deleteFolder() throws OperationFailureException {

        try {

            /* AccessControlToken���擾���܂� */
            fetchAccessControlToken();

            /* �t�H���_�I�u�W�F�N�g���폜���܂� */
            folderInfo.deleteFolder(
                    folderInfo.getObjectHandle(selectFolderNo),
                    accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �I���t�H���_�ԍ���Ԃ��܂�
     *
     * @return  �I���t�H���_�ԍ�
     */
    public int getSelectFolderNo() {
        return selectFolderNo;
    }

    /**
     * �I���t�H���_�ԍ���ݒ肵�܂�
     *
     * @param  folderNo �I���t�H���_�ԍ�
     */
    public void setSelectFolderNo(int folderNo) {

        selectFolderNo = folderNo;

        return;
    }

    /**
     * �I���t�H���_�ԍ������������܂�
     */
    public void resetSelectFolderNo() {

        selectFolderNo = Integer.MAX_VALUE;

        return;
    }

    /**
     * �t�H���_�̑I����Ԃ�Ԃ��܂�
     *
     * @return  �t�H���_�I�����
     */
    public boolean isSelected() {

        if (selectFolderNo != Integer.MAX_VALUE) {
            return true;
        }

        return false;
    }

    /**
     * �t�@�C���{�b�N�X�ԍ���Ԃ��܂�
     *
     * @return  �t�@�C���{�b�N�X�ԍ�
     */
    public int getFileBoxNo() {
        return fileBoxNo;
    }

    /**
     * �t�H���_����Ԃ��܂�
     *
     * @return  �t�H���_��
     */
    public int getFolderCount() {
        return folderInfo.getFolderCount();
    }

    /**
     * �t�H���_�I�u�W�F�N�g��Ԃ��܂�
     *
     * @return  �t�H���_�I�u�W�F�N�g
     */
    public UserBoxDocument getFolderObjectHandle() {
        return folderInfo.getObjectHandle(selectFolderNo);
    }

    /**
     * �t�H���_����Ԃ��܂�
     *
     * @return  �t�H���_��
     */
    public String getFolderName(int folderNo) {
        return folderInfo.getFolderName(folderNo);
    }

    /**
     * �y�[�W����Ԃ��܂�
     *
     * @return  �y�[�W��
     */
    public long getPageSize(int folderNo) {
        return folderInfo.getPageSize(folderNo);
    }

    /**
     * ���t�E������Ԃ��܂�
     *
     * @return  ���t�E����
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderInfo.getTimeStamp(folderNo);
    }

}/* end class FileBox */

/* end FileBox.java */
