package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

public class RecipeDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RecipeRepository recipeRepository;

    public RecipeDataGenerator(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @PostConstruct
    private void generateRecipeWithItems() {
        if (recipeRepository.findAll().size() > 0) {
            LOGGER.debug("recipe already generated");
        } else {
            LOGGER.debug("generating 2 recipe entries");

            Recipe recipe1 = Recipe.builder()
                .name("Lasagne Bolognese")
                .description("Zuerst die Bolognesesauce zubereiten: Dafür Zwiebeln und Knoblauch schälen "
                    + "und fein hacken. Die Karotten schälen und raspeln. Dann Öl in einem Topf erhitzen, Faschiertes"
                    + " darin gleichmäßig anrösten und Zwiebeln, Knoblauch und Karotten zugeben und weiter anbraten. "
                    + "Tomaten und Tomatenmark sowie das Ketchup, Basilikum, Oregano, Thymian, Salz und Pfeffer "
                    + "hinzugeben und auf kleinster Stufe ca. 10 Min. köcheln lassen.\n"
                    + "Anschließend die Bechamelsauce herstellen: Dafür Butter in einem Topf zerlassen, Mehl zufügen "
                    + "und sofort mit einem Schneebesen umrühren. Jetzt ganz langsam unter ständigem Rühren die Milch "
                    + "zufügen. Langsam aufkochen und rühren bis die Sauce dicklich ist. Sodann mit Pfeffer, Salz und"
                    + "Muskat würzen.\n"
                    + "Für die Lasagne eine Auflaufform mit Olivenöl einfetten. Nun abwechselnd die Lasagneblätter und "
                    + "Bolognesesauce einschichten. Mit den Lasagneblättern beginnen, danach die Sauce usw. abwechselnd schichten.\n"
                    + "Ganz zum Schluss mit der Bechamelsauce abschließen und frisch geriebenen Gouda draufgeben. "
                    + "Im vorgeheizten Ofen bei 180° C Heißluft ca. 30 Minuten backen.")
                .isPublic(true)
                .portionSize(1).likes(1).dislikes(1).build();

            Item item1 = Item.builder().description("Rinderfaschiertes").unit(Unit.Gram).amount(400).build();
            Item item2 = Item.builder().description("Zwiebel").unit(Unit.Piece).amount(1).build();
            Item item3 = Item.builder().description("Knoblauchzehe").unit(Unit.Piece).amount(1).build();
            Item item4 = Item.builder().description("Öl").unit(Unit.Gram).amount(1).build();
            Item item5 = Item.builder().description("Karotte").unit(Unit.Piece).amount(1).build();
            Item item6 = Item.builder().description("Tomaten").unit(Unit.Gram).amount(1).build();
            Item item7 = Item.builder().description("Tomatenmark").unit(Unit.Gram).amount(5).build();
            Item item8 = Item.builder().description("Ketchup").unit(Unit.Gram).amount(3).build();
            Item item9 = Item.builder().description("Basilikum").unit(Unit.Gram).amount(1).build();
            Item item10 = Item.builder().description("Oregano").unit(Unit.Gram).amount(1).build();
            Item item11 = Item.builder().description("Thymian").unit(Unit.Gram).amount(1).build();
            Item item12 = Item.builder().description("Salz").unit(Unit.Gram).amount(1).build();
            Item item13 = Item.builder().description("Pfeffer").unit(Unit.Gram).amount(1).build();

            List<Item> ingredientsToAdd1 = Arrays.asList(item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,
                item11, item12, item13);

            for (Item item : ingredientsToAdd1) {
                recipe1.addIngredient(item);
            }

            LOGGER.debug("saving recipe {}", recipe1);
            recipeRepository.save(recipe1);

            Recipe recipe2 = Recipe.builder()
                .name("PaprikaHuhn")
                .description("Das Huhn waschen, die Flügel vom Huhn abtrennen, den Rest in 4 Teile "
                    + "zerlegen und mit Salz und Pfeffer würzen. Die Flügel nicht wegwerfen, diese können"
                    + " für eine Hühnersuppe verwendet werden.\n"
                    + "Danach die Zwiebel und den Knoblauch schälen und sehr fein würfeln. Das Öl in einer "
                    + "großen Pfanne erhitzen, Zwiebeln und Knoblauch darin goldgelb braten und vom Herd nehmen. "
                    + "Nun Paprikapulver und das Huhn dazugeben. Anschließend mit der Hühnersuppe auffüllen und "
                    + "zugedeckt 15 Minuten dünsten lassen, dabei öfter umrühren.\n"
                    + "Dann die gewaschenen und in Streifen geschnittenen Paprikaschoten und Tomatenstücke "
                    + "zugeben und bei schwacher Hitze in etwa 25-30 Minuten weich garen.\n"
                    + "Anschließend das Hühnerfleisch aus der Suppe nehmen und von Haut und Knochen lösen. "
                    + "Die Knochen können weggeworfen werden. Nun den Schlagobers und den Sauerrahm einrühren, "
                    + "mit dem Pürierstab pürieren und mit Salz und Pfeffer nochmals abschmecken. Zum Schluss nochmals"
                    + " das ausgelöste Hühnerfleisch hinzufügen und ganz kurz aufkochen lassen.")
                .isPublic(false)
                .portionSize(1).likes(1).dislikes(1).build();


            Item item14 = Item.builder().description("Huhn (im Ganzen, küchenfertig)").unit(Unit.Piece).amount(1).build();
            Item item15 = Item.builder().description("Salz").unit(Unit.Gram).amount(1).build();
            Item item16 = Item.builder().description("Pfeffer").unit(Unit.Gram).amount(1).build();
            Item item17 = Item.builder().description("Öl (für die Pfanne)").unit(Unit.Gram).amount(2).build();
            Item item18 = Item.builder().description("Knoblauchzehe").unit(Unit.Piece).amount(1).build();
            Item item19 = Item.builder().description("Zwiebel (groß)").unit(Unit.Piece).amount(1).build();
            Item item20 = Item.builder().description("Paprikapulver (edelsüß)").unit(Unit.Gram).amount(1).build();
            Item item21 = Item.builder().description("Hühnersuppe (klar)").unit(Unit.Milliliter).amount(250).build();
            Item item22 = Item.builder().description("Paprikaschoten").unit(Unit.Piece).amount(1).build();
            Item item23 = Item.builder().description("Tomaten").unit(Unit.Piece).amount(1).build();
            Item item24 = Item.builder().description("Schlagobers").unit(Unit.Milliliter).amount(100).build();
            Item item25 = Item.builder().description("Sauerrahm").unit(Unit.Milliliter).amount(200).build();

            List<Item> ingredientsToAdd2 = Arrays.asList(item14, item15, item16, item17, item18, item19, item20, item21, item22, item23,
                item24, item25);

            for (Item item : ingredientsToAdd2) {
                recipe2.addIngredient(item);
            }

            LOGGER.debug("saving recipe {}", recipe2);
            recipeRepository.save(recipe2);
        }
    }
}