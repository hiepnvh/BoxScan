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
 * ＢＯＸスキャンサンプルプログラム　フォルダ情報クラス
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FolderInfo {

    /* フォルダ属性クラス */
    private FolderAttribute[] folderAttribute;

    /* フォルダ数 */
    private int folderCount;

    /**
     * コンストラクタ
     */
    public FolderInfo() {
        super();

    }

    /**
     * フォルダ情報を更新します
     *
     * @param      fileBoxObjectHandle ファイルボックスオブジェクト
     * @param      accessControlToken
     *
     * @exception  OperationFailureException オペレーション例外
     */
    public void updateFolderInfo(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList outParamListObjects2 = null;

        folderAttribute = null;
        folderCount = 0;

        try {

            /* フォルダリストを取得します */
            outParamListObjects2 = fetchFolderList(
                    fileBoxObjectHandle, accessControlToken);

            /* フォルダが格納されていればフォルダ属性を更新します */
            if (outParamListObjects2.size() > 0) {

                /* フォルダ属性クラスを生成します */
                folderAttribute
                        = new FolderAttribute[outParamListObjects2.size()];
                folderCount = outParamListObjects2.size();

                /* フォルダ属性を設定します */
                setFolderAttribute(outParamListObjects2, accessControlToken);
            }

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * フォルダリストを取得します
     *
     * @param      fileBoxObjectHandle ファイルボックスオブジェクト
     * @param      accessControlToken
     *
     * @return     フォルダリスト
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private UserBoxDocumentList fetchFolderList(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList userBoxDocumentList = null;

        /* ファイルボックス情報リスト取得オペレーションを投入します */
        try {
            /* ユーザボックス文書の一覧を取得します */
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
     * フォルダ属性リストを取得します
     *
     * @param      outParamListFiles フォルダリスト
     * @param      accessControlToken
     *
     * @return     フォルダ属性リスト
     *
     * @exception  OperationFailureException オペレーション例外
     *
    private ObjAttribListInfo2[] fetchFolderAttribute(
            OutParamListObjects2 outParamListObjects2,
            AccessControlToken accessControlToken)
            throws OperationFailureException {
    }*/

    /**
     * フォルダ属性を設定します
     *
     * @param  objAttribListInfo フォルダ属性リスト
     */
    private void setFolderAttribute(UserBoxDocumentList objAttribListInfo2,
                                    AccessControlToken accessControlToken) {

        String stringFolderName = null;

        /* フォルダ属性を設定します */
        for (int i = 0; i < objAttribListInfo2.size(); i++) {

            folderAttribute[i] = new FolderAttribute();

            /* フォルダオブジェクト */
            folderAttribute[i].objectHandle = objAttribListInfo2.get(i);

            try {
            /* フォルダ名 */
                stringFolderName =
                    objAttribListInfo2.get(i).getDocumentName(
                            accessControlToken);

                /* ページ数 */
                folderAttribute[i].pageSize =
                    objAttribListInfo2.get(i).getPageCount(
                            accessControlToken);

                /* 日付・時刻 */
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
     * フォルダ属性値を返します
     *
     * @param   objAttribListInfo フォルダ属性リスト
     * @param   attribute フォルダ属性
     *
     * @return  フォルダ属性値
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * フォルダオブジェクトを削除します
     *
     * @param   folderObjectHandle フォルダオブジェクト
     * @param   accessControlToken
     *
     * @exception  OperationFailureException オペレーション例外
     */
    public void deleteFolder(UserBoxDocument folderObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        /* ファイル削除オペレーションを投入します */
        try {

            /* 格納先ボックスを取得する */
            UserBox box = (UserBox)folderObjectHandle.getParentBox(
                                                    accessControlToken);

            /* ユーザーボックス文書の一覧を生成する */
            UserBoxDocumentList list = new UserBoxDocumentList();

            /* ユーザーボックス文書を追加する */
            list.add(folderObjectHandle);

            /* ユーザーボックス文書の一覧を削除する */
            box.deleteDocument(accessControlToken, list);

        } catch (InvalidPasswordException ipe) {
            throw new OperationFailureException("Invalid password.");

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * フォルダ数を返します
     *
     * @return  フォルダ数
     */
    public int getFolderCount() {
        return folderCount;
    }

    /**
     * フォルダオブジェクトを返します
     *
     * @param   folderNo フォルダ番号
     *
     * @return  フォルダオブジェクト
     */
    public UserBoxDocument getObjectHandle(int folderNo) {
        return folderAttribute[folderNo].objectHandle;
    }

    /**
     * フォルダ名を返します
     *
     * @param   folderNo フォルダ番号
     *
     * @return  フォルダ名
     */
    public String getFolderName(int folderNo) {
        return folderAttribute[folderNo].folderName;
    }

    /**
     * ページ数を返します
     *
     * @param   folderNo フォルダ番号
     *
     * @return  ページ数
     */
    public long getPageSize(int folderNo) {
        return folderAttribute[folderNo].pageSize;
    }

    /**
     * 日付・時刻を返します
     *
     * @param   folderNo フォルダ番号
     *
     * @return  日付・時刻
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderAttribute[folderNo].timeStamp;
    }

}/* end class FolderInfo */

/* end FolderInfo.java */
