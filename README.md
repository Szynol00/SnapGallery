# SnapGallery

SnapGallery to aplikacja mobilna zaprojektowana, aby pomóc użytkownikom efektywnie zarządzać plikami multimedialnymi, takimi jak zdjęcia i filmy, na urządzeniach z systemem Android. Aplikacja oferuje funkcje do przechwytywania, organizowania i zabezpieczania plików multimedialnych za pomocą łatwego w użyciu interfejsu.

## Spis Treści

1. [Opis Projektu](#opis-projektu)
2. [Funkcje](#funkcje)
3. [Instalacja](#instalacja)
4. [Wymagania](#wymagania)
5. [Użytkowanie](#użytkowanie)
6. [Architektura](#architektura)
7. [Widoki Aplikacji](#widoki-aplikacji)
8. [Szczegółowa Dokumentacja](#szczegółowa-dokumentacja)
9. [Autorzy](#autorzy)
10. [Licencja](#licencja)

## Opis Projektu

SnapGallery to aplikacja na system Android, stworzona w celu dostarczenia użytkownikom prostego i funkcjonalnego narzędzia do zarządzania plikami multimedialnymi. Umożliwia robienie zdjęć i nagrywanie filmów, wyszukiwanie, usuwanie oraz udostępnianie plików. Kluczową funkcją jest możliwość ukrywania i zabezpieczania multimediów za pomocą kodu PIN lub autoryzacji odciskiem palca.

## Funkcje

- **Zarządzanie multimediami:** Przeglądanie, organizowanie i edytowanie kolekcji zdjęć i filmów.
- **Przechwytywanie multimediów:** Robienie zdjęć i nagrywanie filmów bezpośrednio w aplikacji.
- **Zabezpieczanie multimediów:** Ukrywanie i zabezpieczanie zdjęć i filmów za pomocą kodu PIN lub odcisku palca.
- **Minimalistyczny interfejs:** Prosty i przyjazny interfejs użytkownika, skoncentrowany na treści multimedialnej.
- **Obsługa gestów:** Intuicyjna nawigacja i interakcja za pomocą gestów.
- **Opcje udostępniania:** Udostępnianie wybranych multimediów na platformach społecznościowych lub innym użytkownikom.
- **Dostęp do aparatu:** Wbudowana funkcja aparatu do robienia zdjęć i nagrywania filmów.
- **Używanie latarki:** Możliwość używania latarki w warunkach słabego oświetlenia podczas robienia zdjęć.

## Instalacja

Aby zainstalować SnapGallery:

1. Pobranie pliku APK na urządzenie z Androidem.
2. Otwarcie pliku APK i postępowanie zgodnie z instrukcjami instalacji.
3. Po zakończeniu instalacji uruchomienie aplikacji i postępowanie zgodnie z procesem konfiguracji.

## Wymagania

- Urządzenie z systemem Android w wersji 10 (API 29) lub nowszym.
- Minimum 40MB wolnej przestrzeni na dysku.
- Dostęp do kamery urządzenia do robienia zdjęć i nagrywania filmów.
- Kod PIN lub czytnik linii papilarnych do korzystania z funkcji zabezpieczeń aplikacji.

## Użytkowanie

1. **Uruchamianie aplikacji:**
   - Otwarcie aplikacji i uwierzytelnienie się za pomocą kodu PIN lub odcisku palca.
   
2. **Przechwytywanie multimediów:**
   - Użycie wbudowanej funkcji aparatu, aby robić zdjęcia lub nagrywać filmy.
   
3. **Zarządzanie multimediami:**
   - Przeglądanie biblioteki multimediów, organizowanie plików w albumach i edytowanie według potrzeb.
   
4. **Zabezpieczanie multimediów:**
   - Ukrywanie i zabezpieczanie poufnych zdjęć i filmów za pomocą funkcji zabezpieczeń.
   
5. **Udostępnianie multimediów:**
   - Udostępnianie multimediów bezpośrednio z aplikacji na platformy społecznościowe lub innym użytkownikom.

## Architektura

Aplikacja składa się z kilku kluczowych komponentów:

- **Activities (Aktywności):** Zarządzają interakcjami użytkownika i przepływem aplikacji.
- **Adapters (Adaptery):** Dostarczają dane do widoków w interfejsie użytkownika.
- **Fragments (Fragmenty):** Wyświetlają różne widoki w zależności od interakcji użytkownika.
- **Models (Modele):** Definiują strukturę i właściwości danych używanych w aplikacji.
- **Repositories (Repozytoria):** Zarządzają operacjami dostępu do danych, oddzielając logikę danych od logiki interfejsu użytkownika.
- **Resources (Zasoby):** Zawierają zasoby interfejsu użytkownika, takie jak kolory, układy i obrazy.

## Widoki Aplikacji

### Ekran Główny

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/71cb37b6-d8d0-4242-960d-86ed4355c4b2" alt="Ekran Główny" width="300" style="border: 2px solid #000;"/>

### Przeglądanie Wszystkich Zdjęć

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/2b6d8de2-c938-4940-9e0b-cc2376147f20" alt="Przeglądanie Wszystkich Zdjęć" width="300" style="border: 2px solid #000;"/>

### Przeglądanie Konkretnego Zdjęcia

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/55cbb252-26b6-4cc1-82e0-51f3971f0d33" alt="Przeglądanie Konkretnego Zdjęcia" width="300" style="border: 2px solid #000;"/>

### Przeglądanie Wszystkich Filmów

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/f411fa92-c5d5-468e-bba7-901c2d0f5944" alt="Przeglądanie Wszystkich Filmów" width="300" style="border: 2px solid #000;"/>

### Przeglądanie Konkretnego Filmu

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/d6265d84-e28e-4612-986c-282f34e1e97e" alt="Przeglądanie Konkretnego Filmu" width="300" style="border: 2px solid #000;"/>

### Widok Aparatu

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/00ae0bb9-8e2f-4e7e-bdab-2eec8e04d842" alt="Widok Aparatu" width="300" style="border: 2px solid #000;"/>

### Widok Albumów

<img src="https://github.com/Szynol00/SnapGallery/assets/104465225/cfdf716c-ff62-4e2d-b825-7a43d6295e43" alt="Widok Albumów" width="300" style="border: 2px solid #000;"/>

## Szczegółowa Dokumentacja

Szczegółowa dokumentacja znajduje się w plikach [dokumentacja.pdf](Dokumentacja.pdf) oraz [manual.pdf](Manual.pdf).

## Autorzy

- **Szymon Całka** - [GitHub](https://github.com/Szynol00)
- **Artur Pas** - [GitHub](https://github.com/Pasek108)

## Licencja

Ten projekt jest licencjonowany na podstawie licencji MIT - zobacz plik [LICENSE](LICENSE) po więcej szczegółów.
