
## Laboratorium 10

__Uwaga__. W rozwiązaniach dzisiejszych zadań wykorzystaj „szablon” projektu wygenerowany za pomocą polecenia

```
sbt new wpug/scala3-akka.g8
```

__Zadanie 35__:
Zdefiniuj klasę

```scala
class Gracz35 extends Actor {
    // ...
}
```

która, z poziomu programu głównego, posłuży do utworzenia dwóch aktorów, rozgrywających między sobą partię „ping-ponga”. Jako „wirtualnej piłeczki” użyj komunikatu

```scala
case object Piłeczka
```

Ponieważ któryś z graczy musi rozpocząć grę, w programie głównym (po utworzeniu obu graczy) prześlij do jednego z nich komunikat typu

```scala
case class Graj35(przeciwnik: ActorRef)
```

zawierający referencję do jego przeciwnika. Pamiętaj, że „tożsamość” aktora można zmieniać używając konstrukcji

```scala
context.become(...)
```

__Zadanie 36__:

Zmodyfikuj swoje rozwiązanie poprzedniego zadania definiując klasę

```scala
class Gracz36 extends Actor {
    // ...
}
```

tak, aby rozgrywka składała się z zadanej liczby odbić, podanej w komunikacie typu

```scala
case class Graj36(przeciwnik: ActorRef, maks: Int)
```

Po wykonaniu `maks` odbić program powinien zakończyć działanie korzystając z metody

```scala
context.system.terminate()
```

__Zadanie 37__:

Zmodyfikuj rozwiązanie Zadania 35, definiując klasę

```scala
class Gracz37 extends Actor {
    // ...
}
```

oraz proponując odpowiednią definicję klasy „inicjującej rozgrywkę”

```scala
case class Graj37( ??? )
```

w taki sposób, aby po utworzeniu trzech aktorów typu `Gracz37` mogli oni grać w „ping-ponga” w trójkącie. Jako „piłeczki” użyj zdefiniowanego już wcześniej obiektu `Piłeczka`.

__Zadanie 38__:

Zmodyfikuj rozwiązanie zadania poprzedniego, definiując klasę

```scala
class Gracz38 extends Actor {
    // ...
}
```

oraz zmodyfikowaną reprezentację komunikatu „startowego”:

```scala
case class Graj38( ??? )
```

tak, aby rozgrywka mogła odbywać sie „po okręgu” złożonym z zadanej (z poziomu programu głównego) liczby graczy.
