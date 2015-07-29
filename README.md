# Analyseverktøy for Sparebanken Vest

## Kva er det?
Analyseverktøyet er eit program som gir deg oversikt over utgiftene og inntektene dine. Kor mykje brukte du på daglegvarer i august? Kor mykje brukar du på dagligvarer i gjennomsnitt? Kor mykje løn har eg fått så langt i år? 
Programmet viser dette i form av både tabellar, linje- og kakediagram.

## Korleis bruke det?
1. Logg inn i [nettbanken](http://spv.no).  
2. Gå til Mine kontoer -> Søke i transaksjoner.  
3. Vel dei transaksjonane du ønsker å analysere. Til dømes alle det siste året frå brukskontoen din.
4. Trykk på lenka "Kopier til tekstfil". Du vil få lasta ned ei fil ved namn "transaksjoner.csv".  
5. Last ned [analyseverktøyet](https://github.com/draperunner/Sparebanken-Vest-Analyse/blob/master/spv-analyse.jar?raw=true).  
6. Opne fila du lasta ned (spv-analyse.jar).  
7. Trykk knappen "Finn fil", og vel transaksjoner.csv frå trinn 4.  
8. Trykk "Start analyse".

## Korleis fungerer det?
Dagligvareutgifter vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
REMA, ICA, BUNNPRIS, SIT KAFE, SIT STORKIOSK, MENY, RIMI, NARVESEN, JOKER, KIWI    

Restaurantar vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
SZECHUAN AS, SESAM, ALPINO, SMILE PIZZA, LA FIESTA, HARD ROCK CAFE, UPPER CRUST    

Nattliv vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
CROWBAR, GOSSIP, DATTERA TIL HAGEN, SERVERINGSGJENG, CAFE 3B, CAFE MONO, RAMP PUB, TRONDHEIM MIKRO, FIRE FINE, GOOD OMENS    

Kollektivtrafikk vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
ATB, RUTER, BUSS, FLYTOGET, FLYBUSS, NSB    

Streaming vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
SPOTIFY, NETFLIX, HBO    

Telefonrekningar vert henta frå alle transaksjonar som inneheld eitt eller fleire av desse orda:  
TELEREGNING    

Løn vert henta frå alle transaksjonar med tekstkode "LØNN".  
Stipend/Lån vert henta frå alle transaksjonar med skildring som inneheld "STATENS LÅNEKASSE".
