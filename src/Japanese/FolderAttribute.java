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
 * ＢＯＸスキャンサンプルプログラム　フォルダ属性クラス
 *
 * @version     1.01  2004/09/01
 * @author
 */
public class FolderAttribute {

    /* フォルダオブジェクト */
    public UserBoxDocument objectHandle;

    /* フォルダ名 */
    public String folderName;

    /* ページ数 */
    public long pageSize;

    /* 日付・時刻 */
    public Calendar timeStamp;

    /**
     * コンストラクタ
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
