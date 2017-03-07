package edu.mtholyoke.cs341bd.bookz;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jfoley.
 */
public class ServerRequest {
  public final HttpServletRequest req;
  public final HttpServletResponse resp;
  private final HashMap<String, String> params;
  public String method;
  public String path;

  public ServerRequest(HttpServletRequest req, HttpServletResponse resp) {
    this.req = req;
    this.resp = resp;
    this.method = req.getMethod();
    this.path = req.getPathInfo();
    this.params = new HashMap<>();
    for (Map.Entry<String, String[]> kv : req.getParameterMap().entrySet()) {
      params.put(kv.getKey(), Util.join(kv.getValue()));
    }
  }

  public String getParameter(String name, String ifNotThere) {
    return params.getOrDefault(name, ifNotThere);
  }

  public String getURL() {
    return path;
  }

  @Override
  public String toString() {
    return method + "\t" + getURL() + "\t" + params;
  }
}
