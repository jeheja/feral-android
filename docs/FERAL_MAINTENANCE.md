# Maintenance du fork Feral (Android)

Référentiel de maintenance de `jeheja/feral-android`, fork de
`element-hq/element-x-android`. But : **rester synchronisé avec l'upstream en
quasi-temps réel, sans perdre les customisations Feral, sans sacrifier la
sécurité.** Les builds manuels (signature) sont assumés.

> Ce document fait autorité. Il **remplace** le système `patches/` +
> `scripts/apply-feral-patches.sh` (obsolète et contradictoire avec le workflow
> par commits directs) — voir §7.

---

## 1. Ce qui n'allait pas (constat)

- **L'override members-only était du code mort, jamais compilé.** Il vivait dans
  `features/enterprise/impl/FeralEnterpriseService.kt`, un dossier **sans
  `build.gradle.kts`**. Or `settings.gradle.kts` n'inclut un projet que s'il a un
  fichier de build → ce dossier n'était **pas** un module Gradle et n'était donc
  **jamais compilé**. Il traînait en plus un import Anvil périmé (upstream a migré
  **Anvil → Metro**). Upstream n'a pas ce dossier (seulement `api`, `impl-foss`,
  `test`). _(À noter : cela signifie que `develop` compilait — le fichier cassé
  n'était pas dans la chaîne de build ; le vrai problème est le verrou inactif
  ci-dessous.)_
- **Le verrou members-only était inactif dans l'APK public.** Le build FOSS
  compile `features/enterprise/impl-foss` (car
  `isEnterpriseBuild = File("enterprise/README.md").exists()` = `false`), dont le
  `DefaultEnterpriseService` renvoyait `defaultHomeserverList() = emptyList()` +
  `isAllowedToConnectToHomeserver() = true`. L'override Feral vivait dans l'autre
  module, jamais compilé → **l'app acceptait n'importe quel homeserver**.
- **Un sync upstream passé (merge de ~2916 commits) avait silencieusement
  reverté** l'override. C'est le mode d'échec classique du merge géant : il noie
  une régression *sémantique* dans un diff illisible.

## 2. Ce que corrige cette branche

- Suppression du dossier mort `features/enterprise/impl` (jamais compilé).
- Nouvel `features/enterprise/impl-foss/…/FeralEnterpriseService.kt` :
  `@ContributesBinding(AppScope::class, replaces = [DefaultEnterpriseService::class])`
  (Metro), qui restaure la liste d'homeservers Feral **dans le module réellement
  compilé**. Upstream n'édite jamais ce fichier → un sync ne peut plus le reverter
  en silence.
- **Test garde-fou** `FeralEnterpriseServiceTest` : `canConnectToAnyHomeserver()`
  doit rester `false`, la liste doit contenir `https://feralisme.fr` et rejeter
  les serveurs tiers. Il tourne en CI (§5) et casse le build **avant** qu'un APK
  « n'importe quel homeserver » puisse être signé.
- ⚠️ À vérifier : `feralism.net` est-il un vrai homeserver Matrix ? Sinon, réduire
  `feralServers` à `feralisme.fr` (cf. TODO dans le code).

## 3. Modèle de branches

Trois remotes, une pile de customisation **mince** :

- `origin` = `jeheja/feral-android` (le fork).
- `upstream` = `github.com/element-hq/element-x-android` (lecture seule) :
  `git remote add upstream https://github.com/element-hq/element-x-android.git`.
- `element-call-embedded` vient de **Maven Central**, pas d'un remote source
  (aucun fork EC — voir §6).

Branches :
- `feral/main` (cible : la branche de release = tag upstream stable + pile Feral ;
  aujourd'hui le rôle est tenu par `develop`, à migrer).
- `feral/sync/vYY.MM.N` : branche jetable ouverte par l'automatisation à chaque
  sync, mergée après revue.

## 4. Suivre l'upstream : rebase-onto-tag, depuis les TAGS

**Pourquoi rebase et pas merge :** le rebase force à re-présenter *chaque* commit
Feral contre le nouvel upstream → un revert silencieux d'une customisation
devient visible. Le merge géant, lui, le cache.

**Pourquoi les tags stables (`vYY.MM.N`) et pas `develop` :** pour un mainteneur
unique, un tag testé vaut mieux que `develop` mouvant. Cadence upstream ≈ un tag
toutes les ~2 semaines (CalVer, `vYY.MM.N` ; les `-rc.N` sont ignorés).

Boucle de sync :
```
git fetch upstream --tags
git checkout -b feral/sync/vYY.MM.N feral/main
git rebase --onto vYY.MM.N <tag-de-base-précédent>
# résoudre les conflits (surtout features/login onboarding + account-provider)
./gradlew :features:enterprise:impl-foss:testDebugUnitTest   # garde-fou members-only
./gradlew :app:assembleFdroidDebug                            # compile + graphe Metro
```
Activer **`git rerere`** (`git config rerere.enabled true`) pour rejouer
automatiquement les résolutions récurrentes.

### Amincir le fork (priorité)

La divergence se concentre aujourd'hui sur les fichiers upstream les plus
mouvants — `OnBoardingView.kt` (touché par 7 commits Feral), les 5 presenters
`account-provider`, `SuperButton.kt`. **Chaque rebase retombe dessus.** Objectif :
sortir les customisations du code cœur vers des points d'extension conçus pour ça.

| Customisation | Aujourd'hui (fragile) | Cible (résistant au rebase) |
|---|---|---|
| App id / nom | édition de `BuildTimeConfig` + `appconfig` | idem (déjà central), OK à garder |
| Serveur / members-only | ✅ `FeralEnterpriseService` (impl-foss, Metro `replaces`) | conserver ce pattern |
| Branding visuel (logo, couleurs, thème) | patchs dans `OnBoardingView.kt`, `SuperButton.kt` | un `productFlavor` Feral + overrides `res/` |
| Notice members-only i18n | 1 clé ajoutée dans les `translations.xml` Localazy régénérés | ressource **Feral-owned** hors des fichiers Localazy |

Cible : ~4 commits atomiques, sur des fichiers que **Feral possède**, hors des
fichiers churny upstream.

## 5. Automatisation (« toujours à jour »)

« Quasi-temps réel » réaliste = **détection + PR + build auto** en quelques heures
d'un tag upstream ; **revue + signature = manuelles** (ralentissement délibéré,
assumé). Pas de ship automatique.

- **`.github/workflows/feral-ci.yml`** — sur chaque push/PR `feral/**` : build
  d'un **APK FOSS debug NON signé** (`assembleFdroidDebug`, valide aussi le graphe
  Metro) + le **test garde-fou**. Zéro secret, zéro keystore. C'est le vérificateur
  de compilation.
- **`.github/workflows/feral-upstream-sync.yml`** — planifié : détecte le dernier
  tag stable upstream et ouvre une **PR de sync** (merge propre) ou une **issue**
  (conflits → rebase manuel). Ne ship jamais rien. Le lancer d'abord à la main
  (`Run workflow`) avant de compter sur le cron.
- **Renovate** — `.github/renovate.json` (hérité, fonctionnel ; Dependabot est
  neutralisé via `open-pull-requests-limit: 0`, ne pas le réactiver). Il suffit
  d'**activer l'app Renovate** sur `jeheja/feral-android`. `element-call-embedded`
  est marqué **review-required** (pas d'auto-merge) — cf. §6.
- Épingler les actions GitHub **par SHA** avant de faire confiance à la CI.

## 6. Element Call : jamais un merge de source

**Aucun fork d'Element Call.** Les 3 clients consomment EC en boîte noire :
- **Android** : artefact Maven Central `io.element.android:element-call-embedded`
  (unique conso : `features/call/impl`). MaJ = **bump d'une ligne** dans
  `gradle/libs.versions.toml` + rebuild. **Ne jamais bumper en standalone** :
  laisser le merge du tag upstream porter la version EC qu'il embarque et teste.
  Un bump Renovate isolé reste review-required (risque d'incompat widget/SDK).
- **Web déployé** (`/opt/feral-source`) : widget distant `call.element.io`. À
  terme, **self-héberger la SPA EC** (Feral a déjà LiveKit + lk-jwt) pour ne pas
  fuiter les métadonnées d'appel vers Element.
- **Desktop** (`~/feral-build`) : bundle npm `@element-hq/element-call-embedded`.

⚠️ **Dérive de versions à aligner** : Android `0.17.0`, desktop `0.21.0`, web =
ce que sert `call.element.io`. Vérifier aussi que le widget d'appel Android pointe
sur **le LiveKit de Feral**, pas sur `call.element.io` par défaut.

Supply-chain : activer la **dependency verification Gradle**
(`verification-metadata.xml`, sha256 + PGP) pour cet AAR et les autres deps.

## 7. Consolidation

Deux systèmes de customisation coexistaient et se contredisaient :
- `patches/` (10 `.patch`) + `scripts/apply-feral-patches.sh`,
- les commits directs (que `FERAL_CUSTOMIZATION.md` déclarait canoniques).

**Décision : un seul système = commits directs (branche Feral).** Le dossier
`patches/` est à retirer (il n'est pas appliqué par le build et induit en erreur).
Non supprimé par cette branche pour ne pas détruire d'historique sans validation :
```
git rm -r patches scripts/apply-feral-patches.sh
```

## 8. Signature & sécurité

La **clé de signature est l'ancre de confiance de tout le système** : pas de Play
Store (donc pas de re-signature Google en filet), et le futur updater intégré fait
confiance à cette signature pour accepter une MaJ. La compromettre = pousser une
MaJ malveillante signée à tous les membres.

- **Signer sur eheyu, manuellement.** Le keystore ne quitte jamais eheyu
  (`signing.properties` gitignoré, `FERAL_RELEASE_*`). **Rejeté** : keystore dans
  les secrets CI (exposé à toute la supply-chain), signature sur le VPS de prod
  (exposé). La CI produit le **non signé** ; eheyu signe.
- Durcir : passphrase forte **hors des notes en clair**, sauvegarde offline
  chiffrée, idéalement token matériel (PKCS#11 / YubiKey). APK Signing Scheme
  v2+v3+v4.
- **Build reproductible** (à valider) : eheyu rebuild et *diffe* l'artefact CI
  avant de signer → une CI compromise ne peut pas faire signer du code injecté.

### Updater intégré (façon Telegram) — invariants de sécurité

- **Flux public signé** : manifeste + APK sur URL publique (l'app est publique ;
  seuls les membres peuvent s'y *connecter*). Intégrité par HTTPS **+ sha256 + et
  surtout la signature de l'APK** (le sha256 seul prouve le transport, pas
  l'authenticité).
- **Vérifier l'empreinte du certificat de signature Feral (pinnée) AVANT
  l'installation**, pas seulement le sha256.
- **Manifeste signé** (signature détachée, clé Feral vérifiée in-app) — sinon un
  MITM/compromission serveur peut le pointer vers un APK attaquant.
- **`versionCode` monotone** (refuser ≤ installé) → anti-downgrade.
- **TLS/public-key pinning** vers `feralisme.fr` en défense en profondeur.

## 9. Licence & marque

- **AGPL-3.0-only** (les en-têtes SPDX machine font foi). Garder les en-têtes SPDX
  et les copyrights Element/New Vector intacts ; marquer les fichiers modifiés
  (§5(a)). Distribuer l'APK **oblige** à fournir la source correspondante (§6/§13)
  → ajouter un lien **« code source »** in-app / sur `feralisme.fr` pointant vers
  le repo + tag exact.
- **Marque** (séparée de l'AGPL) : ne pas utiliser « Element »/« Element X », le
  logo ni le branding d'Element ; ne pas suggérer une affiliation. Feral est déjà
  conforme (nom/logo/identité distincts, services Element mis à `null` dans
  `BuildTimeConfig`). Autorisé, en texte : « compatible avec Element »,
  « construit sur la technologie open-source d'Element, sans affiliation ».

## 10. Registre de risques (synthèse)

| Risque | Mitigation |
|---|---|
| Revert silencieux d'une customisation sécu au sync (**déjà arrivé**) | test garde-fou bloquant + rebase-onto-tag + pile mince |
| Verrou members-only inactif (module non compilé) | override dans `impl-foss` via Metro `replaces` (fait) |
| Updater : install d'un APK malveillant / MITM | vérif empreinte certif + manifeste signé + `versionCode` monotone + pinning |
| Downgrade (ancien APK vulnérable, sha+signature valides) | `versionCode` monotone, version-plancher dans le manifeste signé |
| Exposition du keystore | eheyu offline uniquement, token matériel, sauvegarde chiffrée |
| Supply-chain `element-call-embedded` / transitives | version épinglée + dependency verification + bump review-required |
| Conflits récurrents sur les `translations.xml` Localazy | déplacer la notice dans une ressource Feral-owned |

---

_Analyse et mise en place initiales : 2026-08-21._

---

## 11. Updater intégré — implémentation (2026-08-21)

**Statut : implémenté** (branche `feral/fix-members-only-and-maintenance`), à valider
par le build CI puis un build signé eheyu.

### Côté app
- Nouveau module **`features/appupdate/{api,impl}`** (auto-inclus par
  `settings.gradle` + `allFeaturesImpl`) :
  - `AppUpdateChecker` — GET public `…/media/downloads/android/update.json`
    (OkHttp + kotlinx `ignoreUnknownKeys`), au plus une requête / 6 h
    (`AppUpdateConfig.CHECK_INTERVAL_MS`), résultat mis en cache (DataStore
    `feral_appupdate`), fail-quiet. Anti-downgrade (`versionCode` strictement
    supérieur) + version ignorée après « fermer ».
  - `ApkDownloader` — télécharge dans `cacheDir/updates/` (FileProvider `cache-path`),
    vérifie **sha256 manifeste** + **empreinte du certificat de signature épinglée**
    (`AppUpdateConfig.SIGNING_CERT_SHA256`, extraite de l'APK 25.05.4 servi) +
    **versionCode de l'archive** (> installé et == manifeste) + `packageName`,
    puis lance l'installation (`ACTION_VIEW` + FileProvider ; permission
    `REQUEST_INSTALL_PACKAGES` déjà dans le manifeste).
  - `AppUpdateBannerPresenter` — check au premier affichage de la liste, bannière
    via le composant design-system `Announcement`.
- **Édits cœur minimaux** (5 fichiers home) : champ `appUpdateBannerState` dans
  `RoomListContentState.Rooms`, presenter injecté, `AppUpdateBanner` en tête de la
  LazyColumn, fixtures/tests mis à jour. Strings dans
  `strings_feral_appupdate.xml` (en+fr, hors Localazy).
- `appconfig/AppUpdateConfig.kt` : URL du canal, empreinte certif, intervalle,
  interrupteur `ENABLED`.
- ⚠️ Le versionCode par ABI = base×10+chiffre ABI (`app/build.gradle.kts`) → le
  manifeste porte un versionCode **par APK** (géré par le script de release).

### Côté serveur (fait, sans sudo)
- `/var/www/html/feralism/media/downloads/android/` créé — servi publiquement par le
  bloc nginx `location /media/` existant (vérifié 200/206 en HTTPS). Les APK 25.05.4
  + .sha256 y sont copiés. `update.json` n'y sera déposé qu'à la **prochaine**
  release (une app sans updater ne le lit pas ; 404 = silencieux côté app).

### Runbook de release (sur eheyu)
```
./gradlew assembleGplayRelease            # build signé (signing.properties)
./tools/feral/publish-release.sh \
    --version 26.08.1 \
    --apk-dir app/build/outputs/apk/gplay/release \
    --changelog-fr "…" --changelog-en "…" \
    --deploy loic_feral@172.232.45.124
```
Le script : renomme selon la convention `Feral-<ver>[-abi].apk`, génère
`.sha256`/`update.json`/`version.json`/`latest.json` (versionCode lus via aapt),
déploie **les APK d'abord, les manifestes en dernier** (atomicité), vers le
répertoire public ET `protected_downloads/` (page membre).

### Invariants de sécurité implémentés
sha256 (transport) + certificat épinglé (authenticité — sans le keystore eheyu,
aucun APK accepté) + versionCode monotone (anti-downgrade) + `packageName` vérifié +
HTTPS. Non implémenté (amélioration future) : signature détachée du manifeste,
TLS pinning.
