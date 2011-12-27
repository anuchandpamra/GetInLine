package bootstrap.liftweb

import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import actors.Actor._
import java.net.ServerSocket
import java.io.IOException
import Lift.comet.SocketBasedTokenObserver
import net.liftweb.util.Props
import Lift.lib.WebSvcTokenDispenser


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  private val socketServer = actor {
    val port = Props.getInt("socketBasedTokenObserver.port", 7777)
    try {
      val listener = new ServerSocket(port)
      while (true) SocketBasedTokenObserver(listener.accept())
      listener.close()
    }
    catch {
      case e: IOException => System.err.println("Could not connect to port: " + port)
    }
  }

  def boot {
    // where to search snippet
    LiftRules.addToPackages("Lift")

    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index"
    )

    LiftRules.setSiteMapFunc(() => sitemap())
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.statelessDispatchTable.append(WebSvcTokenDispenser)
    LiftRules.early.append(makeUtf8)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
