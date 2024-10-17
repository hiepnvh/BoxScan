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
 * ＢＯＸスキャンサンプルプログラム　スキャンジョブクラス
 *
 * @version     2.02  2004/06/21
 * @author
 */
public class ScanJob {

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * コンストラクタ
     */
    public ScanJob() {
        super();

    }

    /**
     * スキャンを開始します
     *
     * @param      fileBoxObjectHandle ファイルボックスオブジェクト
     */
    public boolean startScan(UserBox fileBoxObjectHandle) {

        try {

            /* AccessControlTokenを取得します */
            fetchAccessControlToken();

            /* ジョブスクリプトを投入します */
            performJobScript(fileBoxObjectHandle);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());

            return false;
        }

        return true;
    }

    /**
     * スキャンを終了します
     */
    public void endScan() {
    }

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
     * ジョブスクリプトを投入します
     *
     * @param      fileBoxObjectHandle ファイルボックスオブジェクト
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private void performJobScript(UserBox fileBoxObjectHandle)
            throws OperationFailureException {

        try {

            /*
             * ボックススキャン機能へのリクエストを行うためのインスタンスを
             * 生成します
             */
            BoxScanRequest request =
                    BoxScanRequest.createInstance(accessControlToken);

            /* 保存先のボックスを設定します */
            request.setBox(accessControlToken, fileBoxObjectHandle);

            /* 原稿サイズを設定します */
            request.setScanSize(
                accessControlToken,
                new StandardSize(
                    new StandardSizeId(StandardSizeId.SIZE_ISO_A4),
                    new Orientation(Orientation.ORIENTATION_LONG_EDGE_FEED)));

            /* ジョブ管理を取得します */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator._bundle,
                                                  accessControlToken);
            /* ジョブを投入します */
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
     *「通知先とイベント(id_att_notification_profile)」の設定を返します
     *
     * @return 「通知先とイベント」の設定
     *
    private NotificationProfile[] getNotificationProfileList() {
    }*/


}/* end class ScanJob */

/* end ScanJob.java */
