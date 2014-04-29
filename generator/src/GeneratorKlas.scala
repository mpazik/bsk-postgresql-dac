import scala.util.Random

case class Profil(name: String, klasy: List[Set[Int]]) {
  def klasa(nr: Int) = klasy(nr - 1)
}

case class Przedmiot(nazwa: String) extends Rekord {
  def toSQL = s"('$nazwa')"
}

case class Klasa(rok: Int, poziom: Int, nazwa: String, przedmioty: Set[Int]) extends Rekord {
  val nauczyciele = GeneratorOsob.pobierzDlaKlasy(przedmioty)

  def toSQL = s"('$nazwa', $poziom, $rok)"
}


case class Kurs(nauczyciel: Int, przedmiot: Int, klasa: Int, iloscGodzin: Int) extends Rekord {
  def toSQL = s"($nauczyciel, $przedmiot, $klasa, $iloscGodzin)"
}

class Ocena(uczen: Int, kurs: Int, stopien: Int, waga: Int) extends Rekord {
  def toSQL = s"($uczen, $kurs, $stopien, $waga)"
}


object GeneratorKlas {
  val obowiazkowe = Map(1 -> "j. polski", 2 -> "matematyka", 3 -> "angielski")
  val reszta = Map(4 -> "biologia", 5 -> "chemia", 6 -> "fizyka",
    7 -> "historia", 8 -> "geografia", 9 -> "informatyka",
    10 -> "wiedza o społeczeństwie", 11 -> "j. niemiecki", 12 -> "j. francuski",
    13 -> "fychowanie fizyczne", 14 -> "religia", 15 -> "podstawy przedsiębiorczości")
  val maturalne = Map(1 -> "j. polski", 2 -> "matematyka", 3 -> "angielski",
    4 -> "biologia", 5 -> "chemia", 6 -> "fizyka",
    7 -> "historia", 8 -> "geografia", 9 -> "informatyka",
    10 -> "wiedza o społeczeństwie", 11 -> "j. niemiecki", 12 -> "j. francuski")
  val ustne = Map(1 -> "j. polski", 3 -> "angielski")
  val wszystkie = obowiazkowe ++ reszta
  var profile = List.empty[Profil]
  
  def losowyDowolnyPrzedmiot = {
    val przedmioty = wszystkie.keySet.toList
    przedmioty(Random.nextInt(przedmioty.size))
  }
  
  def losowyObowiazkowyPrzedmiot = {
    val przedmioty = obowiazkowe.keySet.toList
    przedmioty(Random.nextInt(przedmioty.size))
  }

  def losowyDdatkowyPrzedmiot = {
    val przedmioty = reszta.keySet.toList
    przedmioty(Random.nextInt(przedmioty.size))
  }

  def tabelaPrzedmiotow = new Tabela(listaPrzedmiotow, "przedmiot", List("nazwa"))

  def listaPrzedmiotow = (obowiazkowe ++ reszta).values.map(Przedmiot).toList

  def generujKlasy(iloscProfili: Int, lata: Range) = {
    val nrToName = (nr: Int) => ('A' + nr).toChar.toString
    profile = (0 to iloscProfili).map(nrToName andThen generujProfil).toList
    val lista = for {
      rok <- lata.toList
      poziom <- 1 to 3
      (profil, nr) <- profile.zipWithIndex
    } yield new Klasa(rok, poziom, poziom.toString + profil.name, profil.klasa(poziom))
    new Tabela(lista, "klasa", List("nazwa", "poziom", "rok_utworzenia"))
  }

  def generujProfil(name: String) = {
    val obowiazkoweId = obowiazkowe.keySet
    val resztaId = reszta.keySet.toList.drop(2)
    val reszta1 = Random.shuffle(resztaId).drop(1).toSet[Int]
    val reszta2 = Random.shuffle(resztaId).drop(1).toSet[Int]
    val reszta3 = Random.shuffle(resztaId).drop(1).toSet[Int]
    val przedmioty = List(obowiazkoweId ++ reszta1, obowiazkoweId ++ reszta2, obowiazkoweId ++ reszta3)

    new Profil(name, przedmioty)
  }

  def generujKursy(klasy: List[Klasa]) = {
    def iloscGodzin = 10 * (Random.nextInt(8) + 2)
    val lista = for {
      (klasa, idKlasy) <- klasy.zipWithIndex
      przedmiot <- klasa.przedmioty
    } yield new Kurs(klasa.nauczyciele(przedmiot), przedmiot, idKlasy + 1, iloscGodzin)
    new Tabela(lista, "kurs", List("id_nauczyciel", "id_przedmiot", "id_klasa", "ilosc_godzin"))
  }

  def generujOceny(kursy:List[Kurs], uczniowie: List[Uczen], klasy: List[Klasa]) = {
    def iloscOcen = Random.nextInt(5) + 2
    def waga = Random.nextInt(4)
    def stopien = Random.nextInt(6) + 1

    val klasaKursy = kursy.groupBy(_.klasa)
    val lista = for{
      (uczen, uczenId) <- uczniowie.zipWithIndex
      (kurs, kursId) <- klasaKursy(uczen.klasa).zipWithIndex
      _ <- 1 to iloscOcen
    } yield new Ocena(uczenId + 1, kursId + 1, stopien, waga)
    new Tabela(lista, "ocena", List("id_uczen", "id_kurs", "stopien", "waga"))
  }
}

