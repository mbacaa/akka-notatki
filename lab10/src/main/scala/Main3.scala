import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

@main
def main3: Unit = {
    val system3 = ActorSystem("system3")
    val aktor1c = system3.actorOf(Props[Gracz37](),"aktor1c")
    val aktor2c = system3.actorOf(Props[Gracz37](),"aktor2c")
    val aktor3c = system3.actorOf(Props[Gracz37](),"aktor3c")

    val actorList: List[ActorRef] = List(aktor1c,aktor2c,aktor3c)

    aktor1c ! Graj37(actorList)
}

// object Pileczka3 {
//     case object PileczkaAdidas
//     case object PileczkaNike
// }

object Pileczka3
case class Init(przeciwnik: ActorRef, msg: String)

case class Graj37(actorList: List[ActorRef])

class Gracz37 extends Actor {
    def receive: Receive = {
        case Graj37(actorList) => {
            println("ping1")
            self ! Init(actorList(1),"1 -> 2")
            actorList(1) ! Init(actorList(2),"2 -> 3")
            actorList(2) ! Init(actorList(0),"3 -> 1")
            
            self ! Pileczka3
        }
        case Init(przeciwnik,msg) => {
            context.become(odbij(przeciwnik,msg))
        }
    }

    def odbij(przeciwnik: ActorRef,msg: String): Receive = {
        case Pileczka3 => {
            println(msg)
            przeciwnik ! Pileczka3
        }
    }
}