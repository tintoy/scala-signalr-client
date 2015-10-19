import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn
import scala.util.{Failure, Success}

/**
 * The main program entry-point.
 */
object Application extends App with SignalRSupport {
  val signalRServiceUrl = "http://localhost:19123/signalr"

  println(s"Connecting to SignalR on '$signalRServiceUrl'...")

  println("Waiting for connection...")
  connectSignalR { connection =>
    println("Creating hub proxy...")
    val broadcastHub = connection.createHubProxy("broadcast")

    broadcastHub.subscribe(new Object {
      def OnSay(what: String): Unit = {
        println(s"Said: $what")

        broadcastHub.invoke("Say", "Hello from Scala")
      }
    })
  } andThen {
    case Success(_) =>
      println("Connected (press enter to terminate).")

      StdIn.readLine()

      println("Disconnecting...")
      disconnectSignalR()
      println("Disconnected.")
    case Failure(error) => println(s"Failed to connect to SignalR: $error")
  }
}
