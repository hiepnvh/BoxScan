// ****************************************************************************
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
// ***************************************************************************

import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Calendar;
import com.canon.meap.ctk.awt.CArrowButton;
import com.canon.meap.ctk.awt.CColor;
import com.canon.meap.ctk.awt.CHorizontalLine;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;
import com.canon.meap.imaging.ImagingException;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnavailableMethodException;
import com.canon.meap.imi.box.BoxContentAppendedEvent;
import com.canon.meap.imi.box.BoxContentDeletedEvent;
import com.canon.meap.imi.box.BoxEventAdapter;
import com.canon.meap.imi.box.BoxManager;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.data.JobState;
import com.canon.meap.imi.job.boxprint.BoxPrintJobDeletedEvent;
import com.canon.meap.imi.job.boxprint.BoxPrintJobManagerEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobDeletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobScanImagesStoreCompletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobScanPageCountEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobStateChangedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.log.Logger;

/**
 * �a�n�w�X�L�����T���v���v���O�����@�t�H���_���X�g��ʃN���X
 *
 * @version 2.02 2004/09/01
 * @author
 */
public class FolderListPanel extends Panel implements ActionListener {

  /**
   * version ID for serialized form.
   */
  private static final long serialVersionUID = 3932463120881006951L;

  private JobService jobService;

  private FileBox fileBox;

  private ScanJob scanJob;

  private CLabel nameHeader;
  private CLabel pagesHeader;
  private CLabel dateTimeHeader;

  private Panel folderInfoPanel;

  private CLabel[] nameLabel;
  private CLabel[] pageLabel;
  private CLabel[] dateLabel;
  private CLabel[] timeLabel;

  private Panel[] folderInfoLine;
  private CHorizontalLine partitionLine;

  private CArrowButton pageUpButton;
  private CArrowButton pageDownButton;
  private CLabel pageCountLabel;

  private CLabelButton scanButton;
  private CLabelButton sendButton;
  private CLabelButton delButton;

  private CLabel messageLabel;

  private static final int FOLDER_INFO_FONT = 16;
  private static final int FOLDER_INFO_MAX = 8;

  /* EventListener */
  private MouseEventAdapter mouseEventAdapter;
  private BoxEventReceiver boxEventReceiver;
  private BoxScanJobEventReceiver scanJobEventReceiver;
  private BoxScanRequest boxScanRequest;
  
  private Logger logger;
  private LoginContext loginContext;

  private int dispPage;
  private int dispFolderCount;
  private boolean disableUI;

  /**
   */
  public FolderListPanel() {
    super();

    setSize(CAppletContext.MAX_APPLET_WIDTH, CAppletContext.MAX_APPLET_HEIGHT);
    setLayout(null);
    setBackground(CColor.gainsboro);

    locateHeader();
    locateFolderLists();
    locatePageButtons();
    locateJobButtons();
    locateMessage();

    setVisible(false);

    mouseEventAdapter = new MouseEventAdapter();
    
    logger = AppletActivator.getAppletActivator().getLogService().getLogger(LogService.LOGKIND_APP);
    loginContext = BoxScanApplet.getBoxScanApplet().getLoginContext();
  }

  /**
   */
  public void display() {

    logger.log(loginContext, Logger.LOG_LEVEL_INFO, "Display xxx");
    jobService = new JobService();

    fileBox = new FileBox();

    try {

      fileBox.activate();

      dispFolderCount = fileBox.getFolderCount();

      dispPage = 0;

      dispFolderLists();
      dispPageButtons();
      dispJobButtons();
      dispFileBoxNo();

      enableComponents();

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

    removeCpcaEventAdapter();
    removeScanEventListener();

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

    nameHeader = new CLabel("Name", CLabel.LEFT);
    nameHeader.setBounds(30, 15, 200, 20);
    add(nameHeader);

    pagesHeader = new CLabel("Pages", CLabel.LEFT);
    pagesHeader.setBounds(230, 15, 80, 20);
    add(pagesHeader);

    dateTimeHeader = new CLabel("Date Time", CLabel.LEFT);
    dateTimeHeader.setBounds(310, 15, 110, 20);
    add(dateTimeHeader);

    return;
  }

  /**
   */
  private void locateFolderLists() {

    folderInfoLine = new Panel[FOLDER_INFO_MAX];

    nameLabel = new CLabel[FOLDER_INFO_MAX];
    pageLabel = new CLabel[FOLDER_INFO_MAX];
    dateLabel = new CLabel[FOLDER_INFO_MAX];
    timeLabel = new CLabel[FOLDER_INFO_MAX];

    folderInfoPanel = new Panel();
    folderInfoPanel.setBounds(30, 40, 385, (30 * FOLDER_INFO_MAX) - 2);
    folderInfoPanel.setLayout(null);
    folderInfoPanel.setBackground(CColor.white);

    add(folderInfoPanel);

    for (int i = 0; i < FOLDER_INFO_MAX; i++) {

      nameLabel[i] = new CLabel("", CLabel.LEFT);
      nameLabel[i].setBounds(0, 2, 198, 30);
      nameLabel[i].setFont(new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

      pageLabel[i] = new CLabel("", CLabel.LEFT);
      pageLabel[i].setBounds(200, 2, 78, 30);
      pageLabel[i].setFont(new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

      dateLabel[i] = new CLabel("", CLabel.LEFT);
      dateLabel[i].setBounds(280, 2, 53, 30);
      dateLabel[i].setFont(new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

      timeLabel[i] = new CLabel("", CLabel.LEFT);
      timeLabel[i].setBounds(330, 2, 53, 30);
      timeLabel[i].setFont(new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

      folderInfoLine[i] = new Panel();
      folderInfoLine[i].setLayout(null);
      folderInfoLine[i].setBounds(0, 30 * i, 385, 30);
      folderInfoLine[i].setBackground(CColor.white);

      if (i < (FOLDER_INFO_MAX - 1)) {
        partitionLine = new CHorizontalLine(CHorizontalLine.LOWERED);
        partitionLine.setBounds(0, 30 - 2, 385, 2);
        folderInfoLine[i].add(partitionLine);
      }

      folderInfoLine[i].add(nameLabel[i]);
      folderInfoLine[i].add(pageLabel[i]);
      folderInfoLine[i].add(dateLabel[i]);
      folderInfoLine[i].add(timeLabel[i]);

      folderInfoPanel.add(folderInfoLine[i]);
    }

    return;
  }

  /**
   */
  private void dispFolderLists() {

    int startNumber;
    String stringPageSize = null;

    startNumber = dispPage * FOLDER_INFO_MAX;
    if ((startNumber >= dispFolderCount) && (dispPage > 0)) {
      dispPage--;
      startNumber = dispPage * FOLDER_INFO_MAX;
    }

    for (int i = 0; i < FOLDER_INFO_MAX; i++) {

      if (i + startNumber < dispFolderCount) {

        nameLabel[i].setText(fileBox.getFolderName(i + startNumber));

        stringPageSize = "000" + Long.toString(fileBox.getPageSize(i + startNumber));
        pageLabel[i].setText(stringPageSize.substring(stringPageSize.length() - 4));

        dateLabel[i].setText(convTimeStampToDateString(fileBox.getTimeStamp(i + startNumber)));

        timeLabel[i].setText(convTimeStampToTimeString(fileBox.getTimeStamp(i + startNumber)));

        folderInfoLine[i].setVisible(true);

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
   * @param dateTimes ���t�E����
   *
   */
  private String convTimeStampToDateString(Calendar dateTimes) {

    String stringDate = null;
    String stringMMDD = null;

    stringDate = "0" + Long.toString(dateTimes.get(Calendar.MONTH) + 1);
    stringMMDD = stringDate.substring(stringDate.length() - 2) + "/";
    stringDate = "0" + Long.toString(dateTimes.get(Calendar.DATE));
    stringMMDD = stringMMDD + stringDate.substring(stringDate.length() - 2);

    return stringMMDD;
  }

  /**
   *
   *
   */
  private String convTimeStampToTimeString(Calendar dateTimes) {

    String stringTime = null;
    String stringHHMM = null;

    stringTime = "0" + Long.toString(dateTimes.get(Calendar.HOUR));
    stringHHMM = stringTime.substring(stringTime.length() - 2) + ":";
    stringTime = "0" + Long.toString(dateTimes.get(Calendar.MINUTE));
    stringHHMM = stringHHMM + stringTime.substring(stringTime.length() - 2);

    return stringHHMM;
  }

  private void setInfoLineBackground(int lineNo, Color color) {

    nameLabel[lineNo].setBackground(color);
    pageLabel[lineNo].setBackground(color);
    dateLabel[lineNo].setBackground(color);
    timeLabel[lineNo].setBackground(color);
    folderInfoLine[lineNo].setBackground(color);

    return;
  }

  private void locatePageButtons() {

    pageUpButton = new CArrowButton(CArrowButton.ARROW_UP);
    pageUpButton.setBounds(520, 40, 40, 40);
    pageUpButton.addActionListener(this);
    add(pageUpButton);

    pageDownButton = new CArrowButton(CArrowButton.ARROW_DOWN);
    pageDownButton.setBounds(520, 110, 40, 40);
    pageDownButton.addActionListener(this);
    add(pageDownButton);

    pageCountLabel = new CLabel();
    pageCountLabel.setBounds(515, 85, 50, 20);
    pageCountLabel.setFont(new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));
    add(pageCountLabel);

    return;
  }

  /**
   * �y�[�W�ؑփ{�^����\�����܂�
   */
  private void dispPageButtons() {

    if (dispPage > 0) {
      pageUpButton.setEnabled(true);
    } else {
      pageUpButton.setEnabled(false);
    }

    if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX) {
      pageDownButton.setEnabled(true);
    } else {
      pageDownButton.setEnabled(false);
    }

    pageCountLabel.setText((dispPage + 1) + "/" + (((dispFolderCount - 1) / FOLDER_INFO_MAX) + 1));

    return;
  }

  /**
   */
  private void locateJobButtons() {

    /* [Send] button */
    sendButton = new CLabelButton("Send", CLabelButton.CENTER, CLabelButton.CENTER, CColor.black,
        CLabelButton.ARROW_NONE);
    sendButton.setBounds(220, 290, 116, 42);
    sendButton.addActionListener(this);
    add(sendButton);

    /* [Cancel] button */
    delButton = new CLabelButton("Cancel", CLabelButton.CENTER, CLabelButton.CENTER, CColor.black,
        CLabelButton.ARROW_NONE);
    delButton.setBounds(350, 290, 116, 42);
    delButton.addActionListener(this);
    add(delButton);

    scanButton = new CLabelButton("Scan", CLabelButton.CENTER, CLabelButton.CENTER, CColor.black,
        CLabelButton.ARROW_NONE);
    scanButton.setBounds(90, 290, 116, 42);
    scanButton.addActionListener(this);
    add(scanButton);

    return;
  }

  /**
   * �W���u�{�^����\�����܂�
   */
  private void dispJobButtons() {

    /**
     * Validate the [Delete] [Scan] [Print] button while folder is selected
     */
    if (fileBox.isSelected()) {
      scanButton.setEnabled(true);
      sendButton.setEnabled(true);
      delButton.setEnabled(true);

      /* Validate the [Scan] button only when folder isnot selected */
    } else {
      scanButton.setEnabled(true);
      sendButton.setEnabled(false);
      delButton.setEnabled(false);
    }

    return;
  }

  /**
   * ���b�Z�[�W���x����z�u���܂�
   */
  private void locateMessage() {

    messageLabel = new CLabel();
    messageLabel.setBounds(10, 340, 600, 30);
    add(messageLabel);
    messageLabel.setBackground(CColor.white);

    return;
  }

  /**
   */
  private void dispFileBoxNo() {

    StringBuffer messageFileBoxNo = null;
    String stringFileBoxNo = null;
    int intFileBoxNo = 0;

    messageFileBoxNo = new StringBuffer("FileBox No. : ");

    intFileBoxNo = fileBox.getFileBoxNo();
    if (intFileBoxNo < 10) {
      messageFileBoxNo.append('0');
    }
    stringFileBoxNo = new Integer(intFileBoxNo).toString();
    messageFileBoxNo.append(stringFileBoxNo);

    displayMessage(messageFileBoxNo.toString());

    return;
  }

  /**
   *
   */
  private void displayMessage(String message) {

    messageLabel.setText(message);

    return;
  }

  /**
   */
  private void enableComponents() {

    for (int i = 0; i < FOLDER_INFO_MAX; i++) {
      nameLabel[i].addMouseListener(mouseEventAdapter);
      pageLabel[i].addMouseListener(mouseEventAdapter);
      dateLabel[i].addMouseListener(mouseEventAdapter);
      timeLabel[i].addMouseListener(mouseEventAdapter);
      folderInfoLine[i].addMouseListener(mouseEventAdapter);
    }

    dispJobButtons();
    dispPageButtons();

    disableUI = false;

    return;
  }

  /**
   */
  private void disableComponents() {

    for (int i = 0; i < FOLDER_INFO_MAX; i++) {
      nameLabel[i].removeMouseListener(mouseEventAdapter);
      pageLabel[i].removeMouseListener(mouseEventAdapter);
      dateLabel[i].removeMouseListener(mouseEventAdapter);
      timeLabel[i].removeMouseListener(mouseEventAdapter);
      folderInfoLine[i].removeMouseListener(mouseEventAdapter);
    }

    delButton.setEnabled(false);
    scanButton.setEnabled(false);
    sendButton.setEnabled(false);

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
          BoxManager.getInstance(AppletActivator.bundleContext.getBundle(), jobService.accessControlToken);
      /* Registers an event listener */
      manager.addBoxEventListener(AppletActivator.bundleContext.getBundle(), jobService.accessControlToken,
          boxEventReceiver);

    } catch (OperationFailureException oe) {
      // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
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
      // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
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
            BoxManager.getInstance(AppletActivator.bundleContext.getBundle(), jobService.accessControlToken);
        /* Deletes listeners that receive events */
        manager.removeBoxEventListener(AppletActivator.bundleContext.getBundle(), jobService.accessControlToken,
            boxEventReceiver);

      } catch (OperationFailureException oe) {
        // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
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
        // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
      }

      scanJobEventReceiver = null;
    }
  }

  private void executeSend() {

    // send to destination
    displayMessage("Sending...");
    UserBox userbox = fileBox.getObjectHandle();
    String targetPath  = "D:";
    SingleDocumentJob documentJob = new SingleDocumentJob(targetPath);
    try {
      documentJob.addPage(userbox.getHandle(jobService.accessControlToken));
    } catch (AccessControlException e) {
      logger.log(loginContext, Logger.LOG_LEVEL_INFO, e.getMessage());
    } catch (UnavailableMethodException e) {
      logger.log(loginContext, Logger.LOG_LEVEL_INFO, e.getMessage());
    } catch (ImagingException e) {
      logger.log(loginContext, Logger.LOG_LEVEL_INFO, e.getMessage());
    } catch (IOException e) {
      logger.log(loginContext, Logger.LOG_LEVEL_INFO, e.getMessage());
    } 
    displayMessage("Done");
  }

  private void executeDel() {

    /* Invalidate the user interface */
    disableComponents();

    try {

      /* Delete the folder object */
      fileBox.deleteFolder();

    } catch (OperationFailureException oe) {
      // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
    }

    return;
  }

  /**
   */
  private void executeScan() {

    addScanRequestListener();
    scanJob = new ScanJob(boxScanRequest);

    if (jobService.isSendAvailable()) {

      disableComponents();

      if (false == scanJob.startScan(fileBox.getObjectHandle())) {

        enableComponents();

        scanJob = null;

        return;
      }

    } else {

      displayMessage("Cannot submit the job.");
    }

    scanJob = null;

    return;
  }

  /**
   *
   * @param ae ActionEvent
   */
  public void actionPerformed(ActionEvent ae) {

    dispFileBoxNo();

    if (ae.getSource() == pageUpButton) {
      if (dispPage > 0) {
        dispPage--;
        dispFolderLists();
        dispPageButtons();
      }
    }

    if (ae.getSource() == pageDownButton) {
      if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX) {
        dispPage++;
        dispFolderLists();
        dispPageButtons();
      }
    }

    if (ae.getSource() == sendButton) {
      executeSend();
    }

    if (ae.getSource() == delButton) {
      executeDel();
    }

    if (ae.getSource() == scanButton) {
      executeScan();
    }

    return;
  }


  /**
   */
  private class MouseEventAdapter extends MouseAdapter {

    /**
     *
     * @param me MouseEvent
     */
    public void mousePressed(MouseEvent me) {

      for (int i = 0; i < FOLDER_INFO_MAX; i++) {
        setInfoLineBackground(i, CColor.white);
      }

      for (int i = 0; i < FOLDER_INFO_MAX; i++) {

        if ((me.getComponent() == nameLabel[i]) || (me.getComponent() == pageLabel[i])
            || (me.getComponent() == dateLabel[i]) || (me.getComponent() == timeLabel[i])
            || (me.getComponent() == folderInfoLine[i])) {

          if (((dispPage * FOLDER_INFO_MAX) + i) != fileBox.getSelectFolderNo()) {
            fileBox.setSelectFolderNo((dispPage * FOLDER_INFO_MAX) + i);
            setInfoLineBackground(i, CColor.powderblue);

          } else {
            fileBox.resetSelectFolderNo();
          }

          dispJobButtons();
          break;
        }
      }

      return;
    }

  }/* end class MouseEventAdapter */

  /**
   */
  private class BoxEventReceiver extends BoxEventAdapter {

    /**
     *
     */
    public void boxContentAppended(BoxContentAppendedEvent event) {

      try {

        fileBox.updateFolderInfo();

        dispFolderCount = fileBox.getFolderCount();

      } catch (OperationFailureException oe) {
        System.out.println(oe.getMessage());
      }

      fileBox.resetSelectFolderNo();
      dispFolderLists();
      dispPageButtons();
    }

    /**
     *
     */
    public void boxContentDeleted(BoxContentDeletedEvent event) {

      try {

        fileBox.updateFolderInfo();

        dispFolderCount = fileBox.getFolderCount();

      } catch (OperationFailureException oe) {
        System.out.println(oe.getMessage());
      }

      fileBox.resetSelectFolderNo();
      dispFolderLists();

      if (disableUI == true) {

        enableComponents();
      }
    }

  }/* end class BoxEventReceiver */

  /**
   */
  private class BoxScanJobEventReceiver extends BoxScanJobEventAdapter {

    public void jobDeleted(BoxScanJobDeletedEvent event) {

      enableComponents();
    }
    
    public void jobScanPageCount(BoxScanJobScanPageCountEvent event) {
        displayMessage("Scanned " + String.valueOf(event.getCount()) + " pages");
    }
    
    public void jobScanImagesStoreCompleted(BoxScanJobScanImagesStoreCompletedEvent event) {
      displayMessage("Scanned " + String.valueOf(event.getJobId()) + " done");
    }
    
    public void jobStateChanged(final BoxScanJobStateChangedEvent event) {
      JobState jobState = event.getJobState();
      if (jobState != null && jobState.getState() == JobState.STATE_COMPLETED) {
        displayMessage("Job " + String.valueOf(event.getJobId()) + " completed");
        removeScanEventListener();
      }
      
    }

  }/* end class BoxScanJobEventReceiver */

}/* end class FolderListPanel */

/* end FolderListPanel.java */
