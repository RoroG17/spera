package com.example.spera.data.feed

import com.example.spera.models.Recipe
import com.example.spera.models.Training
import com.example.spera.models.User

/**
 * Données mockées du fil d'actualité (US4, sans backend).
 *
 * Les [authors] jouent le rôle des abonnements de l'utilisateur connecté : le
 * repo mock renvoie leurs posts. Quand Supabase sera branché, le filtrage se
 * fera réellement sur `user.following`. Les dates sont au format ISO
 * `yyyy-MM-dd` pour un tri lexicographique simple.
 */
object FeedMockData {

    private fun author(
        id: String,
        name: String,
        firstName: String,
        pseudo: String,
    ): User = User(
        id = id,
        name = name,
        firstName = firstName,
        pseudo = pseudo,
        mail = "$pseudo@elan.fr",
        height = 175,
        weight = 70,
        activities = emptyList(),
        followers = emptyList(),
        following = emptyList(),
        favoriteRecipes = emptyList(),
        recipes = emptyList(),
        trainings = emptyList(),
    )

    private val camille = author("a-01", "Bonnet", "Camille", "camille_runs")
    private val yanis = author("a-02", "Mercier", "Yanis", "yanis_lift")
    private val sofia = author("a-03", "Leroy", "Sofia", "sofia_cooks")
    private val thomas = author("a-04", "Girard", "Thomas", "tom_trail")

    private fun training(
        id: String,
        author: User,
        date: String,
        name: String,
        description: String,
    ): FeedItem.TrainingPost = FeedItem.TrainingPost(
        Training(
            id = id,
            date = date,
            name = name,
            description = description,
            photo = "",
            users = author,
            likes = emptyList(),
            comments = emptyList(),
        ),
    )

    private fun recipe(
        id: String,
        author: User,
        date: String,
        name: String,
        description: String,
        prep: Int,
        cook: Int,
    ): FeedItem.RecipePost = FeedItem.RecipePost(
        Recipe(
            id = id,
            name = name,
            date = date,
            users = author,
            description = description,
            photo = "",
            ingredients = emptyList(),
            timePreparation = prep,
            timeCooking = cook,
            likes = emptyList(),
            comments = emptyList(),
        ),
    )

    /** Fil complet, déjà trié du plus récent au plus ancien. */
    val posts: List<FeedItem> = listOf(
        training("t-01", camille, "2026-07-11", "Sortie longue 15 km", "Footing tranquille le long du canal, allure 5:40/km. Jambes légères ce matin !"),
        recipe("r-01", sofia, "2026-07-11", "Bowl protéiné quinoa & poulet", "Mon repas post-training préféré : quinoa, poulet grillé, avocat et pois chiches.", 15, 20),
        training("t-02", yanis, "2026-07-10", "Séance jambes", "Squats, presse et fentes. Nouveau PR au squat à 120 kg 💪", ),
        training("t-03", thomas, "2026-07-10", "Trail des crêtes", "18 km et 900 m de D+. Vue incroyable au sommet, dur mais ça valait le coup."),
        recipe("r-02", sofia, "2026-07-09", "Overnight oats banane-cacao", "Prêt en 5 min la veille : flocons d'avoine, lait d'amande, banane et cacao.", 5, 0),
        training("t-04", camille, "2026-07-09", "Fractionné 10x400m", "Séance de piste intense. Récup 1 min entre chaque. Objectif 10 km sous 45 min."),
        recipe("r-03", yanis, "2026-07-08", "Pancakes protéinés", "3 œufs, 1 banane, flocons et whey. Idéal pour un petit-déj costaud avant la muscu.", 10, 10),
        training("t-05", thomas, "2026-07-08", "Vélo route 60 km", "Belle boucle vallonnée, moyenne 28 km/h. Test des nouvelles roues carbone."),
        recipe("r-04", sofia, "2026-07-07", "Curry de lentilles corail", "Plat végétarien réconfortant, riche en fibres et en protéines. Parfait le soir.", 15, 25),
        training("t-06", yanis, "2026-07-07", "Séance dos & biceps", "Tractions, rowing et curls. Grosse congestion, super sensations."),
        training("t-07", camille, "2026-07-06", "Récupération active", "30 min de footing très lent + étirements. On écoute son corps après la longue."),
        recipe("r-05", sofia, "2026-07-06", "Salade de riz complète", "Riz complet, thon, maïs, tomates et feta. Se transporte facilement au boulot.", 20, 15),
        training("t-08", thomas, "2026-07-05", "Sortie trail nocturne", "12 km à la frontale, ambiance magique. Attention aux racines !"),
        recipe("r-06", yanis, "2026-07-05", "Shake gainer maison", "Lait, banane, beurre de cacahuète, avoine et whey. 700 kcal pour la prise de masse.", 5, 0),
        training("t-09", camille, "2026-07-04", "Tempo run 8 km", "Allure marathon sur 8 km. Régularité au top, mental solide aujourd'hui."),
        recipe("r-07", sofia, "2026-07-04", "Wrap avocat-saumon", "Tortilla complète, saumon fumé, avocat et roquette. Un déjeuner rapide et sain.", 10, 0),
        training("t-10", yanis, "2026-07-03", "Push day", "Développé couché, militaire et dips. Focus sur la technique et le tempo."),
        training("t-11", thomas, "2026-07-03", "Sortie gravel 45 km", "Chemins et pistes forestières. Un peu de boue mais que du bonheur."),
        recipe("r-08", sofia, "2026-07-02", "Soupe miso & tofu", "Légère et réconfortante, parfaite le soir après l'entraînement.", 10, 10),
        training("t-12", camille, "2026-07-02", "Côtes 6x200m", "Travail de puissance en côte. Ça pique mais c'est efficace pour la foulée."),
        recipe("r-09", yanis, "2026-07-01", "Riz au poulet & brocolis", "Le classique de la sèche : simple, efficace et facile à doser.", 15, 25),
        training("t-13", thomas, "2026-07-01", "Rando-course montagne", "20 km, 1200 m D+. Alternance marche et course, grosse journée."),
        recipe("r-10", sofia, "2026-06-30", "Energy balls dattes-amandes", "Snack pré-effort maison, sans sucre ajouté. À garder au frigo.", 15, 0),
        training("t-14", camille, "2026-06-30", "Footing découverte", "Petite sortie de reprise, 6 km relax pour reprendre le rythme en douceur."),
    )
}
