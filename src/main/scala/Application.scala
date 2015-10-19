import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.io.StdIn

/**
 * The main program entry-point.
 */
object Application extends App with SignalRSupport {
  println("Connecting...")

  val handler = new MyHandler()

  println("Creating hub proxy...")
  val broadcastHub = connection.createHubProxy("broadcast");
  broadcastHub.subscribe(handler)

  println("Waiting for connection...")
  Await.result(
    connectSignalR(),
    atMost = 30.seconds
  )
  println("Connected (press enter to terminate).")

  StdIn.readLine()

  println("Disconnecting...")
  connection.stop()
  println("Disconnected.")

  class MyHandler {
    def OnSay(what: String): Unit = {
      println(s"Said: $what")

      broadcastHub.invoke("Say", "Hello from Scala")
    }
  }
}
