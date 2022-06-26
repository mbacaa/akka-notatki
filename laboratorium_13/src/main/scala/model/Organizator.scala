package jp1.akka.lab13.model

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.PoisonPill
import jp1.akka.lab13.model.Grupa.Wynik
import shapeless.ops.tuple

val akkaPathAllowedChars = ('a' to 'z').toSet union
  ('A' to 'Z').toSet union
  "-_.*$+:@&=,!~';.)".toSet

object Organizator {
  case object Start
  // rozpoczynamy zawody – losujemy 50 osób, tworzymy z nich zawodników
  // i grupę eliminacyjną
  case object Runda
  // polecenie rozegrania rundy (kwalifikacyjnej bądź finałowej) –  wysyłamy Grupa.Runda
  // do aktualnej grupy
  case object Wyniki
  // polecenie wyświetlenia klasyfikacji dla aktualnej grupy
  case class Wyniki(w: Map[ActorRef, Option[Ocena]])
  // wyniki zwracane przez Grupę
  case object Stop
  // kończymy działanie
}

class Organizator extends Actor {
  // importujemy komunikaty na które ma reagować Organizator
  import Organizator._
  var trashBin: List[(ActorRef,Ocena)] = List()
  def receive: Receive = {
    case Start =>
      // tworzenie 50. osób, opdowiadających im Zawodników
      // oraz Grupy eliminacyjnej
      val zawodnicy = List.fill(50) {
        val o = Utl.osoba()
        context.actorOf(Props(Zawodnik(o)), s"${o.imie}-${o.nazwisko}" filter akkaPathAllowedChars)
      }
      context.become(eliminacje(zawodnicy))
      // val grupa = context.actorOf(Props[Grupa](), "nazwa")
      
      
    // Obsługa pozostałych komunikatów

    case Stop =>
      println("kończymy zawody...")
      context.system.terminate()
  }

  def eliminacje(zawodnicy: List[ActorRef]): Receive = {
    case Runda => 
      val grupa = context.actorOf(Props(Grupa(zawodnicy)),"grupa")
      grupa ! Grupa.Runda
      context.become(runda(Map(),0,zawodnicy.length))
  }

  def runda(result: Map[ActorRef,Option[Ocena]], doneParticipants: Int,totalParticipants: Int): Receive = {
    case Wyniki(wynik) if(totalParticipants - 1  == doneParticipants) => {
      sender() ! PoisonPill
      println("konczymy balet")
      context.become(resolver(result ++ wynik))
      self ! Organizator.Wyniki
    }

    case Wyniki(wynik) => 
      context.become(runda(result ++ wynik,doneParticipants + 1,totalParticipants))
  }

  def resolver(result: Map[ActorRef,Option[Ocena]]): Receive = {
    case Wyniki => {
      val wyniki = result.toList
      .filter(tuple => tuple._2 != None)
      .map(tuple => (tuple._1,tuple._2.get))
      .map(tuple => (tuple._1,tuple._2.nota1 + tuple._2.nota2 + tuple._2.nota3))
      .groupBy(tuple => tuple._2)
      .toList
      .sortBy(tuple => tuple._1)
      .reverse
      .map(tuple => tuple._2)
      .flatten
      .take(20)
      .map(tuple => tuple._1)
      .map(tuple => (tuple,result(tuple).get))
      // println(wyniki)

      trashBin = trashBin ++ wyniki
      

      context.become(rundaFinalowa(trashBin))
    }
  }

  def rundaFinalowa(wyniki: List[(ActorRef,Ocena)]): Receive = {
    case Runda => {
      println("tutaj runda finalowa")
      val finalists = wyniki
      .map(tuple => tuple._1)
      val grupaFinalowa = context.actorOf(Props(Grupa(finalists)),"grupaFinalowa")
      grupaFinalowa ! Grupa.Runda
      context.become(runda(Map(),0,finalists.length))
    }
    case Wyniki => {
      println
      println(wyniki.length)
    }
  }
  
} 