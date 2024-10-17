//****************************************************************************
//
// Copyright CANON INC. 2010
// 
//
// FolderAttribute.java
//
// MEAP SDK
//
// Version 2.10
//
//***************************************************************************

import java.util.Calendar;

import com.canon.meap.imi.box.userbox.*;

/**
 * Box scan sample program, FolderAttribute class
 *
 * @version     1.01  2004/09/01
 * @author
 */
public class FolderAttribute {

    /* Folder object */
    public UserBoxDocument objectHandle;

    /* Folder name */
    public String folderName;

    /* Page count */
    public long pageSize;

    /* Date and time */
    public Calendar timeStamp;

    /**
     * Constructor^
     */
    public FolderAttribute() {
        super();

        objectHandle = null;
        folderName = "";
        pageSize = 0;
        timeStamp = null;
    }

}/* end class FolderAttribute */

/* end FolderAttribute.java */
