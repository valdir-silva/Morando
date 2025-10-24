@file:Suppress("MaxLineLength", "MagicNumber", "LongMethod")

package com.alunando.morando.data.repository

import android.content.Context
import com.alunando.morando.core.Result
import com.alunando.morando.data.datasource.CookingPreferencesDataSource
import com.alunando.morando.domain.model.CookingStep
import com.alunando.morando.domain.model.Ingredient
import com.alunando.morando.domain.model.MiseEnPlaceStep
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.RecipeDifficulty
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.repository.CookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Date
import java.util.UUID

/**
 * Implementação mock do CookingRepository
 */
@Suppress("LargeClass", "TooManyFunctions")
class CookingRepositoryMock(
    private val context: Context,
) : CookingRepository {
    private val preferencesDataSource = CookingPreferencesDataSource(context)

    private val recipesFlow =
        MutableStateFlow(
            listOf(
                createArrozBrancoRecipe(),
                createOmeleteSimplesRecipe(),
                createBifeGrelhadoRecipe(),
                createRisotoDeCogmelosRecipe(),
                createFrangoAssadoRecipe(),
                createBrigadeiroRecipe(),
            ),
        )

    override fun getRecipes(): Flow<List<Recipe>> = recipesFlow

    override fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> =
        MutableStateFlow(recipesFlow.value.filter { it.categoria == category })

    override suspend fun getRecipeById(id: String): Result<Recipe> {
        val recipe = recipesFlow.value.find { it.id == id }
        return if (recipe != null) {
            Result.Success(recipe)
        } else {
            Result.Error(Exception("Receita não encontrada"))
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Result<Unit> {
        val newRecipe = recipe.copy(id = UUID.randomUUID().toString(), createdAt = Date())
        recipesFlow.value = recipesFlow.value + newRecipe
        return Result.Success(Unit)
    }

    override suspend fun updateRecipe(recipe: Recipe): Result<Unit> {
        val index = recipesFlow.value.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            val updatedList = recipesFlow.value.toMutableList()
            updatedList[index] = recipe
            recipesFlow.value = updatedList
            return Result.Success(Unit)
        }
        return Result.Error(Exception("Receita não encontrada"))
    }

    override suspend fun deleteRecipe(id: String): Result<Unit> {
        recipesFlow.value = recipesFlow.value.filter { it.id != id }
        return Result.Success(Unit)
    }

    override suspend fun getUserStovePreference(): StoveType = preferencesDataSource.getStoveType()

    override suspend fun saveUserStovePreference(stoveType: StoveType): Result<Unit> {
        preferencesDataSource.saveStoveType(stoveType)
        return Result.Success(Unit)
    }

    override suspend fun uploadRecipeImage(
        recipeId: String,
        imageData: ByteArray,
    ): Result<String> {
        // Mock: retorna URL fake
        return Result.Success("https://via.placeholder.com/400x300?text=Recipe+Image")
    }

    // ==================== RECEITAS MOCK ====================

    @Suppress("MagicNumber")
    private fun createArrozBrancoRecipe() =
        Recipe(
            id = "1",
            nome = "Arroz Branco",
            descricao = "Arroz branco soltinho e perfeito, com instruções específicas para cada tipo de fogão",
            categoria = RecipeCategory.ACOMPANHAMENTO,
            tempoPreparo = 5,
            porcoes = 4,
            dificuldade = RecipeDifficulty.FACIL,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Arroz branco", 2.0, "xícaras"),
                    Ingredient("Água", 4.0, "xícaras"),
                    Ingredient("Óleo", 2.0, "colheres de sopa"),
                    Ingredient("Sal", 1.0, "colher de chá"),
                    Ingredient("Alho", 2.0, "dentes"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Arroz",
                        quantidade = "2 xícaras (400g)",
                        instrucao = "Separar e medir o arroz",
                        tipoDePreparo = "Separar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Alho",
                        quantidade = "2 dentes",
                        instrucao = "Picar finamente",
                        tipoDePreparo = "Cortar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 3,
                        ingrediente = "Água",
                        quantidade = "4 xícaras (800ml)",
                        instrucao = "Medir a água (proporção 1:2 de arroz para água)",
                        tipoDePreparo = "Separar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Refogar o alho",
                        tempoMinutos = 2,
                        instrucoesGerais = "Aqueça o óleo e refogue o alho até dourar levemente",
                        instrucaoInducao =
                            "Configure em nível 7 (1400W / ~180°C), adicione óleo e alho. " +
                                "Refogue por 1-2 minutos",
                        instrucaoGas =
                            "Coloque em fogo médio-alto (~180°C), adicione óleo e alho. " +
                                "Refogue por 1-2 minutos",
                        instrucaoEletrico =
                            "Configure em temperatura 180°C, adicione óleo e alho. " +
                                "Refogue por 1-2 minutos",
                        instrucaoLenha =
                            "Coloque sobre brasas médias (~180°C), adicione óleo e alho. " +
                                "Refogue por 1-2 minutos",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Adicionar o arroz",
                        tempoMinutos = 1,
                        instrucoesGerais = "Adicione o arroz e misture bem para envolver com o óleo",
                        instrucaoInducao = "Mantenha em nível 7 (~180°C), adicione arroz e misture por 1 minuto",
                        instrucaoGas = "Mantenha fogo médio-alto (~180°C), adicione arroz e misture por 1 minuto",
                        instrucaoEletrico = "Mantenha em 180°C, adicione arroz e misture por 1 minuto",
                        instrucaoLenha = "Mantenha sobre brasas médias (~180°C), adicione arroz e misture por 1 minuto",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Adicionar água e sal",
                        tempoMinutos = 15,
                        instrucoesGerais = "Adicione a água e o sal, deixe ferver e depois reduza o fogo",
                        instrucaoInducao =
                            "Adicione água e sal, aguarde ferver (nível 9 / 2000W / ~200°C). " +
                                "Após ferver, reduza para nível 3 (600W / ~100°C) e tampe",
                        instrucaoGas =
                            "Adicione água e sal, aguarde ferver em fogo alto (~200°C). " +
                                "Após ferver, reduza para fogo baixo (~100°C) e tampe",
                        instrucaoEletrico =
                            "Adicione água e sal, aguarde ferver em 200°C. " +
                                "Após ferver, reduza para 120°C e tampe",
                        instrucaoLenha =
                            "Adicione água e sal, aguarde ferver sobre brasas fortes (~200°C). " +
                                "Após ferver, mova para área com brasas fracas (~100°C) e tampe",
                    ),
                    CookingStep(
                        ordem = 4,
                        titulo = "Cozinhar",
                        tempoMinutos = 12,
                        instrucoesGerais = "Deixe cozinhar tampado até a água secar completamente",
                        instrucaoInducao =
                            "Mantenha em nível 3 (~100°C) por 12 minutos. " +
                                "Não destampe durante o cozimento",
                        instrucaoGas =
                            "Mantenha fogo baixo (~100°C) por 12 minutos. " +
                                "Não destampe durante o cozimento",
                        instrucaoEletrico =
                            "Mantenha em 120°C por 12 minutos. " +
                                "Não destampe durante o cozimento",
                        instrucaoLenha =
                            "Mantenha sobre brasas fracas (~100°C) por 12-15 minutos. " +
                                "Não destampe durante o cozimento",
                    ),
                    CookingStep(
                        ordem = 5,
                        titulo = "Finalizar",
                        tempoMinutos = 3,
                        instrucoesGerais =
                            "Desligue o fogo, mantenha tampado por 3 minutos " +
                                "e depois solte o arroz com um garfo",
                        instrucaoInducao =
                            "Desligue (nível 0), mantenha tampado por 3 minutos. " +
                                "Solte com garfo",
                        instrucaoGas = "Desligue o fogo, mantenha tampado por 3 minutos. Solte com garfo",
                        instrucaoEletrico = "Desligue (0°C), mantenha tampado por 3 minutos. Solte com garfo",
                        instrucaoLenha = "Retire do fogo, mantenha tampado por 3 minutos. Solte com garfo",
                    ),
                ),
            tipoFogaoPadrao = StoveType.INDUCTION,
            userId = "mock_user",
            createdAt = Date(),
        )

    @Suppress("MagicNumber", "LongMethod")
    private fun createOmeleteSimplesRecipe() =
        Recipe(
            id = "2",
            nome = "Omelete Simples",
            descricao = "Omelete macia e deliciosa para café da manhã",
            categoria = RecipeCategory.CAFE_DA_MANHA,
            tempoPreparo = 5,
            porcoes = 1,
            dificuldade = RecipeDifficulty.FACIL,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Ovos", 3.0, "unidades"),
                    Ingredient("Leite", 2.0, "colheres de sopa"),
                    Ingredient("Sal", 1.0, "pitada"),
                    Ingredient("Pimenta do reino", 1.0, "pitada"),
                    Ingredient("Manteiga", 1.0, "colher de sopa"),
                    Ingredient("Queijo ralado", 2.0, "colheres de sopa", "opcional"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Ovos",
                        quantidade = "3 unidades",
                        instrucao = "Quebrar os ovos em uma tigela",
                        tipoDePreparo = "Separar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Mistura",
                        quantidade = "Ovos + leite + temperos",
                        instrucao = "Bater bem os ovos com leite, sal e pimenta até ficar homogêneo",
                        tipoDePreparo = "Misturar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Aquecer a frigideira",
                        tempoMinutos = 1,
                        instrucoesGerais = "Aqueça a frigideira em fogo médio com manteiga",
                        instrucaoInducao =
                            "Configure em nível 6 (1200W / ~160°C), " +
                                "adicione manteiga e espalhe",
                        instrucaoGas = "Coloque em fogo médio (~160°C), adicione manteiga e espalhe",
                        instrucaoEletrico = "Configure em 160°C, adicione manteiga e espalhe",
                        instrucaoLenha =
                            "Coloque sobre brasas médias (~160°C), " +
                                "adicione manteiga e espalhe",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Despejar os ovos",
                        tempoMinutos = 3,
                        instrucoesGerais =
                            "Despeje os ovos batidos e deixe cozinhar sem mexer " +
                                "por 2-3 minutos",
                        instrucaoInducao =
                            "Mantenha nível 5 (1000W / ~140°C), " +
                                "despeje os ovos e aguarde formar a base",
                        instrucaoGas =
                            "Mantenha fogo médio-baixo (~140°C), " +
                                "despeje os ovos e aguarde formar a base",
                        instrucaoEletrico = "Mantenha 140°C, despeje os ovos e aguarde formar a base",
                        instrucaoLenha =
                            "Mantenha sobre brasas médias-baixas (~140°C), " +
                                "despeje os ovos e aguarde formar a base",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Adicionar recheio e dobrar",
                        tempoMinutos = 2,
                        instrucoesGerais =
                            "Quando a base estiver firme mas o topo ainda úmido, " +
                                "adicione queijo (se usar) e dobre ao meio",
                        instrucaoInducao =
                            "Reduza para nível 3 (600W / ~100°C), adicione queijo, " +
                                "dobre e cozinhe por mais 1-2 minutos",
                        instrucaoGas =
                            "Reduza para fogo baixo (~100°C), adicione queijo, " +
                                "dobre e cozinhe por mais 1-2 minutos",
                        instrucaoEletrico =
                            "Reduza para 120°C, adicione queijo, " +
                                "dobre e cozinhe por mais 1-2 minutos",
                        instrucaoLenha =
                            "Mova para área com brasas fracas (~100°C), adicione queijo, " +
                                "dobre e cozinhe por mais 1-2 minutos",
                    ),
                ),
            tipoFogaoPadrao = StoveType.INDUCTION,
            userId = "mock_user",
            createdAt = Date(),
        )

    @Suppress("MagicNumber", "LongMethod")
    private fun createBifeGrelhadoRecipe() =
        Recipe(
            id = "3",
            nome = "Bife Grelhado",
            descricao = "Bife suculento com crosta dourada e interior macio",
            categoria = RecipeCategory.PRATO_PRINCIPAL,
            tempoPreparo = 10,
            porcoes = 2,
            dificuldade = RecipeDifficulty.MEDIA,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Bife (contrafilé ou filé mignon)", 2.0, "unidades (cerca de 300g cada)"),
                    Ingredient("Sal grosso", 1.0, "colher de chá"),
                    Ingredient("Pimenta do reino", 1.0, "colher de chá"),
                    Ingredient("Alho em pó", 0.5, "colher de chá", "opcional"),
                    Ingredient("Manteiga", 2.0, "colheres de sopa"),
                    Ingredient("Alecrim", 2.0, "ramos", "opcional"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Bife",
                        quantidade = "2 unidades",
                        instrucao =
                            "Retirar da geladeira 30 minutos antes. " +
                                "Secar bem com papel toalha",
                        tipoDePreparo = "Preparar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Temperos",
                        quantidade = "Sal, pimenta, alho em pó",
                        instrucao = "Temperar generosamente ambos os lados dos bifes",
                        tipoDePreparo = "Temperar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Aquecer bem a frigideira/grill",
                        tempoMinutos = 3,
                        instrucoesGerais =
                            "Aqueça a frigideira/grill até ficar bem quente " +
                                "(essencial para selar)",
                        instrucaoInducao =
                            "Configure em nível máximo 9 (2000W / ~220°C) e aguarde 3 minutos. " +
                                "Frigideira deve estar fumegando levemente",
                        instrucaoGas =
                            "Coloque em fogo alto (~220°C) e aguarde 3 minutos. " +
                                "Frigideira deve estar fumegando levemente",
                        instrucaoEletrico =
                            "Configure em temperatura máxima 220°C " +
                                "e aguarde 3-4 minutos",
                        instrucaoLenha =
                            "Coloque sobre brasas bem fortes (~220-240°C) " +
                                "e aguarde 3-4 minutos",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Selar o primeiro lado",
                        tempoMinutos = 3,
                        instrucoesGerais =
                            "Coloque o bife e não mexa por 3 minutos " +
                                "para formar crosta dourada",
                        instrucaoInducao =
                            "Mantenha nível 8 (1800W / ~200°C), " +
                                "coloque bife e NÃO MEXA por 3 minutos",
                        instrucaoGas =
                            "Mantenha fogo alto (~200°C), " +
                                "coloque bife e NÃO MEXA por 3 minutos",
                        instrucaoEletrico = "Mantenha 200°C, coloque bife e NÃO MEXA por 3-4 minutos",
                        instrucaoLenha =
                            "Mantenha sobre brasas fortes (~200-220°C), " +
                                "coloque bife e NÃO MEXA por 3-4 minutos",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Selar o outro lado",
                        tempoMinutos = 3,
                        instrucoesGerais = "Vire o bife e repita o processo do outro lado",
                        instrucaoInducao =
                            "Mantenha nível 8 (~200°C), vire o bife e aguarde mais 3 minutos " +
                                "(mal passado) ou 4-5 minutos (ao ponto)",
                        instrucaoGas =
                            "Mantenha fogo alto (~200°C), vire o bife e aguarde mais 3 minutos " +
                                "(mal passado) ou 4-5 minutos (ao ponto)",
                        instrucaoEletrico =
                            "Mantenha 200°C, vire o bife e aguarde mais 3-4 minutos " +
                                "(mal passado) ou 5-6 minutos (ao ponto)",
                        instrucaoLenha =
                            "Mantenha sobre brasas fortes (~200°C), " +
                                "vire o bife e aguarde mais 3-4 minutos",
                    ),
                    CookingStep(
                        ordem = 4,
                        titulo = "Finalizar com manteiga",
                        tempoMinutos = 1,
                        instrucoesGerais = "Adicione manteiga e alecrim, incline a frigideira e regue o bife",
                        instrucaoInducao =
                            "Reduza para nível 5 (~140°C), adicione manteiga e alecrim, " +
                                "regue o bife por 1 minuto",
                        instrucaoGas =
                            "Reduza para fogo médio (~140-160°C), adicione manteiga e alecrim, " +
                                "regue o bife por 1 minuto",
                        instrucaoEletrico =
                            "Reduza para 160°C, adicione manteiga e alecrim, " +
                                "regue o bife por 1 minuto",
                        instrucaoLenha =
                            "Mova para área com brasas médias (~140-160°C), " +
                                "adicione manteiga e alecrim, regue o bife",
                    ),
                    CookingStep(
                        ordem = 5,
                        titulo = "Descansar",
                        tempoMinutos = 5,
                        instrucoesGerais =
                            "Retire do fogo e deixe descansar por 5 minutos " +
                                "antes de servir (essencial!)",
                        instrucaoInducao =
                            "Desligue (nível 0), retire os bifes e " +
                                "deixe descansar em prato por 5 minutos",
                        instrucaoGas =
                            "Desligue o fogo, retire os bifes e " +
                                "deixe descansar em prato por 5 minutos",
                        instrucaoEletrico =
                            "Desligue, retire os bifes e " +
                                "deixe descansar em prato por 5 minutos",
                        instrucaoLenha = "Retire do fogo, deixe descansar em prato por 5 minutos",
                    ),
                ),
            tipoFogaoPadrao = StoveType.INDUCTION,
            userId = "mock_user",
            createdAt = Date(),
        )

    @Suppress("MagicNumber", "LongMethod")
    private fun createRisotoDeCogmelosRecipe() =
        Recipe(
            id = "4",
            nome = "Risoto de Cogumelos",
            descricao = "Risoto cremoso e aromático com cogumelos frescos",
            categoria = RecipeCategory.PRATO_PRINCIPAL,
            tempoPreparo = 15,
            porcoes = 4,
            dificuldade = RecipeDifficulty.MEDIA,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Arroz arbóreo", 2.0, "xícaras (400g)"),
                    Ingredient("Cogumelos variados", 300.0, "g"),
                    Ingredient("Caldo de legumes", 1.5, "litros"),
                    Ingredient("Vinho branco seco", 0.5, "xícara (100ml)"),
                    Ingredient("Cebola", 1.0, "unidade média"),
                    Ingredient("Alho", 3.0, "dentes"),
                    Ingredient("Manteiga", 4.0, "colheres de sopa"),
                    Ingredient("Queijo parmesão ralado", 0.5, "xícara (100g)"),
                    Ingredient("Azeite", 2.0, "colheres de sopa"),
                    Ingredient("Sal e pimenta", 1.0, "a gosto"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Cebola",
                        quantidade = "1 unidade",
                        instrucao = "Picar bem fininho",
                        tipoDePreparo = "Cortar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Alho",
                        quantidade = "3 dentes",
                        instrucao = "Picar finamente",
                        tipoDePreparo = "Cortar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 3,
                        ingrediente = "Cogumelos",
                        quantidade = "300g",
                        instrucao = "Limpar com papel úmido e fatiar",
                        tipoDePreparo = "Cortar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 4,
                        ingrediente = "Caldo",
                        quantidade = "1.5 litros",
                        instrucao = "Aquecer em panela separada e manter quente",
                        tipoDePreparo = "Preparar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 5,
                        ingrediente = "Queijo",
                        quantidade = "100g",
                        instrucao = "Ralar fino",
                        tipoDePreparo = "Preparar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Refogar cogumelos",
                        tempoMinutos = 5,
                        instrucoesGerais = "Doure os cogumelos em manteiga até ficarem macios, reserve",
                        instrucaoInducao =
                            "Nível 7 (1400W / ~180°C), derreta 2 colheres de manteiga " +
                                "e refogue cogumelos por 5 minutos",
                        instrucaoGas =
                            "Fogo médio-alto (~180°C), derreta 2 colheres de manteiga " +
                                "e refogue cogumelos por 5 minutos",
                        instrucaoEletrico =
                            "180°C, derreta 2 colheres de manteiga " +
                                "e refogue cogumelos por 5 minutos",
                        instrucaoLenha =
                            "Brasas médias-fortes (~180°C), derreta manteiga " +
                                "e refogue cogumelos por 5-6 minutos",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Refogar cebola e alho",
                        tempoMinutos = 3,
                        instrucoesGerais =
                            "Na mesma panela, refogue cebola e alho no azeite " +
                                "até ficarem transparentes",
                        instrucaoInducao =
                            "Nível 6 (1200W / ~160°C), adicione azeite, cebola e alho. " +
                                "Refogue por 3 minutos",
                        instrucaoGas =
                            "Fogo médio (~160°C), adicione azeite, cebola e alho. " +
                                "Refogue por 3 minutos",
                        instrucaoEletrico =
                            "160°C, adicione azeite, cebola e alho. " +
                                "Refogue por 3 minutos",
                        instrucaoLenha =
                            "Brasas médias (~160°C), adicione azeite, cebola e alho. " +
                                "Refogue por 3 minutos",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Tostar o arroz",
                        tempoMinutos = 2,
                        instrucoesGerais =
                            "Adicione o arroz e mexa bem por 2 minutos " +
                                "até os grãos ficarem nacarados",
                        instrucaoInducao =
                            "Mantenha nível 6 (~160°C), adicione arroz " +
                                "e mexa constantemente por 2 minutos",
                        instrucaoGas =
                            "Mantenha fogo médio (~160°C), adicione arroz " +
                                "e mexa constantemente por 2 minutos",
                        instrucaoEletrico =
                            "Mantenha 160°C, adicione arroz " +
                                "e mexa constantemente por 2 minutos",
                        instrucaoLenha =
                            "Mantenha brasas médias (~160°C), adicione arroz " +
                                "e mexa por 2-3 minutos",
                    ),
                    CookingStep(
                        ordem = 4,
                        titulo = "Adicionar vinho",
                        tempoMinutos = 3,
                        instrucoesGerais = "Adicione o vinho e mexa até evaporar completamente",
                        instrucaoInducao =
                            "Aumente para nível 7 (~180°C), adicione vinho " +
                                "e mexa até secar (cerca de 3 minutos)",
                        instrucaoGas =
                            "Aumente para fogo médio-alto (~180°C), adicione vinho " +
                                "e mexa até secar (cerca de 3 minutos)",
                        instrucaoEletrico =
                            "Aumente para 180°C, adicione vinho " +
                                "e mexa até secar (cerca de 3 minutos)",
                        instrucaoLenha =
                            "Mova para área com brasas mais fortes (~180°C), " +
                                "adicione vinho e mexa até secar",
                    ),
                    CookingStep(
                        ordem = 5,
                        titulo = "Cozinhar o risoto",
                        tempoMinutos = 18,
                        instrucoesGerais =
                            "Adicione o caldo quente aos poucos (1 concha por vez), mexendo sempre. " +
                                "Adicione mais caldo quando o anterior secar",
                        instrucaoInducao =
                            "Reduza para nível 5 (1000W / ~140°C). " +
                                "Adicione caldo aos poucos, mexendo sempre. Processo leva 18-20 minutos",
                        instrucaoGas =
                            "Reduza para fogo médio (~140°C). " +
                                "Adicione caldo aos poucos, mexendo sempre. Processo leva 18-20 minutos",
                        instrucaoEletrico =
                            "Reduza para 150°C. " +
                                "Adicione caldo aos poucos, mexendo sempre. Processo leva 20-22 minutos",
                        instrucaoLenha =
                            "Mova para área com brasas médias (~140-150°C). " +
                                "Adicione caldo aos poucos, mexendo sempre. Processo leva 20-25 minutos",
                    ),
                    CookingStep(
                        ordem = 6,
                        titulo = "Finalizar (mantecare)",
                        tempoMinutos = 2,
                        instrucoesGerais =
                            "Desligue o fogo, adicione cogumelos reservados, manteiga e queijo. " +
                                "Mexa vigorosamente",
                        instrucaoInducao =
                            "Desligue (nível 0), adicione cogumelos, 2 colheres de manteiga e queijo. " +
                                "Mexa vigorosamente por 2 minutos",
                        instrucaoGas =
                            "Desligue o fogo, adicione cogumelos, 2 colheres de manteiga e queijo. " +
                                "Mexa vigorosamente por 2 minutos",
                        instrucaoEletrico =
                            "Desligue, adicione cogumelos, manteiga e queijo. " +
                                "Mexa vigorosamente por 2 minutos",
                        instrucaoLenha =
                            "Retire do fogo, adicione cogumelos, manteiga e queijo. " +
                                "Mexa vigorosamente por 2 minutos",
                    ),
                ),
            tipoFogaoPadrao = StoveType.INDUCTION,
            userId = "mock_user",
            createdAt = Date(),
        )

    @Suppress("MagicNumber", "LongMethod")
    private fun createFrangoAssadoRecipe() =
        Recipe(
            id = "5",
            nome = "Frango Assado ao Forno",
            descricao = "Frango inteiro assado com pele crocante e carne suculenta",
            categoria = RecipeCategory.PRATO_PRINCIPAL,
            tempoPreparo = 20,
            porcoes = 6,
            dificuldade = RecipeDifficulty.MEDIA,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Frango inteiro", 1.0, "unidade (cerca de 1,5kg)"),
                    Ingredient("Limão", 2.0, "unidades"),
                    Ingredient("Alho", 6.0, "dentes"),
                    Ingredient("Manteiga amolecida", 4.0, "colheres de sopa"),
                    Ingredient("Azeite", 3.0, "colheres de sopa"),
                    Ingredient("Sal grosso", 2.0, "colheres de sopa"),
                    Ingredient("Pimenta do reino", 1.0, "colher de sopa"),
                    Ingredient("Tomilho fresco", 4.0, "ramos", "ou 1 colher de chá seco"),
                    Ingredient("Alecrim fresco", 4.0, "ramos", "ou 1 colher de chá seco"),
                    Ingredient("Cebola", 2.0, "unidades grandes"),
                    Ingredient("Batatas", 6.0, "unidades médias", "opcional"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Frango",
                        quantidade = "1 unidade (1,5kg)",
                        instrucao = "Lavar bem, secar com papel toalha e deixar em temperatura ambiente por 30 minutos",
                        tipoDePreparo = "Preparar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Temperos",
                        quantidade = "Alho, ervas, sal, pimenta",
                        instrucao = "Amassar o alho, picar as ervas e misturar com manteiga, sal e pimenta",
                        tipoDePreparo = "Preparar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 3,
                        ingrediente = "Frango temperado",
                        quantidade = "1 unidade",
                        instrucao =
                            "Esfregar a mistura de manteiga por dentro e por fora do frango, " +
                                "inclusive sob a pele",
                        tipoDePreparo = "Temperar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 4,
                        ingrediente = "Limão",
                        quantidade = "2 unidades",
                        instrucao =
                            "Cortar ao meio e colocar dentro da cavidade do frango " +
                                "junto com ramos de ervas",
                        tipoDePreparo = "Preparar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 5,
                        ingrediente = "Cebola e batatas",
                        quantidade = "2 cebolas e 6 batatas",
                        instrucao =
                            "Cortar cebolas em 4 partes e batatas ao meio. " +
                                "Temperar com sal, pimenta e azeite",
                        tipoDePreparo = "Cortar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Pré-aquecer o forno",
                        tempoMinutos = 10,
                        instrucoesGerais = "Pré-aqueça o forno em temperatura alta",
                        instrucaoInducao = "N/A - usar forno convencional a 220°C",
                        instrucaoGas =
                            "Pré-aquecer forno a gás em temperatura alta (220°C) " +
                                "por 10 minutos",
                        instrucaoEletrico = "Pré-aquecer forno elétrico a 220°C por 10 minutos",
                        instrucaoLenha =
                            "Preparar forno a lenha com boa quantidade de brasas, " +
                                "temperatura em torno de 220-240°C",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Montar a assadeira",
                        tempoMinutos = 0,
                        instrucoesGerais =
                            "Colocar as cebolas e batatas no fundo da assadeira, " +
                                "posicionar o frango sobre elas com peito para cima",
                        instrucaoInducao = "N/A - usar forno",
                        instrucaoGas =
                            "Usar assadeira funda. Cebolas e batatas no fundo, " +
                                "frango sobre elas",
                        instrucaoEletrico =
                            "Usar assadeira funda. Cebolas e batatas no fundo, " +
                                "frango sobre elas",
                        instrucaoLenha =
                            "Usar assadeira ou forma de ferro. Cebolas e batatas no fundo, " +
                                "frango sobre elas",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Assar em alta temperatura",
                        tempoMinutos = 30,
                        instrucoesGerais = "Assar em temperatura alta para dourar a pele",
                        instrucaoInducao = "N/A - forno a 220°C por 30 minutos",
                        instrucaoGas = "Forno a 220°C por 30 minutos",
                        instrucaoEletrico = "Forno a 220°C por 30 minutos na grade do meio",
                        instrucaoLenha =
                            "Temperatura alta (220-240°C) por 30 minutos. " +
                                "Girar assadeira se necessário",
                    ),
                    CookingStep(
                        ordem = 4,
                        titulo = "Reduzir temperatura e continuar assando",
                        tempoMinutos = 40,
                        instrucoesGerais =
                            "Reduzir temperatura e continuar assando, " +
                                "regando ocasionalmente com os próprios sucos",
                        instrucaoInducao =
                            "N/A - reduzir forno para 180°C " +
                                "e assar por mais 40-50 minutos",
                        instrucaoGas =
                            "Reduzir para 180°C e assar por mais 40-50 minutos. " +
                                "Regar a cada 15 minutos",
                        instrucaoEletrico =
                            "Reduzir para 180°C e assar por mais 40-50 minutos. " +
                                "Regar a cada 15 minutos",
                        instrucaoLenha =
                            "Reduzir temperatura para 180°C (remover algumas brasas se necessário). " +
                                "Assar por mais 45-55 minutos",
                    ),
                    CookingStep(
                        ordem = 5,
                        titulo = "Verificar cozimento",
                        tempoMinutos = 0,
                        instrucoesGerais =
                            "Espetar um garfo na coxa: se sair líquido transparente, está pronto. " +
                                "Se rosado, assar mais 10 minutos",
                        instrucaoInducao =
                            "N/A - usar termômetro " +
                                "(temperatura interna deve estar em 75°C)",
                        instrucaoGas = "Termômetro deve marcar 75°C na parte mais grossa da coxa",
                        instrucaoEletrico = "Termômetro deve marcar 75°C na parte mais grossa da coxa",
                        instrucaoLenha =
                            "Verificar com termômetro ou garfo. " +
                                "Líquido deve sair transparente",
                    ),
                    CookingStep(
                        ordem = 6,
                        titulo = "Descansar",
                        tempoMinutos = 15,
                        instrucoesGerais = "Retirar do forno e deixar descansar antes de cortar",
                        instrucaoInducao = "N/A - cobrir com papel alumínio e deixar descansar 15 minutos",
                        instrucaoGas = "Cobrir com papel alumínio e deixar descansar 15 minutos",
                        instrucaoEletrico = "Cobrir com papel alumínio e deixar descansar 15 minutos",
                        instrucaoLenha = "Cobrir com papel alumínio e deixar descansar 15 minutos",
                    ),
                ),
            tipoFogaoPadrao = StoveType.ELECTRIC,
            userId = "mock_user",
            createdAt = Date(),
        )

    @Suppress("MagicNumber", "LongMethod")
    private fun createBrigadeiroRecipe() =
        Recipe(
            id = "6",
            nome = "Brigadeiro",
            descricao = "Brigadeiro cremoso tradicional brasileiro",
            categoria = RecipeCategory.SOBREMESA,
            tempoPreparo = 5,
            porcoes = 30,
            dificuldade = RecipeDifficulty.FACIL,
            fotoUrl = "",
            ingredientes =
                listOf(
                    Ingredient("Leite condensado", 1.0, "lata (395g)"),
                    Ingredient("Chocolate em pó", 3.0, "colheres de sopa"),
                    Ingredient("Manteiga", 1.0, "colher de sopa"),
                    Ingredient("Chocolate granulado", 1.0, "xícara", "para decorar"),
                ),
            etapasMiseEnPlace =
                listOf(
                    MiseEnPlaceStep(
                        ordem = 1,
                        ingrediente = "Ingredientes",
                        quantidade = "Leite condensado, chocolate, manteiga",
                        instrucao = "Separar e medir todos os ingredientes",
                        tipoDePreparo = "Separar",
                    ),
                    MiseEnPlaceStep(
                        ordem = 2,
                        ingrediente = "Prato",
                        quantidade = "1 unidade",
                        instrucao = "Untar um prato fundo com manteiga para despejar o brigadeiro pronto",
                        tipoDePreparo = "Preparar",
                    ),
                ),
            etapasPreparo =
                listOf(
                    CookingStep(
                        ordem = 1,
                        titulo = "Misturar ingredientes",
                        tempoMinutos = 1,
                        instrucoesGerais =
                            "Em panela, misture leite condensado, " +
                                "chocolate em pó e manteiga",
                        instrucaoInducao =
                            "Em panela funda, misture todos os ingredientes. " +
                                "Ainda não ligar o fogão",
                        instrucaoGas =
                            "Em panela funda, misture todos os ingredientes. " +
                                "Ainda não ligar o fogão",
                        instrucaoEletrico =
                            "Em panela funda, misture todos os ingredientes. " +
                                "Ainda não ligar o fogão",
                        instrucaoLenha = "Em panela funda, misture todos os ingredientes",
                    ),
                    CookingStep(
                        ordem = 2,
                        titulo = "Cozinhar mexendo sempre",
                        tempoMinutos = 12,
                        instrucoesGerais =
                            "Cozinhe em fogo médio-baixo, mexendo SEMPRE " +
                                "até desgrudar do fundo da panela",
                        instrucaoInducao =
                            "Configure nível 4 (800W / ~120°C). " +
                                "Mexa CONSTANTEMENTE em movimentos circulares por 10-12 minutos. " +
                                "Ponto: massa desprende do fundo",
                        instrucaoGas =
                            "Fogo médio-baixo (~120°C). " +
                                "Mexa CONSTANTEMENTE em movimentos circulares por 10-12 minutos. " +
                                "Ponto: massa desprende do fundo",
                        instrucaoEletrico =
                            "Configure 130°C. " +
                                "Mexa CONSTANTEMENTE em movimentos circulares por 12-15 minutos. " +
                                "Ponto: massa desprende do fundo",
                        instrucaoLenha =
                            "Brasas baixas/médias (~120-130°C). " +
                                "Mexa CONSTANTEMENTE por 12-15 minutos. " +
                                "Pode levar mais tempo, cuidado para não queimar",
                    ),
                    CookingStep(
                        ordem = 3,
                        titulo = "Verificar o ponto",
                        tempoMinutos = 0,
                        instrucoesGerais =
                            "O brigadeiro está no ponto quando você consegue ver o fundo " +
                                "da panela ao mexer e a massa não escorre",
                        instrucaoInducao =
                            "Teste: incline a panela, se a massa escorrer lentamente " +
                                "e você ver o fundo, está pronto",
                        instrucaoGas =
                            "Teste: incline a panela, se a massa escorrer lentamente " +
                                "e você ver o fundo, está pronto",
                        instrucaoEletrico =
                            "Teste: incline a panela, se a massa escorrer lentamente " +
                                "e você ver o fundo, está pronto",
                        instrucaoLenha =
                            "Teste: incline a panela, se a massa escorrer lentamente " +
                                "e você ver o fundo, está pronto",
                    ),
                    CookingStep(
                        ordem = 4,
                        titulo = "Resfriar",
                        tempoMinutos = 60,
                        instrucoesGerais =
                            "Despeje no prato untado, deixe esfriar e leve à geladeira " +
                                "por pelo menos 1 hora",
                        instrucaoInducao =
                            "Desligue (nível 0), despeje no prato untado, " +
                                "deixe esfriar e leve à geladeira",
                        instrucaoGas =
                            "Desligue o fogo, despeje no prato untado, " +
                                "deixe esfriar e leve à geladeira",
                        instrucaoEletrico =
                            "Desligue, despeje no prato untado, " +
                                "deixe esfriar e leve à geladeira",
                        instrucaoLenha =
                            "Retire do fogo, despeje no prato untado, " +
                                "deixe esfriar e leve à geladeira",
                    ),
                    CookingStep(
                        ordem = 5,
                        titulo = "Enrolar",
                        tempoMinutos = 15,
                        instrucoesGerais = "Unte as mãos com manteiga, faça bolinhas e passe no granulado",
                        instrucaoInducao = "N/A - processo manual",
                        instrucaoGas = "N/A - processo manual",
                        instrucaoEletrico = "N/A - processo manual",
                        instrucaoLenha = "N/A - processo manual",
                    ),
                ),
            tipoFogaoPadrao = StoveType.INDUCTION,
            userId = "mock_user",
            createdAt = Date(),
        )
}
