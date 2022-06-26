import akka.actor.{ActorSystem, Actor, ActorRef, Props}

case class Init(liczbaPracowników: Int)

case class Zlecenie(tekst: List[String])

case class Wykonaj(linia: String)

case class Wynik(result: Int)

class Szef extends Actor {
  def receive: Receive = {
    case Init(num) if (num != 0 ) => {
      val listaPracownikow = (for{
        i <- (0 to num)
      }yield(context.actorOf(Props[Pracownik](), s"Pracownik${i}"))).toList
      context.become(zlecenia(listaPracownikow,0,0))
    } 
   
  }

  def zlecenia(listaPracownikow: List[ActorRef],activeWorkers: Int,acc: Int): Receive = {
    case Zlecenie(text) if(text != Nil) => {
      listaPracownikow.head ! Wykonaj(text.head)
      context.become(zlecenia(listaPracownikow.tail :+ listaPracownikow.head,activeWorkers + 1,acc))
      self ! Zlecenie(text.tail)
    }

    case Zlecenie(Nil) if(activeWorkers != 0) => {
      context.become(zlecenia(listaPracownikow,activeWorkers,acc))
      self ! Zlecenie(Nil)
    }

    case Zlecenie(Nil) if(activeWorkers == 0) => {
      println(acc)
      context.system.terminate()
      }

    case Wynik(result) => {
      context.become(zlecenia(listaPracownikow,activeWorkers - 1,acc + result))
    }
  }
}


class Pracownik extends Actor {
  def receive: Receive = {
    case Wykonaj(text) => {
      if(text.length == 0) {
      sender() ! Wynik(0)
      } else {
      val result = text.split(" ").length
      sender() ! Wynik(result)
      }
    }
  }
}

@main
def zadanie_39: Unit = {

  val listaZadan = io.Source
      .fromResource("ogniem_i_mieczem.txt")
      .getLines
      .toList
  
  val system = ActorSystem("system")
  val szef = system.actorOf(Props[Szef](),"szef")
  szef ! Init(10)
  szef ! Zlecenie(listaZadan)
}
