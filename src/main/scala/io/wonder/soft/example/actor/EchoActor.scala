package io.wonder.soft.example.actor

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, PoisonPill, Props, UntypedAbstractActor}
import akka.stream.{ActorMaterializer, Materializer}
import org.slf4j.LoggerFactory


class ParentEchoActor extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  val logger = LoggerFactory.getLogger(getClass.getName)

  override def preStart(): Unit = {
    this.logger.info("starting parent actor")
  }

  override def postStop(): Unit = {
    this.logger.info("stopping parent actor")
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = true) {
      case _: ArithmeticException      =>
        this.logger.info("enter ArithmeticException")
        Stop

      case re: RuntimeException      =>
        this.logger.error("enter RuntimeException", re)
        Restart

      case _: NullPointerException     =>
        this.logger.info("enter NullPointerException")
        Stop

      case _: IllegalArgumentException =>
        this.logger.info("enter IllegalArgumentException")
        Stop

      case t =>
        super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Stop)
    }


  override def receive = {
    case (p: Props, name: String) =>
      sender() ! context.actorOf(p, name)
  }
}

class EchoActor extends Actor {
  val logger = LoggerFactory.getLogger(getClass.getName)

  var count = 0

  val subEchoActor = context.actorOf(Props[SubEchoActor], "sub-echo-actor")

  override def preStart(): Unit = {
    this.logger.info("starting child actor")
  }

  override def postStop(): Unit = {
    this.logger.info("stopping child actor")
  }

  def receive = {
    case count: Int =>
      this.logger.info(count.toString)
      subEchoActor ! 'do_something

    case 'crash =>
      subEchoActor ! 'do_something
      //throw new ArithmeticException
      throw new RuntimeException("crash it")

    case CountAsk() =>
      this.sender() ! this.count

    case EchoMessage(message) =>
      Thread.sleep(3000L)
      println(s"count: ${this.count}, message: ${message}")

  }

}


class SubEchoActor extends Actor {

  def receive = {
    case 'do_something =>
      Thread.sleep(10000L)
      println("some work finished")
  }
}
