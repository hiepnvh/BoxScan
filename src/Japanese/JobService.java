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
 * ＢＯＸスキャンサンプルプログラム　ジョブサービスクラス
 *
 * @version     2.01  2004/04/27
 * @author
 */
public class JobService {

    /* AccessControlToken */
    public AccessControlToken accessControlToken;

    /**
     * コンストラクタ
     */
    public JobService() {
        super();

        try {
            /* AccessControlTokenを取得します */
            fetchAccessControlToken();
        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * ジョブサービスの状態を取得します
     *
     * @return  ジョブサービスの状態
     */
    public short getJobServiceState() {
        return 0;
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
     * ジョブサービスの状態を文字列表現に変換します
     *
     * @param   state ジョブサービスの状態
     *
     * @return  ジョブサービスの状態の文字列表現
     */
    public String toJobServiceStateString(short state) {
        return null;
    }

    /**
     * ジョブが投入可能かどうかを調べます
     *
     * @return  投入可能な場合は true、投入不可能な場合は false
     */
    public boolean isSendAvailable() {

        try {

            /* ジョブ管理を取得します */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator._bundle,
                                                  accessControlToken);

            /* ジョブが投入可能かどうかを調べます */
            return manager.isSendAvailable(accessControlToken);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return false;
    }

}/* end class JobService */

/* end JobService.java */
