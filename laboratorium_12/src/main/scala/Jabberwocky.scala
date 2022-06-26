import akka.actor.{ActorSystem, Actor, Props}
import scala.concurrent.duration._
import akka.actor.ActorRef
import SiłaWyższa.Strzelać
import SiłaWyższa.StrzelajDoZamku
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
case class JestesRanny(liczba: Int)
case class Przezylem(obronca: ActorRef)
case class Przegralem(zamek:ActorRef)
object SiłaWyższa {
  case object Cyk
  case object Strzelać
  case object StrzelajDoZamku
  
}
class SiłaWyższa extends Actor {
  import SiłaWyższa._
  def receive = {
    case Cyk =>
      println("Cyk")
      val zamek1 = context.actorOf(Props[Zamek](),"zamek1")
      val zamek2 = context.actorOf(Props[Zamek](),"zamek2")
      zamek1 ! InitZamek(zamek2)
      zamek2 ! InitZamek(zamek1) 
      Thread.sleep(500)
      context.become(cykanie(zamek1,zamek2))
  }

  def cykanie(zamek1:ActorRef,zamek2:ActorRef): Receive = {
    case Cyk => {
      zamek1 ! Strzelać
      zamek2 ! Strzelać
    } 
    case Przegralem(zamek) => {
      context.system.terminate()
      println(s"przegral ${zamek.path.name}")
    }
  }
}

class Zamek extends Actor {
  def receive: Receive = {
    case InitZamek(przeciwnik) => {
      val obroncy = (for {
        i <- 0 to 99
      } yield(context.actorOf(Props[Obrońca](),s"obronca${i}_z_zamku_${self.path.name}"))).toList
      obroncy.foreach(obronca => obronca ! InitObronca(przeciwnik))
      context.become(atak(obroncy,List()))
    }
  }
  def atak(obroncy:List[ActorRef],counter:List[ActorRef]): Receive = {

    case Strzelać if(obroncy != Nil)=> {
      obroncy.foreach(obronca => obronca ! Strzelać)
    }
    case Strzelać if(obroncy == Nil && counter != Nil) => context.become(atak(obroncy,counter))

    case Strzelać if(obroncy == Nil && counter == Nil) => sender() ! Przegralem(self)

    case StrzelajDoZamku if(obroncy != Nil)=> {
      val generatorLiczbLosowych = scala.util.Random
      val losowyIndex = generatorLiczbLosowych.nextInt(obroncy.length)
      val postrzelony = obroncy(losowyIndex)
      postrzelony ! JestesRanny(obroncy.length)
      context.watch(postrzelony)
      val obroncyZaktualizowani = obroncy.take(losowyIndex) ++ obroncy.drop(losowyIndex + 1)
      context.become(atak(obroncyZaktualizowani,postrzelony :: counter))
    }
    case StrzelajDoZamku if(obroncy == Nil) => context.become(atak(obroncy,counter))

    case Terminated(obronca) => {
      context.unwatch(obronca)
      
      val indeksPoleglegoObroncy = counter.indexOf(obronca)
      val counterZaktualizowany = counter.take(indeksPoleglegoObroncy) ++ counter.drop(indeksPoleglegoObroncy + 1)
      context.become(atak(obroncy,counterZaktualizowany))
    }

    case Przezylem(obronca) => {
      context.unwatch(obronca)
      val indeksObroncy = counter.indexOf(obronca)
      val counterZaktualizowany = counter.take(indeksObroncy) ++ counter.drop(indeksObroncy + 1)
      context.become(atak(obronca :: obroncy,counterZaktualizowany))
    } 
  }
}

class Obrońca extends Actor {
  def receive: Receive = {
    case InitObronca(przeciwnik) => {
      context.become(bronienie(przeciwnik))
      
    }
  }
  def bronienie(przeciwnik:ActorRef): Receive = {

    case Strzelać => przeciwnik ! StrzelajDoZamku  

    case JestesRanny(liczba) => {
      
      val szansaNaSmierc = liczba.toDouble/200
      val generatorLiczbLosowych = scala.util.Random
      val czyUmre = generatorLiczbLosowych.nextDouble()
      val werdykt = BigDecimal(czyUmre).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      if(werdykt <= szansaNaSmierc) {
        sender() ! Przezylem(self)
      } else { 
        self ! PoisonPill
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
    200.milliseconds, // odstęp pomiedzy kolejnymi komunikatami
    siłaWyższa,        // adresat „korespondencji”
    SiłaWyższa.Cyk     // komunikat
  )

  // Oczywiście zatrzymanie symulacji NIE MOŻE się odbyć tak, jak poniżej
  // Thread.sleep(3000)
  // val res = if pantaRhei.cancel()
  //   then "Udało się zakończyć „cykanie”"
  //   else "Coś poszło nie tak – dalej „cyka”"
  // println(res)
  // system.terminate()
}
