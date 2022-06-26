package jp1.akka.lab13

// „Interfejs użytkownika” wymaga pewnych dodatkowych elementów:
import scala.concurrent.ExecutionContext
import scala.util.control.Breaks._
import scala.io.StdIn

import akka.actor.{ActorSystem, Props}

import model.*

@main
def zawody: Unit = {

  val system = ActorSystem("system")
  val organizator = system.actorOf(Props[Organizator](), "organizator")

  // Interfejs „organizatora”:
  implicit val ec: ExecutionContext = ExecutionContext.global

  breakable {
    while (true) {
      StdIn.readLine("polecenie: ") match {
        case "start" =>
          println("czesc")
          organizator ! Organizator.Start
        case "eliminacje" =>
          println("start eliminacji")
          organizator ! Organizator.Runda
        case "finał" =>
          // polecenie rozegrania rundy finałowej
          // wymaga zamknięcia Rundy eliminacyjnej i utworzenie
          // Rundy finałowej, zawierającej najlepszych 20.
          // zawodników z Rundy eliminacyjnej
          organizator ! Organizator.Runda
        case "wyniki" =>
          organizator ! Organizator.Wyniki
        case "stop" =>
          organizator ! Organizator.Stop
          break()
        case _ =>
      }
    }
  }
}
