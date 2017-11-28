import com.pryv.Connection;
import com.pryv.Pryv;
import com.pryv.auth.AuthBrowserView;
import com.pryv.auth.AuthController;
import com.pryv.auth.AuthView;
import com.pryv.exceptions.ApiException;
import com.pryv.model.Event;
import com.pryv.model.Filter;
import com.pryv.model.Permission;
import com.pryv.model.Stream;
import com.pryv.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This example lets the user sign in, then retrieves the access information
 * (type and permissions), the available streams structure and the last 20
 * events.
 *
 * @author ik
 *
 */
public class BasicExample implements AuthView {

  private List<Event> events = new ArrayList<Event>();
  private Map<String, Stream> streams = new HashMap<String,Stream>();
  private Connection connection;

  public static void main(String[] args) {

    // Turn off logging
    Logger logger = Logger.getInstance();
    logger.turnOff();

    AuthView exampleUser = new BasicExample();

    // Authenticate user

    // Application settings
    String reqAppId = "pryv-lib-java-example";
    String language = "en";
    String returnURL = "unused";

    // Permissions settings
    List<Permission> permissions = new ArrayList<Permission>();
    Permission diaryPermission = new Permission("diary", Permission.Level.read, "Journal");
    Permission positionPermission = new Permission("position", Permission.Level.read, "Position");
    permissions.add(diaryPermission);
    permissions.add(positionPermission);

    AuthController authenticator = new AuthController(reqAppId, permissions, language, returnURL, exampleUser);

    // start authentication. On successful authentication, the Streams structure
    // as well as 20 Events will be retrieved from the API (defined on the
    // onAuthSuccess() method).
    authenticator.signIn();

  }

  @Override
  public void displayLoginView(String loginURL) {
    printExampleMessage(loginURL);
    new AuthBrowserView().displayLoginView(loginURL);
  }

  @Override
  public void onAuthSuccess(String userID, String accessToken) {

    // Instantiate Connection object - used to access Streams and Events data
    connection = new Connection(userID, accessToken, Pryv.DOMAIN);

    try {
      // Retrieve the Streams structure
      Map<String, Stream> streams = connection.streams.get(null);
      appendStreams(streams);

      // Retrieve 20 Events
      Filter eventsFilter = new Filter();
      eventsFilter.setLimit(20);
      List<Event> events = connection.events.get(eventsFilter);
      appendEvents(events);
    }

    catch(IOException e) {
      System.out.println("IO Exception: " + e);
    }

    catch (ApiException e) {
      System.out.println("API Exception: " + e.getMsg());
    }
  }

  @Override
  public void onAuthError(String message) {
    System.out.println("Auth error");
  }

  @Override
  public void onAuthRefused(int reasonId, String message, String detail) {
    System.out.println("Auth refused");
  }
  
  private void appendEvents(List<Event> retrievedEvents) {
	  for (Event event : retrievedEvents) {
	      System.out.println(event);
	      this.events.add(event);
	    }
  }
  
  private void appendStreams(Map<String, Stream> retrievedStreams) {
	  for (Stream stream : retrievedStreams.values()) {
	      System.out.println(stream);
	      this.streams.put(stream.getId(), stream);
	    }
  }
  
  /**
   * print informative message
   *
   * @param loginUrl
   */
  private static void printExampleMessage(String loginUrl) {
    System.out.println("#########################################################");
    System.out.println("##                  Basic Example started              ##");
    System.out.println("#########################################################\n");
    System.out.println("Sign in the open web page or copy this link in your browser:\n");
    System.out.println(loginUrl + "\n");
    System.out
      .println("Use your own staging account credentials or user \'perkikiki\' and password \'poilonez\'\n");
  }

}