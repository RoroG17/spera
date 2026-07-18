---
name: us
description: Implémente une User Story du projet Élan de bout en bout (branche, plan, ViewModel, Repository, requêtes Supabase, UI depuis la maquette, doc). Invoquer avec /us puis le numéro et le titre de l'US, ex. "/us US4 Consulter le fil d'actualité". À utiliser dès qu'on demande de réaliser/implémenter une US ou un item du backlog.
argument-hint: "US<n> <titre>"
user-invocable: true
allowed-tools: Read, Grep, Glob, Edit, Write, Bash
---

# Réaliser une User Story — Élan

Cible : **$ARGUMENTS**

Suis les conventions de `CLAUDE.md` (stack, structure, sécurité, réutilisation
de l'auth US2). Déroule les étapes ci-dessous **dans l'ordre**. Ne saute aucune
étape et **n'écris aucun code avant la validation du plan (étape 4)**.

## 0. Consulter la documentation du repo (obligatoire, avant tout)

Avant de commencer l'US, lis les fichiers de documentation du dépôt pour te
donner le contexte. **Ne passe pas à l'étape suivante tant que ces quatre
sources n'ont pas été lues.** (Les noms peuvent varier légèrement selon le
repo ; utilise `Glob`/`Grep` pour les localiser si besoin.)

- **`data.txt`** (doc Data / Supabase) → format des données : entités, champs,
  relations. Sert de base au schéma SQL et aux modèles.
- **`tech.txt`** (doc Technologie) → technologies imposées à utiliser pour
  cette US (ex. Supabase, Compose Multiplatform, MapLibre pour les cartes,
  Porcupine pour la voix). N'introduis pas d'autre techno sans raison.
- **`ui-state.txt`** (doc UI State) → états attendus pour la feature ; ils
  définissent la sealed interface `UiState` du ViewModel (Success / Loading /
  Error + états spécifiques).
- **`maquettes.pdf`** (prototype) → aperçu de l'UI cible. ⚠️ Ce fichier est en
  réalité une archive zip d'images JPEG (une par écran). Extrais-le
  (`unzip -o maquettes.pdf -d /tmp/proto`) et ouvre le JPEG de l'écran concerné
  (table de correspondance dans `CLAUDE.md`).

Résume en une ou deux lignes ce que chaque source t'apprend pour cette US
précise, puis continue.

## 1. Lire l'US

- Retrouve l'US ciblée dans le fichier des épics
  (`____Épic_1__Authentification.txt` et docs liés) : acteur, besoin, objectif,
  et surtout les **critères d'acceptation**. Liste-les explicitement.
- Croise avec la doc lue à l'étape 0 : l'entité concernée dans `data.txt`,
  les états attendus dans `ui-state.txt`, les technos imposées dans `tech.txt`,
  et l'écran cible dans `maquettes.pdf`.

## 2. Vérifier l'existant

- Avant d'écrire quoi que ce soit, explore le code : `Glob`/`Grep` sur les
  packages `core/`, `data/`, `ui/screens/`.
- Identifie ce qui est **déjà en place et réutilisable** (client Supabase,
  session, modèles, navigation, composables partagés, module Koin).
- Note explicitement ce qui existe déjà pour ne pas le dupliquer.
  (Rappel : l'auth et la session viennent d'US2 — ne pas recréer.)

## 3. Proposer le plan et attendre validation

Présente un plan concis couvrant DB → data → ViewModel → UI, en indiquant :
- le nom de branche `feature/US<n>-<slug>` ;
- la/les table(s) Supabase + RLS à créer ou modifier ;
- les fichiers à créer/modifier, par couche ;
- l'écran de la maquette utilisé comme référence ;
- ce qui reste à la charge de l'utilisateur (config, secrets).

**Stop.** Attends la validation avant de continuer.

## 4. Créer la branche (après validation)

```bash
git checkout develop && git pull
git checkout -b feature/US<n>-<slug>
```

## 5. Data — requêtes Supabase

- Écris la migration SQL dans `supabase/migrations/` : tables, colonnes,
  contraintes, **RLS activée + policies**. Rappelle à l'utilisateur de
  l'exécuter (dashboard ou `supabase db push`).
- Aucun mot de passe / secret en table publique.

## 6. Repository

- Crée l'interface + l'implémentation dans `data/<feature>/`.
- Réutilise le `SupabaseClient` injecté par Koin.
- Mappe les erreurs Supabase vers une sealed interface de résultat métier.
- Enregistre le module Koin de la feature et ajoute-le à `startKoin { modules(...) }`.
- Modèle de référence : `AuthRepository` / `AuthModule` (US2).

## 7. ViewModel + UiState

- `UiState` : sealed interface alignée sur le doc `UI State` pour cette feature.
- `ViewModel` (`androidx.lifecycle.ViewModel`, commonMain) : état en `StateFlow`,
  validation locale, appels au Repository, mapping vers l'UiState.
- Modèle de référence : `LoginUiState` / `LoginViewModel` (US2).

## 8. UI — depuis la maquette

- Reprends l'écran de `maquettes.pdf` déjà consulté à l'étape 0 (si besoin,
  ré-extrais : `unzip -o maquettes.pdf -d /tmp/proto` ; correspondance des
  écrans dans `CLAUDE.md`).
- Reproduis fidèlement l'écran : mise en page, couleurs, formes, libellés FR,
  états (Loading avec spinner, Error en bannière, etc.).
- Câble la navigation et le ViewModel.

## 9. Documenter

- Rédige un `INTEGRATION.md` (ou une section dédiée) : changements Gradle,
  câblage Koin, navigation, table des fichiers livrés, mapping aux critères
  d'acceptation, et checklist de vérification manuelle.

## 10. Clôturer

- Résume les changements.
- Donne la commande de push : `git push -u origin feature/US<n>-<slug>`.
- Ne committe aucun secret ; vérifie `git status` / `git diff` avant.