//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// FolderAttribute.java
//
// MEAP SDK
//
// Version 2.10
//
//***************************************************************************

import java.util.Calendar;

import com.canon.meap.imi.box.userbox.*;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�t�H���_�����N���X
 *
 * @version     1.01  2004/09/01
 * @author
 */
public class FolderAttribute {

    /* �t�H���_�I�u�W�F�N�g */
    public UserBoxDocument objectHandle;

    /* �t�H���_�� */
    public String folderName;

    /* �y�[�W�� */
    public long pageSize;

    /* ���t�E���� */
    public Calendar timeStamp;

    /**
     * �R���X�g���N�^
     */
    public FolderAttribute() {
        super();

        objectHandle = null;
        folderName = "";
        pageSize = 0;
        timeStamp = null;
    }

}/* end class FolderAttribute */

/* end FolderAttribute.java */
