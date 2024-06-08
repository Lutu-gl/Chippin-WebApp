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

        // Recipe 6: Blueberry Muffins
        Recipe newRecipe6 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(12)
            .description("Moist and fluffy blueberry muffins, perfect for breakfast or a snack. "
                + "Instructions: \n1. Preheat the oven to 375°F (190°C). \n2. Line a muffin tin with paper liners. "
                + "\n3. In a large bowl, whisk together the flour, sugar, baking powder, and salt. "
                + "\n4. In another bowl, whisk together the eggs, milk, and melted butter. "
                + "\n5. Pour the wet ingredients into the dry ingredients and stir until just combined. "
                + "\n6. Gently fold in the blueberries. \n7. Divide the batter evenly among the muffin cups. "
                + "\n8. Bake for 18-20 minutes or until a toothpick inserted into the center comes out clean. "
                + "\n9. Let cool in the pan for 5 minutes, then transfer to a wire rack to cool completely.")
            .name("Blueberry Muffins")
            .build();
        newRecipe6.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(300)
                .unit(Unit.Gram)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Baking Powder")
                .amount(15)
                .unit(Unit.Gram)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Milk")
                .amount(250)
                .unit(Unit.Milliliter)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe6.addIngredient(
            Item.builder()
                .description("Blueberries")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe6);
        recipeRepository.saveAndFlush(newRecipe6);

        // Recipe 7: Banana Bread
        Recipe newRecipe7 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(8)
            .description("Classic banana bread that's moist and delicious. "
                + "Instructions: \n1. Preheat the oven to 350°F (175°C). \n2. Grease a 9x5 inch loaf pan. "
                + "\n3. In a large bowl, combine the flour, baking soda, and salt. "
                + "\n4. In another bowl, cream together the butter and brown sugar. "
                + "\n5. Stir in the eggs and mashed bananas until well blended. "
                + "\n6. Stir banana mixture into flour mixture; stir just to moisten. "
                + "\n7. Pour batter into prepared loaf pan. "
                + "\n8. Bake for 60-65 minutes or until a toothpick inserted into the center of the loaf comes out clean. "
                + "\n9. Let bread cool in pan for 10 minutes, then turn out onto a wire rack.")
            .name("Banana Bread")
            .build();
        newRecipe7.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(250)
                .unit(Unit.Gram)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Baking Soda")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(3)
                .unit(Unit.Gram)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(115)
                .unit(Unit.Gram)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Brown Sugar")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe7.addIngredient(
            Item.builder()
                .description("Mashed Bananas")
                .amount(475)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe7);
        recipeRepository.saveAndFlush(newRecipe7);

        // Recipe 8: Pancakes
        Recipe newRecipe8 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Fluffy and light pancakes perfect for a weekend breakfast. "
                + "Instructions: \n1. In a large bowl, sift together the flour, baking powder, salt, and sugar. "
                + "\n2. Make a well in the center and pour in the milk, egg, and melted butter; mix until smooth. "
                + "\n3. Heat a lightly oiled griddle or frying pan over medium high heat. "
                + "\n4. Pour or scoop the batter onto the griddle, using approximately 1/4 cup for each pancake. "
                + "\n5. Brown on both sides and serve hot.")
            .name("Pancakes")
            .build();
        newRecipe8.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Baking Powder")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(3)
                .unit(Unit.Gram)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(25)
                .unit(Unit.Gram)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Milk")
                .amount(300)
                .unit(Unit.Milliliter)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Egg")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe8.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(50)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe8);
        recipeRepository.saveAndFlush(newRecipe8);

        // Recipe 9: Spaghetti Carbonara
        Recipe newRecipe9 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Classic Italian pasta dish with a creamy egg-based sauce. "
                + "Instructions: \n1. Bring a large pot of salted water to a boil. "
                + "\n2. Cook spaghetti according to package instructions. "
                + "\n3. In a large skillet, cook pancetta over medium heat until crispy. "
                + "\n4. In a bowl, whisk together the eggs, cheese, and pepper. "
                + "\n5. Drain the pasta, reserving some pasta water. "
                + "\n6. Quickly toss the hot pasta with the egg mixture, pancetta, and a bit of reserved pasta water until creamy. "
                + "\n7. Serve immediately with extra cheese and pepper.")
            .name("Spaghetti Carbonara")
            .build();
        newRecipe9.addIngredient(
            Item.builder()
                .description("Spaghetti")
                .amount(400)
                .unit(Unit.Gram)
                .build());
        newRecipe9.addIngredient(
            Item.builder()
                .description("Pancetta")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe9.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe9.addIngredient(
            Item.builder()
                .description("Grated Parmesan Cheese")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe9.addIngredient(
            Item.builder()
                .description("Black Pepper")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe9);
        recipeRepository.saveAndFlush(newRecipe9);

        // Recipe 10: Caesar Salad
        Recipe newRecipe10 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(2)
            .description("A fresh and tangy Caesar salad with homemade dressing. "
                + "Instructions: \n1. In a large bowl, toss the romaine lettuce with the croutons and grated Parmesan. "
                + "\n2. In a small bowl, whisk together the olive oil, lemon juice, garlic, anchovy paste, Dijon mustard, and Worcestershire sauce. "
                + "\n3. Drizzle the dressing over the salad and toss to coat. "
                + "\n4. Serve immediately.")
            .name("Caesar Salad")
            .build();
        newRecipe10.addIngredient(
            Item.builder()
                .description("Romaine Lettuce")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Croutons")
                .amount(50)
                .unit(Unit.Gram)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Grated Parmesan Cheese")
                .amount(30)
                .unit(Unit.Gram)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(60)
                .unit(Unit.Milliliter)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Lemon Juice")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Anchovy Paste")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Dijon Mustard")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe10.addIngredient(
            Item.builder()
                .description("Worcestershire Sauce")
                .amount(5)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe10);
        recipeRepository.saveAndFlush(newRecipe10);

        // Recipe 11: Beef Stew
        Recipe newRecipe11 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(6)
            .description("Hearty beef stew with tender meat and vegetables. "
                + "Instructions: \n1. In a large pot, heat oil over medium heat. "
                + "\n2. Add the beef and cook until browned on all sides. "
                + "\n3. Add the onions and garlic, and cook until softened. "
                + "\n4. Stir in the flour and cook for a minute. "
                + "\n5. Add the beef broth, potatoes, carrots, celery, bay leaf, salt, and pepper. "
                + "\n6. Bring to a boil, then reduce heat and simmer for 2 hours, until the meat is tender. "
                + "\n7. Remove bay leaf and serve hot.")
            .name("Beef Stew")
            .build();
        newRecipe11.addIngredient(
            Item.builder()
                .description("Beef Stew Meat")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Onions")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(30)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Beef Broth")
                .amount(1000)
                .unit(Unit.Milliliter)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Potatoes")
                .amount(300)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Carrots")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Celery")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Bay Leaf")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe11.addIngredient(
            Item.builder()
                .description("Black Pepper")
                .amount(3)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe11);
        recipeRepository.saveAndFlush(newRecipe11);

        // Recipe 12: Chicken Curry
        Recipe newRecipe12 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Aromatic chicken curry with a rich, flavorful sauce. "
                + "Instructions: \n1. Heat oil in a large pan over medium heat. "
                + "\n2. Add the onions and cook until golden brown. "
                + "\n3. Add the garlic, ginger, and spices, and cook for another minute. "
                + "\n4. Add the chicken pieces and cook until no longer pink. "
                + "\n5. Stir in the tomatoes and coconut milk. "
                + "\n6. Simmer for 20-25 minutes, until the chicken is cooked through. "
                + "\n7. Garnish with cilantro and serve with rice.")
            .name("Chicken Curry")
            .build();
        newRecipe12.addIngredient(
            Item.builder()
                .description("Chicken Breast")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Onions")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(3)
                .unit(Unit.Piece)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Ginger")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Spices")
                .amount(20)
                .unit(Unit.Gram)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Tomatoes")
                .amount(400)
                .unit(Unit.Gram)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Coconut Milk")
                .amount(400)
                .unit(Unit.Milliliter)
                .build());
        newRecipe12.addIngredient(
            Item.builder()
                .description("Cilantro")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe12);
        recipeRepository.saveAndFlush(newRecipe12);

        // Recipe 13: Chocolate Cake
        Recipe newRecipe13 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(8)
            .description("Rich and moist chocolate cake perfect for celebrations. "
                + "Instructions: \n1. Preheat the oven to 350°F (175°C). "
                + "\n2. Grease and flour two 9-inch round pans. "
                + "\n3. In a large bowl, stir together the sugar, flour, cocoa, baking powder, baking soda, and salt. "
                + "\n4. Add the eggs, milk, oil, and vanilla, and mix for 2 minutes on medium speed of mixer. "
                + "\n5. Stir in the boiling water last. "
                + "\n6. Pour batter into prepared pans. "
                + "\n7. Bake for 30 to 35 minutes or until wooden pick inserted in center comes out clean. "
                + "\n8. Cool for 10 minutes; remove from pans to wire racks. Cool completely.")
            .name("Chocolate Cake")
            .build();
        newRecipe13.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(400)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(250)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Cocoa Powder")
                .amount(75)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Baking Powder")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Baking Soda")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Milk")
                .amount(240)
                .unit(Unit.Milliliter)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Vegetable Oil")
                .amount(120)
                .unit(Unit.Milliliter)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Vanilla Extract")
                .amount(10)
                .unit(Unit.Milliliter)
                .build());
        newRecipe13.addIngredient(
            Item.builder()
                .description("Boiling Water")
                .amount(240)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe13);
        recipeRepository.saveAndFlush(newRecipe13);

        // Recipe 14: Vegetable Stir Fry
        Recipe newRecipe14 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Quick and easy vegetable stir fry with a savory sauce. "
                + "Instructions: \n1. Heat oil in a large pan over medium-high heat. "
                + "\n2. Add the garlic and cook for 1 minute. "
                + "\n3. Add the vegetables and cook until tender-crisp. "
                + "\n4. In a small bowl, whisk together the soy sauce, cornstarch, and water. "
                + "\n5. Pour the sauce over the vegetables and cook until the sauce has thickened. "
                + "\n6. Serve hot with rice or noodles.")
            .name("Vegetable Stir Fry")
            .build();
        newRecipe14.addIngredient(
            Item.builder()
                .description("Mixed Vegetables")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe14.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe14.addIngredient(
            Item.builder()
                .description("Soy Sauce")
                .amount(60)
                .unit(Unit.Milliliter)
                .build());
        newRecipe14.addIngredient(
            Item.builder()
                .description("Cornstarch")
                .amount(15)
                .unit(Unit.Gram)
                .build());
        newRecipe14.addIngredient(
            Item.builder()
                .description("Water")
                .amount(120)
                .unit(Unit.Milliliter)
                .build());
        newRecipe14.addIngredient(
            Item.builder()
                .description("Vegetable Oil")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe14);
        recipeRepository.saveAndFlush(newRecipe14);

        // Recipe 15: French Toast
        Recipe newRecipe15 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Classic French toast that's crispy on the outside and fluffy on the inside. "
                + "Instructions: \n1. In a shallow bowl, whisk together the eggs, milk, sugar, cinnamon, and vanilla extract. "
                + "\n2. Heat a lightly oiled griddle or frying pan over medium heat. "
                + "\n3. Soak bread slices in the egg mixture, coating both sides. "
                + "\n4. Cook bread on the griddle until both sides are golden brown. "
                + "\n5. Serve hot with syrup, butter, and powdered sugar.")
            .name("French Toast")
            .build();
        newRecipe15.addIngredient(
            Item.builder()
                .description("Bread Slices")
                .amount(8)
                .unit(Unit.Piece)
                .build());
        newRecipe15.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe15.addIngredient(
            Item.builder()
                .description("Milk")
                .amount(240)
                .unit(Unit.Milliliter)
                .build());
        newRecipe15.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(25)
                .unit(Unit.Gram)
                .build());
        newRecipe15.addIngredient(
            Item.builder()
                .description("Cinnamon")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe15.addIngredient(
            Item.builder()
                .description("Vanilla Extract")
                .amount(5)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe15);
        recipeRepository.saveAndFlush(newRecipe15);

        // Recipe 16: Tomato Soup
        Recipe newRecipe16 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Comforting and rich tomato soup. "
                + "Instructions: \n1. Heat oil in a large pot over medium heat. "
                + "\n2. Add the onions and cook until soft. "
                + "\n3. Add the garlic and cook for another minute. "
                + "\n4. Stir in the tomatoes, broth, sugar, salt, and pepper. "
                + "\n5. Bring to a boil, then reduce heat and simmer for 30 minutes. "
                + "\n6. Blend the soup until smooth. "
                + "\n7. Serve hot with a dollop of cream or a sprinkling of cheese.")
            .name("Tomato Soup")
            .build();
        newRecipe16.addIngredient(
            Item.builder()
                .description("Tomatoes")
                .amount(800)
                .unit(Unit.Gram)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Onion")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Vegetable Broth")
                .amount(500)
                .unit(Unit.Milliliter)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Black Pepper")
                .amount(3)
                .unit(Unit.Gram)
                .build());
        newRecipe16.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe16);
        recipeRepository.saveAndFlush(newRecipe16);

        // Recipe 17: Tacos
        Recipe newRecipe17 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Delicious tacos with a variety of fillings. "
                + "Instructions: \n1. In a large pan, cook the ground beef until browned. "
                + "\n2. Add the taco seasoning and water, and cook until thickened. "
                + "\n3. Warm the taco shells in the oven or microwave. "
                + "\n4. Fill the taco shells with beef, lettuce, cheese, tomatoes, and any other desired toppings. "
                + "\n5. Serve immediately.")
            .name("Tacos")
            .build();
        newRecipe17.addIngredient(
            Item.builder()
                .description("Ground Beef")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Taco Seasoning")
                .amount(30)
                .unit(Unit.Gram)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Water")
                .amount(120)
                .unit(Unit.Milliliter)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Taco Shells")
                .amount(8)
                .unit(Unit.Piece)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Lettuce")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Shredded Cheese")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe17.addIngredient(
            Item.builder()
                .description("Tomatoes")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe17);
        recipeRepository.saveAndFlush(newRecipe17);

        // Recipe 18: Lemon Bars
        Recipe newRecipe18 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(12)
            .description("Sweet and tangy lemon bars with a shortbread crust. "
                + "Instructions: \n1. Preheat the oven to 350°F (175°C). "
                + "\n2. In a bowl, mix together the flour, sugar, and butter for the crust. "
                + "\n3. Press the mixture into the bottom of a greased 9x13 inch pan. "
                + "\n4. Bake for 15-20 minutes or until firm and golden. "
                + "\n5. In another bowl, whisk together the sugar, flour, eggs, lemon juice, and lemon zest for the filling. "
                + "\n6. Pour the filling over the baked crust. "
                + "\n7. Bake for an additional 20-25 minutes. "
                + "\n8. Let cool completely before cutting into squares.")
            .name("Lemon Bars")
            .build();
        newRecipe18.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(250)
                .unit(Unit.Gram)
                .build());
        newRecipe18.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe18.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe18.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe18.addIngredient(
            Item.builder()
                .description("Lemon Juice")
                .amount(120)
                .unit(Unit.Milliliter)
                .build());
        newRecipe18.addIngredient(
            Item.builder()
                .description("Lemon Zest")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe18);
        recipeRepository.saveAndFlush(newRecipe18);

        // Recipe 19: Garlic Bread
        Recipe newRecipe19 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Crispy and buttery garlic bread. "
                + "Instructions: \n1. Preheat the oven to 375°F (190°C). "
                + "\n2. In a small bowl, mix together the butter, garlic, and parsley. "
                + "\n3. Spread the mixture onto the bread slices. "
                + "\n4. Place the bread on a baking sheet and bake for 10-12 minutes, or until golden and crispy. "
                + "\n5. Serve hot.")
            .name("Garlic Bread")
            .build();
        newRecipe19.addIngredient(
            Item.builder()
                .description("Bread")
                .amount(8)
                .unit(Unit.Piece)
                .build());
        newRecipe19.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe19.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe19.addIngredient(
            Item.builder()
                .description("Parsley")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe19);
        recipeRepository.saveAndFlush(newRecipe19);

        // Recipe 20: Chicken Alfredo
        Recipe newRecipe20 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Creamy and comforting chicken Alfredo pasta. "
                + "Instructions: \n1. Cook the fettuccine according to package instructions. "
                + "\n2. In a large skillet, melt the butter over medium heat. "
                + "\n3. Add the garlic and cook for 1 minute. "
                + "\n4. Add the chicken and cook until no longer pink. "
                + "\n5. Stir in the heavy cream and Parmesan cheese. "
                + "\n6. Simmer for 5 minutes, until the sauce thickens. "
                + "\n7. Toss the pasta with the sauce and serve hot.")
            .name("Chicken Alfredo")
            .build();
        newRecipe20.addIngredient(
            Item.builder()
                .description("Fettuccine")
                .amount(300)
                .unit(Unit.Gram)
                .build());
        newRecipe20.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(50)
                .unit(Unit.Gram)
                .build());
        newRecipe20.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe20.addIngredient(
            Item.builder()
                .description("Chicken Breast")
                .amount(300)
                .unit(Unit.Gram)
                .build());
        newRecipe20.addIngredient(
            Item.builder()
                .description("Heavy Cream")
                .amount(240)
                .unit(Unit.Milliliter)
                .build());
        newRecipe20.addIngredient(
            Item.builder()
                .description("Parmesan Cheese")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe20);
        recipeRepository.saveAndFlush(newRecipe20);

        // Recipe 21: Pancakes
        Recipe newRecipe21 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Fluffy and delicious pancakes. "
                + "Instructions: \n1. In a large bowl, sift together the flour, baking powder, salt, and sugar. "
                + "\n2. Make a well in the center and pour in the milk, egg, and melted butter. "
                + "\n3. Mix until smooth. "
                + "\n4. Heat a lightly oiled griddle or frying pan over medium-high heat. "
                + "\n5. Pour or scoop the batter onto the griddle, using approximately 1/4 cup for each pancake. "
                + "\n6. Brown on both sides and serve hot with syrup or toppings of your choice.")
            .name("Pancakes")
            .build();
        newRecipe21.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(250)
                .unit(Unit.Gram)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Baking Powder")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(25)
                .unit(Unit.Gram)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Milk")
                .amount(300)
                .unit(Unit.Milliliter)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Egg")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe21.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(30)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe21);
        recipeRepository.saveAndFlush(newRecipe21);

        // Recipe 22: Caesar Salad
        Recipe newRecipe22 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Classic Caesar salad with crisp romaine lettuce and creamy dressing. "
                + "Instructions: \n1. In a small bowl, whisk together the garlic, mustard, lemon juice, Worcestershire sauce, and egg yolk. "
                + "\n2. Gradually whisk in the olive oil until the dressing is thick and creamy. "
                + "\n3. Toss the lettuce with the dressing, Parmesan cheese, and croutons. "
                + "\n4. Serve immediately.")
            .name("Caesar Salad")
            .build();
        newRecipe22.addIngredient(
            Item.builder()
                .description("Romaine Lettuce")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Dijon Mustard")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Lemon Juice")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Worcestershire Sauce")
                .amount(5)
                .unit(Unit.Milliliter)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Egg Yolk")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(60)
                .unit(Unit.Milliliter)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Parmesan Cheese")
                .amount(50)
                .unit(Unit.Gram)
                .build());
        newRecipe22.addIngredient(
            Item.builder()
                .description("Croutons")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe22);
        recipeRepository.saveAndFlush(newRecipe22);

        // Recipe 23: Beef Tacos
        Recipe newRecipe23 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Flavorful beef tacos with a variety of toppings. "
                + "Instructions: \n1. In a large pan, cook the ground beef until browned. "
                + "\n2. Add the taco seasoning and water, and cook until thickened. "
                + "\n3. Warm the taco shells in the oven or microwave. "
                + "\n4. Fill the taco shells with beef, lettuce, cheese, tomatoes, and any other desired toppings. "
                + "\n5. Serve immediately.")
            .name("Beef Tacos")
            .build();
        newRecipe23.addIngredient(
            Item.builder()
                .description("Ground Beef")
                .amount(500)
                .unit(Unit.Gram)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Taco Seasoning")
                .amount(30)
                .unit(Unit.Gram)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Water")
                .amount(120)
                .unit(Unit.Milliliter)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Taco Shells")
                .amount(8)
                .unit(Unit.Piece)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Lettuce")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Shredded Cheese")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe23.addIngredient(
            Item.builder()
                .description("Tomatoes")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe23);
        recipeRepository.saveAndFlush(newRecipe23);

        // Recipe 24: Chicken Soup
        Recipe newRecipe24 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(4)
            .description("Hearty chicken soup with vegetables and noodles. "
                + "Instructions: \n1. In a large pot, heat the oil over medium heat. "
                + "\n2. Add the onions, carrots, and celery, and cook until softened. "
                + "\n3. Add the garlic and cook for another minute. "
                + "\n4. Pour in the chicken broth and bring to a boil. "
                + "\n5. Add the chicken and noodles, and simmer until the chicken is cooked through and the noodles are tender. "
                + "\n6. Season with salt and pepper to taste. "
                + "\n7. Serve hot.")
            .name("Chicken Soup")
            .build();
        newRecipe24.addIngredient(
            Item.builder()
                .description("Chicken Breast")
                .amount(300)
                .unit(Unit.Gram)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Onions")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Carrots")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Celery")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Chicken Broth")
                .amount(1)
                .unit(Unit.Milliliter)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Noodles")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe24.addIngredient(
            Item.builder()
                .description("Black Pepper")
                .amount(3)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe24);
        recipeRepository.saveAndFlush(newRecipe24);

        // Recipe 25: Brownies
        Recipe newRecipe25 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(12)
            .description("Rich and fudgy brownies. "
                + "Instructions: \n1. Preheat the oven to 350°F (175°C). "
                + "\n2. In a bowl, mix together the melted butter and sugar. "
                + "\n3. Add the eggs and vanilla extract, and mix until well combined. "
                + "\n4. Stir in the cocoa powder, flour, salt, and baking powder. "
                + "\n5. Pour the batter into a greased 9x13 inch pan. "
                + "\n6. Bake for 20-25 minutes, or until a toothpick inserted into the center comes out clean. "
                + "\n7. Let cool before cutting into squares.")
            .name("Brownies")
            .build();
        newRecipe25.addIngredient(
            Item.builder()
                .description("Butter")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Sugar")
                .amount(400)
                .unit(Unit.Gram)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Eggs")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Vanilla Extract")
                .amount(10)
                .unit(Unit.Milliliter)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Cocoa Powder")
                .amount(75)
                .unit(Unit.Gram)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Flour")
                .amount(125)
                .unit(Unit.Gram)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe25.addIngredient(
            Item.builder()
                .description("Baking Powder")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        user1.addRecipe(newRecipe25);
        recipeRepository.saveAndFlush(newRecipe25);

        Recipe newRecipe26 = Recipe.builder()
            .owner(user1)
            .isPublic(true)
            .portionSize(8)
            .description("Hearty and flavorful stew with a wide variety of ingredients. "
                + "Instructions: \n1. Heat the olive oil in a large pot over medium heat. "
                + "\n2. Add the onions, garlic, and cook until softened. "
                + "\n3. Add the beef and cook until browned. "
                + "\n4. Stir in the carrots, celery, potatoes, and all other vegetables. "
                + "\n5. Add the tomatoes, broth, wine, and all seasonings. "
                + "\n6. Bring to a boil, then reduce heat and simmer for 2 hours. "
                + "\n7. Stir in the beans, corn, peas, and adjust seasoning as needed. "
                + "\n8. Cook for another 30 minutes, then serve hot with crusty bread.")
            .name("Ultimate Stew")
            .build();
        newRecipe26.addIngredient(
            Item.builder()
                .description("Olive Oil")
                .amount(60)
                .unit(Unit.Milliliter)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Onions")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Garlic")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Beef Stew Meat")
                .amount(1000)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Carrots")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Celery Stalks")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Potatoes")
                .amount(4)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Tomatoes")
                .amount(800)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Beef Broth")
                .amount(1500)
                .unit(Unit.Milliliter)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Red Wine")
                .amount(240)
                .unit(Unit.Milliliter)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Salt")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Black Pepper")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Bay Leaves")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Thyme")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Rosemary")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Paprika")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Cumin")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Coriander")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Chili Powder")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Oregano")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Parsley")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Basil")
                .amount(5)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Red Bell Pepper")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Green Bell Pepper")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Yellow Bell Pepper")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Zucchini")
                .amount(1)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Mushrooms")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Green Beans")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Corn Kernels")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Peas")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Kidney Beans")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Chickpeas")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Lentils")
                .amount(150)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Barley")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Spinach")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Kale")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Cabbage")
                .amount(100)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Leeks")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Parsnips")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Turnips")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Sweet Potatoes")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Butternut Squash")
                .amount(200)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Beef Bouillon Cubes")
                .amount(2)
                .unit(Unit.Piece)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Soy Sauce")
                .amount(30)
                .unit(Unit.Milliliter)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Worcestershire Sauce")
                .amount(15)
                .unit(Unit.Milliliter)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Brown Sugar")
                .amount(10)
                .unit(Unit.Gram)
                .build());
        newRecipe26.addIngredient(
            Item.builder()
                .description("Apple Cider Vinegar")
                .amount(15)
                .unit(Unit.Milliliter)
                .build());
        user1.addRecipe(newRecipe26);
        recipeRepository.saveAndFlush(newRecipe26);
        userRepository.saveAndFlush(user1);
    }

    @Override
    public void cleanData() {
        LOGGER.debug("cleaning data for recipes");
        recipeRepository.deleteAll();
    }
}