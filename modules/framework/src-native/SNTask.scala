package weaver

import sbt.testing.Task
import sbt.testing.{ EventHandler, Logger }
import scala.concurrent.Future

private[weaver] trait SNTask extends Task {
  def executeFuture(
      eventHandler: EventHandler,
      loggers: Array[Logger]): Future[Unit]
}
