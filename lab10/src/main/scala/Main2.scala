import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props

@main
def main2: Unit = {
    val system2 = ActorSystem("system2")
    val aktor1b = system2.actorOf(Props[Gracz36](),"aktor1b")
    val aktor2b = system2.actorOf(Props[Gracz36](),"aktor2b")

    aktor1b ! Graj36(aktor2b,6)
}

case class Message(odbicia: Int)
case class Graj36(przeciwnik: ActorRef, odbicia: Int)

class Gracz36 extends Actor {
    def receive: Receive = {
        case Graj36(przeciwnik, odbicia) if(odbicia > 0)=> { //odbicia == 1
           println("ping")
           przeciwnik ! Message(odbicia - 1)
           context.become(ping)
        }

        case Message(odbicia) => {
            if(odbicia != 0) {
                println("pong")
                sender() ! Message(odbicia - 1)
            }
            else {
                println("koniec")
                context.system.terminate() //zabija system
            }
        }
    }

    def ping: Receive = {
        case Message(odbicia) => {
            if(odbicia != 0) {
                    println("ping")
                    sender() ! Message(odbicia - 1)
                }
            else {
                println("koniec")
                context.system.terminate() //zabija system
            }
        }
    }
}