package io.wonder.soft.example.actor

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.ExecutionContextExecutor


class SubEchoActor extends Actor {

  def receive = {
    case 'do_something =>
      Thread.sleep(10000L)
      println("some work finished")
  }
}

class EchoActor extends Actor {
  implicit val system: ActorSystem = ActorSystem("AkkaSimpleExample")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: Materializer = ActorMaterializer()
  val subEchoActor = system.actorOf(Props[SubEchoActor], s"sub-echo-actor")

  var count = 0

  def receive = {
    case EchoMessage(message) =>
      Thread.sleep(3000L)
      println(s"count: ${this.count}, message: ${message}")

    case count: Int =>
      subEchoActor ! 'do_something
      this.count += count

    case CountAsk() =>
      this.sender() ! this.count

    case x =>
      println(s"some unknown message come,,,, ${x.toString}")
  }

}
