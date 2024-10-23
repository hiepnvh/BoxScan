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

import java.security.AccessControlException;
import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.UnacceptableException;
import com.canon.meap.imi.UnavailableJobException;
import com.canon.meap.imi.UnavailableMethodException;
import com.canon.meap.imi.box.BoxManager;
import com.canon.meap.imi.box.meapbox.MeapBox;
import com.canon.meap.imi.box.userbox.UserBox;
import com.canon.meap.imi.data.JobId;
import com.canon.meap.imi.data.Orientation;
import com.canon.meap.imi.data.StandardSize;
import com.canon.meap.imi.data.StandardSizeId;
import com.canon.meap.imi.job.boxscan.BoxScanJob;
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
    private BoxScanJob currJob;

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
    public boolean startScan() {

        try {

            /* Acquire the Access Control Token */
            fetchAccessControlToken();

            /* Submit the job script */
            performJobScript();

        } catch (OperationFailureException oe) {
            LoggerUtil.i(oe.getMessage());

            return false;
        }

        return true;
    }
    
    /**
     * Start the scan
     *
     * @param      fileBoxObjectHandle  File box Object
     */
    public void continueScan() {

      try {

        currJob.resumeInteraction(accessControlToken);
      } catch (AccessControlException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnavailableMethodException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (OperationFailureException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnavailableJobException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnacceptableException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      }

    }

    /**
     * End the scan
     */
    public void endScan() {
      try {
        currJob.cancel(accessControlToken);
      } catch (AccessControlException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnavailableMethodException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (OperationFailureException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnavailableJobException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      } catch (UnacceptableException e) {
        // TODO Auto-generated catch block
        LoggerUtil.i(e.toString());
      }
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
    private void performJobScript()
            throws OperationFailureException {

        try {

            /* Creates an instance to make a request to box scan functions */
//            BoxScanRequest request =
//                    BoxScanRequest.createInstance(accessControlToken);

            /* Specifies the box in which the document is to be saved */
          BoxManager boxManager = BoxManager.getInstance(AppletActivator.bundleContext.getBundle(), accessControlToken);

          MeapBox meapBox = boxManager.getMeapBox(AppletActivator.bundleContext.getBundle(), accessControlToken);
          
            boxScanRequest.setBox(accessControlToken, meapBox);

            /* Sets the size of originals */
            boxScanRequest.setScanSize(
                accessControlToken,
                new StandardSize(
                    new StandardSizeId(StandardSizeId.SIZE_ISO_A4),
                    new Orientation(Orientation.ORIENTATION_LONG_EDGE_FEED)));

            /* Obtains a job management instance */
            BoxScanJobManager manager = BoxScanJobManager.getInstance(AppletActivator.bundleContext.getBundle(),
                                          accessControlToken);
            /* Submits a job */
            currJob = manager.send(accessControlToken, boxScanRequest);

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
