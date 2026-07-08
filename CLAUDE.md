# CLAUDE.md — Projet Élan (spera)

Application mobile **Élan** : sport & recettes, ensemble. Suivi de progression,
partage de séances, recettes adaptées.

## Stack

- **Frontend** : Kotlin Multiplatform + Compose Multiplatform (Android + iOS).
- **Backend / BD** : Supabase (PostgreSQL + Auth + RLS).
- **DI** : Koin.
- **HTTP** : Ktor (OkHttp sur Android, Darwin sur iOS).
- **Map** : MapLibre (US13). **Reconnaissance vocale** : Porcupine (US15/16).

## Structure du code

- Code partagé : `shared/src/commonMain/kotlin/com/example/spera/`
  (⚠️ vérifie toujours le package racine réel avant de créer un fichier).
- Code spécifique plateforme : `shared/src/androidMain/...`, `shared/src/iosMain/...`.
- App Android : `androidApp/`. App iOS : `iosApp/`.
- Migrations SQL Supabase : `supabase/migrations/`.

Organisation par couche (convention établie en US2) :
- `core/` — config transverse (ex. `SupabaseConfig.kt`).
- `data/<feature>/` — Repository + module Koin de la feature.
- `ui/screens/<feature>/` — UiState, ViewModel, Screen composable.

## Documentation du repo (à consulter avant chaque US)

Quatre sources de référence, à lire systématiquement au début d'une US
(étape 0 du skill `us`). Les noms exacts peuvent varier ; localise-les avec
`Glob`/`Grep` si besoin.

- **`data.txt`** — format des données (entités, champs, relations Supabase).
- **`tech.txt`** — technologies imposées à utiliser (Supabase, Compose MP,
  Koin, MapLibre, Porcupine…).
- **`ui-state.txt`** — états UI par feature → définissent les `UiState` des
  ViewModels.
- **`maquettes.pdf`** — prototype des écrans (archive zip de JPEG ; voir la
  table de correspondance ci-dessous).

## Workflow imposé pour chaque User Story

0. **Consulter la doc** ci-dessus (`data.txt`, `tech.txt`, `ui-state.txt`,
   `maquettes.pdf`) avant toute chose.
1. Créer une branche depuis `develop` : `feature/US<n>-<slug>`.
2. Proposer un **plan** (DB → data → ViewModel → UI) et **attendre validation**
   avant de coder.
3. Implémenter en suivant les conventions ci-dessous.
4. Terminer par un résumé des changements + la commande de push.

## Conventions

- **Auth / session** : réutiliser l'existant d'US2 — ne PAS créer de second
  `SupabaseClient` ni de nouvelle gestion de session.
- **Sécurité** : aucun secret committé (client IDs, clés service_role…).
  Les valeurs sensibles vont dans `SupabaseConfig.kt` avec des placeholders,
  ou via BuildConfig / gradle properties.
- **Mots de passe** : jamais stockés dans une table publique — Supabase Auth
  gère les credentials hashés.
- **RLS** : toute table exposée doit avoir Row Level Security activée + policies.
- **UiState** : sealed interface alignée sur le doc `UI State` (Success / Loading
  / Error, + états spécifiques). Pattern de référence : `LoginUiState`.
- **ViewModel** : `androidx.lifecycle.ViewModel` (commonMain), état exposé en
  `StateFlow`, validation locale avant appel réseau. Référence : `LoginViewModel`.
- **Repository** : interface + impl, mapping des erreurs Supabase vers une
  sealed interface de résultat métier. Référence : `AuthRepository` / `SignInResult`.
- **UI** : reproduire fidèlement l'écran correspondant du prototype (JPEG dans
  le zip `_lan__Prototype.pdf`) — mêmes couleurs, formes, libellés FR.
  Palette auth de référence : fond `#0F0D14`, surface `#1C1922`,
  primaire `#8B2FF0`, accent rose `#E93D9B`.

## Modules Koin

Enregistrer chaque nouveau module dans `startKoin { modules(...) }`.
Modules existants : `appModule`, `authModule`.

## Prototype (maquette)

Le fichier `_lan__Prototype.pdf` est en réalité une archive zip d'images JPEG,
une par écran, numérotées. Correspondance indicative :
1 = Welcome, 2 = Créer un compte, 3 = Bon retour (login), 4 = Fil d'actualité,
5 = Publication (détail post), 6 = Nouvelle publication, 7 = Rechercher user,
8 = Recettes (liste), 9 = Détail recette, 10 = Entraînement (calendrier),
11 = Nouveau footing, 12 = Séance en cours, 13 = Timer effort/repos,
14 = Profil, 15 = Modifier le profil.

## Backlog (issues GitHub : RoroG17/spera)

Épic 1 Auth : US1 création compte, **US2 connexion (fait)**, US3 Google/Apple.
Épic 2 Fil : US4 fil, US5 like/commentaire, US6 ajouter post, US7 recherche user, US8 favori.
Épic 3 Recettes : US9 mes recettes, US10 recherche recettes, US11 adapter quantités IA.
Épic 4 Entraînement : US12 calendrier, US13 footing, US14 timer, US15 assistant vocal, US16 modif vocale.
Épic 5 Profil : US17 consulter, US18 modifier, US19 besoins nutritionnels.
Bonus : US20 s'abonner, US21 notifications.