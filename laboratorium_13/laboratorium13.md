
# Laboratorium 13

__Zadanie 41__:

Wykorzystując „zalążek rozwiązania” znajdujący się w pliku `lab13.zip` zaimplementuj „symulator konkursu”, który rozgrywany jest w dwóch rundach – eliminacyjnej oraz finałowej. W eliminacjach bierze udział 50. (losowo wygenerowanych) zawodników, z których 20. – tych z najlepszymi wynikami – przechodzi do rundy finałowej.

System aktorów składa się z _organizatora_, który steruje całością, wylosowuje zawodników, inicjuje rozgrywanie obu rund, agreguje ich wyniki oraz oblicza i prezentuje klasyfikację. Jest on reprezentantem klasy `Organizator`.

Oprócz organizatora, w symulatorze występują (tworzeni przez niego) aktorzy typu `Grupa`. Ich zadaniem jest przeprowadzenie pojedynczych rund konkursu. Odbywa się to poprzez __sekwencyjne__ wykonywanie „prób” przez kolejnych zawodników. `Grupa` powinna na bieżąco przekazywać „surowe” wyniki tych prób do organizatora.

Kiedy rywalizacja w ramach grupy dobiegnie końca, a dane na temat jej wyników znajdą się u organizatora, ten ostatni kończy działanie aktora grupy (wysyłając do niego odpowiedni komunikat). Następnie, `Organizator` oblicza wyniki i wyłania na ich podstawie 20. uczestników rundy finałowej. Następnie tworzy kolejnego aktora typu `Grupa`, przekazując mu (w argumencie konstruktora klasy `Grupa`) listę finalistów.

Klasyfikacja rundy ustalana jest na podstawie porządku opisanego w pliku `Ocena.scala` (w podkatalogu `model` głównego katalogu szkieletu aplikacji). W przypadku rundy finałowej, ostateczna ocena powstaje przez zsumowanie odpowiednich not „po współrzędnych”, np.
```scala
ocenaKwalifikacyjna == Ocena(10, 12, 17)
ocenaFinałowa == Ocena(8, 17, 10)

ocenKońcowa == Ocena(18, 29, 27)
```

`Organizator`, reagując na komunikat `Organizator.Wyniki` wyświetla na konsoli aktualną klasyfikację. Zadbaj, aby podczas wyświetlania poprawnie traktowane były sytuacje remisowe – np.
```
1. John Williams – 18-29-27 = 74
2. Bill Wayman – 17-26-21 = 64
2. Ronald Curtis – 17-26-21 = 64
4. Thomas Frank – 16-26-22 = 64
...
```

Zwróć uwagę, że „Thomas Frank” zajął miejsce 4. a nie 3. oraz na to, że ważniejsze są punkty za pierwszą (potem drugą itd.) rundę.

Symulator sterowany jest poprzez „interfejs użytkownika”, który bezpośrednio komunikuje się z organizatorem. Szkielet kodu interfejsu znajduje się w pliku `zawody.scala`.

__Podpowiedź__: Do generowania danych typu Osoba wykorzystaj przygotowaną metodę `Utl.osoba()`, a do generowania ocen – metodę `Utl.ocena()` (Uwaga! Zwraca ona wartość typu `Option[Ocena]`).
