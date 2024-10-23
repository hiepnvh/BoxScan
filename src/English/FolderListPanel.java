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

import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.canon.meap.ctk.awt.CArrowButton;
import com.canon.meap.ctk.awt.CColor;
import com.canon.meap.ctk.awt.CHorizontalLine;
import com.canon.meap.ctk.awt.CLabel;
import com.canon.meap.ctk.awt.CLabelButton;
import com.canon.meap.imaging.ImagingException;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnavailableMethodException;
import com.canon.meap.imi.box.meapbox.MeapBox;
import com.canon.meap.imi.data.JobState;
import com.canon.meap.imi.job.boxscan.BoxScanJobDeletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobEventAdapter;
import com.canon.meap.imi.job.boxscan.BoxScanJobScanImagesStoreCompletedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobScanPageCountEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobStateChangedEvent;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.service.avs.CAppletContext;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @version 2.02 2004/09/01
 * @author
 */
public class FolderListPanel extends Panel implements ActionListener {

  /**
   * version ID for serialized form.
   */
  private static final long serialVersionUID = 3932463120881006951L;

  private JobService jobService;

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
  private CLabelButton continueButton;

  private CLabel messageLabel;

  private static final int FOLDER_INFO_FONT = 16;
  private static final int FOLDER_INFO_MAX = 8;

  private BoxScanJobEventReceiver scanJobEventReceiver;
  private BoxScanRequest boxScanRequest;
  private SingleDocumentJob documentJob;

  private int dispPage;
  private int dispFolderCount;

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

  }

  /**
   */
  public void display() {

    LoggerUtil.i("Display xxx");
    jobService = new JobService();

    dispPage = 0;
    dispJobButtons();
    enableComponents();

    setVisible(true);

    return;
  }

  /**
   * ��ʂ𖳌������܂�
   */
  public void unDisplay() {

    removeScanEventListener();

    disableComponents();

    dispPage = 0;
    dispFolderCount = 0;

    jobService = null;

    documentJob = null;

    setVisible(false);

    return;
  }

  /**
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

    /* [Continue] button */
    continueButton = new CLabelButton("Continue", CLabelButton.CENTER, CLabelButton.CENTER,
        CColor.black, CLabelButton.ARROW_NONE);
    continueButton.setBounds(480, 290, 116, 42);
    continueButton.addActionListener(this);
    add(continueButton);

    scanButton = new CLabelButton("Scan", CLabelButton.CENTER, CLabelButton.CENTER, CColor.black,
        CLabelButton.ARROW_NONE);
    scanButton.setBounds(90, 290, 116, 42);
    scanButton.addActionListener(this);
    add(scanButton);

    return;
  }

  /**
   */
  private void dispJobButtons() {

    /**
     * Validate the [Delete] [Scan] [Print] button while folder is selected
     */
    scanButton.setEnabled(true);
    sendButton.setEnabled(true);
    delButton.setEnabled(true);

    /* Validate the [Scan] button only when folder isnot selected */

    return;
  }

  /**
   */
  private void locateMessage() {

    messageLabel = new CLabel();
    messageLabel.setBounds(10, 340, 600, 30);
    add(messageLabel);
    messageLabel.setBackground(CColor.white);

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


    dispJobButtons();


    return;
  }

  /**
   */
  private void disableComponents() {

    continueButton.setEnabled(false);
    delButton.setEnabled(true);
    scanButton.setEnabled(false);
    sendButton.setEnabled(false);

    pageUpButton.setEnabled(false);
    pageDownButton.setEnabled(false);

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

  private void executeContinue() {
    disableComponents();
    if (scanJob != null) {
      LoggerUtil.i("continue scan");
      scanJob.continueScan();
    } else {
      LoggerUtil.i("scan job nll");
    }
  }

  private void executeSend() {
    List<String> imgUrls = documentJob.getDocumentList();
    // Send
    for (String imgUrl : imgUrls) {
      LoggerUtil.i(imgUrl);
      // try {
      // ByteArrayOutputStream os = createByteArrayOutputStreamFromFile(imgUrl);
      // FTPUtil.uploadFile("pc1511sq", 21, "anonymous", "", imgUrl, "result.pdf");
      //// EmailUtil.sendEmailWithAttachment("hiepnvh@gmail.com", "test", "test", os);
      // } catch (IOException e) {
      // LoggerUtil.i(e.getMessage());
      // }
    }

  }

  // private ByteArrayOutputStream createByteArrayOutputStreamFromFile(String filePath) throws
  // IOException {
  // File file = new File(filePath);
  // LoggerUtil.i(file.getAbsolutePath());
  // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
  //
  // try (FileInputStream fileInputStream = new FileInputStream(file)) {
  // LoggerUtil.i("created fileInputStream");
  // byte[] buffer = new byte[1024];
  // int bytesRead;
  //
  // // Read the file and write to ByteArrayOutputStream
  // while ((bytesRead = fileInputStream.read(buffer)) != -1) {
  // byteArrayOutputStream.write(buffer, 0, bytesRead);
  // }
  // }
  // LoggerUtil.i("created byteArrayOutputStream");
  // return byteArrayOutputStream;
  // }

  private void createCacheImage() {

    // send to destination
    LoggerUtil.i("createCacheImage...");
    // UserBox userbox = fileBox.getObjectHandle();

    String targetPath = "test";
    documentJob = new SingleDocumentJob(targetPath);
    try {
      MeapBox meapBox = (MeapBox) boxScanRequest.getBox(jobService.accessControlToken);
      documentJob.addPage(meapBox.getHandle(jobService.accessControlToken));
      LoggerUtil.i("Create cache done");
    } catch (AccessControlException e) {
      LoggerUtil.i(e.getMessage());
    } catch (UnavailableMethodException e) {
      LoggerUtil.i(e.getMessage());
    } catch (ImagingException e) {
      LoggerUtil.i(e.getMessage());
    } catch (IOException e) {
      LoggerUtil.i(e.getMessage());
    } catch (OperationFailureException e) {
      LoggerUtil.i(e.getMessage());
    }
  }

  // private void executeDel() {
  //
  // /* Invalidate the user interface */
  // disableComponents();
  //
  // try {
  //
  // /* Delete the folder object */
  //// fileBox.deleteFolder();
  //
  // } catch (OperationFailureException oe) {
  // // logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
  // }
  //
  // return;
  // }

  private void executeCancel() {
    scanJob.endScan();
  }

  /**
   */
  private void executeScan() {

    addScanRequestListener();
    scanJob = new ScanJob(boxScanRequest);

    if (jobService.isSendAvailable()) {

      disableComponents();

      if (false == scanJob.startScan()) {

        enableComponents();

        // scanJob = null;

        return;
      }

    } else {

      displayMessage("Cannot submit the job.");
    }

    // scanJob = null;

    return;
  }

  /**
   *
   * @param ae ActionEvent
   */
  public void actionPerformed(ActionEvent ae) {

    // dispFileBoxNo();

    if (ae.getSource() == pageUpButton) {
      if (dispPage > 0) {
        dispPage--;
        // dispFolderLists();
        // dispPageButtons();
      }
    }

    if (ae.getSource() == pageDownButton) {
      if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX) {
        dispPage++;
        // dispFolderLists();
        // dispPageButtons();
      }
    }

    if (ae.getSource() == sendButton) {
      executeSend();
    }

    if (ae.getSource() == delButton) {
      // executeDel();
      executeCancel();
    }

    if (ae.getSource() == scanButton) {
      executeScan();
    }

    if (ae.getSource() == continueButton) {
      executeContinue();
    }

    return;
  }


  /**
   */
  private class BoxScanJobEventReceiver extends BoxScanJobEventAdapter {

    private ExecutorService imageProcExecutor;
    private JobState jobState;

    BoxScanJobEventReceiver() {
      imageProcExecutor = Executors.newSingleThreadExecutor(
          new NamedThreadFactory(getClass().getSimpleName() + ".imageProcExecutor"));
    }

    public void jobDeleted(BoxScanJobDeletedEvent event) {
      LoggerUtil.i("jobDeleted");
      enableComponents();
    }

    public void jobScanPageCount(BoxScanJobScanPageCountEvent event) {
      LoggerUtil.i("jobScanPageCount");
      displayMessage("Scanned " + String.valueOf(event.getCount()) + " pages");
      imageProcExecutor.submit(new ImageProcRunnable(jobState));
    }

    public void jobScanImagesStoreCompleted(BoxScanJobScanImagesStoreCompletedEvent event) {
      LoggerUtil.i("jobScanImagesStoreCompleted");
      displayMessage(
          "Scanned " + String.valueOf(event.getJobId()) + " done, stored, creating cache images");
      createCacheImage();
    }

    public void jobStateChanged(final BoxScanJobStateChangedEvent event) {
      LoggerUtil.i("jobStateChanged " + event.getJobState().getState());
      jobState = event.getJobState();
      if (jobState != null && jobState.getState() == JobState.STATE_COMPLETED) {
        imageProcExecutor.submit(new ImageProcCompleteRunnable());
        imageProcExecutor.shutdown();
        // continueButton.setEnabled(true);
        displayMessage("Job state completed, jobid " + String.valueOf(event.getJobId()) + "");

      } else if (jobState.getState() == JobState.STATE_INTERACTION
          && jobState.getReason() == JobState.REASON_OPERATED_BY_OPERATOR) {
        // continueButton.setEnabled(true);
        // event when there are some errors
      }

    }

  }/* end class BoxScanJobEventReceiver */

  @AllArgsConstructor
  private class ImageProcCompleteRunnable implements Runnable {
    @Override
    public void run() {
      // end scan job
      try {
        documentJob.endJob();
      } catch (IOException e) {
        LoggerUtil.i(e.toString());
      }
      // delete all docs
      // remove listener
      removeScanEventListener();
    }
  }

  @Value
  private class ImageProcRunnable implements Runnable {
    private JobState jobState;

    public ImageProcRunnable(JobState jobState) {
      this.jobState = jobState;
    }

    @Override
    public void run() {
      // if job state is ready, then can continue scan
      // if (jobState == JobState.)
    }
  }

}/* end class FolderListPanel */

/* end FolderListPanel.java */
