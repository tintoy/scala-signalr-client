import microsoft.aspnet.signalr.client.{SignalRFuture, Action, ErrorCallback}
import microsoft.aspnet.signalr.client.hubs.HubConnection

import scala.concurrent.{Promise, Future}
import scala.util.Success

/**
 * Basic support for SignalR.
 */
trait SignalRSupport {
  val connection = new HubConnection("http://localhost:19123/signalr")
  connection.error(new ErrorCallback {
    override def onError(error: Throwable): Unit = {
      onSignalRError(error)
    }
  })

  def connectSignalR(): Future[Void] = {
    toNativeFuture(
      connection.start()
    )
  }

  // Handlers

  def onSignalRError(error: Throwable): Unit = {
    println(s"connection.onError: $error")
  }

  /**
   * Convert a SignalR future to a native (Scala) future.
   * @param signalRFuture The SignalR future to convert.
   * @tparam TResult The future result type.
   * @return The native (Scala) future.
   */
  private def toNativeFuture[TResult](signalRFuture: SignalRFuture[TResult]): Future[TResult] = {
    if (signalRFuture == null)
      throw new IllegalArgumentException("SignalR future cannot be null.")

    val nativePromise = Promise[TResult]()

    signalRFuture
      .done(new Action[TResult] {
        override def run(result: TResult): Unit = {
          nativePromise.complete(Success(result))
        }
      })
      .onError(new ErrorCallback {
        override def onError(error: Throwable): Unit = {
          nativePromise.failure(error)
        }
      })

    nativePromise.future
  }
}
