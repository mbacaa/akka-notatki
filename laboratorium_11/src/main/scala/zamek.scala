import akka.actor.{ActorSystem, Actor, Props}
import scala.concurrent.duration._
import akka.actor.ActorRef
import SiłaWyższa.Strzelać
import SiłaWyższa.Arrow
import akka.actor.PoisonPill
import akka.actor.Terminated
/*
  W konfiguracji projektu wykorzystana została wtyczka
  sbt-revolver. W związku z tym uruchamiamy program poleceniem

    reStart

  a zatrzymujemy pisząc (mimo przesuwających się komunikatów)

     reStop

  i naciskając klawisz ENTER. Jeśli czynności powyższe
  już wykonywaliśmy to możemy też przywołać poprzednie
  polecenia używając strzałek góra/dół na klawiaturze.
*/

case class InitZamek(przeciwnik: ActorRef)
case class InitObronca(przeciwnik: ActorRef)
case class GotShot(n: Int)
case class Recovered(self: ActorRef)
case class Defeated(self: ActorRef)

object SiłaWyższa {
  case object Tura
  case object Strzelać
  case object Arrow
}
class SiłaWyższa extends Actor {
  import SiłaWyższa._
  def receive = {
    case Tura =>
      println("Start")
      val zamek1 = context.actorOf(Props[Zamek](),"red")
      val zamek2 = context.actorOf(Props[Zamek](),"blue")
      zamek1 ! InitZamek(zamek2)
      zamek2 ! InitZamek(zamek1)
      context.become(resolver(zamek1,zamek2,0))
  }

  def resolver(zamek1: ActorRef,zamek2: ActorRef,counter: Int): Receive = {
    case Tura => {
        println(s"Tura ${counter}")
        zamek1 ! Strzelać
        zamek2 ! Strzelać
        context.become(resolver(zamek1,zamek2,counter + 1))
    }

    case Defeated(zamek) => {
        println(s"${zamek.path.name} got defeated")
        context.system.terminate()
    }
  }
}

class Zamek extends Actor {
  def receive: Receive = {
    case InitZamek(przeciwnik) => {
        val active = (for { i <- 1 to 100}
        yield(context.actorOf(Props[Obrońca](),s"obronca${i}_${self.path.name}"))).toList
        active.foreach(obronca => obronca ! InitObronca(przeciwnik))
        context.become(bitwa(active,List()))
    }
  }

  def bitwa(active: List[ActorRef],hospital: List[ActorRef]): Receive = {
    case Strzelać if(active != Nil)=> {
        
        active.foreach(obronca => obronca ! Strzelać)
    }

    case Strzelać if(active == Nil && hospital != Nil) => {
    println(active)
    context.become(bitwa(active,hospital))
    }

    case Strzelać if(active == Nil && hospital == Nil) => sender() ! Defeated(self)

    case Arrow if(active != Nil)=> {
        val randGenerator = new scala.util.Random
        val randIndex = randGenerator.nextInt(active.length)
        val injured = active(randIndex) // trafiony obronca
        injured ! GotShot(active.length)
        context.watch(injured)
        val activeUpdated = active.take(randIndex) ++ active.drop(randIndex + 1)
        context.become(bitwa(activeUpdated,injured :: hospital))
    }

    case Arrow if(active == Nil) => context.become(bitwa(active,hospital))

    case Terminated(obronca) => {
        context.unwatch(obronca)
        println(s"${obronca.path.name} is dead")
        val index = hospital.indexOf(obronca)
        val updatedHospital = hospital.take(index) ++ hospital.drop(index + 1)
        context.become(bitwa(active,updatedHospital))
    }

    case Recovered(obronca) => {
        context.unwatch(obronca)
        val index = hospital.indexOf(obronca)
        val updatedHospital = hospital.take(index) ++ hospital.drop(index + 1)
        context.become(bitwa(obronca::active,updatedHospital))
    }
  }
}

class Obrońca extends Actor {
  def receive: Receive = {
    case InitObronca(przeciwnik) => {
        context.become(attack(przeciwnik))
    }
  }

  def attack(przeciwnik: ActorRef): Receive = {
    case Strzelać => {
        przeciwnik ! Arrow
    }
    case GotShot(activeNum) => {
        val probability = activeNum.toDouble/200
        val randGenerator = new scala.util.Random
        val shotEffect = randGenerator.nextDouble()
        if(shotEffect <= probability) {
            self ! PoisonPill
        } else {
            sender() ! Recovered(self)
        }
    }
  }
}

@main
def bitwa: Unit = {
  val system = ActorSystem("Jabberwocky")
  import system.dispatcher

  // UWAGA: „nazwy”, tworzące ścieżkę do aktora muszą być zapisywane
  // z użyciem znaków znaków ASCII (a nie np. UTF8) – stąd „SilaWyzsza”
  val siłaWyższa = system.actorOf(Props[SiłaWyższa](), "SilaWyzsza")

  // Do „animacji” SiłyWyższej wykorzystamy „Planistę” (Scheduler)
  val pantaRhei = system.scheduler.scheduleWithFixedDelay(
    Duration.Zero,     // opóźnienie początkowe
    1000.milliseconds, // odstęp pomiedzy kolejnymi komunikatami
    siłaWyższa,        // adresat „korespondencji”
    SiłaWyższa.Tura     // komunikat
  )

  // Oczywiście zatrzymanie symulacji NIE MOŻE się odbyć tak, jak poniżej
//   Thread.sleep(3000)
//   val res = if pantaRhei.cancel()
//     then "Udało się zakończyć „cykanie”"
//     else "Coś poszło nie tak – dalej „cyka”"
//   println(res)
//   system.terminate()
}
