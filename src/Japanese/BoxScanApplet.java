//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// BoxScanApplet.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.applet.Applet;

import org.osgi.framework.Bundle;

import com.canon.meap.imi.IMI;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.login.LocalLoginService;
import com.canon.meap.service.login.event.UserEvent;
import com.canon.meap.service.login.event.UserEventListener;

/**
 * ＢＯＸスキャンサンプルプログラム　アプレットクラス
 *
 * @version     0.01  2002/06/18
 * @author
 */
public class BoxScanApplet extends Applet {

    /**
	 * version ID for serialized form.
	 */
	private static final long serialVersionUID = -3093283682069856894L;

    /* Bundle */
    private Bundle bundle;

    /* BoxScanApplet */
    private static BoxScanApplet boxScanApplet;

    /* フォルダリスト画面クラス */
    private FolderListPanel folderListPanel;

    /* ログインEventListener */
    private UserEventAdapter userEventAdapter;

    /* LoginContext */
    private LoginContext loginContext;

    /* ログアウト状態 */
    private boolean logoutState;

    /**
     * コンストラクタ
     *
     * @param   bundle Bundle
     */
    public BoxScanApplet(Bundle bundle) {
        super();

        this.bundle = bundle;
    }

    /**
     * 初期化を行います
     */
    public void init() {
        super.init();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);

        boxScanApplet = this;

        try {

            /* IMIを初期化します */
            IMI.initialize(bundle);

            /* フォルダリスト画面を生成、追加します */
            folderListPanel = new FolderListPanel();
            folderListPanel.setLocation(0, 0);
            add(folderListPanel);

            /* ログインEventListenerを定義します */
            addLoginEventListener();

        } catch (Throwable se) {
            System.out.println(se.getMessage());
        }

        return;
    }

    /**
     * 終了処理を行います
     */
    public void destroy() {
        super.destroy();

        /* アプレットを終了します */
        if (logoutState == false) {
            stopApplet();
            logoutState = true;
        }

        /* ログインEventListenerを削除します */
        removeLoginEventListener();

        folderListPanel = null;

        try {

        /* IMIを終了します */
        IMI.terminate(bundle);

        } catch (Throwable th) {
            System.out.println(th.getMessage());
        }

        return;
    }

    /**
     * アプレットを開始します
     */
    private void startApplet() {

        /* フォルダリスト画面を表示します */
        folderListPanel.display();

        return;
    }

    /**
     * アプレットを終了します
     */
    private void stopApplet() {

        /* フォルダリスト画面を無効化します */
        folderListPanel.unDisplay();

        return;
    }

    /**
     * Appletを返します
     *
     * @return  BoxScanApplet
     */
    public static BoxScanApplet getBoxScanApplet() {
        return boxScanApplet;
    }

    /**
     * LoginContextを返します
     *
     * @return  LoginContext
     */
    public LoginContext getLoginContext() {
        return loginContext;
    }

    /**
     * ログインEventListenerを定義します
     */
    private void addLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* AppletActivatorを取得します */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivatorからLLSを取得します */
        localLoginService = appletActivator.getLocalLoginService();

        /* ログインEventListenerを定義します */
        userEventAdapter = new UserEventAdapter();
        localLoginService.addUserEventListener(userEventAdapter);

        return;
    }

    /**
     * ログインEventListenerを削除します
     */
    private void removeLoginEventListener() {

        AppletActivator appletActivator = null;
        LocalLoginService localLoginService = null;

        /* AppletActivatorを取得します */
        appletActivator = AppletActivator.getAppletActivator();

        /* AppletActivatorからLLSを取得します */
        localLoginService = appletActivator.getLocalLoginService();

        /* ログインEventListenerを削除します */
        localLoginService.removeUserEventListener(userEventAdapter);
        userEventAdapter = null;

        return;
    }


    /**
     *  ログイン／ログアウトイベント受信クラス
     */
    public class UserEventAdapter implements UserEventListener {

        /**
　       * ログインイベント発生時の処理を行います
         *
         * @param   userEvent ユーザ情報
         */
        public void login(UserEvent userEvent) {

            /* UserEventからLoginContextを取得します */
            loginContext = userEvent.getLoginContext();

            /* アプレットを開始します */
            startApplet();
            logoutState = false;

            return;
        }

        /**
　       * ログアウトイベント発生時の処理を行います
         *
         * @param   userEvent ユーザ情報
         */
        public void logout(UserEvent userEvent) {

            /* アプレットを終了します */
            stopApplet();
            logoutState = true;

            return;
        }

    }/* end class UserEventAdapter */


}/* end class BoxScanApplet */

/* end BoxScanApplet.java */
