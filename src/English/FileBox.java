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
 * Box scan sample program, FileBox class
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FileBox {

    /* Folder information class */
    private FolderInfo folderInfo;

    /* File box Object */
    private UserBox objectHandle;

    /* File box number  */
    private int fileBoxNo;

    /* Number of the selected folder  */
    private int selectFolderNo;

    /* AccessControlToken */
    private AccessControlToken accessControlToken;

    /**
     * Constructor
     */
    public FileBox() {
        super();

    }

    /**
     * Activate the file box
     *
     * @exception  OperationFailureException operation exception
     */
    public void activate() throws OperationFailureException {

        fileBoxNo = Integer.MAX_VALUE;
        selectFolderNo = Integer.MAX_VALUE;

        try {

            /* Acquire the Access Control Token  */
            fetchAccessControlToken();

            /* Select the file box */
            selectFileBox();

            /* Create the folder information */
            folderInfo = new FolderInfo();

            /* Update the folder information  */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Terminate the file box
     *
     * @exception  OperationFailureException operation exception
     *
    public void deactivate() throws OperationFailureException {
    }*/

    /**
     * Acquire the Access Control Token
     *
     * @exception  OperationFailureException operation exception
     */
    private void fetchAccessControlToken() throws OperationFailureException {

        BoxScanApplet boxScanApplet = null;
        LoginContext loginContext = null;
        AppletActivator appletActivator = null;
        SecurityAgent securityAgent = null;

        /* Acquire the Applet */
        boxScanApplet = BoxScanApplet.getBoxScanApplet();

        /* Acquire the loginContext from applet */
        loginContext = boxScanApplet.getLoginContext();

        /* Acquire the appletActivator */
        appletActivator = AppletActivator.getAppletActivator();

        /* Acquire the SecurityAgent from AppletActivator  */
        securityAgent = appletActivator.getSecurityAgent();

        /* Acquire the Access Control Token from securityAgent  */
        accessControlToken = securityAgent.getAccessControlToken(loginContext);

        if (accessControlToken == null) {
            throw new OperationFailureException("ACT is null. ");
        }

        return;
    }

    /**
     * Select the file box
     *
     * @exception  OperationFailureException operation exception
     */
    private void selectFileBox() throws OperationFailureException {

        UserBox[] outParamListObjects2 = null;

        try {

            /* Acquire the fild box list  */
            outParamListObjects2 = fetchFileBoxList();

            /* Select file box if file box is installed */
            if (outParamListObjects2.length > 0) {

                /* Select the file box */
                choiceFileBox(outParamListObjects2);

                /* Check the file box selection result */
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
     * Acquire the fild box list
     *
     * @return     File box list
     *
     * @exception  OperationFailureException operation exception
     */
    private UserBox[] fetchFileBoxList()
            throws OperationFailureException {

        UserBox[] operationResult = null;

        /* Submit the ListFile operation */
        try {
            /* Obtains an instance of the box management class */
            BoxManager manager =
                        BoxManager.getInstance(AppletActivator.bundleContext.getBundle(),
                                               accessControlToken);
            /* Obtains a list of user boxes */
            operationResult = manager.getUserBoxList(AppletActivator.bundleContext.getBundle(),
                                                     accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return operationResult;
    }

    /**
     * Acquire the file box attribute list
     *
     * @param      outParamListFiles File box list
     *
     * @return     File box attribute list
     *
     * @exception  OperationFailureException operation exception
     *
    private ObjAttribListInfo2[] fetchFileBoxAttribute(
            OutParamListObjects2 outParamListObjects2)
            throws OperationFailureException {
    }*/

    /**
     * Select the file box
     *
     * @param  objAttribListInfo File box attribute list
     */
    private void choiceFileBox(UserBox[] objAttribListInfo2) {

        /* Select the file box */
        for (int i = 0; (i < objAttribListInfo2.length)
                && (fileBoxNo == Integer.MAX_VALUE); i++) {

            try {

                /* Permit the access when password does not exist */
                if (!objAttribListInfo2[i].isPasswordProtected(
                                                accessControlToken)) {

                /* Set the file box number */
                fileBoxNo = i;

                /* Set the file box object  */
                objectHandle = objAttribListInfo2[i];

                    break;
                }

            } catch (Throwable e) {
            }
        }

        return;
    }

    /**
     * Return the attribute value of file box
     *
     * @param   attribListInfo File box attribute list
     * @param   attribute  Fiel box attribute
     *
     * @return  File box attribute value
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * Register the file box content modification event
     *
     * @exception  OperationFailureException operation exception
     *
    private void appendFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * Erase the file box content modification event
     *
     * @exception  OperationFailureException operation exception
     *
    private void deleteFileBoxEvent() throws OperationFailureException {
    }*/

    /**
     * Return file box object
     *
     * @return      File box Object
     */
    public UserBox getObjectHandle() {
        return objectHandle;
    }

    /**
     * Update the folder information
     *
     * @exception  OperationFailureException operation exception
     */
    public void updateFolderInfo() throws OperationFailureException {

        try {

            /* Acquire the Access Control Token  */
            fetchAccessControlToken();

            /* Update the folder information  */
            folderInfo.updateFolderInfo(objectHandle, accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Delete the folder object
     *
     * @exception  OperationFailureException operation exception
     */
    public void deleteFolder() throws OperationFailureException {

        try {

            /* Acquire the Access Control Token  */
            fetchAccessControlToken();

            /* Delete the folder object */
            folderInfo.deleteFolder(
                    folderInfo.getObjectHandle(selectFolderNo),
                    accessControlToken);

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Return the selected folder number
     *
     * @return  Number of the selected folder
     */
    public int getSelectFolderNo() {
        return selectFolderNo;
    }

    /**
     * Set the selected folder number
     *
     * @param  folderNo Number of the selected folder
     */
    public void setSelectFolderNo(int folderNo) {

        selectFolderNo = folderNo;

        return;
    }

    /**
     * Initialize the selected folder number
     */
    public void resetSelectFolderNo() {

        selectFolderNo = Integer.MAX_VALUE;

        return;
    }

    /**
     * Return the selection state of folder
     *
     * @return  State indicates if folder is selected
     */
    public boolean isSelected() {

        if (selectFolderNo != Integer.MAX_VALUE) {
            return true;
        }

        return false;
    }

    /**
     * Return the file box number
     *
     * @return  File box number
     */
    public int getFileBoxNo() {
        return fileBoxNo;
    }

    /**
     * Return the folder count
     *
     * @return  Folder account
     */
    public int getFolderCount() {
        return folderInfo.getFolderCount();
    }

    /**
     * Return the folder object
     *
     * @return      Folder object
     */
    public UserBoxDocument getFolderObjectHandle() {
        return folderInfo.getObjectHandle(selectFolderNo);
    }

    /**
     * Return the folder name
     *
     * @return  Folder name
     */
    public String getFolderName(int folderNo) {
        return folderInfo.getFolderName(folderNo);
    }

    /**
     * Return the page count
     *
     * @return  Page number
     */
    public long getPageSize(int folderNo) {
        return folderInfo.getPageSize(folderNo);
    }

    /**
     * Return date and time
     *
     * @return  Date and time
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderInfo.getTimeStamp(folderNo);
    }

}/* end class FileBox */

/* end FileBox.java */
