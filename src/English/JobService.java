//****************************************************************************
//
// Copyright CANON INC. 2010
//
//
// JobService.java
//
// MEAP SDK
//
// Version 5.0
//
//***************************************************************************

import com.canon.meap.imi.OperationFailureException;
import com.canon.meap.imi.job.boxscan.BoxScanJobManager;
import com.canon.meap.security.AccessControlToken;
import com.canon.meap.security.LoginContext;
import com.canon.meap.service.sa.SecurityAgent;

/**
 * Box scan sample program, JobService class
 *
 * @version     2.01  2004/04/27
 * @author
 */
public class JobService {

    /* AccessControlToken */
    public AccessControlToken accessControlToken;

    /**
     * Constructor
     */
    public JobService() {
        super();

        try {
            /* Acquire the Access Control Token */
            fetchAccessControlToken();
        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * Acquire the job service's state
     *
     * @return  Job service's state
     */
    public short getJobServiceState() {
        return 0;
    }

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
     * Change the state of the job service to its string expression
     *
     * @param   state Job service's state
     *
     * @return  String expression of the job service
     */
    public String toJobServiceStateString(short state) {
        return null;
    }

    /**
     * Checks whether or not a job can be submitted
     *
     * @return  If the job can be submitted, true is returned. If the job
     *          cannot be submitted, false is returned
     */
    public boolean isSendAvailable() {

        try {

            /* Obtains a job management instance */
            BoxScanJobManager manager =
                    BoxScanJobManager.getInstance(AppletActivator.bundleContext.getBundle(),
                                                  accessControlToken);

            /* Checks whether or not a job can be submitted */
            return manager.isSendAvailable(accessControlToken);

        } catch (OperationFailureException oe) {
            System.out.println(oe.getMessage());
        }

        return false;
    }

}/* end class JobService */

/* end JobService.java */
