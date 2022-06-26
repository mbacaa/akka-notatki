import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
@main
def main4: Unit = {
    val actorNum = 10
    val system = ActorSystem("n_liczba_graczy")
    val listaAktorow: List[ActorRef] = (for {
        i <- 1 to actorNum
    }yield(system.actorOf(Props[Gracz38](), s"Gracz${i}")))
    .toList

    listaAktorow(0) ! Graj38(listaAktorow.tail :+ listaAktorow.head) //tasma
}

case class Graj38(listaAktorow: List[ActorRef])
case class Message2(listaAktorow: List[ActorRef], msg: String)

class Gracz38 extends Actor {
    def receive: Receive = {
        case Graj38(listaAktorow) if (listaAktorow.length != 0) => {
            println(s"${self.path.name}: ping")
            listaAktorow.head ! Message2(listaAktorow.tail :+ listaAktorow.head,"pong")
        }
        case Message2(listaAktorow, text) => {
            println(s"${self.path.name}: ${text}")
            listaAktorow.head ! Message2(listaAktorow.tail :+ listaAktorow.head,"ping")
        }
    }
}