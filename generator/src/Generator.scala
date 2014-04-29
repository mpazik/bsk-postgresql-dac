object Generator {

  def main(args: Array[String]) {
    println("Rozpoczęto generowanie")
    GeneratorOsob.loadNames()
    val przedmioty = GeneratorKlas.tabelaPrzedmiotow
    val nauczyciele = GeneratorOsob.generujNauczycieli()
    val klasy = GeneratorKlas.generujKlasy(3, 2010 to 2013)
    val uczniowie = GeneratorOsob.generujUczniow(klasy.lista)
    val kursy = GeneratorKlas.generujKursy(klasy.lista)
    val oceny = GeneratorKlas.generujOceny(kursy.lista, uczniowie.lista, klasy.lista)
    val rangi = Konkursy.tabelaRank
    val udzialyWKonkursach = Konkursy.generujUdzialyWKonkursach(uczniowie.lista)

    println("Zakończono generowanie")
    println("Rozpoczeto zapis do pliktów")

    przedmioty.zapiszDoPliku("przedmioty.sql")
    nauczyciele.zapiszDoPliku("nauczyciele.sql")
    klasy.zapiszDoPliku("klasy.sql")
    uczniowie.zapiszDoPliku("uczniowie.sql")
    kursy.zapiszDoPliku("kursy.sql")
    oceny.zapiszDoPliku("oceny.sql")
    rangi.zapiszDoPliku("rangi.sql")
    udzialyWKonkursach.zapiszDoPliku("udzialyWKonkursach.sql")

    println("Zakończono zapis do pliktów")

  }
}