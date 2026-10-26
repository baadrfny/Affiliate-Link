
# AffiliateLink — Profile Module

## Présentation

Le module **Profile** constitue la première brique de la plateforme AffiliateLink, une plateforme d'affiliation qui met en relation des publishers et des advertisers.

Il permet de créer et de consulter les profils des utilisateurs. L'administrateur peut également valider, suspendre ou réactiver un compte avant que celui-ci puisse utiliser les fonctionnalités de la plateforme.

## Objectifs

- Gérer les profils des publishers et des advertisers.
- Garantir l'unicité des adresses email sur toute la plateforme.
- Gérer les statuts des comptes selon les règles métier.
- Exposer une API REST pour créer et consulter les profils.
- Organiser le code par domaine métier.
- Garantir la cohérence de la base de données avec Flyway et Hibernate.

## Technologies utilisées

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Bean Validation
- Maven
- Docker Compose
- JUnit 5
- Postman

## Fonctionnalités

### 1. Gestion des publishers

Un publisher peut créer un profil contenant les informations suivantes :

- Email
- Prénom
- Nom
- Niche
- Biographie
- Pays

Les niches autorisées sont :

`BEAUTY`, `TECH`, `GAMING`, `TRAVEL`, `LIFESTYLE`, `FOOD`, `SPORT`.

L'application permet également de consulter un publisher par son identifiant ou de récupérer la liste des publishers.

### 2. Gestion des advertisers

Un advertiser peut créer un compte contenant :

- Email
- Raison sociale
- Secteur d'activité
- Site web

L'application permet également de consulter un advertiser par son identifiant ou de récupérer la liste des advertisers.

### 3. Gestion des statuts

Chaque nouveau compte est créé avec le statut `PENDING`.

Les transitions autorisées sont les suivantes :

| Statut actuel | Nouveau statut autorisé |
|---|---|
| `PENDING` | `ACTIVE` |
| `ACTIVE` | `SUSPENDED` |
| `SUSPENDED` | `ACTIVE` |

L'administrateur peut valider un compte, suspendre un compte actif ou réactiver un compte suspendu.

Un compte n'est jamais supprimé : il peut être suspendu.

## Règles métier

- L'adresse email doit être unique sur toute la plateforme.
- Un même email ne peut pas être utilisé simultanément par un publisher et un advertiser.
- Tout nouveau compte possède le statut `PENDING`.
- Seules les transitions de statut définies dans le brief sont autorisées.
- Les règles métier liées aux transitions de statut sont gérées par les entités.
- Les erreurs métier sont traitées de manière centralisée.

## Modèle de données

### Profile

Classe abstraite commune aux différents types de profils.

Attributs :

- `id`
- `email`
- `status`
- `createdAt`

### Publisher

Hérite de `Profile`.

Attributs spécifiques :

- `firstName`
- `lastName`
- `niche`
- `bio`
- `country`

### Advertiser

Hérite de `Profile`.

Attributs spécifiques :

- `companyName`
- `sector`
- `website`

### Héritage JPA

L'héritage est implémenté avec la stratégie `JOINED`, qui utilise une table commune pour les attributs de `Profile` et une table spécifique pour chaque type de profil.

Exemples de tables :

- `profile_profiles`
- `profile_publishers`
- `profile_advertisers`

## API REST

| Méthode | Endpoint | Description | Réponses attendues |
|---|---|---|---|
| `POST` | `/api/publishers` | Créer un publisher | `201`, `400`, `409` |
| `GET` | `/api/publishers` | Lister les publishers | `200` |
| `GET` | `/api/publishers/{id}` | Consulter un publisher | `200`, `404` |
| `POST` | `/api/advertisers` | Créer un advertiser | `201`, `400`, `409` |
| `GET` | `/api/advertisers` | Lister les advertisers | `200` |
| `GET` | `/api/advertisers/{id}` | Consulter un advertiser | `200`, `404` |
| `PATCH` | `/api/admin/profiles/{id}/status` | Modifier le statut d'un profil | `204`, `404`, `409` |

## Architecture du code

Le projet suit une organisation **package-by-feature** afin de regrouper les composants liés au même domaine métier.

```text
com.affiliateLink
├── common
│   ├── exception
│   └── ...
└── profile
    ├── entity
    ├── repository
    ├── service
    ├── dto
    └── controller
```

Les responsabilités sont réparties comme suit :

- **Controller** : reçoit les requêtes HTTP et retourne les réponses.
- **Service** : orchestre les opérations métier.
- **Repository** : gère l'accès aux données.
- **Entity** : représente le modèle métier et porte les règles de gestion.
- **DTO** : définit les données acceptées et retournées par l'API.
- **Common** : contient les exceptions et les composants communs de gestion des erreurs.

Les dépendances sont injectées par constructeur.

## Validation et gestion des erreurs

Le module utilise Bean Validation pour contrôler les données entrantes, notamment avec `@Valid`, `@NotBlank` et `@Email`.

Les DTO d'entrée et de sortie sont définis sous forme de `record`. Les entités JPA ne sont pas exposées directement par l'API.

Un `@RestControllerAdvice` centralise la gestion des erreurs :

- `400 Bad Request` : données invalides.
- `404 Not Found` : profil introuvable.
- `409 Conflict` : email déjà utilisé ou transition de statut interdite.

## Base de données

PostgreSQL est lancé avec Docker Compose.

Flyway gère les migrations et la version du schéma de la base de données. Hibernate est configuré avec :

`spring.jpa.hibernate.ddl-auto=validate`

Cette configuration permet de vérifier la correspondance entre les entités JPA et le schéma créé par les migrations.

## Installation et lancement

### Prérequis

- Java 17
- Maven
- Docker Desktop
- Postman (pour tester l'API)

### Démarrer la base de données

À la racine du projet :

```bash
docker compose up -d
```

### Lancer l'application

```bash
mvn spring-boot:run
```

### Compiler et exécuter les tests

```bash
mvn clean verify
```

Vérifier la configuration de la connexion PostgreSQL dans `application.properties` ou `application.yml` avant le démarrage.

## Tests à effectuer

- Créer un publisher avec des données valides.
- Créer un advertiser avec des données valides.
- Vérifier l'unicité de l'email entre publishers et advertisers.
- Consulter un profil par son identifiant.
- Récupérer la liste des publishers et des advertisers.
- Vérifier la validation des données entrantes.
- Tester les transitions de statut autorisées et interdites.
- Vérifier les réponses HTTP et la gestion centralisée des erreurs.
- Vérifier la cohérence du schéma avec Flyway et Hibernate.

## Résultat attendu

Un module Profile fonctionnel, organisé par domaine, qui permet de gérer les comptes des publishers et des advertisers, de contrôler leurs statuts et d'exposer une API REST conforme aux exigences du brief.
