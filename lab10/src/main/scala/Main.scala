import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

case object Pileczka //nasza pileczka do grania, tak zwany "singleton"
case class Graj35(przeciwnik: ActorRef) // polecenie do rozpoczecia gry

//object -> bez parametrow, class -> mozna je dodac (np. wiadomosc z jakimis danymi)

class Gracz35 extends Actor {
    // Gracz35 = Gracz35 + Actor
    def receive: Receive = { //aktor odbiera polecenie
      case Graj35(przeciwnik) => {
        println("ping") //pingponguje
        przeciwnik ! Pileczka //wysylamy pileczke przeciwnikowi
        context.become(ping()) //zmieniamy mu kontekst na ping
      }

      case Pileczka => {
        println("pong")
        sender() ! Pileczka
      }
    }

    def ping(): Receive = {
      case Pileczka => {
        println("ping")
        sender() ! Pileczka
      }
    }

    /* 
    aktor1 ! Graj35(aktor2) -> aktor2 ! Pileczka
    1.
    aktor1: ping()
    aktor2: receive

    2.
    aktor2 -> println("pong")
    sender() ! Pileczka == akto1 ! Pileczka

    case class Message(msg: String)

    aktor1 ! Message("ping")
    sender() ! Message("pong")

    */
}

@main
def main: Unit = {
  val system = ActorSystem("system") //tworzy nowy system aktorow
  val aktor1 = system.actorOf(Props[Gracz35](),"aktor1") //"uchwyt" do aktora
  val aktor2 = system.actorOf(Props[Gracz35](),"aktor2") //Props -> ustawianie wlasciwosci, () => apply method, konstruktor

  //tutaj zaczynamy gre
  aktor1 ! Graj35(aktor2) //! wysylanie wiadomosci do aktorow

}
