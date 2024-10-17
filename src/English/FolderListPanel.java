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
import com.canon.meap.imi.job.boxscan.BoxScanJobScanImageStoredCountEvent;
import com.canon.meap.imi.job.boxscan.BoxScanJobScanPageCountEvent;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.avs.CAppletContext;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.log.Logger;

/**
 * Box scan sample program, FolderListPanel class
 *
 * @version     2.01  2004/09/01
 * @author
 */
public class FolderListPanel extends Panel implements ActionListener {

    /**
     * version ID for serialized form.
     */
    private static final long serialVersionUID = 3932463120881006951L;

    /* Job service class  */
    private JobService jobService;

    /* File box class */
    private FileBox fileBox;

    /* Scan job class */
    private ScanJob scanJob;

    /* Label of the folder attribtue's header */
    private CLabel nameHeader;
    private CLabel pagesHeader;
    private CLabel dateTimeHeader;

    /* Folder information panel */
    private Panel folderInfoPanel;

    /* Label of the folder attribute */
    private CLabel[] nameLabel;
    private CLabel[] pageLabel;
    private CLabel[] dateLabel;
    private CLabel[] timeLabel;

    /* Folder information line */
    private Panel[] folderInfoLine;
    private CHorizontalLine partitionLine;

    /* Page switch button */
    private CArrowButton pageUpButton;
    private CArrowButton pageDownButton;
    private CLabel pageCountLabel;

    /* Job button */
    private CLabelButton scanButton;
    private CLabelButton sendButton;
    private CLabelButton delButton;

    /* Message label */
    private CLabel messageLabel;

    /* Constant */
    private static final int FOLDER_INFO_FONT = 16;
    private static final int FOLDER_INFO_MAX = 8;

    /* EventListener */
    private MouseEventAdapter mouseEventAdapter;
    private BoxEventReceiver boxEventReceiver;
    private BoxScanJobEventReceiver scanJobEventReceiver;
    private BoxScanRequest boxScanRequest;
//    private BoxPrintJobEventReceiver printJobEventReceiver;
    
    private Logger logger;
    private LoginContext loginContext;

    /* Variable */
    private int dispPage;
    private int dispFolderCount;
    private boolean disableUI;

    /**
     * Constructor
     */
    public FolderListPanel() {
        super();

        setSize(CAppletContext. MAX_APPLET_WIDTH,
                CAppletContext. MAX_APPLET_HEIGHT);
        setLayout(null);
        setBackground(CColor.gainsboro);

        /* Configure each of the components */
        locateHeader();
        locateFolderLists();
        locatePageButtons();
        locateJobButtons();
        locateMessage();

        setVisible(false);
        
        logger = AppletActivator.getAppletActivator().getLogService().getLogger(LogService.LOGKIND_APP);
        loginContext = BoxScanApplet.getBoxScanApplet().getLoginContext();

        /* Generate the mouse eventListener */
        mouseEventAdapter = new MouseEventAdapter();
    }

    /**
     * Perform the display of the window
     */
    public void display() {
      logger.log(loginContext, Logger.LOG_LEVEL_INFO, "Display xxx");

        /* Create the job service */
        jobService = new JobService();

        /* Create the file box */
        fileBox = new FileBox();

        try {

            /* Activate the file box */
            fileBox.activate();

            /* Acquire the count of the folderes */
            dispFolderCount = fileBox.getFolderCount();

            dispPage = 0;

            /* Display each of the components */
            dispFolderLists();
            dispPageButtons();
            dispJobButtons();
            dispFileBoxNo();

            /* Valid the user interface */
            enableComponents();

            /* Define the CPCA Eventlistener */
            addCpcaEventAdapter();

        } catch (OperationFailureException oe) {
            logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
        }

        setVisible(true);

        return;
    }

    /**
     * Invalidate the window
     */
    public void unDisplay() {

        /* Delete the CPCAEventListener */
            removeCpcaEventAdapter();
            removeScanEventListener();

        /* Invalidate the user interface */
        disableComponents();

        dispPage = 0;
        dispFolderCount = 0;

        fileBox = null;

        jobService = null;

        setVisible(false);

        return;
    }

    /**
     * Configure the label of folder attribute's header
     */
    private void locateHeader() {

        /* Folder name */
        nameHeader = new CLabel("Name", CLabel.LEFT);
        nameHeader.setBounds(30, 15, 200, 20);
        add(nameHeader);

        /* Page number */
        pagesHeader = new CLabel("Pages", CLabel.LEFT);
        pagesHeader.setBounds(230, 15, 80, 20);
        add(pagesHeader);

        /* Date and time */
        dateTimeHeader = new CLabel("Date Time", CLabel.LEFT);
        dateTimeHeader.setBounds(310, 15, 110, 20);
        add(dateTimeHeader);

        return;
    }

    /**
     * Set the folder information
     */
    private void locateFolderLists() {

        /* Create the folder information line */
        folderInfoLine = new Panel[FOLDER_INFO_MAX];

        /* Create the label of folder attribute */
        nameLabel = new CLabel[FOLDER_INFO_MAX];
        pageLabel = new CLabel[FOLDER_INFO_MAX];
        dateLabel = new CLabel[FOLDER_INFO_MAX];
        timeLabel = new CLabel[FOLDER_INFO_MAX];

        /* Create the folder information panel */
        folderInfoPanel = new Panel();
        folderInfoPanel.setBounds(30, 40, 385, (30 * FOLDER_INFO_MAX) - 2);
        folderInfoPanel.setLayout(null);
        folderInfoPanel.setBackground(CColor.white);

        add(folderInfoPanel);

        /* Set each attribute in the folder */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            /* Folder name */
            nameLabel[i] = new CLabel("", CLabel.LEFT);
            nameLabel[i].setBounds(0, 2, 198, 30);
            nameLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* Page number */
            pageLabel[i] = new CLabel("", CLabel.LEFT);
            pageLabel[i].setBounds(200, 2, 78, 30);
            pageLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* Date */
            dateLabel[i] = new CLabel("", CLabel.LEFT);
            dateLabel[i].setBounds(280, 2, 53, 30);
            dateLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* Time */
            timeLabel[i] = new CLabel("", CLabel.LEFT);
            timeLabel[i].setBounds(330, 2, 53, 30);
            timeLabel[i].setFont(
                    new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));

            /* Create the folder information line */
            folderInfoLine[i] = new Panel();
            folderInfoLine[i].setLayout(null);
            folderInfoLine[i].setBounds(0, 30 * i, 385, 30);
            folderInfoLine[i].setBackground(CColor.white);

            /* Create the ruled line */
            if (i < (FOLDER_INFO_MAX - 1)) {
                partitionLine = new CHorizontalLine(CHorizontalLine.LOWERED);
                partitionLine.setBounds(0, 30 - 2, 385, 2);
                folderInfoLine[i].add(partitionLine);
            }

            /* Paste the folder attributes' label to folder information line */
            folderInfoLine[i].add(nameLabel[i]);
            folderInfoLine[i].add(pageLabel[i]);
            folderInfoLine[i].add(dateLabel[i]);
            folderInfoLine[i].add(timeLabel[i]);

            /* Add the folder information line to folder information panel */
            folderInfoPanel.add(folderInfoLine[i]);
        }

        return;
    }

    /**
     * Display the folder information panel
     */
    private void dispFolderLists() {

        int startNumber;
        String stringPageSize = null;

        /* Check the display page */
        startNumber = dispPage * FOLDER_INFO_MAX;
        if ((startNumber >= dispFolderCount) && (dispPage > 0)) {
            dispPage--;
            startNumber = dispPage * FOLDER_INFO_MAX;
        }

        /* Display each attribute of all the folders in the display page */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {

            if (i + startNumber < dispFolderCount) {

                /* Folder name */
                nameLabel[i].setText(fileBox.getFolderName(i + startNumber));

                /* Page number */
                stringPageSize = "000" + Long.toString(
                        fileBox.getPageSize(i + startNumber));
                pageLabel[i].setText(
                        stringPageSize.substring(stringPageSize.length() - 4));

                /* Date */
                dateLabel[i].setText(convTimeStampToDateString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* Time */
                timeLabel[i].setText(convTimeStampToTimeString(
                        fileBox.getTimeStamp(i + startNumber)));

                /* Visualize the folder information line */
                folderInfoLine[i].setVisible(true);

                /* Adjust the color of the selected folder information line */
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
     * Return the date in the form of "MM/DD"
     *
     * @param   dateTimes  Date and time
     *
     * @return  Date(MM/DD)
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
     * Return the time in the form of "HH:MM"
     *
     * @param   dateTimes  Date and time
     *
     * @return  Time(HH:MM)
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
     * Adjust the color of the folder information line
     *
     * @param  lineNo  Folder information LineNo
     * @param  color  Background color
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
     * Configure the page switch button
     */
    private void locatePageButtons() {

        /* [Up] button */
        pageUpButton = new CArrowButton(CArrowButton.ARROW_UP);
        pageUpButton.setBounds(520, 40, 40, 40);
        pageUpButton.addActionListener(this);
        add(pageUpButton);

        /* [Down] button */
        pageDownButton = new CArrowButton(CArrowButton.ARROW_DOWN);
        pageDownButton.setBounds(520, 110, 40, 40);
        pageDownButton.addActionListener(this);
        add(pageDownButton);

        /* Page label */
        pageCountLabel = new CLabel();
        pageCountLabel.setBounds(515, 85, 50, 20);
        pageCountLabel.setFont(
                new Font("Dialog", Font.BOLD, FOLDER_INFO_FONT));
        add(pageCountLabel);

        return;
    }

    /**
     * Display the page switch button
     */
    private void dispPageButtons() {

        /* [Up] button */
        if (dispPage > 0) {
            pageUpButton.setEnabled(true);
        } else {
            pageUpButton.setEnabled(false);
        }

        /* [Down] button */
        if (dispPage < (dispFolderCount -1) / FOLDER_INFO_MAX ) {
            pageDownButton.setEnabled(true);
        } else {
            pageDownButton.setEnabled(false);
        }

        /* Page label */
        pageCountLabel.setText((dispPage + 1)
                + "/" + (((dispFolderCount - 1) / FOLDER_INFO_MAX ) + 1));

        return;
    }

    /**
     * Configure the job button
     */
    private void locateJobButtons() {

        /* [Scan] button */
        scanButton = new CLabelButton(
                "Scan", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        scanButton.setBounds(90, 290, 116, 42);
        scanButton.addActionListener(this);
        add(scanButton);
        
        /* [Send] button */
        sendButton = new CLabelButton(
                "Send", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        sendButton.setBounds(220, 290, 116, 42);
        sendButton.addActionListener(this);
        add(sendButton);
        
        /* [Cancel] button */
        delButton = new CLabelButton(
                "Cancel", CLabelButton.CENTER, CLabelButton.CENTER,
                CColor.black, CLabelButton.ARROW_NONE);
        delButton.setBounds(350, 290, 116, 42);
        delButton.addActionListener(this);
        add(delButton);

        return;
    }

    /**
     * Display the job button
     */
    private void dispJobButtons() {

        /**
         * Validate the [Delete] [Scan] [Print] button while folder
         * is selected
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
     * Configure the message label
     */
    private void locateMessage() {

        /* Message label */
        messageLabel = new CLabel();
        messageLabel.setBounds(10, 340, 600, 30);
        add(messageLabel);
        messageLabel.setBackground(CColor.white);

        return;
    }

    /**
     * Display the file box number
     */
    private void dispFileBoxNo() {

        StringBuffer messageFileBoxNo = null;
        String stringFileBoxNo = null;
        int intFileBoxNo = 0;

        messageFileBoxNo = new StringBuffer("FileBox No. : ");

        /* Acquire the file box number */
        intFileBoxNo = fileBox.getFileBoxNo();
        if(intFileBoxNo < 10) {
            messageFileBoxNo.append('0');
        }
        stringFileBoxNo = new Integer(intFileBoxNo).toString();
        messageFileBoxNo.append(stringFileBoxNo);

        /* Display the file box number */
        displayMessage(messageFileBoxNo.toString());

        return;
    }

    /**
     * Display the message
     *
     * @param  Message
     */
    private void displayMessage(String message) {

        messageLabel.setText(message);

        return;
    }

    /**
     * Valid the user interface
     */
    private void enableComponents() {

        /* Add the MouseListener */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].addMouseListener(mouseEventAdapter);
            pageLabel[i].addMouseListener(mouseEventAdapter);
            dateLabel[i].addMouseListener(mouseEventAdapter);
            timeLabel[i].addMouseListener(mouseEventAdapter);
            folderInfoLine[i].addMouseListener(mouseEventAdapter);
        }

        /* Validate the button component */
        dispJobButtons();
        dispPageButtons();

        disableUI = false;

        return;
    }

    /**
     * Invalidate the user interface
     */
    private void disableComponents() {

        /* Add the MouseListener */
        for (int i = 0; i < FOLDER_INFO_MAX; i++) {
            nameLabel[i].removeMouseListener(mouseEventAdapter);
            pageLabel[i].removeMouseListener(mouseEventAdapter);
            dateLabel[i].removeMouseListener(mouseEventAdapter);
            timeLabel[i].removeMouseListener(mouseEventAdapter);
            folderInfoLine[i].removeMouseListener(mouseEventAdapter);
        }

        /* Delete the MouseListener */
        scanButton.setEnabled(false);

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
            BoxManager manager = BoxManager.getInstance(
                                        AppletActivator._bundle,
                                        jobService.accessControlToken);
            /* Registers an event listener */
            manager.addBoxEventListener(AppletActivator._bundle,
                                        jobService.accessControlToken,
                                        boxEventReceiver);

        } catch (OperationFailureException oe) {
            logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
        }

        return;
    }
    
    private void addScanRequestListener() {
//      scanJobEventReceiver = new BoxScanJobEventReceiver();

      try {
          /* Obtains a job management instance */
//          BoxScanJobManager manager = BoxScanJobManager.getInstance(
//                                      AppletActivator._bundle,
//                                      jobService.accessControlToken);
//          /* Registers an event listener */
//          manager.addBoxScanJobManagerEventListener(
//                                      jobService.accessControlToken,
//                                      scanJobEventReceiver);
        boxScanRequest = BoxScanRequest.createInstance(jobService.accessControlToken);
//        boxScanRequest.addBoxScanJobEventListener(jobService.accessControlToken, scanJobEventReceiver);
      } catch (OperationFailureException oe) {
          logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
      }

    }

    /**
     * Delete the CPCAEventListener
     */
    private void removeCpcaEventAdapter() {

        if ( null != boxEventReceiver ) {

            try {
                /* Obtains an instance of the box management class */
                BoxManager manager = BoxManager.getInstance(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken);
                /* Deletes listeners that receive events */
                manager.removeBoxEventListener(
                                            AppletActivator._bundle,
                                            jobService.accessControlToken,
                                            boxEventReceiver);

            } catch (OperationFailureException oe) {
                logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
            }

            boxEventReceiver = null;
        }

        return;
    }
    
    private void removeScanEventListener() {
      if ( null != scanJobEventReceiver  && null != boxScanRequest) {

//        try {
            /* Obtains a job management instance */
//            BoxScanJobManager manager = BoxScanJobManager.getInstance(
//                                        AppletActivator._bundle,
//                                        jobService.accessControlToken);
//            /* Deletes listeners that receive events */
//            manager.removeBoxScanJobManagerEventListener(
//                                        jobService.accessControlToken,
//                                        scanJobEventReceiver);
//          boxScanRequest.removeBoxScanJobEventListener(jobService.accessControlToken, scanJobEventReceiver);
//
//        } catch (OperationFailureException oe) {
//            logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
//        }

        scanJobEventReceiver = null;
    }
    }

    /**
     * Perform the scan
     * @throws OperationFailureException 
     * @throws UnavailableMethodException 
     * @throws AccessControlException 
     */
    private void executeScan() {

      try {
        addScanRequestListener();
//        boxScanRequest = BoxScanRequest.createInstance(jobService.accessControlToken);
        /* Create the scan job */
        scanJob = new ScanJob(boxScanRequest);

        /* Checks whether or not a job can be submitted */
        if (jobService.isSendAvailable()) {

            /* Invalidate the user interface */
            disableComponents();

            /* Notify the start of the scan to scan job */
            if (false == scanJob.startScan(fileBox.getObjectHandle())) {

                /* Valid the user interface */
                enableComponents();

                scanJob = null;

                return;
            }

        } else {

            /* Display a message that indicates the job cannot be executed */
            displayMessage("Cannot submit the job.");
        }

        scanJob = null;

        return;
      } catch (AccessControlException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (UnavailableMethodException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
        
    }
    
    private void executeSend() {

      //send to destination
      displayMessage("Sending...");
  }
    
    private void executeDel() {

      /* Invalidate the user interface */
      disableComponents();

      try {

          /* Delete the folder object */
          fileBox.deleteFolder();

      } catch (OperationFailureException oe) {
          logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
      }

      return;
  }

    /**
     * Perform the process when actionPerformed occurs
     *
     * @param  ae  ActionEvent
     */
    public void actionPerformed(ActionEvent ae) {

        /* Display the file box number */
        dispFileBoxNo();

        /* Display previous page */
        if (ae.getSource() == pageUpButton) {
            if (dispPage > 0) {
                dispPage--;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* Display next page */
        if (ae.getSource() == pageDownButton) {
            if (dispPage < (dispFolderCount - 1) / FOLDER_INFO_MAX ) {
                dispPage++;
                dispFolderLists();
                dispPageButtons();
            }
        }

        /* Perform the scan */
        if (ae.getSource() == scanButton) {
            executeScan();
        }
        
        if (ae.getSource() == sendButton) {
          executeSend();
      }
        
        if (ae.getSource() == delButton) {
          executeDel();
      }

        return;
    }


    /**
     * MouseEvent receiver class
     */
    private class MouseEventAdapter extends MouseAdapter {

    /**
     * Perform the process when mousePressed occurs
     *
     * @param  me MouseEvent
     */
        public void mousePressed(MouseEvent me) {

            /* Change all the selected folderes to unselect state */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {
                setInfoLineBackground(i, CColor.white);
            }

            /* Check the selected folder */
            for (int i = 0; i < FOLDER_INFO_MAX; i++) {

                if ((me.getComponent() == nameLabel[i]) ||
                    (me.getComponent() == pageLabel[i]) ||
                    (me.getComponent() == dateLabel[i]) ||
                    (me.getComponent() == timeLabel[i]) ||
                    (me.getComponent() == folderInfoLine[i])) {

                    /* Select the folder if the folder is not selected */
                    if (((dispPage * FOLDER_INFO_MAX) + i)
                            != fileBox.getSelectFolderNo()) {
                        fileBox.setSelectFolderNo(
                                (dispPage * FOLDER_INFO_MAX) + i);
                        setInfoLineBackground(i, CColor.powderblue);

                    /* Clear the selection if the folder is selected */
                    } else {
                        fileBox.resetSelectFolderNo();
                    }

                    /* Display the job button */
                    dispJobButtons();
                    break;
                }
            }

            return;
        }

    }/* end class MouseEventAdapter */


    /**
     * Cpca event receiver class
     *
    private class CpcaEventAdapter extends EventAdapter {

        /**
         * Start receiving the event
         *
         * @param   object  Event occurence object
         *
         * @return  true : Perform event delivery
         *          false: Not perform event delivery
         *
        public boolean beginEvent(long object) {
        }

        /**
         * Terminate receiving the event
         *
         * @param  object  Event occurence object
         *
        public void endEvent(long object) {
        }

        /**
         * Perfrom the process when reportFileBoxContentChange occurs
         *
         * @param  object  Event occurence object
         * @param  report  content
         *
         * @return  true : Perform event delivery
         *          false: Not perform event delivery
         *
        public boolean reportFileBoxContentChange(
                long object, ReportFileBoxContentChange report) {
        }

        /**
         * Perform the prcess when reportObjectDeleted2 occurs
         *
         * @param  object  Event occurence object
         * @param  report  Event content
         *
         * @return  true : Perform event delivery
         *          false: Not perform event delivery
         *
        public boolean reportObjectDeleted2(
                long object, ReportObjectDeleted2 report) {
        }

    }* end class CpcaEventAdapter */

    /**
     * Box event receiver class
     */
    private class BoxEventReceiver extends BoxEventAdapter {

        /**
         * It is called when a document is added to a box.
         *
         * @param   event   An event object
         */
        public void boxContentAppended(BoxContentAppendedEvent event) {

            /* Update the contents of the file box */
            try {

                /* Update the folder information */
                fileBox.updateFolderInfo();

                /* Acquire the count of the folderes */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
            }

            /* Redisplay the folder information panel */
            fileBox.resetSelectFolderNo();
            dispFolderLists();
            dispPageButtons();
        }

        /**
         * It is called when a document is deleted from a box.
         *
         * @param   event   An event object
         */
        public void boxContentDeleted(BoxContentDeletedEvent event) {

            /* Update the contents of the file box */
            try {

                /* Update the folder information */
                fileBox.updateFolderInfo();

                /* Acquire the count of the folderes */
                dispFolderCount = fileBox.getFolderCount();

            } catch (OperationFailureException oe) {
                logger.log(loginContext, Logger.LOG_LEVEL_INFO, oe.getMessage());
            }

            /* Redisplay the folder information panel */
            fileBox.resetSelectFolderNo();
            dispFolderLists();

            /* Delete the folder */
            if (disableUI == true) {

                /* Valid the user interface */
                enableComponents();
            }
        }

    }/* end class BoxEventReceiver */

    /**
     * BoxScanJob event receiver class
     */
    private class BoxScanJobEventReceiver
            extends BoxScanJobEventAdapter {

        /**
         * It is called if a job is deleted within the device.
         *
         * @param   event   An event object
         */
        public void jobDeleted(BoxScanJobDeletedEvent event) {

            /* Valid the user interface */
            enableComponents();
        }
        
        public void jobScanPageCount(BoxScanJobScanPageCountEvent event) {
          displayMessage("Scanning " + event.getCount() + " pages");
        }
        
        public void jobScanImageStoredCount(BoxScanJobScanImageStoredCountEvent event) {
          //completed
          displayMessage("Scanning completed " + event.getCount() + " pages");
        }

    }/* end class BoxScanJobEventReceiver */

}/* end class FolderListPanel */

/* end FolderListPanel.java */
