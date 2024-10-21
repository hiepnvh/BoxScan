import com.canon.meap.security.LoginContext;
import com.canon.meap.service.log.LogService;
import com.canon.meap.service.log.Logger;

public class LoggerUtil {

  public static void i(String msg) {
    Logger logger = AppletActivator.getAppletActivator().getLogService().getLogger(LogService.LOGKIND_APP);
    LoginContext loginContext = BoxScanApplet.getBoxScanApplet().getLoginContext();
    logger.log(loginContext, Logger.LOG_LEVEL_INFO, msg);
  }
}
