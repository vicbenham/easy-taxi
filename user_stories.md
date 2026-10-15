# Projet Uber --- User Stories

## Story 1 --- Réserver une course

**En tant que** Rider,\
**Je souhaite** réserver une course pouvant m'amener à ma destination,\
**Afin de** disposer d'une alternative efficace aux transports en
commun.

### Critères d'acceptation

-   Le Rider doit avoir suffisamment de fonds sur son compte pour
    réserver une course.
-   Tant qu'un Driver n'est pas assigné à une réservation en cours, le
    Rider ne peut pas effectuer une autre réservation.
-   Pour effectuer une autre réservation, le Rider doit d'abord annuler
    la précédente.
-   Le prix de base dépend des villes de départ et d'arrivée :
    -   Paris → Paris : 2 €.
    -   Extérieur de Paris → Paris : 0 €.
    -   Paris → extérieur de Paris : 10 €.
-   Le prix par kilomètre est de 0,50 €.

## Story 2 --- Annuler une course

**En tant que** Rider,\
**Je souhaite** annuler une course,\
**Car** le Driver met trop de temps à venir.

### Critères d'acceptation

-   Si le Rider annule une course alors que le Driver est déjà en
    chemin, une pénalité de 5 € est appliquée.
-   Une course déjà annulée ne peut pas être annulée à nouveau.
-   Si c'est l'anniversaire du Rider, l'annulation est gratuite, quelle
    que soit la raison.

## Story 3 --- Persister les données et tester les fonctions

### Objectifs techniques

-   Ajouter une base de données PostgreSQL pour stocker les Riders,
    Rides et Drivers.
-   Utiliser Knex comme query builder.
-   Créer les migrations avec la commande
    `npm run knex migrate:make NOM_DE_LA_MIGRATION`.
-   Tester chaque fonction de la classe avec un test d'intégration.

## Story 4 --- Consulter l'historique des courses

**En tant que** Rider,\
**Je souhaite** lister tout l'historique de mes courses avec les Drivers
respectifs,\
**Afin de** pouvoir me figurer la fréquence de mon utilisation.

## Story 5 --- Appliquer un tarif spécial à Noël

**En tant que** Driver,\
**Je souhaite** facturer ma course plus cher le jour de Noël.

### Critère d'acceptation

-   Le jour ou la soirée de Noël, le montant total est doublé.

## Story 6 --- Facturer le service UberX

**En tant que** Driver,\
**Je souhaite** facturer mon service UberX 5 € de plus,\
**Afin de** proposer un service premium.

### Critères d'acceptation

-   Le supplément UberX est de 5 € au total.
-   Le supplément est offert le jour de l'anniversaire du Rider.

## Story 7 --- Calculer les distances et identifier les villes avec Google Maps

### Objectifs techniques

-   Ajouter un composant utilisant l'API Google Maps pour obtenir les
    distances et les informations sur la ville d'une adresse.
-   Utiliser la bibliothèque `@googlemaps/google-maps-services-js`.
-   Créer une clé API Google Maps.

## Story 8 --- Exposer les cas d'utilisation via une API

### Objectifs techniques

-   Rendre les trois cas d'utilisation disponibles aux utilisateurs via
    une API.
-   Implémenter les tests end-to-end (E2E) correspondants.

> Le document ne précise pas davantage quels sont les trois cas
> d'utilisation concernés.

## Story 9 --- Appliquer un tarif majoré aux heures de pointe

**En tant que** Driver,\
**Je souhaite** facturer plus cher aux heures de pointe,\
**Afin de** compenser la forte demande.

### Critères d'acceptation

-   Entre 8 h et 9 h, et entre 18 h et 20 h, une majoration de 50 % est
    appliquée au prix total.
-   Cette majoration est cumulable avec les autres promotions.
-   Un stub `Clock` est nécessaire pour tester le comportement.

## Story 10 --- Bloquer un Driver

**En tant que** Rider,\
**Je souhaite** bloquer un Driver,\
**Afin de** ne plus être assigné à lui.

### Critères d'acceptation

-   Un Rider peut bloquer jusqu'à trois Drivers.
-   Un Driver blacklisté ne peut pas être assigné à ce Rider.
-   Le Rider peut débloquer un Driver.

## Story 11 --- Estimer le prix avant de réserver

**En tant que** Rider,\
**Je souhaite** voir une estimation du prix avant de réserver,\
**Afin de** décider si je prends la course ou non.

### Critères d'acceptation

-   L'estimation retourne le prix sans créer de `Ride` en base de
    données.
-   Elle inclut tous les modificateurs : surge, Noël et UberX.
-   L'estimation expire après cinq minutes.
-   Un token unique est généré pour réserver au prix garanti.
-   Une réservation avec un token expiré retourne une erreur.

## Story 12 --- Tester le parcours E2E d'estimation et de réservation

**En tant que** développeur,\
**Je souhaite** écrire les tests E2E pour la fonctionnalité
d'estimation,\
**Afin de** valider le flux complet `estimate → book` via l'API.

### Critères d'acceptation

-   `POST /rides/estimate` retourne `200` avec le prix, le token et
    `expiresAt`.
-   `POST /rides/estimate` sans `from` ou `to` retourne `400`.
-   `POST /rides/book` avec un token valide retourne `201` et crée le
    `Ride`.
-   `POST /rides/book` avec un token expiré retourne `410`.
-   `POST /rides/book` avec un token invalide retourne `404`.
-   `POST /rides/book` avec un token déjà utilisé retourne `409`.
-   Le prix du `Ride` créé correspond au prix estimé.

## Story bonus --- Attribuer un point de fidélité

**En tant que** Rider,\
**Je souhaite** gagner un point de fidélité pour chaque course
effectuée,\
**Afin de** proposer un service de fidélisation.

### Critères d'acceptation

-   Le processus de réservation (`booking`) doit effectuer une
    transaction et celle-ci doit être testée.
-   Si le point de fidélité n'est pas ajouté, la course (`Ride`) ne doit
    pas être sauvegardée.
