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
import java.security.AccessControlException;
import java.util.Calendar;

import com.canon.meap.ctk.awt.CArrowButton;
import com.canon.meap.ctk.awt.CColor;
import com.canon.meap.ctk.awt.CHorizontalLine;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnavailableMethodException;
import com.canon.meap.imi.box.BoxContentAppendedEvent;
import com.canon.meap.imi.box.BoxContentDeletedEvent;
import com.canon.meap.imi.box.BoxEventAdapter;
import com.canon.meap.imi.box.BoxManager;
import com.canon.meap.imi.job.boxprint.BoxPrintJobDeletedEvent;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManager;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManagerEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobDeletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.imi.job.boxscan.BoxScanJobManagerEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.log.Logger;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�t�H���_���X�g��ʃN���X
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FolderListPanel extends Panel implements ActionListener {

    /**
	 * version ID for serialized form.
	 */
	private static final long serialVersionUID = 3932463120881006951L;

    /* �W���u�T�[�r�X�N���X */
    private JobService jobService;

    /* �t�@�C���{�b�N�X�N���X */
    private FileBox fileBox;

    /* �X�L�����W���u�N���X */
    private ScanJob scanJob;

    /* �t�H���_�����w�b�_���x�� */
    private CLabel nameHeader;
    private CLabel pagesHeader;
    private CLabel dateTimeHeader;

    /* �t�H���_���p�l�� */
    private Panel folderInfoPanel;

    /* �t�H���_�������x�� */
    private CLabel[] nameLabel;
    private CLabel[] pageLabel;
    private CLabel[] dateLabel;
    private CLabel[] timeLabel;

    /* �t�H���_��񃉃C�� */
    private Panel[] folderInfoLine;
    private CHorizontalLine partitionLine;

    /* �y�[�W�ؑփ{�^�� */
    private CArrowButton pageUpButton;
    private CArrowButton pageDownButton;
    private CLabel pageCountLabel;

    /* �W���u�{�^�� */
    private CLabelButton deleteButton;
    private CLabelButton scanButton;
    private CLabelButton printButton;

    /* ���b�Z�[�W���x�� */
    private CLabel messageLabel;

    /* �萔 */
    private static final int FOLDER_INFO_FONT = 16;
    private static final int FOLDER_INFO_MAX = 8;

    /* EventListener */
    private MouseEventAdapter mouseEventAdapter;
    private BoxEventReceiver boxEventReceiver;
    private BoxScanJobEventReceiver scanJobEventReceiver;
    private BoxScanRequest boxScanRequest;
    private BoxPrintJobEventReceiver printJobEventReceiver;

    /* �ϐ� */
    private int dispPage;
    private int dispFolderCount;
    private boolean disableUI;

    /**
     * �R���X�g���N�^
     */
    public FolderListPanel() {
        super();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);
        setBackground(CColor.gainsboro);

        /* �e�R���|�[�l���g��z�u���܂� */
        locateHeader();
        locateFolderLists();
        locatePageButtons();
        locateJobButtons();
        locateMessage();

        setVisible(false);

        /* �}�E�XEventListener�𐶐����܂� */
        mouseEventAdapter = new MouseEventAdapter();
    }

    /**
     * ��ʂ̕\�����s���܂�
     */
    public void display() {

        /* �W���u�T�[�r�X�𐶐����܂� */
        jobService = new JobService();

        /* �t�@�C���{�b�N�X�𐶐����܂� */
        fileBox = new FileBox();

        try {

            /* �t�@�C���{�b�N�X�����������܂� */
            fileBox.activate();

            /* �t�H���_�����擾���܂� */
            dispFolderCount = fileBox.getFolderCount();

            dispPage = 0;

            /* �e�R���|�[�l���g��\�����܂� */
            dispFolderLists();
            dispPageButtons();
            dispJobButtons();
            dispFileBoxNo();

            /* ���[�U�C���^�t�F�[�X��L���ɂ��܂� */
            enableComponents();

            /* CPCAEventListener���`���܂� */
            addCpcaEventAdapter();

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        setVisible(true);

        return;
    }

    /**
     * ��ʂ𖳌������܂�
     */
    public void unDisplay() {

        /* CPCAEventListener���폜���܂� */
            removeCpcaEventAdapter();

        /* ���[�U�C���^�t�F�[�X�𖳌��ɂ��܂� */
        disableComponents();

        dispPage = 0;
        dispFolderCount = 0;

        fileBox = null;

        jobService = null;

        setVisible(false);

        return;
    }

    /**
     * �t�H���_�����w�b�_���x����z�u���܂�
     */
    private void locateHeader() {

        /* �t�H���_�� */
        nameHeader = new CLabel("Name", CLabel.LEFT);
        nameHeader.setBounds(30, 15, 200, 20);
        add(nameHeader);

        /* �y�[�W�� */
        pagesHeader = new CLabel("Pages", CLabel.LEFT);
        pagesHeader.setBounds(230, 15, 80, 20);
        add(pagesHeader);

        /* ���t�E���� */
        dateTimeHeader = new CLabel("Date Time", CLabel.LEFT);
        dateTimeHeader.setBounds(310, 15, 110, 20);
        add(dateTimeHeader);

        return;
    }

    /**
     * �t�H���_����ݒ肵�܂�
     */
    private void locateFolderLists() {

        /* �t�H���_��񃉃C���𐶐����܂� */
        folderInfoLine = new Panel[FOLDER_INFO_MAX];

        /* �t�H���_�������x���𐶐����܂� */
        nameLabel = new CLabel[FOLDER_INFO_MAX];
        pageLabel = new CLabel[FOLDER_INFO_MAX];
        dateLabel = new CLabel[FOLDER_INFO_MAX];
        timeLabel = new CLabel[FOLDER_INFO_MAX];

        /* �t�H���_���p�l���𐶐����܂� */
        folderInfoPanel = new Panel();
        folderInfoPanel.setBounds(30, 40, 385, (30 * FOLDER_INFO_MAX) - 2);
        folderInfoPanel.setLayout(null);
        folderInfoPanel.setBackground(CColor.white);

        add(folderInfoPanel);

        /* �S�t�H���_�ɂ��Ċe������ݒ肵�܂� */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            /* �t�H���_�� */
            nameLabel[i] = new CLabel("", CLabel.LEFT);
            nameLabel[i].setBounds(0, 2, 198, 30);
            nameLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* �y�[�W�� */
            pageLabel[i] = new CLabel("", CLabel.LEFT);
            pageLabel[i].setBounds(200, 2, 78, 30);
            pageLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* ���t */
            dateLabel[i] = new CLabel("", CLabel.LEFT);
            dateLabel[i].setBounds(280, 2, 53, 30);
            dateLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* ���� */
            timeLabel[i] = new CLabel("", CLabel.LEFT);
            timeLabel[i].setBounds(330, 2, 53, 30);
            timeLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* �t�H���_��񃉃C���𐶐����܂� */
            folderInfoLine[i] = new Panel();
            folderInfoLine[i].setLayout(null);
            folderInfoLine[i].setBounds(0, 30 * i, 385, 30);
            folderInfoLine[i].setBackground(CColor.white);

            /* �r���𐶐����܂� */
            if (i < (FOLDER_INFO_MAX - 1)) {
                partitionLine = new CHorizontalLine(CHorizontalLine.LOWERED);
                partitionLine.setBounds(0, 30 - 2, 385, 2);
                folderInfoLine[i].add(partitionLine);
            }

            /* �t�H���_�������x�����t�H���_��񃉃C���ɓ\��t���܂� */
            folderInfoLine[i].add(nameLabel[i]);
            folderInfoLine[i].add(pageLabel[i]);
            folderInfoLine[i].add(dateLabel[i]);
            folderInfoLine[i].add(timeLabel[i]);

            /* �t�H���_��񃉃C�����t�H���_���p�l���ɓ\��t���܂� */
            folderInfoPanel.add(folderInfoLine[i]);
        }

        return;
    }

    /**
     * �t�H���_���p�l����\�����܂�
     */
    private void dispFolderLists() {

        int startNumber;
        String stringPageSize = null;

        /* �\���y�[�W���`�F�b�N���܂� */
        startNumber = dispPage * FOLDER_INFO_MAX;
        if ((startNumber >= dispFolderCount) && (dispPage > 0)) {
            dispPage--;
            startNumber = dispPage * FOLDER_INFO_MAX;
        }

        /* �\���y�[�W���̑S�t�H���_�ɂ��Ċe������\�����܂� */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            if (i + startNumber < dispFolderCount) {

                /* �t�H���_�� */
                nameLabel[i].setText(fileBox.getFolderName(i + startNumber));

                /* �y�[�W�� */
                stringPageSize = "000" + Long.toString(
                        fileBox.getPageSize(i + startNumber));
                pageLabel[i].setText(
                        stringPageSize.substring(stringPageSize.length() - 4));

                /* ���t */
                dateLabel[i].setText(convTimeStampToDateString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* ���� */
                timeLabel[i].setText(convTimeStampToTimeString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* �t�H���_��񃉃C�����������܂� */
                folderInfoLine[i].setVisible(true);

                /* �I�����ꂽ�t�H���_��񃉃C���̃J���[�𒲐����܂� */
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
     * ���t��"MM/DD"�`���ŕԂ��܂�
     *
     * @param   dateTimes ���t�E����
     *
     * @return  ���t(MM/DD)
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
     * ������"HH:MM"�`���ŕԂ��܂�
     *
     * @param   dateTimes ���t�E����
     *
     * @return  ����(HH:MM)
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
     * �t�H���_��񃉃C���̃J���[�𒲐����܂�
     *
     * @param  lineNo �t�H���_���Line��
     * @param  color �w�i�F
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
     * �y�[�W�ؑփ{�^����z�u���܂�
     */
    private void locatePageButtons() {

        /* [Up]�{�^�� */
        pageUpButton = new CArrowButton(CArrowButton.ARROW_UP);
        pageUpButton.setBounds(520, 40, 40, 40);
        pageUpButton.addActionListener(this);
        add(pageUpButton);

        /* [Down]�{�^�� */
        pageDownButton = new CArrowButton(CArrowButton.ARROW_DOWN);
        pageDownButton.setBounds(520, 110, 40, 40);
        pageDownButton.addActionListener(this);
        add(pageDownButton);

        /* �y�[�W���x�� */
        pageCountLabel = new CLabel();
        pageCountLabel.setBounds(515, 85, 50, 20);
        pageCountLabel.setFont(
                new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));
        add(pageCountLabel);

        return;
    }

    /**
     * �y�[�W�ؑփ{�^����\�����܂�
     */
    private void dispPageButtons() {

        /* [Up]�{�^�� */
        if (dispPage > 0) {
            pageUpButton.setEnabled(true);
        } else {
            pageUpButton.setEnabled(false);
        }

        /* [Down]�{�^�� */
        if (dispPage < (dispFolderCount -1) / FOLDER_INFO_MAX ) {
            pageDownButton.setEnabled(true);
        } else {
            pageDownButton.setEnabled(false);
        }

        /* �y�[�W���x�� */
        pageCountLabel.setText((dispPage + 1)
                + "/" + (((dispFolderCount - 1) / FOLDER_INFO_MAX ) + 1));

        return;
    }

    /**
     * �W���u�{�^����z�u���܂�
     */
    private void locateJobButtons() {

        /* [Delete]�{�^�� */
        deleteButton = new CLabelButton(
                "Delete", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        deleteButton.setBounds(30, 290, 116, 42);
        deleteButton.addActionListener(this);
        add(deleteButton);

        /* [Scan]�{�^�� */
        scanButton = new CLabelButton(
                "Scan", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        scanButton.setBounds(190, 290, 116, 42);
        scanButton.addActionListener(this);
        add(scanButton);

        /* [Print]�{�^�� */
        printButton = new CLabelButton(
                "Print", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        printButton.setBounds(350, 290, 116, 42);
        printButton.addActionListener(this);
        add(printButton);

        return;
    }

    /**
     * �W���u�{�^����\�����܂�
     */
    private void dispJobButtons() {

        /* �t�H���_�I������ [Delete] [Scan] [Print] ��L���Ƃ��܂� */
        if (fileBox.isSelected()) {
            deleteButton.setEnabled(true);
            scanButton.setEnabled(true);
            printButton.setEnabled(true);

        /* �t�H���_���I������ [Scan] �݂̂�L���Ƃ��܂� */
        } else {
            deleteButton.setEnabled(false);
            scanButton.setEnabled(true);
            printButton.setEnabled(false);
        }

        return;
    }

    /**
     * ���b�Z�[�W���x����z�u���܂�
     */
    private void locateMessage() {

        /* ���b�Z�[�W���x�� */
        messageLabel = new CLabel();
        messageLabel.setBounds(10, 340, 600, 30);
        add(messageLabel);
        messageLabel.setBackground(CColor.white);

        return;
    }

    /**
     * �t�@�C���{�b�N�X�ԍ���\�����܂�
     */
    private void dispFileBoxNo() {

        StringBuffer messageFileBoxNo = null;
        String stringFileBoxNo = null;
        int intFileBoxNo = 0;

        messageFileBoxNo = new StringBuffer("FileBox No. : ");

        /* �t�@�C���{�b�N�X�ԍ����擾���܂� */
        intFileBoxNo = fileBox.getFileBoxNo();
        if(intFileBoxNo < 10) {
            messageFileBoxNo.append('0');
        }
        stringFileBoxNo = new Integer(intFileBoxNo).toString();
        messageFileBoxNo.append(stringFileBoxNo);

        /* �t�@�C���{�b�N�X�ԍ���\�����܂� */
        displayMessage(messageFileBoxNo.toString());

        return;
    }

    /**
     * ���b�Z�[�W��\�����܂�
     *
     * @param  ���b�Z�[�W
     */
    private void displayMessage(String message) {

        messageLabel.setText(message);

        return;
    }

    /**
     * ���[�U�C���^�t�F�[�X��L���ɂ��܂�
     */
    private void enableComponents() {

        /* MouseListener��ǉ����܂� */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].addMouseListener(mouseEventAdapter);
            pageLabel[i].addMouseListener(mouseEventAdapter);
            dateLabel[i].addMouseListener(mouseEventAdapter);
            timeLabel[i].addMouseListener(mouseEventAdapter);
            folderInfoLine[i].addMouseListener(mouseEventAdapter);
        }

        /* �{�^���R���|�[�l���g��L���ɂ��܂� */
        dispJobButtons();
        dispPageButtons();

        disableUI = false;

        return;
    }

    /**
     * ���[�U�C���^�t�F�[�X�𖳌��ɂ��܂�
     */
    private void disableComponents() {

        /* MouseListener���폜���܂� */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].removeMouseListener(mouseEventAdapter);
            pageLabel[i].removeMouseListener(mouseEventAdapter);
            dateLabel[i].removeMouseListener(mouseEventAdapter);
            timeLabel[i].removeMouseListener(mouseEventAdapter);
            folderInfoLine[i].removeMouseListener(mouseEventAdapter);
        }

        /* �{�^���R���|�[�l���g�𖳌��ɂ��܂� */
        deleteButton.setEnabled(false);
        scanButton.setEnabled(false);
        printButton.setEnabled(false);

        pageUpButton.setEnabled(false);
        pageDownButton.setEnabled(false);

        disableUI = true;

        return;
    }

    /**
     * Define the CPCA Eventlistener
     */
    private void addCpcaEventAdapter() {

      boxEventReceiver = new BoxEventReceiver();

      try {
        /* Obtains an instance of the box management class */
        BoxManager manager =
            BoxManager.getInstance(AppletActivator._bundle, jobService.accessControlToken);
        /* Registers an event listener */
        manager.addBoxEventListener(AppletActivator._bundle, jobService.accessControlToken,
            boxEventReceiver);

      } catch (OperationFailureException oe) {
//        logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
      }

      return;
    }

    private void addScanRequestListener() {
      scanJobEventReceiver = new BoxScanJobEventReceiver();

      try {
        /* Obtains a job management instance */
        boxScanRequest = BoxScanRequest.createInstance(jobService.accessControlToken);
        boxScanRequest.addBoxScanJobEventListener(jobService.accessControlToken,
            scanJobEventReceiver);
      } catch (OperationFailureException oe) {
//        logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
      }

    }

    /**
     * Delete the CPCAEventListener
     */
    private void removeCpcaEventAdapter() {

      if (null != boxEventReceiver) {

        try {
          /* Obtains an instance of the box management class */
          BoxManager manager =
              BoxManager.getInstance(AppletActivator._bundle, jobService.accessControlToken);
          /* Deletes listeners that receive events */
          manager.removeBoxEventListener(AppletActivator._bundle, jobService.accessControlToken,
              boxEventReceiver);

        } catch (OperationFailureException oe) {
//          logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
        }

        boxEventReceiver = null;
      }

      return;
    }

    private void removeScanEventListener() {
      if (null != scanJobEventReceiver && null != boxScanRequest) {

        try {
          /* Obtains a job management instance */
          boxScanRequest.removeBoxScanJobEventListener(jobService.accessControlToken,
              scanJobEventReceiver);
          //
        } catch (OperationFailureException oe) {
//          logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
        }

        scanJobEventReceiver = null;
      }
    }

    /**
     * �t�H���_�폜�����s���܂�
     */
    private void executeDelete() {

        /* ���[�U�C���^�t�F�[�X�𖳌��ɂ��܂� */
        disableComponents();

        try {

            /* �t�H���_�I�u�W�F�N�g���폜���܂� */
            fileBox.deleteFolder();

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return;
    }

    /**
     * �X�L���������s���܂�
     */
    private void executeScan() {

        /* �X�L�����W���u�𐶐����܂� */
      addScanRequestListener();
        scanJob = new ScanJob(boxScanRequest);

        /* �W���u�������\���ǂ����𒲂ׂ܂� */
        if (jobService.isSendAvailable()) {

            /* ���[�U�C���^�t�F�[�X�𖳌��ɂ��܂� */
            disableComponents();

            /* �X�L�����W���u�ɃX�L�����̊J�n��ʒm���܂� */
            if (false == scanJob.startScan(fileBox.getObjectHandle())) {

                /* ���[�U�C���^�t�F�[�X��L���ɂ��܂� */
                enableComponents();

                scanJob = null;

                return;
            }

        } else {

            /* �W���u���s�s���b�Z�[�W��\�����܂� */
            displayMessage("Cannot submit the job.");
        }

        scanJob = null;

        return;
    }

    /**
     * actionPerformed�������̏������s���܂�
     *
     * @param  ae ActionEvent
     */
    public void actionPerformed(ActionEvent ae) {

        /* �t�@�C���{�b�N�X�ԍ���\�����܂� */
        dispFileBoxNo();

        /* �O�y�[�W��\�����܂�*/
        if (ae.getSource() == pageUpButton) {
            if (dispPage > 0) {
                dispPage--;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* ���y�[�W��\�����܂� */
        if (ae.getSource() == pageDownButton) {
            if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX ) {
                dispPage++;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* �t�H���_�폜�����s���܂� */
        if (ae.getSource() == deleteButton) {
            executeDelete();
        }

        /* �X�L���������s���܂� */
        if (ae.getSource() == scanButton) {
            executeScan();
        }

        return;
    }


    /**
     * �}�E�X�C�x���g��M�N���X
     */
    private class MouseEventAdapter extends MouseAdapter {

    /**
     * mousePressed�������̏������s���܂�
     *
     * @param  me MouseEvent
     */
        public void mousePressed(MouseEvent me) {

            /* �\������Ă���S�Ẵt�H���_�𖢑I����Ԃɂ��܂� */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {
                setInfoLineBackground(i, CColor.white);
            }

            /* �I�����ꂽ�t�H���_���`�F�b�N���܂� */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {

                if ((me.getComponent() == nameLabel[i]) ||
                    (me.getComponent() == pageLabel[i]) ||
                    (me.getComponent() == dateLabel[i]) ||
                    (me.getComponent() == timeLabel[i]) ||
                    (me.getComponent() == folderInfoLine[i])) {

                    /* ���I����Ԃ̃t�H���_�Ȃ�I���Ƃ��܂� */
                    if (((dispPage * FOLDER_INFO_MAX) + i)
                            != fileBox.getSelectFolderNo()) {
                        fileBox.setSelectFolderNo(
                                (dispPage * FOLDER_INFO_MAX) + i);
                        setInfoLineBackground(i, CColor.powderblue);

                    /* �I����Ԃ̃t�H���_�Ȃ�I�������Ƃ��܂� */
                    } else {
                        fileBox.resetSelectFolderNo();
                    }

                    /* �W���u�{�^����\�����܂� */
                    dispJobButtons();
                    break;
                }
            }

            return;
        }

    }/* end class MouseEventAdapter */

    /**
     * �{�b�N�X�C�x���g��M�N���X
     */
    private class BoxEventReceiver extends BoxEventAdapter {

        /**
         * �{�b�N�X�̕������ǉ����ꂽ�ۂɌĂяo����܂��B
         *
         * @param   event   �C�x���g�I�u�W�F�N�g
         */
        public void boxContentAppended(BoxContentAppendedEvent event) {

            /* �t�@�C���{�b�N�X���e���X�V���܂� */
            try {

                /* �t�H���_�����X�V���܂� */
                fileBox.updateFolderInfo();

                /* �t�H���_�����擾���܂� */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            /* �t�H���_���p�l�����ĕ\�����܂� */
            fileBox.resetSelectFolderNo();
            dispFolderLists();
            dispPageButtons();
        }

        /**
         * �{�b�N�X�̕������폜���ꂽ�ۂɌĂяo����܂��B
         *
         * @param   event   �C�x���g�I�u�W�F�N�g
         */
        public void boxContentDeleted(BoxContentDeletedEvent event) {

            /* �t�@�C���{�b�N�X���e���X�V���܂� */
            try {

                /* �t�H���_�����X�V���܂� */
                fileBox.updateFolderInfo();

                /* �t�H���_�����擾���܂� */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                System.out.println(oe.getMessage());
            }

            /* �t�H���_���p�l�����ĕ\�����܂� */
            fileBox.resetSelectFolderNo();
            dispFolderLists();

            /* �t�H���_�폜 */
            if (disableUI == true) {

                /* ���[�U�C���^�t�F�[�X��L���ɂ��܂� */
                enableComponents();
            }
        }

    }/* end class BoxEventReceiver */

    /**
     * �{�b�N�X�X�L�����W���u�C�x���g��M�N���X
     */
    private class BoxScanJobEventReceiver
            extends BoxScanJobEventAdapter {

        /**
         * �W���u���f�o�C�X�����ŏ��ł����ꍇ�ɌĂяo����܂��B
         *
         * @param   event   �C�x���g�I�u�W�F�N�g
         */
        public void jobDeleted(BoxScanJobDeletedEvent event) {

            /* ���[�U�C���^�t�F�[�X��L���ɂ��܂� */
            enableComponents();
        }

    }/* end class BoxScanJobEventReceiver */

    /**
     * �{�b�N�X�v�����g�W���u�C�x���g��M�N���X
     */
    private class BoxPrintJobEventReceiver
            extends BoxPrintJobManagerEventAdapter {

        /**
         * �W���u���f�o�C�X�����ŏ��ł����ꍇ�ɌĂяo����܂��B
         *
         * @param   event   �C�x���g�I�u�W�F�N�g
         */
        public void jobDeleted(BoxPrintJobDeletedEvent event) {

            /* �t�H���_���p�l�����ĕ\�����܂� */
            fileBox.resetSelectFolderNo();
            dispFolderLists();

            /* ���[�U�C���^�t�F�[�X��L���ɂ��܂� */
            enableComponents();
        }

    }/* end class BoxPrintJobEventReceiver */

}/* end class FolderListPanel */

/* end FolderListPanel.java */
