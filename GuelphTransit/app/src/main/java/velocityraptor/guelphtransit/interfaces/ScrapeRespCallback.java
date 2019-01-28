
//This was an interface originally used for a callback functions
//However it was causing issues with old StopPopup pointers
//Therefore this code is no longer implemented
package velocityraptor.guelphtransit.interfaces;

/**
 * Created by William Aidan Maher on 27/03/15.
 */
public interface ScrapeRespCallback {
    public void resetPopupWithEtas(String eta1,String eta2);
}
