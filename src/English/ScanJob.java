//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// ScanJob.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnacceptableException;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.data.Orientation;
import com.canon.meap.imi.data.StandardSize;
import com.canon.meap.imi.data.StandardSizeId;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.imi.job.boxscan.BoxScanRequest;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * Box scan sample program, ScanJob class
 *
 * @version     2.02  2004/06/21
 * @author
 */
public class ScanJob {

    /* AccessControlToken */
    private AccessControlToken accessControlToken;
    private BoxScanRequest boxScanRequest;

    /**
     * Constructor
     */
    public ScanJob(BoxScanRequest boxScanRequest) {
        super();
        this.boxScanRequest = boxScanRequest;
    }

    /**
     * Start the scan
     *
     * @param      fileBoxObjectHandle  File box Object
     */
    public boolean startScan(UserBox fileBoxObjectHandle) {

        try {

            /* Acquire the Access Control Token */
            fetchAccessControlToken();

            /* Submit the job script */
            performJobScript(fileBoxObjectHandle);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());

            return false;
        }

        return true;
    }

    /**
     * End the scan
     */
    public void endScan() {
    }

    /**
     * Acquire the Access Control Token
     *
     * @exception  OperationFailureException  operation exception
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

        /* Acquire the SecurityAgent from AppletActivator */
        securityAgent = appletActivator.getSecurityAgent();

        /* Acquire the Access Control Token from securityAgent */
        accessControlToken = securityAgent.getAccessControlToken(loginContext);

        if (accessControlToken == null) {
            throw new OperationFailureException("ACT is null. ");
        }

        return;
    }


    /**
     * Submit the job script
     *
     * @param      fileBoxObjectHandle  File box Object
     *
     * @exception  OperationFailureException  operation exception
     */
    private void performJobScript(UserBox fileBoxObjectHandle)
            throws OperationFailureException {

        try {

            /* Creates an instance to make a request to box scan functions */
//            BoxScanRequest request =
//                    BoxScanRequest.createInstance(accessControlToken);

            /* Specifies the box in which the document is to be saved */
            boxScanRequest.setBox(accessControlToken, fileBoxObjectHandle);

            /* Sets the size of originals */
            boxScanRequest.setScanSize(
                accessControlToken,
                new StandardSize(
                    new StandardSizeId(StandardSizeId.SIZE_ISO_A4),
                    new Orientation(Orientation.ORIENTATION_LONG_EDGE_FEED)));

            /* Obtains a job management instance */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator.bundleContext.getBundle(),
                                                  accessControlToken);
            /* Submits a job */
            manager.send(accessControlToken, boxScanRequest);

        } catch (UnacceptableException ce) {
            throw new OperationFailureException(
                    " operation failed. " + ce.getMessage());
        } catch (OperationFailureException oe) {
            throw oe;
        }

        return;
    }

    /**
     * Return the setting of the "notification profile
     *                            (id_att_notification_profile)"
     *
     * @return Set the "notification profile"
     *
    private NotificationProfile[] getNotificationProfileList() {
    }*/


}/* end class ScanJob */

/* end ScanJob.java */
