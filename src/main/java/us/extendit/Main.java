package us.extendit;
 
import us.extendit.channel.ChannelManager;
import org.jpos.iso.*;
import org.jpos.q2.Q2;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;
import org.jpos.util.NameRegistrar;
 
import java.util.Date;
 
/**
 * Created with IntelliJ IDEA.
 * User: ouwaifo
 * Date: 6/7/12
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
 
    static void start() {
        Q2 q2 = new Q2();
        q2.start();
    }
 
    public static void main(String[] args) throws ISOException {
        start();
        ISOUtil.sleep(5000);
        new Thread(new Exec()).start();
    }
 
    static class Exec implements Runnable {
 
        ChannelManager channelManager;
 
        Exec() throws ISOException {
            try {
 
                channelManager = ((ChannelManager) NameRegistrar.get("manager"));
            } catch (NameRegistrar.NotFoundException e) {
                LogEvent evt = channelManager.getLog().createError();
                evt.addMessage(e);
                evt.addMessage(NameRegistrar.getInstance());
                Logger.log(evt);
            } catch (Throwable t) {
                channelManager.getLog().error(t);
            }
 
        }
 
        private ISOMsg createHandshakeISOMsg() throws ISOException {
            ISOMsg m = new ISOMsg();
            m.setMTI("0200");
            m.set(3, "500000");
            m.set(7, ISODate.getDateTime(new Date()));
            m.set(11, String.valueOf(System.currentTimeMillis() % 1000000));
            m.set(12, "213810");
            m.set(13, "0529");
            m.set(18, "6010");
            m.set(32, "111");
            //m.set(37, "787856");
            m.set(41, "JOMI0000");
            m.set(42, "USRJOMI000");
            m.set(48, "B2180018972;00;42840"); //payment
            //m.set(48, "B2180018972"); //inquiry

            return m;
        }
        private void sendHandShake() throws Exception {
            try {
                channelManager.sendMsg(createHandshakeISOMsg());
                channelManager.getLog().info("Handshake sent! ");
            } catch (ISOException e1) {
                channelManager.getLog().error("ISOException :" + e1.getMessage());
            } catch (Exception e) {
                channelManager.getLog().error("Exception :" + e.getMessage());
            }
 
        }
 
        public void run() {
            while (true) {
                ISOUtil.sleep(10000);
                try {
                    sendHandShake();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}