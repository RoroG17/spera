package com.example.spera.data.recipes

import com.example.spera.models.Ingredient
import com.example.spera.models.IngredientQuantity
import com.example.spera.models.Recipe
import com.example.spera.models.User

/**
 * Données mockées des recettes (US9, sans backend).
 *
 * `lea` reprend l'identité du compte de démo (`u-001`, cf. `MockData`) pour que
 * le filtre « Mes recettes » retourne bien ses recettes une fois connectée.
 * Les dates sont au format ISO `yyyy-MM-dd` pour un tri lexicographique simple.
 */
object RecipeMockData {

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
        height = 170,
        weight = 65,
        activities = emptyList(),
        followers = emptyList(),
        following = emptyList(),
        favoriteRecipes = emptyList(),
        recipes = emptyList(),
        trainings = emptyList(),
    )

    private val lea = author("u-001", "Martin", "Léa", "lea_run")
    private val sofia = author("a-03", "Leroy", "Sofia", "sofia_cooks")
    private val yanis = author("a-02", "Mercier", "Yanis", "yanis_lift")

    private fun ingredient(name: String, unit: String, allergens: List<String> = emptyList()) =
        Ingredient(id = "i-$name", name = name, photo = "", allergens = allergens, unit = unit)

    private val quinoa = ingredient("Quinoa", "g")
    private val poulet = ingredient("Poulet", "g")
    private val avocat = ingredient("Avocat", "pièce")
    private val poisChiches = ingredient("Pois chiches", "g")
    private val avoine = ingredient("Flocons d'avoine", "g", allergens = listOf("gluten"))
    private val laitAmande = ingredient("Lait d'amande", "ml", allergens = listOf("fruits à coque"))
    private val banane = ingredient("Banane", "pièce")
    private val cacao = ingredient("Cacao", "g")
    private val oeuf = ingredient("Œuf", "pièce", allergens = listOf("œuf"))
    private val whey = ingredient("Whey", "g", allergens = listOf("lait"))
    private val lentilles = ingredient("Lentilles corail", "g")
    private val laitCoco = ingredient("Lait de coco", "ml")
    private val riz = ingredient("Riz complet", "g")
    private val thon = ingredient("Thon", "g", allergens = listOf("poisson"))
    private val feta = ingredient("Feta", "g", allergens = listOf("lait"))
    private val tortilla = ingredient("Tortilla complète", "pièce", allergens = listOf("gluten"))
    private val saumon = ingredient("Saumon fumé", "g", allergens = listOf("poisson"))
    private val dattes = ingredient("Dattes", "g")
    private val amandes = ingredient("Amandes", "g", allergens = listOf("fruits à coque"))

    private fun recipe(
        id: String,
        author: User,
        date: String,
        name: String,
        description: String,
        prep: Int,
        cook: Int,
        ingredients: List<IngredientQuantity>,
    ): Recipe = Recipe(
        id = id,
        name = name,
        date = date,
        users = author,
        description = description,
        photo = "",
        ingredients = ingredients,
        timePreparation = prep,
        timeCooking = cook,
        likes = emptyList(),
        comments = emptyList(),
    )

    /** Toutes les recettes visibles, déjà triées de la plus récente à la plus ancienne. */
    val recipes: List<Recipe> = listOf(
        recipe(
            "r-101", lea, "2026-07-15", "Bowl poulet-quinoa",
            "Mon repas post-training préféré : complet, rassasiant et prêt en 35 minutes.",
            prep = 15, cook = 20,
            ingredients = listOf(
                IngredientQuantity(quinoa, 80),
                IngredientQuantity(poulet, 150),
                IngredientQuantity(avocat, 1),
                IngredientQuantity(poisChiches, 100),
            ),
        ),
        recipe(
            "r-102", sofia, "2026-07-13", "Curry de lentilles corail",
            "Plat végétarien réconfortant, riche en fibres et en protéines. Parfait le soir.",
            prep = 15, cook = 25,
            ingredients = listOf(
                IngredientQuantity(lentilles, 120),
                IngredientQuantity(laitCoco, 200),
            ),
        ),
        recipe(
            "r-103", lea, "2026-07-11", "Overnight oats banane",
            "Prêt en 5 minutes la veille : flocons d'avoine, lait d'amande, banane et cacao.",
            prep = 5, cook = 0,
            ingredients = listOf(
                IngredientQuantity(avoine, 60),
                IngredientQuantity(laitAmande, 150),
                IngredientQuantity(banane, 1),
                IngredientQuantity(cacao, 10),
            ),
        ),
        recipe(
            "r-104", yanis, "2026-07-09", "Pancakes protéinés",
            "3 œufs, 1 banane, des flocons et de la whey. Le petit-déj costaud avant la muscu.",
            prep = 10, cook = 10,
            ingredients = listOf(
                IngredientQuantity(oeuf, 3),
                IngredientQuantity(banane, 1),
                IngredientQuantity(avoine, 50),
                IngredientQuantity(whey, 30),
            ),
        ),
        recipe(
            "r-105", lea, "2026-07-07", "Salade de riz complète",
            "Riz complet, thon, feta : se transporte facilement pour le déjeuner au travail.",
            prep = 20, cook = 15,
            ingredients = listOf(
                IngredientQuantity(riz, 100),
                IngredientQuantity(thon, 80),
                IngredientQuantity(feta, 40),
            ),
        ),
        recipe(
            "r-106", sofia, "2026-07-05", "Wrap avocat-saumon",
            "Tortilla complète, saumon fumé, avocat et roquette. Un déjeuner rapide et sain.",
            prep = 10, cook = 0,
            ingredients = listOf(
                IngredientQuantity(tortilla, 1),
                IngredientQuantity(saumon, 60),
                IngredientQuantity(avocat, 1),
            ),
        ),
        recipe(
            "r-107", lea, "2026-07-03", "Energy balls dattes-amandes",
            "Snack pré-effort maison, sans sucre ajouté. À garder au frigo toute la semaine.",
            prep = 15, cook = 0,
            ingredients = listOf(
                IngredientQuantity(dattes, 150),
                IngredientQuantity(amandes, 80),
                IngredientQuantity(cacao, 15),
            ),
        ),
        recipe(
            "r-108", yanis, "2026-07-01", "Riz au poulet & brocolis",
            "Le classique de la sèche : simple, efficace et facile à doser sur la semaine.",
            prep = 15, cook = 25,
            ingredients = listOf(
                IngredientQuantity(riz, 100),
                IngredientQuantity(poulet, 150),
            ),
        ),
    )

    /** Favoris du compte de démo (liste `favoriteRecipes` de `data.txt`, mockée). */
    val demoFavoriteIds: Set<String> = setOf("r-102", "r-104")
}
