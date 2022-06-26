
# Laboratorium 12

__Zadanie 40__:

Używając aktorów stwórz symulację bitwy dwóch rodów. Każdy ród posiada `Zamek`, a w nim 100 `Obrońców`, którzy ostrzeliwują zamek przeciwnika z łuków. Żeby walka była uczciwa rozkaz do ostrzału wydaje (pojedyncza) `SiłaWyższa`. Rozkaz ten trafia do `Zamku`, a następnie rozsyłany jest wśród `Obrońców`, którzy w danej chwili bronią jego murów. Zasady walki stanowią:

- `Zamki` rozpoczynają bitwę ze 100 `Obrońcami`, których nazwiemy „aktywnymi”;
- Co 1. sekundę `SiłaWyższa` daje `Zamkom` sygnał do strzelania;
- `Zamek`, po otrzymaniu sygnału od `SiłyWyższej`, wydaje rozkaz oddania strzału wszystkim „aktywnym obrońcom”;
- Strzały trafiają w `Zamek` przeciwnika i w losowy sposób spadają na jego `Obrońców` (losowanie „ofiary” ostrzału powinno odbywać się na poziomie ostrzelanego `Zamku`)
- `Obrońca`, na którego spada strzała ma szansę na trafienie równą ([liczba_aktywnych_obrońców]/(2 * 100)), np:
  - gdy `Zamek` ma 100 aktywnych `Obrońców`, strzała ma 50% szans na trafienie;
  - gdy `Zamek` ma 50 aktywnych `Obrońców`, strzała ma 25% szans na trafienie.
- `Obrońca`, który otrzymał „skuteczny” postrzał traci życie;
- Żeby być zdolnym do walki `Zamek` musi mieć przynajmniej jednego aktywnego `Obrońcę`;
- Gdy wszyscy `Obrońcy` danego `Zamku` zginą, ogłasza on, że przegrał bitwę – kończy to symulację (`system.terminate`).

__Uwaga__:

- To, że strzelanie odbywa się co 1 sekundę nie jest ważne – może to być 0.01 sekundy żeby bitwa trwała krócej. Ważne żeby każdy `Obrońca` miał taką samą „szybkostrzelność”.

__Uwagi techniczne__:

- Komunikat wysyłany przez `Zamek` do `Obrońców` reprezentujący „strzałę” przeciwnika, powinien zawierać w sobie informację o aktualnej liczbie „aktywnych obrońców”. Informacja ta zostanie następnie wykorzystana przez trafionego `Obrońcę` do losowego określenia efektu postrzału.
- W celu poinformowania `Zamku`, że jego obrońca ginie, z poziomu `Zamku`, wykorzystaj metodę `context.watch(obrońca)`. Wówczas w przypadku śmierci obrońcy `Zamek` automatycznie otrzyma komunikat `Terminated(obrońca)`, na który powinien stosownie zareagować (usuwając ze swojego stanu informację o poległym `Obrońcy`).

__Podpowiedź__: Do stworzenia rozwiązania użyj szablonu projektu zawartego w pliku `Laboratorium_12.zip`.
