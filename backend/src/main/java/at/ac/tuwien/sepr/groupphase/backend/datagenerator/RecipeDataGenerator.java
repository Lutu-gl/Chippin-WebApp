package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.entity.Recipe;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import at.ac.tuwien.sepr.groupphase.backend.repository.ItemRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.RecipeRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.UserService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@AllArgsConstructor
public class RecipeDataGenerator implements DataGenerator {
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
        random.setSeed(12345);
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

        Random random = new Random();
        random.setSeed(12345);
        Faker faker = new Faker(Locale.getDefault(), random);

        recipeRepository.saveAndFlush(Recipe.builder()
            .name(faker.lorem().word())
            .description(faker.lorem().paragraph())
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
        ApplicationUser user = users.stream().filter(o -> o.getEmail().equals("rafael@chippin.com")).findFirst().get();
        for (int i = 0; i < 30; i++) {
            Recipe recipe = Recipe.builder()
                .name(faker.lorem().word())
                .description(faker.lorem().paragraph())
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
        ApplicationUser user1 = userRepository.findByEmail("rafael@chippin.com");
        Recipe pantryTestRecipe1 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description(faker.lorem().paragraph())
            .name(faker.lorem().word()).build();
        pantryTestRecipe1.addIngredient(
            Item.builder()
                .description(descriptions[random.nextInt(descriptions.length)])
                .amount(2)
                .unit(Unit.Piece).build());
        pantryTestRecipe1.addIngredient(
            Item.builder()
                .description(descriptions[random.nextInt(descriptions.length)])
                .amount(200)
                .unit(Unit.Milliliter).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe1);
        userRepository.saveAndFlush(user1);

        Recipe pantryTestRecipe2 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description(faker.lorem().paragraph())
            .name(faker.lorem().word()).build();
        pantryTestRecipe2.addIngredient(
            Item.builder()
                .description(descriptions[random.nextInt(descriptions.length)])
                .amount(2)
                .unit(Unit.Piece).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe2);
        userRepository.saveAndFlush(user1);

        Recipe pantryTestRecipe3 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description(faker.lorem().paragraph())
            .name(faker.lorem().word()).build();
        pantryTestRecipe3.addIngredient(
            Item.builder()
                .description(descriptions[random.nextInt(descriptions.length)])
                .amount(2)
                .unit(Unit.Piece).build());
        user1.addRecipe(pantryTestRecipe1);
        recipeRepository.saveAndFlush(pantryTestRecipe3);
        userRepository.saveAndFlush(user1);


        Recipe newRecipe1 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("A hearty and delicious homemade spaghetti bolognese. "
                + "Instructions: \n1. Cook the spaghetti according to the package instructions. "
                + "\n2. In a pan, cook the ground beef until browned. \n3. Add the tomato sauce to the beef and simmer for 10 minutes. "
                + "\n4. Serve the sauce over the spaghetti and enjoy.")
            .name("Spaghetti Bolognese")
            .build();
        newRecipe1.addIngredient(
            Item.builder()
                .description("Spaghetti")
                .amount(400)
                .unit(Unit.Gram)
                .build());
        newRecipe1.addIngredient(
            Item.builder()
                .description("Ground Beef")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe1.addIngredient(
            Item.builder()
                .description("Tomato Sauce")
                .amount(2)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe1);
        recipeRepository.saveAndFlush(newRecipe1);

        // Recipe 2: Avocado Toast
        Recipe newRecipe2 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(2)
            .description("A quick and easy avocado toast, perfect for breakfast or a light lunch. "
                + "Instructions: \n1. Toast the bread slices. \n2. Mash the avocado and spread it on the toasted bread."
                + " \n3. Sprinkle with salt and pepper. \n4. Serve immediately.")
            .name("Avocado Toast")
            .build();
        newRecipe2.addIngredient(
            Item.builder()
                .description("Avocado")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe2.addIngredient(
            Item.builder()
                .description("Whole Grain Bread")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe2.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        newRecipe2.addIngredient(
            Item.builder()
                .description("Pepper")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe2);
        recipeRepository.saveAndFlush(newRecipe2);

        // Recipe 3: Cucumber Salad
        Recipe newRecipe3 = Recipe.builder()
            .owner(user1)
            .isPublic(false)
            .portionSize(1)
            .description("A simple and refreshing cucumber salad, perfect as a side dish or a light snack. "
                + "Instructions: \n1. Slice the cucumber thinly. \n2. Mix vinegar, olive oil, salt, and pepper in a bowl. "
                + "\n3. Add the cucumber slices and toss to coat. \n4. Serve immediately or chill in the refrigerator before serving.")
            .name("Cucumber Salad")
            .build();
        newRecipe3.addIngredient(
            Item.builder()
                .description("Cucumber")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe3.addIngredient(
            Item.builder()
                .description("Vinegar")
                .amount(2)
                .unit(Unit.Milliliter)
                .build());
        newRecipe3.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe3.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        newRecipe3.addIngredient(
            Item.builder()
                .description("Pepper")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe3);
        recipeRepository.saveAndFlush(newRecipe3);

        // Recipe 4: Chicken Caesar Salad
        Recipe newRecipe4 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(2)
            .description("A classic chicken Caesar salad that's both healthy and tasty. "
                + "Instructions: \n1. Grill or cook the chicken breasts and slice them. "
                + "\n2. Chop the romaine lettuce and place it in a large bowl. \n3. Add the Caesar dressing and toss to coat. "
                + "\n4. Top with chicken slices, Parmesan cheese, and croutons. \n5. Serve immediately.")
            .name("Chicken Caesar Salad")
            .build();
        newRecipe4.addIngredient(
            Item.builder()
                .description("Chicken Breasts")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe4.addIngredient(
            Item.builder()
                .description("Romaine Lettuce")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe4.addIngredient(
            Item.builder()
                .description("Caesar Dressing")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe4.addIngredient(
            Item.builder()
                .description("Parmesan Cheese")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe4.addIngredient(
            Item.builder()
                .description("Croutons")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe4);
        recipeRepository.saveAndFlush(newRecipe4);

        // Recipe 5: Chocolate Chip Cookies
        Recipe newRecipe5 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(12)
            .description("Delicious homemade chocolate chip cookies, perfect for any occasion. "
                + "Instructions: \n1. Preheat the oven to 350°F (175°C). \n2. Cream together the butter, sugar, and brown sugar. "
                + "\n3. Beat in the eggs and vanilla extract. \n4. In a separate bowl, combine the flour, baking soda, and salt. "
                + "\n5. Gradually add the dry ingredients to the wet ingredients. \n6. Stir in the chocolate chips. \n7. Drop by rounded spoonfuls onto a baking sheet. "
                + "\n8. Bake for 10-12 minutes or until golden brown. \n9. Let cool on a wire rack.")
            .name("Chocolate Chip Cookies")
            .build();
        newRecipe5.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Brown Sugar")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Vanilla Extract")
                .amount(2)
                .unit(Unit.Gram)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(3)
                .unit(Unit.Milliliter)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Baking Soda")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(1)
                .unit(Unit.Gram)
                .build());
        newRecipe5.addIngredient(
            Item.builder()
                .description("Chocolate Chips")
                .amount(2)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe5);
        recipeRepository.saveAndFlush(newRecipe5);

        userRepository.saveAndFlush(user1);
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for recipes");
        recipeRepository.deleteAll();
    }
}