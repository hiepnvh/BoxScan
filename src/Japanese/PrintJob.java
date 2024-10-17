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
 * ＢＯＸスキャンサンプルプログラム　プリントジョブクラス
 *
 * @version     1.01  2004/06/16
 * @author
 */
public class PrintJob {

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * コンストラクタ
     */
    public PrintJob() {
        super();

    }

    /**
     * プリントを開始します
     *
     * @param  folderObjectHandle フォルダオブジェクト
     */
    public void startPrint(UserBoxDocument folderObjectHandle) {

        try {

            /* AccessControlTokenを取得します */
            fetchAccessControlToken();

            /* ジョブスクリプトを投入します */
            performJobScript(folderObjectHandle);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return;
    }

    /**
     * プリントを終了します
     */
    public void endPrint() {
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
     * @param      folderObjectHandle フォルダオブジェクト
     *
     * @exception  OperationFailureException オペレーション例外
     */
    private void performJobScript(UserBoxDocument folderObjectHandle)
            throws OperationFailureException {

        try {

            /*
             * ボックスプリント機能へのリクエストを行うためのインスタンスを
             * 生成します
             */
            BoxPrintRequest request =
                    BoxPrintRequest.createInstance(accessControlToken);

            /* ボックス文書の一覧を生成します */
            UserBoxDocumentList list = new UserBoxDocumentList();

            /* ボックス文書を追加します */
            list.add(folderObjectHandle);

            /* 印刷する文書を設定します */
            request.setPrintDocument(accessControlToken, list);

            /* 「コピー数(Copies)」を設定します */
            request.setCopies(accessControlToken, getNumberOfCopies());

            /* 「出力モード(PrintEjecter)」を設定します */
            /* request.setPrintEjecter(accessControlToken, getOutput()); */

            /* 「給紙トレイ(FeedTray)」を設定します */
            request.setFeedTray(accessControlToken, getInputTray());

            /* ジョブ管理を取得します */
            BoxPrintJobManager manager =
                    BoxPrintJobManager.getInstance(AppletActivator._bundle,
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

    /**
     *「インプレッション数(id_att_impressions_2)」の設定を返します
     *
     * @return 「インプレッション数」の設定
     *
    private Long getNumberOfimpressions() {
    }*/

    /**
     *「コピー数(id_att_copies)」の設定を返します
     *
     * @return 「コピー数」の設定
     */
    private Copies getNumberOfCopies() {
        return new Copies(1);
    }

    /**
     *「給紙トレイ(id_att_input_tray)」の設定を返します
     *
     * @return 「給紙トレイ」の設定
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
     *「メディア(id_att_medium)」の設定を返します
     *
     * @return 「メディア」の設定
     *
    private Medium getMedium() {
    }*/

}/* end class PrintJob */

/* end PrintJob.java */
