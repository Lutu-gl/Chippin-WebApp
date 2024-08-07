package at.ac.tuwien.sepr.groupphase.backend.datageneratorTest;

import at.ac.tuwien.sepr.groupphase.backend.datagenerator.DataGenerator;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class RecipeDataGeneratorTest implements DataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    UserRepository userRepository;
    UserService userService;
    ItemRepository itemRepository;
    RecipeRepository recipeRepository;

    private final Random random = new Random();

    @Override
    @Transactional
    public void generateData() {
        LOGGER.debug("generating data for recipes");
        List<ApplicationUser> users = userRepository.findAll();


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
            .owner(users.get(5))
            .portionSize(1).likes(1).dislikes(1).build();
        users.getFirst().addRecipe(recipe1);

        List<Item> items = itemRepository.findAll();

        for (Item item : items.subList(0, 13)) {
            recipe1.addIngredient(item);
        }
        recipeRepository.saveAndFlush(recipe1);

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
            .owner(users.get(5))
            .portionSize(1).likes(1).dislikes(1).build();
        users.getFirst().addRecipe(recipe2);
        List<Item> ingredientsToAdd2 = items.subList(13, 25);
        for (Item item : ingredientsToAdd2) {
            recipe2.addIngredient(item);
        }
        recipeRepository.saveAndFlush(recipe2);
        userRepository.saveAndFlush(users.getFirst());


        recipeRepository.saveAndFlush(Recipe.builder()
            .name("Empty Recipe")
            .description("This Recipe has no Ingredients")
            .isPublic(true)
            .portionSize(1)
            .owner(users.get(5))
            .ingredients(new ArrayList<>())
            .likes(0).dislikes(0).build());

        String[] descriptions = {
            "Milk", "Chocolate", "Banana", "Butter", "Honey", "Egg", "Cheese", "Bread",
            "Apple", "Orange", "Pear", "Grapes", "Strawberries", "Blueberries", "Raspberries",
            "Tomato", "Cucumber", "Lettuce", "Carrot", "Potato", "Onion", "Garlic", "Pepper",
            "Chicken", "Beef", "Pork", "Fish", "Shrimp", "Tofu", "Beans", "Rice", "Pasta",
            "Bread", "Bagel", "Muffin", "Donut", "Cake", "Pie", "Ice Cream", "Yogurt",
            "Coffee", "Tea", "Juice", "Water", "Soda", "Beer", "Wine", "Whiskey"
        };
        Unit[] units = {Unit.Milliliter, Unit.Gram, Unit.Piece};
        ApplicationUser user = users.stream().filter(o -> o.getEmail().equals("user5@example.com")).findFirst().get();
        for (int i = 0; i < 30; i++) {
            Recipe recipe = Recipe.builder()
                .name("Recipe " + (i + 2))
                .description("This is a random recipe description.")
                .isPublic(random.nextBoolean())
                .portionSize(random.nextInt(10) + 1)
                .owner(user)
                .build();


            for (int j = 0; j < 5; j++) {
                String description = descriptions[random.nextInt(descriptions.length)];
                Unit unit = units[new Random().nextInt(units.length)];
                int amount = new Random().nextInt(500) + 1;
                var item = Item.builder().description(description).unit(unit).amount(amount).build();
                recipe.addIngredient(item);
            }
            recipeRepository.saveAndFlush(recipe);
        }

        //Recipes for Pantry Tests
        ApplicationUser user1 = userRepository.findByEmail("user1@example.com");
        Recipe pantryTestRecipe1 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description("Test Description")
            .name("Test 1").build();
        pantryTestRecipe1.addIngredient(
            Item.builder()
                .description("PantryTest-Potato")
                .amount(2)
                .unit(Unit.Piece).build());
        pantryTestRecipe1.addIngredient(
            Item.builder()
                .description("PantryTest-Milk")
                .amount(200)
                .unit(Unit.Milliliter).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe1);
        userRepository.saveAndFlush(user1);

        Recipe pantryTestRecipe2 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description("Test Description")
            .name("Test 2").build();
        pantryTestRecipe2.addIngredient(
            Item.builder()
                .description("PantryTest-Potato")
                .amount(2)
                .unit(Unit.Piece).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe2);
        userRepository.saveAndFlush(user1);

        Recipe pantryTestRecipe3 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description("Test Description")
            .name("Test 3").build();
        pantryTestRecipe3.addIngredient(
            Item.builder()
                .description("PantryTest-Honey")
                .amount(2)
                .unit(Unit.Piece).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe3);
        userRepository.saveAndFlush(user1);
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for recipes");
        recipeRepository.deleteAll();
    }
}