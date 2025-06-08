# 🐝 BeeSense App

**Android aplikácia na monitorovanie včelích úľov — postavená v Kotline a Jetpack Compose**

---

## 🌟 O aplikácii

Aplikácia získava dáta zo senzorov umiestnených v úľoch a zobrazuje ich v reálnom čase — aby ste mali rýchly prehľad o stave vašich včiel.

Dáta sa ukladajú do databázy (**MySQL**) na serveri, odkiaľ ich aplikácia načítava cez REST API.  
V aplikácii si potom viete tieto údaje vizualizovať a sledovať trendy a zmeny v čase.

---

## 🏆 Hlavné funkcie

### 📋 Prehľad (aktuálne hodnoty)

- Zobrazenie **posledných nameraných hodnôt** pre každý úľ:
  - Aktuálna hmotnosť úľa (ľavá a pravá strana)
  - Zmena hmotnosti oproti predchádzajúcemu meraniu
  - Teplota, vlhkosť, tlak vzduchu
- Rýchly pohľad na to, či je všetko v poriadku alebo niečo treba riešiť.

### 📊 Grafy

- Obrazovka na vizualizáciu údajov:
  - Vyberiete si konkrétny úľ zo zoznamu
  - Vyberiete si typ údajov, ktoré chcete zobraziť (hmotnosť, teplota, vlhkosť, ...)
  - Vyberiete časové obdobie — možnosti:
    - Deň
    - Týždeň
    - 2 týždne
    - Mesiac
    - 3 mesiace
    - 6 mesiacov
    - Rok
    - **Vlastné obdobie** cez DatePicker (napríklad od 7.4. do 9.6.)

- Interaktívny graf:
  - Body sú spojené spojnicou

### 📝 Denník

- Umožňuje vám zapisovať si poznámky ku každému úľu:
  - Poznámka s časom a typom zásahu (napr. kontrola, kŕmenie, ošetrenie a pod.)
  - Možnosť filtrovania a úpravy poznámok

### 🔄 Aktualizácia dát & notifikácie (pripravujeme)

- Aplikácia bude vedieť **pravidelne načítavať nové dáta** aj keď nie je spustená
- Notifikácia príde v prípade, že by zmnena hmotnosti bola príliš vysoká alebo nízka.
---

## ⚙️ Ako to funguje

- Používa architektúru **MVVM (Model-View-ViewModel)**
- Dáta sa načítavajú cez REST API, ktoré komunikuje s databázou **MySQL**
- Senzorické dáta sú uložené v databáze v JSON formáte

Príklad uložených dát v databáze:

```json
{
  "humidity": 68.25,
  "pressure": 1013.75,
  "weight_left": 12.4,
  "weight_right": 12.1,
  "temperature_sensor": 34.2,
  "temperature_outside": 26.7
}
