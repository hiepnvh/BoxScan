//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// FolderInfo.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import java.util.Calendar;

import com.canon.meap.imi.InvalidPasswordException;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.box.userbox.UserBoxDocument;
import com.canon.meap.imi.data.UserBoxDocumentList;
import com.canon.meap.security.AccessControlToken;

/**
 * Box scan sample program, FolderInfo class
 *
 * @version     2.02  2004/09/01
 * @author
 */
public class FolderInfo {

    /* Folder attribute class  */
    private FolderAttribute[] folderAttribute;

    /* Folder account */
    private int folderCount;

    /**
     * Constructor
     */
    public FolderInfo() {
        super();

    }

    /**
     * Update the folder information
     *
     * @param      fileBoxObjectHandle  File box Object
     * @param      accessControlToken
     *
     * @exception  OperationFailureException operation exception
     */
    public void updateFolderInfo(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList outParamListObjects2 = null;

        folderAttribute = null;
        folderCount = 0;

        try {

            /* Acquire the folder list  */
            outParamListObjects2 = fetchFolderList(
                    fileBoxObjectHandle, accessControlToken);

            /* Update the folder attribute if the folder is stored */
            if (outParamListObjects2.size() > 0) {

                /* Create the folder attribute class  */
                folderAttribute
                        = new FolderAttribute[outParamListObjects2.size()];
                folderCount = outParamListObjects2.size();

                /* Set the folder attribute  */
                setFolderAttribute(outParamListObjects2, accessControlToken);
            }

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Acquire the folder list
     *
     * @param      fileBoxObjectHandle  File box Object
     * @param      accessControlToken
     *
     * @return     Folder list
     *
     * @exception  OperationFailureException operation exception
     */
    private UserBoxDocumentList fetchFolderList(UserBox fileBoxObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        UserBoxDocumentList userBoxDocumentList = null;

        /* Submit the ListFile operation  */
        try {
            /* Obtains a list of documents that are stored in the box */
            userBoxDocumentList = fileBoxObjectHandle.getDocumentList(
                    accessControlToken);

        } catch(InvalidPasswordException ce) {
            throw new OperationFailureException(
                    "getDocumentList operation failed. " + ce.getMessage());
        } catch (OperationFailureException oe) {
            throw oe;
        }

        return userBoxDocumentList;
    }

    /**
     * Acquire the folder attribute list
     *
     * @param      outParamListFiles  Folder list
     * @param      accessControlToken
     *
     * @return     Folder attribute list
     *
     * @exception  OperationFailureException operation exception
     *
    private ObjAttribListInfo2[] fetchFolderAttribute(
            OutParamListObjects2 outParamListObjects2,
            AccessControlToken accessControlToken)
            throws OperationFailureException {
    }*/

    /**
     * Set the folder attribute
     *
     * @param  objAttribListInfo  Folder attribute list
     */
    private void setFolderAttribute(UserBoxDocumentList objAttribListInfo2,
                                    AccessControlToken accessControlToken) {

        String stringFolderName = null;

        /* Set the folder attribute  */
        for (int i = 0; i < objAttribListInfo2.size(); i++) {

            folderAttribute[i] = new FolderAttribute();

            /* Folder object */
            folderAttribute[i].objectHandle = objAttribListInfo2.get(i);

            try {
            /* Folder name  */
                stringFolderName =
                    objAttribListInfo2.get(i).getDocumentName(
                            accessControlToken);

                /* Page number  */
                folderAttribute[i].pageSize =
                    objAttribListInfo2.get(i).getPageCount(
                            accessControlToken);

                /* Date and time  */
                folderAttribute[i].timeStamp =
                    objAttribListInfo2.get(i).getCreateDate(
                            accessControlToken);

            } catch (Throwable e) {
            }

            if (stringFolderName != null) {
                folderAttribute[i].folderName = stringFolderName;
            } else {
                folderAttribute[i].folderName = "";
            }
        }

        return;
    }

    /**
     * Return the folder attribute value
     *
     * @param   objAttribListInfo  Folder attribute list
     * @param   attribute   Folder attribute
     *
     * @return    Folder attribute value
     *
    private Object findAttributeValue(
            ObjAttribListInfo2 objAttribListInfo2, int attribute) {
    }*/

    /**
     * Delete the folder object
     *
     * @param   folderObjectHandle  Folder object
     * @param   accessControlToken
     *
     * @exception  OperationFailureException operation exception
     */
    public void deleteFolder(UserBoxDocument folderObjectHandle,
            AccessControlToken accessControlToken)
            throws OperationFailureException {

        /* Submit the DeleteFile operation */
        try {

            /* Obtains the box to which the data is stored */
            UserBox box = (UserBox)folderObjectHandle.getParentBox(
                                                    accessControlToken);

            /* Creates the list of user box documents */
            UserBoxDocumentList list = new UserBoxDocumentList();

            /* Adds user box documents */
            list.add(folderObjectHandle);

            /* Deletes user box documents */
            box.deleteDocument(accessControlToken, list);

        } catch(InvalidPasswordException ipe) {
            throw new OperationFailureException("Invalid password.");

        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Return the folder count
     *
     * @return  Folder account
     */
    public int getFolderCount() {
        return folderCount;
    }

    /**
     * Return the folder object
     *
     * @param   folderNo  Folder number
     *
     * @return  Folder object
     */
    public UserBoxDocument getObjectHandle(int folderNo) {
        return folderAttribute[folderNo].objectHandle;
    }

    /**
     * Return the folder name
     *
     * @param   folderNo  Folder number
     *
     * @return  Folder name
     */
    public String getFolderName(int folderNo) {
        return folderAttribute[folderNo].folderName;
    }

    /**
     * Return the page count
     *
     * @param   folderNo  Folder number
     *
     * @return  Page number
     */
    public long getPageSize(int folderNo) {
        return folderAttribute[folderNo].pageSize;
    }

    /**
     * Return date and time
     *
     * @param   folderNo  Folder number
     *
     * @return  Date and time
     */
    public Calendar getTimeStamp(int folderNo) {
        return folderAttribute[folderNo].timeStamp;
    }

}/* end class FolderInfo */

/* end FolderInfo.java */
