//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// FolderListPanel.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;

import com.canon.meap.ctk.awt.CArrowButton;
import com.canon.meap.ctk.awt.CColor;
import com.canon.meap.ctk.awt.CHorizontalLine;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.box.BoxContentAppendedEvent;
import com.canon.meap.imi.box.BoxContentDeletedEvent;
import com.canon.meap.imi.box.BoxEventAdapter;
import com.canon.meap.imi.box.BoxManager;
import com.canon.meap.imi.job.boxprint.BoxPrintJobDeletedEvent;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManager;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManagerEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobDeletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.imi.job.boxscan.BoxScanJobManagerEventAdapter;
import com.canon.meap.service.avs.CAppletContext;

/**
 * ＢＯＸスキャンサンプルプログラム　フォルダリスト画面クラス
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FolderListPanel extends Panel implements ActionListener {

    /**
	 * version ID for serialized form.
	 */
	private static final long serialVersionUID = 3932463120881006951L;

    /* ジョブサービスクラス */
    private JobService jobService;

    /* ファイルボックスクラス */
    private FileBox fileBox;

    /* スキャンジョブクラス */
    private ScanJob scanJob;

    /* プリントジョブクラス */
    private PrintJob printJob;

    /* フォルダ属性ヘッダラベル */
    private CLabel nameHeader;
    private CLabel pagesHeader;
    private CLabel dateTimeHeader;

    /* フォルダ情報パネル */
    private Panel folderInfoPanel;

    /* フォルダ属性ラベル */
    private CLabel[] nameLabel;
    private CLabel[] pageLabel;
    private CLabel[] dateLabel;
    private CLabel[] timeLabel;

    /* フォルダ情報ライン */
    private Panel[] folderInfoLine;
    private CHorizontalLine partitionLine;

    /* ページ切替ボタン */
    private CArrowButton pageUpButton;
    private CArrowButton pageDownButton;
    private CLabel pageCountLabel;

    /* ジョブボタン */
    private CLabelButton deleteButton;
    private CLabelButton scanButton;
    private CLabelButton printButton;

    /* メッセージラベル */
    private CLabel messageLabel;

    /* 定数 */
    private static final int FOLDER_INFO_FONT = 16;
    private static final int FOLDER_INFO_MAX = 8;

    /* EventListener */
    private MouseEventAdapter mouseEventAdapter;
    private BoxEventReceiver boxEventReceiver;
    private BoxScanJobEventReceiver scanJobEventReceiver;
    private BoxPrintJobEventReceiver printJobEventReceiver;

    /* 変数 */
    private int dispPage;
    private int dispFolderCount;
    private boolean disableUI;

    /**
     * コンストラクタ
     */
    public FolderListPanel() {
        super();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);
        setBackground(CColor.gainsboro);

        /* 各コンポーネントを配置します */
        locateHeader();
        locateFolderLists();
        locatePageButtons();
        locateJobButtons();
        locateMessage();

        setVisible(false);

        /* マウスEventListenerを生成します */
        mouseEventAdapter = new MouseEventAdapter();
    }

    /**
     * 画面の表示を行います
     */
    public void display() {

        /* ジョブサービスを生成します */
        jobService = new JobService();

        /* ファイルボックスを生成します */
        fileBox = new FileBox();

        try {

            /* ファイルボックスを活性化します */
            fileBox.activate();

            /* フォルダ数を取得します */
            dispFolderCount = fileBox.getFolderCount();

            dispPage = 0;

            /* 各コンポーネントを表示します */
            dispFolderLists();
            dispPageButtons();
            dispJobButtons();
            dispFileBoxNo();

            /* ユーザインタフェースを有効にします */
            enableComponents();

            /* CPCAEventListenerを定義します */
            addCpcaEventAdapter();

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        setVisible(true);

        return;
    }

    /**
     * 画面を無効化します
     */
    public void unDisplay() {

        /* CPCAEventListenerを削除します */
            removeCpcaEventAdapter();

        /* ユーザインタフェースを無効にします */
        disableComponents();

        dispPage = 0;
        dispFolderCount = 0;

        fileBox = null;

        jobService = null;

        setVisible(false);

        return;
    }

    /**
     * フォルダ属性ヘッダラベルを配置します
     */
    private void locateHeader() {

        /* フォルダ名 */
        nameHeader = new CLabel("Name", CLabel.LEFT);
        nameHeader.setBounds(30, 15, 200, 20);
        add(nameHeader);

        /* ページ数 */
        pagesHeader = new CLabel("Pages", CLabel.LEFT);
        pagesHeader.setBounds(230, 15, 80, 20);
        add(pagesHeader);

        /* 日付・時刻 */
        dateTimeHeader = new CLabel("Date Time", CLabel.LEFT);
        dateTimeHeader.setBounds(310, 15, 110, 20);
        add(dateTimeHeader);

        return;
    }

    /**
     * フォルダ情報を設定します
     */
    private void locateFolderLists() {

        /* フォルダ情報ラインを生成します */
        folderInfoLine = new Panel[FOLDER_INFO_MAX];

        /* フォルダ属性ラベルを生成します */
        nameLabel = new CLabel[FOLDER_INFO_MAX];
        pageLabel = new CLabel[FOLDER_INFO_MAX];
        dateLabel = new CLabel[FOLDER_INFO_MAX];
        timeLabel = new CLabel[FOLDER_INFO_MAX];

        /* フォルダ情報パネルを生成します */
        folderInfoPanel = new Panel();
        folderInfoPanel.setBounds(30, 40, 385, (30 * FOLDER_INFO_MAX) - 2);
        folderInfoPanel.setLayout(null);
        folderInfoPanel.setBackground(CColor.white);

        add(folderInfoPanel);

        /* 全フォルダについて各属性を設定します */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            /* フォルダ名 */
            nameLabel[i] = new CLabel("", CLabel.LEFT);
            nameLabel[i].setBounds(0, 2, 198, 30);
            nameLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* ページ数 */
            pageLabel[i] = new CLabel("", CLabel.LEFT);
            pageLabel[i].setBounds(200, 2, 78, 30);
            pageLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* 日付 */
            dateLabel[i] = new CLabel("", CLabel.LEFT);
            dateLabel[i].setBounds(280, 2, 53, 30);
            dateLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* 時刻 */
            timeLabel[i] = new CLabel("", CLabel.LEFT);
            timeLabel[i].setBounds(330, 2, 53, 30);
            timeLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* フォルダ情報ラインを生成します */
            folderInfoLine[i] = new Panel();
            folderInfoLine[i].setLayout(null);
            folderInfoLine[i].setBounds(0, 30 * i, 385, 30);
            folderInfoLine[i].setBackground(CColor.white);

            /* 罫線を生成します */
            if (i < (FOLDER_INFO_MAX - 1)) {
                partitionLine = new CHorizontalLine(CHorizontalLine.LOWERED);
                partitionLine.setBounds(0, 30 - 2, 385, 2);
                folderInfoLine[i].add(partitionLine);
            }

            /* フォルダ属性ラベルをフォルダ情報ラインに貼り付けます */
            folderInfoLine[i].add(nameLabel[i]);
            folderInfoLine[i].add(pageLabel[i]);
            folderInfoLine[i].add(dateLabel[i]);
            folderInfoLine[i].add(timeLabel[i]);

            /* フォルダ情報ラインをフォルダ情報パネルに貼り付けます */
            folderInfoPanel.add(folderInfoLine[i]);
        }

        return;
    }

    /**
     * フォルダ情報パネルを表示します
     */
    private void dispFolderLists() {

        int startNumber;
        String stringPageSize = null;

        /* 表示ページをチェックします */
        startNumber = dispPage * FOLDER_INFO_MAX;
        if ((startNumber >= dispFolderCount) && (dispPage > 0)) {
            dispPage--;
            startNumber = dispPage * FOLDER_INFO_MAX;
        }

        /* 表示ページ内の全フォルダについて各属性を表示します */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            if (i + startNumber < dispFolderCount) {

                /* フォルダ名 */
                nameLabel[i].setText(fileBox.getFolderName(i + startNumber));

                /* ページ数 */
                stringPageSize = "000" + Long.toString(
                        fileBox.getPageSize(i + startNumber));
                pageLabel[i].setText(
                        stringPageSize.substring(stringPageSize.length() - 4));

                /* 日付 */
                dateLabel[i].setText(convTimeStampToDateString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* 時刻 */
                timeLabel[i].setText(convTimeStampToTimeString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* フォルダ情報ラインを可視化します */
                folderInfoLine[i].setVisible(true);

                /* 選択されたフォルダ情報ラインのカラーを調整します */
                if ((startNumber + i) == fileBox.getSelectFolderNo()) {
                    setInfoLineBackground(i, CColor.powderblue);
                } else {
                    setInfoLineBackground(i, CColor.white);
                }

            } else {
                folderInfoLine[i].setVisible(false);
            }
        }

        return;
    }

    /**
     * 日付を"MM/DD"形式で返します
     *
     * @param   dateTimes 日付・時刻
     *
     * @return  日付(MM/DD)
     */
    private String convTimeStampToDateString(Calendar dateTimes) {

        String stringDate = null;
        String stringMMDD = null;

        stringDate = "0" + Long.toString(dateTimes.get(Calendar.MONTH) + 1);
        stringMMDD = stringDate.substring(stringDate.length() - 2) + "/";
        stringDate = "0" + Long.toString(dateTimes.get(Calendar.DATE));
        stringMMDD
                = stringMMDD + stringDate.substring(stringDate.length() - 2);

        return stringMMDD;
    }

   /**
     * 時刻を"HH:MM"形式で返します
     *
     * @param   dateTimes 日付・時刻
     *
     * @return  時刻(HH:MM)
     */
    private String convTimeStampToTimeString(Calendar dateTimes) {

        String stringTime = null;
        String stringHHMM = null;

        stringTime = "0" + Long.toString(dateTimes.get(Calendar.HOUR));
        stringHHMM = stringTime.substring(stringTime.length() - 2) + ":";
        stringTime = "0" + Long.toString(dateTimes.get(Calendar.MINUTE));
        stringHHMM
                = stringHHMM + stringTime.substring(stringTime.length() - 2);

        return stringHHMM;
    }

    /**
     * フォルダ情報ラインのカラーを調整します
     *
     * @param  lineNo フォルダ情報Line№
     * @param  color 背景色
     */
    private void setInfoLineBackground(int lineNo, Color color) {

        nameLabel[lineNo].setBackground(color);
        pageLabel[lineNo].setBackground(color);
        dateLabel[lineNo].setBackground(color);
        timeLabel[lineNo].setBackground(color);
        folderInfoLine[lineNo].setBackground(color);

        return;
    }

    /**
     * ページ切替ボタンを配置します
     */
    private void locatePageButtons() {

        /* [Up]ボタン */
        pageUpButton = new CArrowButton(CArrowButton.ARROW_UP);
        pageUpButton.setBounds(520, 40, 40, 40);
        pageUpButton.addActionListener(this);
        add(pageUpButton);

        /* [Down]ボタン */
        pageDownButton = new CArrowButton(CArrowButton.ARROW_DOWN);
        pageDownButton.setBounds(520, 110, 40, 40);
        pageDownButton.addActionListener(this);
        add(pageDownButton);

        /* ページラベル */
        pageCountLabel = new CLabel();
        pageCountLabel.setBounds(515, 85, 50, 20);
        pageCountLabel.setFont(
                new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));
        add(pageCountLabel);

        return;
    }

    /**
     * ページ切替ボタンを表示します
     */
    private void dispPageButtons() {

        /* [Up]ボタン */
        if (dispPage > 0) {
            pageUpButton.setEnabled(true);
        } else {
            pageUpButton.setEnabled(false);
        }

        /* [Down]ボタン */
        if (dispPage < (dispFolderCount -1) / FOLDER_INFO_MAX ) {
            pageDownButton.setEnabled(true);
        } else {
            pageDownButton.setEnabled(false);
        }

        /* ページラベル */
        pageCountLabel.setText((dispPage + 1)
                + "/" + (((dispFolderCount - 1) / FOLDER_INFO_MAX ) + 1));

        return;
    }

    /**
     * ジョブボタンを配置します
     */
    private void locateJobButtons() {

        /* [Delete]ボタン */
        deleteButton = new CLabelButton(
                "Delete", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        deleteButton.setBounds(30, 290, 116, 42);
        deleteButton.addActionListener(this);
        add(deleteButton);

        /* [Scan]ボタン */
        scanButton = new CLabelButton(
                "Scan", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        scanButton.setBounds(190, 290, 116, 42);
        scanButton.addActionListener(this);
        add(scanButton);

        /* [Print]ボタン */
        printButton = new CLabelButton(
                "Print", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        printButton.setBounds(350, 290, 116, 42);
        printButton.addActionListener(this);
        add(printButton);

        return;
    }

    /**
     * ジョブボタンを表示します
     */
    private void dispJobButtons() {

        /* フォルダ選択時は [Delete] [Scan] [Print] を有効とします */
        if (fileBox.isSelected()) {
            deleteButton.setEnabled(true);
            scanButton.setEnabled(true);
            printButton.setEnabled(true);

        /* フォルダ未選択時は [Scan] のみを有効とします */
        } else {
            deleteButton.setEnabled(false);
            scanButton.setEnabled(true);
            printButton.setEnabled(false);
        }

        return;
    }

    /**
     * メッセージラベルを配置します
     */
    private void locateMessage() {

        /* メッセージラベル */
        messageLabel = new CLabel();
        messageLabel.setBounds(10, 340, 600, 30);
        add(messageLabel);
        messageLabel.setBackground(CColor.white);

        return;
    }

    /**
     * ファイルボックス番号を表示します
     */
    private void dispFileBoxNo() {

        StringBuffer messageFileBoxNo = null;
        String stringFileBoxNo = null;
        int intFileBoxNo = 0;

        messageFileBoxNo = new StringBuffer("FileBox No. : ");

        /* ファイルボックス番号を取得します */
        intFileBoxNo = fileBox.getFileBoxNo();
        if(intFileBoxNo < 10) {
            messageFileBoxNo.append('0');
        }
        stringFileBoxNo = new Integer(intFileBoxNo).toString();
        messageFileBoxNo.append(stringFileBoxNo);

        /* ファイルボックス番号を表示します */
        displayMessage(messageFileBoxNo.toString());

        return;
    }

    /**
     * メッセージを表示します
     *
     * @param  メッセージ
     */
    private void displayMessage(String message) {

        messageLabel.setText(message);

        return;
    }

    /**
     * ユーザインタフェースを有効にします
     */
    private void enableComponents() {

        /* MouseListenerを追加します */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].addMouseListener(mouseEventAdapter);
            pageLabel[i].addMouseListener(mouseEventAdapter);
            dateLabel[i].addMouseListener(mouseEventAdapter);
            timeLabel[i].addMouseListener(mouseEventAdapter);
            folderInfoLine[i].addMouseListener(mouseEventAdapter);
        }

        /* ボタンコンポーネントを有効にします */
        dispJobButtons();
        dispPageButtons();

        disableUI = false;

        return;
    }

    /**
     * ユーザインタフェースを無効にします
     */
    private void disableComponents() {

        /* MouseListenerを削除します */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].removeMouseListener(mouseEventAdapter);
            pageLabel[i].removeMouseListener(mouseEventAdapter);
            dateLabel[i].removeMouseListener(mouseEventAdapter);
            timeLabel[i].removeMouseListener(mouseEventAdapter);
            folderInfoLine[i].removeMouseListener(mouseEventAdapter);
        }

        /* ボタンコンポーネントを無効にします */
        deleteButton.setEnabled(false);
        scanButton.setEnabled(false);
        printButton.setEnabled(false);

        pageUpButton.setEnabled(false);
        pageDownButton.setEnabled(false);

        disableUI = true;

        return;
    }

    /**
     * CPCAEventListenerを定義します
     */
    private void addCpcaEventAdapter() {

        boxEventReceiver = new BoxEventReceiver();

        try {
            /* ボックス管理を取得します */
            BoxManager manager = BoxManager.getInstance(
                                        AppletActivator._bundle,
                                        jobService.accessControlToken);
            /* イベントリスナを登録します */
            manager.addBoxEventListener(AppletActivator._bundle,
                                        jobService.accessControlToken,
                                        boxEventReceiver);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        scanJobEventReceiver = new BoxScanJobEventReceiver();

        try {
            /* ジョブ管理を取得します */
            BoxScanJobManager manager = BoxScanJobManager.getInstance(
                                        AppletActivator._bundle,
                                        jobService.accessControlToken);
            /* イベントリスナを登録します */
            manager.addBoxScanJobManagerEventListener(
                                        jobService.accessControlToken,
                                        scanJobEventReceiver);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        printJobEventReceiver = new BoxPrintJobEventReceiver();

        try {
            /* ジョブ管理を取得します */
            BoxPrintJobManager manager = BoxPrintJobManager.getInstance(
                                        AppletActivator._bundle,
                                        jobService.accessControlToken);
            /* イベントリスナを登録します */
            manager.addBoxPrintJobManagerEventListener(
                                        jobService.accessControlToken,
                                        printJobEventReceiver);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return;
    }

    /**
     * CPCAEventListenerを削除します
     */
    private void removeCpcaEventAdapter() {

        if ( null != boxEventReceiver ) {

            try {
                /* ボックス管理を取得します */
                BoxManager manager = BoxManager.getInstance(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken);
                /* イベントリスナを解除します */
                manager.removeBoxEventListener(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken,
                                            boxEventReceiver);

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            boxEventReceiver = null;
        }

        if ( null != scanJobEventReceiver ) {

            try {
                /* ジョブ管理を取得します */
                BoxScanJobManager manager = BoxScanJobManager.getInstance(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken);
                /* イベントリスナを解除します */
                manager.removeBoxScanJobManagerEventListener(
                                            jobService.accessControlToken,
                                            scanJobEventReceiver);

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            scanJobEventReceiver = null;
        }

        if ( null != printJobEventReceiver ) {

            try {
                /* ジョブ管理を取得します */
                BoxPrintJobManager manager = BoxPrintJobManager.getInstance(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken);
                /* イベントリスナを解除します */
                manager.removeBoxPrintJobManagerEventListener(
                                            jobService.accessControlToken,
                                            printJobEventReceiver);

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            printJobEventReceiver = null;
        }

        return;
    }

    /**
     * フォルダ削除を実行します
     */
    private void executeDelete() {

        /* ユーザインタフェースを無効にします */
        disableComponents();

        try {

            /* フォルダオブジェクトを削除します */
            fileBox.deleteFolder();

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return;
    }

    /**
     * スキャンを実行します
     */
    private void executeScan() {

        /* スキャンジョブを生成します */
        scanJob = new ScanJob();

        /* ジョブが投入可能かどうかを調べます */
        if (jobService.isSendAvailable()) {

            /* ユーザインタフェースを無効にします */
            disableComponents();

            /* スキャンジョブにスキャンの開始を通知します */
            if (false == scanJob.startScan(fileBox.getObjectHandle())) {

                /* ユーザインタフェースを有効にします */
                enableComponents();

                scanJob = null;

                return;
            }

        } else {

            /* ジョブ実行不可メッセージを表示します */
            displayMessage("Cannot submit the job.");
        }

        scanJob = null;

        return;
    }

    /**
     * プリントを実行します
     */
    private void executePrint() {

        /* プリントージョブを生成します */
        printJob = new PrintJob();

        /* ユーザインタフェースを無効にします */
        disableComponents();

        /* プリントジョブにプリントの開始を通知します */
        printJob.startPrint(fileBox.getFolderObjectHandle());

        printJob = null;

        return;
    }

    /**
     * actionPerformed発生時の処理を行います
     *
     * @param  ae ActionEvent
     */
    public void actionPerformed(ActionEvent ae) {

        /* ファイルボックス番号を表示します */
        dispFileBoxNo();

        /* 前ページを表示します*/
        if (ae.getSource() == pageUpButton) {
            if (dispPage > 0) {
                dispPage--;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* 次ページを表示します */
        if (ae.getSource() == pageDownButton) {
            if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX ) {
                dispPage++;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* フォルダ削除を実行します */
        if (ae.getSource() == deleteButton) {
            executeDelete();
        }

        /* スキャンを実行します */
        if (ae.getSource() == scanButton) {
            executeScan();
        }

        /* プリントを実行します */
        if (ae.getSource() == printButton) {
            executePrint();
        }

        return;
    }


    /**
     * マウスイベント受信クラス
     */
    private class MouseEventAdapter extends MouseAdapter {

    /**
     * mousePressed発生時の処理を行います
     *
     * @param  me MouseEvent
     */
        public void mousePressed(MouseEvent me) {

            /* 表示されている全てのフォルダを未選択状態にします */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {
                setInfoLineBackground(i, CColor.white);
            }

            /* 選択されたフォルダをチェックします */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {

                if ((me.getComponent() == nameLabel[i]) ||
                    (me.getComponent() == pageLabel[i]) ||
                    (me.getComponent() == dateLabel[i]) ||
                    (me.getComponent() == timeLabel[i]) ||
                    (me.getComponent() == folderInfoLine[i])) {

                    /* 未選択状態のフォルダなら選択とします */
                    if (((dispPage * FOLDER_INFO_MAX) + i)
                            != fileBox.getSelectFolderNo()) {
                        fileBox.setSelectFolderNo(
                                (dispPage * FOLDER_INFO_MAX) + i);
                        setInfoLineBackground(i, CColor.powderblue);

                    /* 選択状態のフォルダなら選択解除とします */
                    } else {
                        fileBox.resetSelectFolderNo();
                    }

                    /* ジョブボタンを表示します */
                    dispJobButtons();
                    break;
                }
            }

            return;
        }

    }/* end class MouseEventAdapter */


    /**
     * ＣＰＣＡイベント受信クラス
     *
    private class CpcaEventAdapter extends EventAdapter {

        /**
　       * イベントの受信を開始します
         *
         * @param   object イベント発生元オブジェクト
         *
         * @return  true:イベントの配信を行う / false:配信を行わない
         *
        public boolean beginEvent(long object) {
        }

        /**
         * イベントの受信を終了します
         *
         * @param  object イベント発生元オブジェクト
         *
        public void endEvent(long object) {
        }

        /**
　       * reportFileBoxContentChange発生時の処理を行います
         *
         * @param  object イベント発生元オブジェクト
         * @param  report イベント内容
         *
         * @return  true:イベントの配信を行う / false:配信を行わない
         *
        public boolean reportFileBoxContentChange(
                long object, ReportFileBoxContentChange report) {
        }

        /**
　       * reportObjectDeleted2発生時の処理を行います
         *
         * @param  object イベント発生元オブジェクト
         * @param  report イベント内容
         *
         * @return  true:イベントの配信を行う / false:配信を行わない
         *
        public boolean reportObjectDeleted2(
                long object, ReportObjectDeleted2 report) {
        }

    } * end class CpcaEventAdapter */

    /**
     * ボックスイベント受信クラス
     */
    private class BoxEventReceiver extends BoxEventAdapter {

        /**
         * ボックスの文書が追加された際に呼び出されます。
         *
         * @param   event   イベントオブジェクト
         */
        public void boxContentAppended(BoxContentAppendedEvent event) {

            /* ファイルボックス内容を更新します */
            try {

                /* フォルダ情報を更新します */
                fileBox.updateFolderInfo();

                /* フォルダ数を取得します */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            /* フォルダ情報パネルを再表示します */
            fileBox.resetSelectFolderNo();
            dispFolderLists();
            dispPageButtons();
        }

        /**
         * ボックスの文書が削除された際に呼び出されます。
         *
         * @param   event   イベントオブジェクト
         */
        public void boxContentDeleted(BoxContentDeletedEvent event) {

            /* ファイルボックス内容を更新します */
            try {

                /* フォルダ情報を更新します */
                fileBox.updateFolderInfo();

                /* フォルダ数を取得します */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            /* フォルダ情報パネルを再表示します */
            fileBox.resetSelectFolderNo();
            dispFolderLists();

            /* フォルダ削除 */
            if (disableUI == true) {

                /* ユーザインタフェースを有効にします */
                enableComponents();
            }
        }

    }/* end class BoxEventReceiver */

    /**
     * ボックススキャンジョブイベント受信クラス
     */
    private class BoxScanJobEventReceiver
            extends BoxScanJobManagerEventAdapter {

        /**
         * ジョブがデバイス内部で消滅した場合に呼び出されます。
         *
         * @param   event   イベントオブジェクト
         */
        public void jobDeleted(BoxScanJobDeletedEvent event) {

            /* ユーザインタフェースを有効にします */
            enableComponents();
        }

    }/* end class BoxScanJobEventReceiver */

    /**
     * ボックスプリントジョブイベント受信クラス
     */
    private class BoxPrintJobEventReceiver
            extends BoxPrintJobManagerEventAdapter {

        /**
         * ジョブがデバイス内部で消滅した場合に呼び出されます。
         *
         * @param   event   イベントオブジェクト
         */
        public void jobDeleted(BoxPrintJobDeletedEvent event) {

            /* フォルダ情報パネルを再表示します */
            fileBox.resetSelectFolderNo();
            dispFolderLists();

            /* ユーザインタフェースを有効にします */
            enableComponents();
        }

    }/* end class BoxPrintJobEventReceiver */

}/* end class FolderListPanel */

/* end FolderListPanel.java */
