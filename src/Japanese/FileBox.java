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
 * ＢＯＸスキャンサンプルプログラム　ファイルボックスクラス
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FileBox {

    /* フォルダ情報クラス */
    private FolderInfo folderInfo;

    /* ファイルボックスオブジェクト */
    private UserBox objectHandle;

    /* ファイルボックス番号 */
    private int fileBoxNo;

    /* 選択フォルダ番号 */
    private int selectFolderNo;

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * コンストラクタ
     */
    public FileBox() {
        super();

    }

    /**
     * ファイルボックスを活性化します
     *
     * @exception  OperationFailureException オペレーション例外
     */
    public void activate() throws OperationFailureException {

        fileBoxNo = Integer.MAX_VALUE;
        selectFolderNo = Integer.MAX_VALUE;

        try {

            /* AccessControlTokenを取得します */
            fetchAccessControlToken();

            /* ファイルボックスを選定します */
            selectFileBox();

            /* フォルダ情報を生成します */
            folderInfo = new FolderInfo();

            /* フォルダ情報を更新します */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * ファイルボックスを終了します
     *
     * @exception  OperationFailureException オペレーション例外
     *
    public void deactivate() throws OperationFailureException {
    }*/

    /**
     * AccessControlTokenを取得します
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private void fetchAccessControlToken() throws OperationFailureException {

        BoxScanApplet boxScanApplet = null;
        LoginContext loginContext = null;
        AppletActivator appletActivator = null;
        SecurityAgent securityAgent = null;

        /* Appletを取得します */
        boxScanApplet = BoxScanApplet.getBoxScanApplet();

        /* AppletからLoginContextを取得します */
        loginContext = boxScanApplet.getLoginContext();

        /* AppletActivatorを取得します */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivatorからSecurityAgentを取得します */
        securityAgent = appletActivator.getSecurityAgent();

        /* SecurityAgentからAccessControlTokenを取得します */
        accessControlToken = securityAgent.getAccessControlToken(loginContext);

        if (accessControlToken == null) {
            throw new OperationFailureException("ACT is null. ");
        }

        return;
    }

    /**
     * ファイルボックスを選定します
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private void selectFileBox() throws OperationFailureException {

        UserBox[] outParamListObjects2 = null;

        try {

            /* ファイルボックスリストを取得します */
            outParamListObjects2 = fetchFileBoxList();

            /* ファイルボックスが実装されていれば選定を行います */
            if (outParamListObjects2.length > 0) {

                /* ファイルボックスを選定します */
                choiceFileBox(outParamListObjects2);

                /* ファイルボックス選択結果をチェックします */
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
     * ファイルボックスリストを取得します
     *
     * @return     ファイルボックスリスト
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private UserBox[] fetchFileBoxList()
            throws OperationFailureException {

        UserBox[] operationResult = null;

        /* ファイルボックス情報リスト取得オペレーションを投入します */
        try {
            /* ボックス管理を取得します */
            BoxManager manager =
                        BoxManager.getInstance(AppletActivator._bundle,
                                               accessControlToken);
            /* ユーザボックス一覧を取得します */
            operationResult = manager.getUserBoxList(AppletActivator._bundle,
                                                     accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return operationResult;
    }

    /**
     * ファイルボックス属性リストを取得します
     *
     * @param      outParamListFiles ファイルボックスリスト
     *
     * @return     ファイルボックス属性リスト
     *
     * @exception  OperationFailureException オペレーション例外
     *
    private ObjAttribListInfo2[] fetchFileBoxAttribute(
            OutParamListObjects2 outParamListObjects2)
            throws OperationFailureException {
    }*/

    /**
     * ファイルボックスを選定します
     *
     * @param  objAttribListInfo ファイルボックス属性リスト
     */
    private void choiceFileBox(UserBox[] objAttribListInfo2) {

        /* ファイルボックスを選定します */
        for (int i = 0; (i < objAttribListInfo2.length)
                && (fileBoxNo == Integer.MAX_VALUE); i++) {

            try {

                /* パスワード無しであれば決定します */
                if (!objAttribListInfo2[i].isPasswordProtected(
                                                accessControlToken)) {

                /* ファイルボックス番号を設定します */
                fileBoxNo = i;

                /* ファイルボックスオブジェクトを設定します */
                objectHandle = objAttribListInfo2[i];

                    break;
                }

            } catch (Throwable e) {
            }
        }

        return;
    }

    /**
     * ファイルボックス属性値を返します
     *
     * @param   attribListInfo ファイルボックス属性リスト
     * @param   attribute ファイルボックス属性
     *
     * @return  ファイルボックス属性値
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * ファイルボックス内容変更イベントを登録します
     *
     * @exception  OperationFailureException オペレーション例外
     *
    private void appendFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * ファイルボックス内容変更イベントを抹消します
     *
     * @exception  OperationFailureException オペレーション例外
     *
    private void deleteFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * ファイルボックスオブジェクトを返します
     *
     * @return  ファイルボックスオブジェクト
     */
    public UserBox getObjectHandle() {
        return objectHandle;
    }

    /**
     * フォルダ情報を更新します
     *
     * @exception  OperationFailureException オペレーション例外
     */
    public void updateFolderInfo() throws OperationFailureException {

        try {

            /* AccessControlTokenを取得します */
            fetchAccessControlToken();

            /* フォルダ情報を更新します */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * フォルダオブジェクトを削除します
     *
     * @exception  OperationFailureException オペレーション例外
     */
    public void deleteFolder() throws OperationFailureException {

        try {

            /* AccessControlTokenを取得します */
            fetchAccessControlToken();

            /* フォルダオブジェクトを削除します */
            folderInfo.deleteFolder(
                    folderInfo.getObjectHandle(selectFolderNo),
                    accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * 選択フォルダ番号を返します
     *
     * @return  選択フォルダ番号
     */
    public int getSelectFolderNo() {
        return selectFolderNo;
    }

    /**
     * 選択フォルダ番号を設定します
     *
     * @param  folderNo 選択フォルダ番号
     */
    public void setSelectFolderNo(int folderNo) {

        selectFolderNo = folderNo;

        return;
    }

    /**
     * 選択フォルダ番号を初期化します
     */
    public void resetSelectFolderNo() {

        selectFolderNo = Integer.MAX_VALUE;

        return;
    }

    /**
     * フォルダの選択状態を返します
     *
     * @return  フォルダ選択状態
     */
    public boolean isSelected() {

        if (selectFolderNo != Integer.MAX_VALUE) {
            return true;
        }

        return false;
    }

    /**
     * ファイルボックス番号を返します
     *
     * @return  ファイルボックス番号
     */
    public int getFileBoxNo() {
        return fileBoxNo;
    }

    /**
     * フォルダ数を返します
     *
     * @return  フォルダ数
     */
    public int getFolderCount() {
        return folderInfo.getFolderCount();
    }

    /**
     * フォルダオブジェクトを返します
     *
     * @return  フォルダオブジェクト
     */
    public UserBoxDocument getFolderObjectHandle() {
        return folderInfo.getObjectHandle(selectFolderNo);
    }

    /**
     * フォルダ名を返します
     *
     * @return  フォルダ名
     */
    public String getFolderName(int folderNo) {
        return folderInfo.getFolderName(folderNo);
    }

    /**
     * ページ数を返します
     *
     * @return  ページ数
     */
    public long getPageSize(int folderNo) {
        return folderInfo.getPageSize(folderNo);
    }

    /**
     * 日付・時刻を返します
     *
     * @return  日付・時刻
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderInfo.getTimeStamp(folderNo);
    }

}/* end class FileBox */

/* end FileBox.java */
