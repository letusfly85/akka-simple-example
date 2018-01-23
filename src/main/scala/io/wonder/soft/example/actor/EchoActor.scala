package io.wonder.soft.example.actor

import akka.actor.Actor

case class Echo(message: String)

class EchoActor extends Actor {

  def receive = {
    case Echo(myMessage) =>
      println(myMessage)
  }

}
