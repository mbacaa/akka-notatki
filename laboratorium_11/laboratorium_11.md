
# Laboratorium 11

__Zadanie 39__:
Używając aktorów zaimplementuj „rozproszony licznik słów”. Powinien on składać się z aktora głównego, typu `Szef`

```scala
class Szef extends Actor { … }
```

oraz dynamicznie określanej (w momencie inicjowania działania szefa) liczby aktorów „roboczych”, typu `Pracownik`

```scala
class Pracownik extends Actor { … }
```

Po uruchomieniu programu, `Szef` jest w stanie przyjąć jedynie komunikat inicjalizacyjny postaci

```scala
case class Init(liczbaPracowników: Int)
```

w którego efekcie powinien utworzyć zadaną liczbę aktorów typu `Pracownik` i przejść do stanu, w którym będzie mógł przyjmować „zlecenia” nadsyłane za pomocą komunikatów

```scala
case class Zlecenie(tekst: List[String])
```

Poszczególne napisy z listy `tekst` powinny być następnie przekazane do „obróbki” pracownikom,
zgodnie z zasadą „w koło Macieju” (ang. „round Robin” ;)). Służyć do tego powinny komunikaty

```scala
case class Wykonaj( /* TODO: wymyśl listę parametrów */ )
```

Pracownicy obliczają i zwracają do nadzorcy informację o liczbie znalezionych w przekazanym im napisie słów,
używając komunikatów 

```scala
case class Wynik( /* TDOO: wymyśl listę parametrów */ )
```

`Szef` sumuje napływające wyniki i po otrzymaniu wszystkich (jak to zapewnić?) odpowiedzi od pracowników wypisuje na konsoli wynik i wraca do stanu oczekiwania na kolejne zlecenie.
