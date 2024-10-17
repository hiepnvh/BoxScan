//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// FolderInfo.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.util.Calendar;

import com.canon.meap.imi.InvalidPasswordException;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.box.userbox.UserBoxDocument;
import com.canon.meap.imi.data.UserBoxDocumentList;
import com.canon.meap.security.AccessControlToken;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�t�H���_���N���X
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FolderInfo {

    /* �t�H���_�����N���X */
    private FolderAttribute[] folderAttribute;

    /* �t�H���_�� */
    private int folderCount;

    /**
     * �R���X�g���N�^
     */
    public FolderInfo() {
        super();

    }

    /**
     * �t�H���_�����X�V���܂�
     *
     * @param      fileBoxObjectHandle �t�@�C���{�b�N�X�I�u�W�F�N�g
     * @param      accessControlToken
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    public void updateFolderInfo(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList outParamListObjects2 = null;

        folderAttribute = null;
        folderCount = 0;

        try {

            /* �t�H���_���X�g���擾���܂� */
            outParamListObjects2 = fetchFolderList(
                    fileBoxObjectHandle, accessControlToken);

            /* �t�H���_���i�[����Ă���΃t�H���_�������X�V���܂� */
            if (outParamListObjects2.size() > 0) {

                /* �t�H���_�����N���X�𐶐����܂� */
                folderAttribute
                        = new FolderAttribute[outParamListObjects2.size()];
                folderCount = outParamListObjects2.size();

                /* �t�H���_������ݒ肵�܂� */
                setFolderAttribute(outParamListObjects2, accessControlToken);
            }

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �t�H���_���X�g���擾���܂�
     *
     * @param      fileBoxObjectHandle �t�@�C���{�b�N�X�I�u�W�F�N�g
     * @param      accessControlToken
     *
     * @return     �t�H���_���X�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    private UserBoxDocumentList fetchFolderList(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList userBoxDocumentList = null;

        /* �t�@�C���{�b�N�X��񃊃X�g�擾�I�y���[�V�����𓊓����܂� */
        try {
            /* ���[�U�{�b�N�X�����̈ꗗ���擾���܂� */
            userBoxDocumentList = fileBoxObjectHandle.getDocumentList(
                    accessControlToken);

        } catch(InvalidPasswordException ce) {
            throw new OperationFailureException(
                    "getDocumentList operation failed. " + ce.getMessage());
        } catch (OperationFailureException oe) {
            throw oe;
        }

        return userBoxDocumentList;
    }

    /**
     * �t�H���_�������X�g���擾���܂�
     *
     * @param      outParamListFiles �t�H���_���X�g
     * @param      accessControlToken
     *
     * @return     �t�H���_�������X�g
     *
     * @exception  OperationFailureException �I�y���[�V������O
     *
    private ObjAttribListInfo2[] fetchFolderAttribute(
            OutParamListObjects2 outParamListObjects2,
            AccessControlToken accessControlToken)
            throws OperationFailureException {
    }*/

    /**
     * �t�H���_������ݒ肵�܂�
     *
     * @param  objAttribListInfo �t�H���_�������X�g
     */
    private void setFolderAttribute(UserBoxDocumentList objAttribListInfo2,
                                    AccessControlToken accessControlToken) {

        String stringFolderName = null;

        /* �t�H���_������ݒ肵�܂� */
        for (int i = 0; i < objAttribListInfo2.size(); i++) {

            folderAttribute[i] = new FolderAttribute();

            /* �t�H���_�I�u�W�F�N�g */
            folderAttribute[i].objectHandle = objAttribListInfo2.get(i);

            try {
            /* �t�H���_�� */
                stringFolderName =
                    objAttribListInfo2.get(i).getDocumentName(
                            accessControlToken);

                /* �y�[�W�� */
                folderAttribute[i].pageSize =
                    objAttribListInfo2.get(i).getPageCount(
                            accessControlToken);

                /* ���t�E���� */
                folderAttribute[i].timeStamp =
                    objAttribListInfo2.get(i).getCreateDate(
                            accessControlToken);

            } catch (Throwable e) {
            }

            if (stringFolderName != null) {
                folderAttribute[i].folderName = stringFolderName;
            } else {
                folderAttribute[i].folderName = "";
            }
        }

        return;
    }

    /**
     * �t�H���_�����l��Ԃ��܂�
     *
     * @param   objAttribListInfo �t�H���_�������X�g
     * @param   attribute �t�H���_����
     *
     * @return  �t�H���_�����l
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * �t�H���_�I�u�W�F�N�g���폜���܂�
     *
     * @param   folderObjectHandle �t�H���_�I�u�W�F�N�g
     * @param   accessControlToken
     *
     * @exception  OperationFailureException �I�y���[�V������O
     */
    public void deleteFolder(UserBoxDocument folderObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        /* �t�@�C���폜�I�y���[�V�����𓊓����܂� */
        try {

            /* �i�[��{�b�N�X���擾���� */
            UserBox box = (UserBox)folderObjectHandle.getParentBox(
                                                    accessControlToken);

            /* ���[�U�[�{�b�N�X�����̈ꗗ�𐶐����� */
            UserBoxDocumentList list = new UserBoxDocumentList();

            /* ���[�U�[�{�b�N�X������ǉ����� */
            list.add(folderObjectHandle);

            /* ���[�U�[�{�b�N�X�����̈ꗗ���폜���� */
            box.deleteDocument(accessControlToken, list);

        } catch (InvalidPasswordException ipe) {
            throw new OperationFailureException("Invalid password.");

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * �t�H���_����Ԃ��܂�
     *
     * @return  �t�H���_��
     */
    public int getFolderCount() {
        return folderCount;
    }

    /**
     * �t�H���_�I�u�W�F�N�g��Ԃ��܂�
     *
     * @param   folderNo �t�H���_�ԍ�
     *
     * @return  �t�H���_�I�u�W�F�N�g
     */
    public UserBoxDocument getObjectHandle(int folderNo) {
        return folderAttribute[folderNo].objectHandle;
    }

    /**
     * �t�H���_����Ԃ��܂�
     *
     * @param   folderNo �t�H���_�ԍ�
     *
     * @return  �t�H���_��
     */
    public String getFolderName(int folderNo) {
        return folderAttribute[folderNo].folderName;
    }

    /**
     * �y�[�W����Ԃ��܂�
     *
     * @param   folderNo �t�H���_�ԍ�
     *
     * @return  �y�[�W��
     */
    public long getPageSize(int folderNo) {
        return folderAttribute[folderNo].pageSize;
    }

    /**
     * ���t�E������Ԃ��܂�
     *
     * @param   folderNo �t�H���_�ԍ�
     *
     * @return  ���t�E����
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderAttribute[folderNo].timeStamp;
    }

}/* end class FolderInfo */

/* end FolderInfo.java */
