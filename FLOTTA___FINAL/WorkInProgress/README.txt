Fleet Monitoring

-----------------------------------------------------------------------------------------------------------------------

Flotta di 4 veicoli dotati di sensore gps con sede a casa mia (Ostiglia - Via Vamba 4) e sensore di batteria
quindi (8 sensori - 4 batterie / 4 gps).

Obbiettivo tracciare gli spostamenti dei 4 veicoli tenendo monitorato il livello di batteria, dato che
nel caso il livello scendesse sotto a tot (20%) dovrebbero vedersi notificare la colonnina di ricarica
più vicina a loro (memorizzate 4 colonnine ognuna vicina ad una destinazione di arrivo).

Quindi esempio per un singolo mezzo:

Utilizzo di CoAP per avere la possibilità di comunicare in entrambe le direzioni.

        GPSCar1 <----- Car1 -----> Battery1

Data manager <----- Veicolo 1 (sensore gps / livello batteria) ---- Richiesta GET (obs)
...
Il data manager fa una richiesta get sulle due risorse settate come osservabili e resta in ascolto.

Data manager (switch-on - posizione colonnina) -----> Veicolo 1 ---- Richiesta PUT o POST (forse meglio POST?)
...
Una volta arrivati nello stato di "Low_Battery", il veicolo deve inviare una GET per ricevere la posizione
della colonnina più vicina,

-----------------------------------------------------------------------------------------------------------------------

Le gps tracks sono state importate grazie a share link su google maps e incollando link in : https://mapstogpx.com/

Esiste un modo per visualizzare a schermo gli spostamenti delle diverse auto una volta salvate le coordinate?

Icone presentazione trovate su : https://icons8.it/