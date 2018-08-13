package io.wonder.soft.example.actor

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, PoisonPill, Props}
import akka.stream.{ActorMaterializer, Materializer}
import org.slf4j.LoggerFactory
import akka.actor.SupervisorStrategy._
import io.wonder.soft.example.Main.system

import scala.concurrent.duration._
import scala.concurrent.ExecutionContextExecutor


class SubEchoActor extends Actor {

  def receive = {
    case 'do_something =>
      Thread.sleep(10000L)
      println("some work finished")
  }
}

class ParentEchoActor extends Actor {
  implicit val system: ActorSystem = ActorSystem("AkkaSimpleExample")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()

  val logger = LoggerFactory.getLogger(getClass.getName)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case x: RuntimeException         =>
        this.logger.info("enter")
        this.logger.error("", x)
        Stop
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
//      case _: Exception                => Escalate
    }

  val echoActor: ActorRef =
    system.actorOf(Props[EchoActor], "echo-actor2")

  def receive = {
    case 'generate =>
      echoActor ! 10
    case x =>
      println(s"some unknown message come,,,, ${x.toString}")
  }
}

class EchoActor extends Actor {
  implicit val system: ActorSystem = ActorSystem("AkkaSimpleExample")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()
  val subEchoActor = system.actorOf(Props[SubEchoActor], s"sub-echo-actor")

  val logger = LoggerFactory.getLogger(getClass.getName)

  var count = 0

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case x: RuntimeException         =>
        this.logger.info("child enter")
        this.logger.error("", x)
        Stop
      case _: ArithmeticException      => Resume
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Stop
//      case _: Exception                => Escalate
    }

  override def preStart(): Unit = {
    this.logger.info("starting actor")
  }

  override def postStop(): Unit = {
    this.logger.info("stopping actor")
  }

  def receive = {
    case EchoMessage(message) =>
      Thread.sleep(3000L)
      println(s"count: ${this.count}, message: ${message}")

    case count: Int =>
      subEchoActor ! 'do_something
      throw new RuntimeException("runtime")
      this.count += count

    case CountAsk() =>
      this.sender() ! this.count

    case x =>
      println(s"some unknown message come,,,, ${x.toString}")
  }

}
